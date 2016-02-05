package edu.tamu.tcat.zotero.basic.v3.commands;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import edu.tamu.tcat.zotero.basic.v3.BasicEditCollectionCommand.ZoteroCollectionMutator;
import edu.tamu.tcat.zotero.basic.v3.BasicZoteroLibrary;
import edu.tamu.tcat.zotero.basic.v3.RestApiV3;

public class SaveCollectionCommand extends ZoteroCommandAdapter<RestApiV3.Collection>
{
   private final static Logger logger = Logger.getLogger(SaveCollectionCommand.class.getName()); 
   private final String COLLECTIONS = "collections";
   private ZoteroCollectionMutator collection;
   private String key;

   public SaveCollectionCommand(BasicZoteroLibrary library, ZoteroCollectionMutator collection)
   {
      super(library);
      this.collection = collection;
   }

   @Override
   protected Invocation buildInvocation(WebTarget apiRoot)
   {
      WebTarget target = apiRoot.path(COLLECTIONS);
      
      return appendHeaders(target.request(MediaType.APPLICATION_JSON))
                             .buildPost(Entity.entity(unwrap(collection), MediaType.APPLICATION_JSON));
   }

   @Override
   protected RestApiV3.Collection handleResponse(Response response)
   {
      if (response.getStatus() == Response.Status.OK.getStatusCode())
      {
         RestApiV3.ZoteroCollectionResponse result = response.readEntity(RestApiV3.ZoteroCollectionResponse.class);
         return result.successful.get("0");
      }
      else
      {
         try
         {
            handleError(response);
         }
         catch (ZoteroResponseException e)
         {
            logger.fine("An error occured while updating the item" + e);
         }
      }
      return null;
   }
   
   private List<Map<String, String>> unwrap(ZoteroCollectionMutator collection)
   {
      List<Map<String, String>> collList = new ArrayList<Map<String,String>>();
      Map<String, String> collMap = new HashMap<String,String>();
      collMap.put("name", collection.getName());
      
      if (collection.getParent() != null)
         collMap.put("partent", collection.getParent());
      
      collList.add(collMap);
      return collList;
   }

   /**
    * Zotero PUT error responses include the following
    *    - 400(Bad Request): Invalid type/field; unparseable JSON
    *    - 409(Conflict): The target library is locked
    *    - 412(Precondition Failed): The provided Zotero-Write-Token has already been submitted.
    *    - 413(Request Entity Too Large): Too many items submitted  
    * 
    * @param response
    * @return
    */
   private static final String CONFLICT = "The target library is locked";
   private static final String PRECONDITION_FAILED = "The provided Zotero-Write-Token has already been submitted.";
   
   private static final String message = "An error occurred while attempting create the item: {0}. Reason: {1}";
   
   private void handleError(Response response) throws ZoteroResponseException
   {
      switch (response.getStatus())
      {
         case 409:
            throw new ZoteroResponseException(MessageFormat.format(message, key, CONFLICT));
         case 412:
            throw new ZoteroResponseException(MessageFormat.format(message, key, PRECONDITION_FAILED));
      }
   }

}
