package edu.tamu.tcat.zotero.basic.v3.commands;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import edu.tamu.tcat.zotero.basic.v3.BasicZoteroLibrary;
import edu.tamu.tcat.zotero.basic.v3.RestApiV3;

/**
 * Command to retrieve a single item.
 */
public class GetItemCommand extends ZoteroCommandAdapter<RestApiV3.Item>
{

   private String itemId;

   public GetItemCommand(BasicZoteroLibrary library)
   {
      super(library);
   }

   public void setItemId(String itemId)
   {
      this.itemId = itemId;
   }

   @Override
   protected Invocation buildInvocation(WebTarget apiRoot)
   {

      WebTarget target = apiRoot.path("items").path(itemId);
      cmdLogger.fine(() -> "[Get Item]: " + target.getUri());

      return appendHeaders(target.request(MediaType.APPLICATION_JSON)).buildGet();
   }

   @Override
   protected RestApiV3.Item handleResponse(Response response)
   {
      return (response.getStatus() == 200)
         ? response.readEntity(RestApiV3.Item.class)
         : handleError(response);
   }

   private RestApiV3.Item handleError(Response response)
   {
      throw new IllegalStateException(response.getStatusInfo().getReasonPhrase());
   }
}
