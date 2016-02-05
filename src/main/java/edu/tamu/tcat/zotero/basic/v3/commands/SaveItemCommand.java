package edu.tamu.tcat.zotero.basic.v3.commands;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import edu.tamu.tcat.zotero.ItemCreator;
import edu.tamu.tcat.zotero.ZoteroCollection;
import edu.tamu.tcat.zotero.basic.v3.BasicEditItemCommand.EditItemMutator;
import edu.tamu.tcat.zotero.basic.v3.BasicZoteroLibrary;
import edu.tamu.tcat.zotero.basic.v3.RestApiV3;

/**
 * Creates or updates a bibliographic item using the Zotero API and returns the id of
 * the updated item upon success.
 *
 */
public class SaveItemCommand extends ZoteroCommandAdapter<RestApiV3.Item>
{
   private final static Logger logger = Logger.getLogger(SaveItemCommand.class.getName()); 
   
   private static final String KEY = "key";
   private static final String ITEM_TYPE = "itemType";
   private static final String CREATORS = "creators";
   private static final String CREATOR_TYPE = "creatorType";
   private static final String CREATOR_NAME = "name";
   private static final String CREATOR_LAST_NAME = "lastName";
   private static final String CREATOR_FIRST_NAME = "firstName";
   private static final String COLLECTIONS = "collections";
   private static final String TAGS = "tags";
   private static final String RELATIONS = "relations";
   
   private final String ITEMS = "items";
   private final EditItemMutator itemData;
   private String key = null;

   public SaveItemCommand(BasicZoteroLibrary library, EditItemMutator itemTypeData)
   {
      super(library);
      this.itemData = itemTypeData;
      this.key = itemTypeData.getKey();
   }

   @Override
   protected Invocation buildInvocation(WebTarget apiRoot)
   {
      WebTarget  target = apiRoot.path(ITEMS);
      
      return appendHeaders(target.request(MediaType.APPLICATION_JSON))
                          .buildPost(Entity.entity(unwrap(itemData), MediaType.APPLICATION_JSON));
   }

   @Override
   protected RestApiV3.Item handleResponse(Response response)
   {
      if (response.getStatus() == Response.Status.OK.getStatusCode())
      {
         RestApiV3.ZoteroItemResponse result = response.readEntity(RestApiV3.ZoteroItemResponse.class);
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
   
   private List<Map<String,Object>> unwrap(EditItemMutator mutator)
   {
      Map<String,Object> dataMap = new HashMap<>();
      
      dataMap.put(KEY, mutator.getKey());
      dataMap.put(ITEM_TYPE, mutator.getItemType().getId());
      dataMap.put(CREATORS, unwrapCreators(mutator.getCreators()));
      dataMap.put(TAGS, new ArrayList<>());
      dataMap.put(RELATIONS, new HashMap<String,String>());
      
      dataMap.put(COLLECTIONS, mutator.getCollections()
                                      .stream()
                                      .map(this::getId)
                                      .collect(Collectors.toList()));
      
      dataMap.putAll(mutator.getFields());
      
      List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
      dataList.add(dataMap);
      return dataList;
   }
   
   private List<Map<String,String>> unwrapCreators(List<ItemCreator> creators)
   {
      Set<String> definedRoles = itemData.getItemType().getCreatorRoles().stream()
       .map(role -> role.getId())
       .collect(Collectors.toSet());
   
      List<Map<String,String>> creatorsDto = creators.stream()
       .filter(creator -> definedRoles.contains(creator.getRole()))
       .map(this::toDtoMap)
       .collect(Collectors.toList());
   
      return creatorsDto;
   }
   
   /**
    * Converts an individual item creator record into the appropriate form for JSON
    * serialization to the Zotero REST API.
    */
   private Map<String, String> toDtoMap(ItemCreator creator)
   {
      Map<String, String> dto = new HashMap<String, String>();
      dto.put(CREATOR_TYPE, creator.getRole());
      String name = creator.getName();
      if (name == null || name.trim().isEmpty())
      {
         // two valued name
         dto.put(CREATOR_FIRST_NAME, creator.getGivenName());
         dto.put(CREATOR_LAST_NAME, creator.getFamilyName());
      }
      else
      {
         // single valued name
         dto.put(CREATOR_NAME, name);
      }
      
      return dto;
   }
   
   private String getId(ZoteroCollection zColl)
   {
      return zColl.getId();
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
   private static final String PRECONDITION_FAILED = "The item has changed since retrieval (i.e., the provided item version no longer matches).";
   private static final String BAD_REQUEST = "Invalid type/field or unparseable JSON";
   private static final String REQUEST_TOO_LARGE = "Too many items submitted";
   
   private static final String message = "An error occurred while attempting create the item: {0}. Reason: {1}";
   
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
         case 413:
            throw new ZoteroResponseException(MessageFormat.format(message, key, REQUEST_TOO_LARGE));
      }
   }
}
