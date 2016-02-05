package edu.tamu.tcat.zotero.basic.v3.commands;

import java.text.MessageFormat;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import edu.tamu.tcat.zotero.basic.v3.BasicZoteroLibrary;

public class DeleteCollectionsCommand extends ZoteroCommandAdapter<Void>
{
   private final static Logger logger = Logger.getLogger(DeleteCollectionsCommand.class.getName());
   private final String COLLECTIONS = "collections";
   private final String COLLECTION_KEY = "collectionKey";
   
   private String collectionKeys = null;
   
   public DeleteCollectionsCommand(BasicZoteroLibrary library, Set<String> collections )
   {
      super(library);
      this.collectionKeys = collections.stream().map(this::getKey).collect(Collectors.joining(","));
   }
   
   public String getKey(String key)
   {
      return key;
   }

   @Override
   protected Invocation buildInvocation(WebTarget apiRoot)
   {
      WebTarget target = apiRoot.path(COLLECTIONS).queryParam(COLLECTION_KEY, collectionKeys);
      return appendHeaders(target.request(MediaType.APPLICATION_JSON)).buildDelete();
   }

   @Override
   protected Void handleResponse(Response response)
   {
      if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode())
         return null;
      else
      {
         try
         {
            return handleError(response);
         }
         catch (ZoteroResponseException e)
         {
            logger.fine("An error occured while deleting an item" + e);
         }
      }
      return null;
   }

   /**
    * Zotero PUT error responses include the following
    *    - 409(Conflict): The target library is locked
    *    - 412(Precondition Failed): The item has changed since retrieval (i.e., the provided item version no longer matches).
    * 
    * @param response
    * @return
    */
   
   private static final String CONFLICT = "The target library is locked";
   private static final String PRECONDITION_FAILED = "The library has changed since retrieval.";
   private static final String message = "An error occurred while attempting to delete one of the following items: {0}. Reason: {1}";
   
   private Void handleError(Response response) throws ZoteroResponseException
   {
      switch (response.getStatus())
      {
         case 409:
            throw new ZoteroResponseException(MessageFormat.format(message, collectionKeys, CONFLICT));
         case 412:
            throw new ZoteroResponseException(MessageFormat.format(message, collectionKeys, PRECONDITION_FAILED));
         default:
            return null;
      }

   }

}
