package edu.tamu.tcat.zotero.basic.v3.commands;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import edu.tamu.tcat.zotero.ZoteroRestException;
import edu.tamu.tcat.zotero.basic.v3.ZoteroApiCommand;
import edu.tamu.tcat.zotero.basic.v3.ZoteroUserCredentials;

/**
 * Responsible for creating and executing commands against the Zotero REST API. This class is
 * used to setup the initial {@link WebTarget} that is passed to the command to configure and
 * for ensuring that user authentication tokens are properly formatted into the command. It
 * is also responsible for reading the supplied result and enforcing any rate limits or
 * back-offs that may be required by the REST API.
 */
public class ZoteroCommandExecutor
{
   // TODO rename to ZoteroAsyncService

   private final ScheduledExecutorService cmdExec;
   private final ExecutorService taskExec;
   private volatile boolean isShutdown = false;

   private final URI zoteroEndpoint;

   private final long timeout;
   private final TimeUnit timeoutUnits;

   private Client client;

   public ZoteroCommandExecutor()
   {
      this(URI.create("https://api.zotero.org"), 10);
   }

   public ZoteroCommandExecutor(URI zoteroEndpoint, int poolSize)
   {
      this.zoteroEndpoint = zoteroEndpoint;
      cmdExec = Executors.newScheduledThreadPool(poolSize);
      taskExec = Executors.newCachedThreadPool();

      client = ClientBuilder.newBuilder().register(JacksonJsonProvider.class).build();

      this.timeout = 10;
      this.timeoutUnits = TimeUnit.SECONDS;
   }

   public void close()
   {
      isShutdown = true;

      shutdownExecutor(cmdExec);
      shutdownExecutor(taskExec);

      client.close();
      client = null;
   }

   private boolean shutdownExecutor(ExecutorService service)
   {
      boolean success = false;
      try {
         service.shutdown();
         success = service.awaitTermination(100, TimeUnit.SECONDS);
      } catch (Exception ex) {

      }

      if (!success) {
         service.shutdownNow();

         // TODO print info about threads that were still running
      }

      return success;
   }

   /**
    * Provides a mechanism for deferring a task to be run in the background. For example, this
    * could be used to lazily load paged data in an item set pending
    *
    * @param task
    * @return
    */
   public <X> Future<X> submitBackgroundTask(Callable<X> task)
   {
      if (isShutdown)
         throw new IllegalStateException("This async service has been shut down.");

      return taskExec.submit(task);
   }

   @Deprecated // create commands directly
   public <X extends ZoteroApiCommand<?>> X createCommand(Class<X> type, ZoteroUserCredentials credentials) throws IllegalArgumentException
   {
      try
      {
         Constructor<X> ctor = type.getConstructor(ZoteroCommandExecutor.class, ZoteroUserCredentials.class);
         X cmd = ctor.newInstance(this, credentials);
         return cmd;
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException("Cannot create API command for " + type, e);
      }
   }

   public <X> Future<X> submit(CommandRunnerContext<X> command)
   {
      if (isShutdown)
         throw new IllegalStateException("This command executor has been shut down.");

      URI uri = command.getUri(this.zoteroEndpoint);
      WebTarget target = client.target(uri);

      Invocation invocation = command.configure(target);
      return cmdExec.submit(() -> {
         Response resp = null;
         try
         {
            resp = invocation.invoke();
            inspectResponse(resp);
            return command.handleResponse(resp);
         }
         finally
         {
            if (resp != null)
               resp.close();
         }
      });
   }

   private void inspectResponse(Response resp) {
      // TODO extract backoff info, etc from response

   }

   public <X> X unwrap(Future<X> result, Supplier<String> message) throws ZoteroRestException
   {
      try
      {
         return result.get(timeout, timeoutUnits);
      }
      catch (InterruptedException | TimeoutException e)
      {
         throw new IllegalStateException(message.get(), e);
      }
      catch (ExecutionException ex)
      {
         Throwable cause = ex.getCause();
         if (cause instanceof ZoteroRestException)
            throw (ZoteroRestException)cause;

         if  (cause instanceof RuntimeException)
            throw (RuntimeException)cause;

         throw new IllegalStateException(message.get(), cause);
      }
   }

}
