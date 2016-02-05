package edu.tamu.tcat.zotero.basic.v3.commands;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import edu.tamu.tcat.zotero.basic.v3.BasicZoteroLibrary;
import edu.tamu.tcat.zotero.basic.v3.RestApiV3;

public class GetCollectionCommand extends ZoteroCommandAdapter<RestApiV3.Collection>
{

   private String collectionId;

   public GetCollectionCommand(BasicZoteroLibrary library)
   {
      super(library);
   }

   public void setCollectionId(String collectionId)
   {
      this.collectionId = collectionId;
   }

   @Override
   protected Invocation buildInvocation(WebTarget apiRoot)
   {
      WebTarget target = apiRoot.path("collections").path(collectionId);
      cmdLogger.fine(() -> "[Get Collection]: " + target.getUri());

      return appendHeaders(target.request(MediaType.APPLICATION_JSON)).buildGet();
   }

   @Override
   protected RestApiV3.Collection handleResponse(Response response)
   {
      // NOTE, this may throw runtime exception if the entity cannot be read.
      return (response.getStatus() == 200)
         ? response.readEntity(RestApiV3.Collection.class)
         : handleError(response);
   }

   private RestApiV3.Collection handleError(Response response)
   {
      // TODO throw correct errors and document
      throw new IllegalStateException(response.getStatusInfo().getReasonPhrase());
   }

}
