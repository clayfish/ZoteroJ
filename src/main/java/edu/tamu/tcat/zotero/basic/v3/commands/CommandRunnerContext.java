package edu.tamu.tcat.zotero.basic.v3.commands;

import java.io.InputStream;
import java.net.URI;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public interface CommandRunnerContext<T>
{
   /**
    * Configures the base {@link WebTarget} with the sub-path and query parameters and returns
    * an {@link Invocation}. The returned builder should be set
    *
    * @param apiRoot
    * @return
    */
   Invocation configure(WebTarget apiRoot);

   /**
    * Handle the response from the Zotero server.
    *
    * <p>Note that this method must fully consume any data supplied by the remote server.
    * Following invocation of this method, the supplied response will be closed by the command
    * executor and any associated data streams that have not been fully read (e.g., an
    * {@link InputStream}) will no longer be accessible.
    *
    * @param response The response from the executing the {@link Invocation} returned by
    *       {@link #configure(WebTarget)}. Note that this may be a failed response.
    * @return The data from the supplied response. This is intended to be a data-vehicle that
    *       directly represents the content returned by the Zotero API. The caller who executed
    *       the command is responsible for inspecting the returned data vehicle and adapting
    *       it into the apporpriate domain model object.
    */
   T handleResponse(Response response);

   /**
    * Construct the base URI to be used for this request.
    *
    * @param zoteroEndpoint
    * @return
    */
   URI getUri(URI zoteroEndpoint);

}
