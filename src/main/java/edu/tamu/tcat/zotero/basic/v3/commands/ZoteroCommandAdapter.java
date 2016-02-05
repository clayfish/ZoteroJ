package edu.tamu.tcat.zotero.basic.v3.commands;

import java.net.URI;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import edu.tamu.tcat.zotero.ZoteroLibrary;
import edu.tamu.tcat.zotero.basic.v3.BasicUserAccount;
import edu.tamu.tcat.zotero.basic.v3.BasicZoteroLibrary;
import edu.tamu.tcat.zotero.basic.v3.ZoteroApiCommand;

/**
 *  A base class to support common functionality of {@link ZoteroApiCommand} implementations.
 *  In general, implementations should be designed narrowly to accept specific information to
 *  parameterize the REST API call and to unpack the response and return a REST DTO rather
 *  than a domain object. It is the responsibility of the client who creates the command to
 *  adapt the DTO into the domain model.
 *
 *  This implementation is designed for use with library-specific components of the API;
 *  chiefly working with items and collections as opposed to the item type metadata or
 *  information about a user's account and access.
 *
 *  <p>
 *  In general, command implementations should extend this adapter (or a library independent
 *  adapter) rather than implementing the command interface directly.
 *
 *  TODO review and update documentation
 */
public abstract class ZoteroCommandAdapter<T> implements ZoteroApiCommand<T>
{
   protected static final Logger cmdLogger = Logger.getLogger(ZoteroCommandAdapter.class.getName());

   // NOTE: may be NULL. Not all commands require authentication.
   private final BasicUserAccount account;

   // NOTE: may be NULL Not all commands require a library. If provided, the WebTarget supplied
   //       the #buildInvocation method will be scoped to this library.
   private final BasicZoteroLibrary library;

   private final ZoteroCommandExecutor executor;

   private final AtomicBoolean hasBeenExecuted = new AtomicBoolean(false);

   public ZoteroCommandAdapter(BasicZoteroLibrary library)
   {
      this.library = library;
      this.account = library.getAccount();
      this.executor = account.getExecutor();
   }

   /**
    * Constructs an adapter for an authenticated user account, but without any library-specific
    * context. This constructor should be used for commands that require access to account
    * information, but that do not execute queries for items within a specific library.
    *
    * @param account
    */
   public ZoteroCommandAdapter(BasicUserAccount account)
   {
      this.library = null;
      this.account = account;
      this.executor = account.getExecutor();
   }

   public ZoteroCommandAdapter(ZoteroCommandExecutor executor)
   {
      this.library = null;
      this.account = null;
      this.executor = executor;
   }

   /**
    * @return The Zotero library (either a user account library or a group library) that
    *    defines the scope in which this command will be executed.
    */
   protected BasicZoteroLibrary getLibrary()
   {
      return library;
   }

   @Override
   public final Future<T> execute()
   {
      if (!hasBeenExecuted.compareAndSet(false, true))
         throw new IllegalStateException("This command has already been executed. " + this);

      Set<String> errors = checkStatus();
      if (!errors.isEmpty())
      {
         throw new IllegalStateException("Command [" + this.getClass().getName() + "] not ready to execute: " +  String.join("\n", errors));
      }

      return executor.submit(new Context());
   }

   /**
    * Called from the command executor in order to construct the Invocation to be executed
    * against the remote API.
    *
    * <p>Implementations <em>MUST</em> call the {@link #appendHeaders(javax.ws.rs.client.Invocation.Builder)}
    * method in order to configure the authentication and authorization headers, API
    * versioning information as well as any additional header information that may need to be
    * supplied by the command executor.
    *
    * <p>
    * This method will be called at most once.
    *
    * @param apiRoot The base {@link WebTarget} configured with the REST API endpoint for the
    *    Zotero API. This will be scoped to the library for this adapter.
    * @return An {@link Invocation} to be executed. The response of executing this invocation
    *    will be supplied to the {@link #handleResponse(Response)} method.
    */
   protected abstract Invocation buildInvocation(WebTarget apiRoot);

   /**
    * Handles the response from the Zotero server in order to produce an instance of the domain
    * object requested by the command. Note that this response may indicate a failure (e.g.,
    * 404 Not Found) as defined by the Zotero API. In these cases, implementations should
    * generally throw an exception that indicates the type of error encountered by the request.
    * The {@link ZoteroCommandExecutor} will be responsible for handling a variety of general
    * error conditions including network failures and backoff/throttling requests from the server.
    *
    * <p>
    * This method will be called at most once.
    *
    * @param response The response returned from the Zotero API to the {@link Invocation}
    *       supplied by {@link #buildInvocation(WebTarget)}.
    * @return The domain object to be produced by this command.
    */
   protected abstract T handleResponse(Response response);

   protected URI getUri(URI zoteroApiRoot)
   {
      return zoteroApiRoot;
   }


   /**
    * Appends headers to support Zotero authorization bearer token and the Zotero API version.
    * <em>Must</em> be called by {@link #handleResponse(Response)} prior to building an
    * {@link Invocation} in order to attach authorization and API version headers.
    *
    * @param builder The invocation builder being used to construct the HTTP request.
    * @return The supplied invocation builder with any required header information. Note that
    *       this may return a different instance than the supplied builder. Callers should use
    *       the returned builder for all subsequent calls.
    */
   protected Invocation.Builder appendHeaders(Invocation.Builder builder)
   {
      builder = account != null ? account.authenticate(builder) : builder;
      return builder.header("Zotero-API-Version", "3");
   }

   protected Set<String> checkStatus()
   {
      return Collections.emptySet();
   };

   private class Context implements CommandRunnerContext<T>
   {

      @Override
      public Invocation configure(WebTarget apiRoot)
      {
         if (library != null)
         {
            String typePath = library.getType() == ZoteroLibrary.Type.User ? "users" : "groups";
            apiRoot = apiRoot.path(typePath).path(library.getId());
         }

         return ZoteroCommandAdapter.this.buildInvocation(apiRoot);
      }

      @Override
      public T handleResponse(Response response)
      {
         return ZoteroCommandAdapter.this.handleResponse(response);
      }

      @Override
      public URI getUri(URI zoteroEndpoint)
      {
         return ZoteroCommandAdapter.this.getUri(zoteroEndpoint);
      }
   }
}
