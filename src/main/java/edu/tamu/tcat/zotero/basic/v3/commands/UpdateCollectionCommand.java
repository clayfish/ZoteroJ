package edu.tamu.tcat.zotero.basic.v3.commands;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import edu.tamu.tcat.zotero.basic.v3.BasicEditCollectionCommand.ZoteroCollectionMutator;
import edu.tamu.tcat.zotero.basic.v3.BasicZoteroLibrary;

public class UpdateCollectionCommand extends ZoteroCommandAdapter<ZoteroCollectionMutator>
{
   private final static Logger logger = Logger.getLogger(DeleteCollectionsCommand.class.getName());
   private final String COLLECTIONS = "collections";
   private final String key;
   private ZoteroCollectionMutator collection;
   
   public UpdateCollectionCommand(BasicZoteroLibrary library, ZoteroCollectionMutator collection)
   {
      super(library);
      this.collection = collection;
      this.key = collection.getId();
   }

   @Override
   protected Invocation buildInvocation(WebTarget apiRoot)
   {
      WebTarget target = apiRoot.path(COLLECTIONS).path(this.key);
      return appendHeaders(target.request(MediaType.APPLICATION_JSON))
            .buildPut(Entity.entity(unwrap(collection), MediaType.APPLICATION_JSON));
   }

   @Override
   protected ZoteroCollectionMutator handleResponse(Response response)
   {

      if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode())
      {
         String version = response.getHeaderString("Last-Modified-Version");
         this.collection.setVersion(Integer.parseInt(version));
         return this.collection;
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
   

   
   private Map<String, String> unwrap(ZoteroCollectionMutator collection)
   {
      Map<String, String> collMap = new HashMap<String,String>();
      collMap.put("key", collection.getId());
      collMap.put("version", String.valueOf(collection.getVersion()));
      collMap.put("name", collection.getName());
      if (collection.getParent() != null)
         collMap.put("parentCollection", collection.getParent());
      
      return collMap;
   }

   /**
    * Zotero PUT error responses include the following
    *    - 400(Bad Request): Invalid type/field; unparseable JSON
    *    - 409(Conflict): The target library is locked
    *    - 412(Precondition Failed):   The item has changed since retrieval (i.e., the provided item version no longer matches).
    * 
    * @param response
    * @return
    */
   
   private static final String CONFLICT = "The target library is locked";
   private static final String PRECONDITION_FAILED = "The collection has changed since retrieval (i.e., the provided collection version no longer matches)";
   private static final String BAD_REQUEST = "Invalid type/field or unparseable JSON";
   private static final String message = "An error occurred while attempting to update item: {0}. Reason: {1}";
   
   private void handleError(Response response) throws ZoteroResponseException
   {
      switch (response.getStatus())
      {
         case 400:
            throw new ZoteroResponseException(MessageFormat.format(message, key, BAD_REQUEST));
         case 409:
            throw new ZoteroResponseException(MessageFormat.format(message, key, CONFLICT));
         case 412:
            throw new ZoteroResponseException(MessageFormat.format(message, key, PRECONDITION_FAILED));
      }

   }
}
