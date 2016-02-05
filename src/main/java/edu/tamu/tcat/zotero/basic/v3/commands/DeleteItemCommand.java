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

public class DeleteItemCommand extends ZoteroCommandAdapter<Void>
{
   private final static Logger logger = Logger.getLogger(DeleteItemCommand.class.getName()); 
   
   private final String ITEMS = "items";
   private final String ITEM_KEY = "itemKey";
   private String itemKeys = null;

   public DeleteItemCommand(BasicZoteroLibrary library, Set<String> items)
   {
      super(library);
      itemKeys = items.stream().map(this::getItem).collect(Collectors.joining(","));
   }

   public String getItem(String item)
   {
      return item;
   }

   @Override
   protected Invocation buildInvocation(WebTarget apiRoot)
   {
      WebTarget target = apiRoot.path(ITEMS).queryParam(ITEM_KEY, itemKeys);
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
            return null;
         }
      }
   }

   /**
    * Zotero PUT error responses include the following
    *    - 409(Conflict): The target library is locked
    *    - 412(Precondition Failed): The item has changed since retrieval (i.e., the provided item version no longer matches).
    *    - 428(Precondition Required): If-Unmodified-Since-Version was not provided. 
    * 
    * @param response
    * @return
    */
   
   private static final String CONFLICT = "The target library is locked";
   private static final String PRECONDITION_FAILED = "The item has changed since retrieval (i.e., the provided item version no longer matches).";
   private static final String PRECONDITION_REQUIRED = "If-Unmodified-Since-Version was not provided.";
   private static final String message = "An error occurred while attempting to delete one of the following items: {0}. Reason: {1}";
   
   private Void handleError(Response response) throws ZoteroResponseException
   {
      switch (response.getStatus())
      {
         case 409:
            throw new ZoteroResponseException(MessageFormat.format(message, itemKeys, CONFLICT));
         case 412:
            throw new ZoteroResponseException(MessageFormat.format(message, itemKeys, PRECONDITION_FAILED));
         case 428:
            throw new IllegalStateException(MessageFormat.format(message, itemKeys, PRECONDITION_REQUIRED));
         default:
            return null;
      }

   }

}
