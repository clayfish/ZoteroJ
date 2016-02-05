package edu.tamu.tcat.zotero.basic.v3.commands;

import java.util.List;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import edu.tamu.tcat.zotero.basic.v3.BasicUserAccount;
import edu.tamu.tcat.zotero.basic.v3.RestApiV3;

public class GetAssociatedGroupsCommand extends ZoteroCommandAdapter<List<RestApiV3.ZoteroGroupLibrary>>
{

   private final String userId;

   /**
    *
    * @param account The user account for which associated groups should be retrieved. Note
    *       that this must have a valid user id, but does not need to have an authentication
    *       token.
    */
   public GetAssociatedGroupsCommand(BasicUserAccount account)
   {
      super(account);
      userId = account.getId();
   }

   @Override
   protected Invocation buildInvocation(WebTarget apiRoot)
   {
      WebTarget target = apiRoot.path("users").path(userId).path("groups");
      cmdLogger.fine(() -> "[Get Groups]: " + target.getUri());

      return appendHeaders(target.request(MediaType.APPLICATION_JSON)).buildGet();
   }

   @Override
   protected List<RestApiV3.ZoteroGroupLibrary> handleResponse(Response response)
   {
      GenericType<List<RestApiV3.ZoteroGroupLibrary>> type = new GenericType<List<RestApiV3.ZoteroGroupLibrary>>(){};
      return (response.getStatus() == 200)
            ? response.readEntity(type)
            : handleError(response);
   }

   private List<RestApiV3.ZoteroGroupLibrary> handleError(Response response)
   {
      // TODO throw correct errors and document
      throw new IllegalStateException(response.getStatusInfo().getReasonPhrase());
   }
}
