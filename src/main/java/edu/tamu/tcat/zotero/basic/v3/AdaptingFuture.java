package edu.tamu.tcat.zotero.basic.v3;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

/**
 * Helper class to adapt a {@link Future} of type {@code IN} into a {@code Future} of type
 * {@code OUT} while maintaining the basic semantics of the original future. Useful to adapt
 * data vehicles returned by the Zotero REST API into instances of the domain model.
 *
 * @param <IN> The Java type returned by the source {@code Future}.
 * @param <OUT> The Java type that the result of the source {@code Future} will be adapted to.
 */
public class AdaptingFuture<IN, OUT> implements Future<OUT>
{
   private final Future<IN> delegate;
   private final Function<IN, OUT> adapter;
   private OUT result;

   /**
    *
    * @param delegate A {@code Future} that will supply the object to be adapted.
    * @param adapter A {@link Function} used to adapt the data returned by the delegate
    *       into an instance of the domain model.
    */
   public AdaptingFuture(Future<IN> delegate, Function<IN, OUT> adapter)
   {
      this.delegate = delegate;
      this.adapter = adapter;
   }

   @Override
   public boolean cancel(boolean mayInterruptIfRunning)
   {
      return delegate.cancel(mayInterruptIfRunning);
   }

   @Override
   public boolean isCancelled()
   {
      return delegate.isCancelled();
   }

   @Override
   public boolean isDone()
   {
      return delegate.isDone();
   }

   @Override
   public OUT get() throws InterruptedException, ExecutionException
   {
      synchronized (this)
      {
         if (result != null)
            return result;

         IN src = delegate.get();
         result = adapter.apply(src);
         return result;
      }
   }

   @Override
   public OUT get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
   {
      synchronized (this)
      {
         if (result != null)
            return result;

         IN src = delegate.get(timeout, unit);
         result = adapter.apply(src);
         return result;
      }
   }

}