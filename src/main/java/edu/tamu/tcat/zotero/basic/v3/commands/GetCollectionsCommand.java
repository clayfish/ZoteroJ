package edu.tamu.tcat.zotero.basic.v3.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import edu.tamu.tcat.zotero.QueryOption;
import edu.tamu.tcat.zotero.basic.v3.BasicZoteroLibrary;
import edu.tamu.tcat.zotero.basic.v3.RestApiV3;

/**
 *  Retrieves a list of collections.
 *
 *  This targets the following three resources defined by the Zotero REST API:
 *  <dl>
 *    <dt><userOrGroupPrefix>/collections</dt>
 *    <dd>The set of all collections in the library</dd>
 *
 *    <dt><userOrGroupPrefix>/collections/top</dt>
 *    <dd>The set of all top-level collections in the library</dd>
 *
 *    <dt><userOrGroupPrefix>/collections/<collectionKey>/collections</dt>
 *    <dd>The set of subcollections within a specific collection in the library</dd>
 *
 *  </dl>
 *
 *  @see https://www.zotero.org/support/dev/web_api/v3/basics#resources
 */
public class GetCollectionsCommand extends ZoteroCommandAdapter<RestApiV3.CollectionList>
{
   private static final String API_RESOURCE = "collections";

   private String collectionId;
   private QueryOption queryType = QueryOption.ALL;

   public GetCollectionsCommand(BasicZoteroLibrary library)
   {
      super(library);
   }

   /**
    * Specifies whether this command should return all collections for the given authorization
    * context or only the top-level collections. Defaults to all.
    *
    * @param recurse If {@code true}, the command will return all collections. If false, only
    *       the top-level collections will be returned.
    */
   public void setRecursive(boolean recurse)
   {
      this.queryType = recurse ? QueryOption.ALL : QueryOption.TOP;
   }

   /**
    * Specifies that this command should return the sub-collections of a specified parent
    * rather than all collections for the associated authorization context.
    *
    * @param id The id of the collection whose child collections should be returned.
    */
   public void setParentCollection(String id)
   {
      this.collectionId = id;
      this.queryType = QueryOption.CHILDREN;
   }

   /**
    * @implNote
    * Not thread safe. This is expected to be confined to a single thread.
    */
   @Override
   protected Invocation buildInvocation(WebTarget apiRoot)
   {
      WebTarget target = configureTarget(apiRoot);
      Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
      return appendHeaders(builder).buildGet();
   }

   @Override
   protected RestApiV3.CollectionList handleResponse(Response response)
   {
      List<RestApiV3.Collection> collections = null;
      if (response.getStatus() == 200)
      {
         GenericType<List<RestApiV3.Collection>> type = new GenericType<List<RestApiV3.Collection>>(){};
         collections = new ArrayList<>(response.readEntity(type));
      }
      else
      {
         handleError(response);
      }

      RestApiV3.CollectionList result = new RestApiV3.CollectionList();
      result.collections = collections;
      return result;
   }

   private WebTarget configureTarget(WebTarget apiRoot)
   {
      switch(queryType)
      {
         case TOP:
            return apiRoot.path(API_RESOURCE).path("top");
         case  CHILDREN:
            Objects.requireNonNull(collectionId, "Cannot retrieve sub-collections. No parent collection has been specified.");
            return apiRoot.path(API_RESOURCE).path(collectionId).path(API_RESOURCE);
         case ALL:
            return apiRoot.path(API_RESOURCE);
      }

      throw new IllegalStateException("Failed to configure web target [" + apiRoot + "] for query type [" + queryType + "]. ");
   }

   private void handleError(Response response)
   {
      // TODO need to supply additional information about the user and the endpoint that was requested.
      String reason = response.getStatusInfo().getReasonPhrase();
      throw new IllegalStateException(reason);
   }

}
