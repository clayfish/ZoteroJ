package edu.tamu.tcat.zotero.basic.v3.types;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import edu.tamu.tcat.zotero.basic.v3.commands.ZoteroCommandAdapter;
import edu.tamu.tcat.zotero.basic.v3.commands.ZoteroCommandExecutor;

public class ZoteroItemTypeCommand extends ZoteroCommandAdapter<List<Map<String,String>>>
{
   private String type;
   private String path;

   public ZoteroItemTypeCommand(ZoteroCommandExecutor executor)
   {
      super(executor);
   }

   /**
    *  Factory method to return all Item Types provided by Zotero API.
    *
    *  This corresponds to the REST resource {@code /itemTypes}
    *
    * @param executor The executor that will be used to execute this command
    * @return The created command.
    */
   public static ZoteroItemTypeCommand getItemTypes(ZoteroCommandExecutor executor)
   {
      ZoteroItemTypeCommand cmd = new ZoteroItemTypeCommand(executor);
      cmd.setPath("itemTypes");
      return cmd;
   }

   /**
    *  Factory method to return all Item Types provided by Zotero API.
    *
    *  This corresponds to the REST resource {@code /itemTypeFields?itemType=?}
    *
    * @param executor The executor that will be used to execute this command
    * @param itemType The Zotero Item Type that will determine which Fields will be returned.
    * @return The created command.
    */
   public static ZoteroItemTypeCommand getItemTypeFields(ZoteroCommandExecutor executor, String itemType)
   {
      ZoteroItemTypeCommand cmd = new ZoteroItemTypeCommand(executor);
      cmd.setPath("itemTypeFields");
      cmd.setType(itemType);
      return cmd;
   }

   /**
    *  Factory method to return all Item Types provided by Zotero API.
    *
    *  This corresponds to the REST resource {@code /itemTypeCreatorTypes?itemType=?}
    *
    * @param executor The executor that will be used to execute this command
    * @param itemType The Zotero Item Type that will determine which Creators will be returned.
    * @return The created command.
    */
   public static ZoteroItemTypeCommand getItemTypeCreatorTypes(ZoteroCommandExecutor executor, String itemType)
   {
      ZoteroItemTypeCommand cmd = new ZoteroItemTypeCommand(executor);
      cmd.setPath("itemTypeCreatorTypes");
      cmd.setType(itemType);
      return cmd;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   public void setPath(String path)
   {

      this.path = path;
   }

   @Override
   protected URI getUri(URI zoteroApiRoot)
   {
      UriBuilder builder = UriBuilder.fromUri(zoteroApiRoot).path(this.path);
      if (type != null)
         builder = builder.queryParam("itemType", this.type);

      return builder.build();
   }



   @Override
   protected Invocation buildInvocation(WebTarget apiRoot)
   {
      return appendHeaders(apiRoot.request(MediaType.APPLICATION_JSON)).buildGet();
   }

   @Override
   protected List<Map<String, String>> handleResponse(Response response)
   {
      GenericType<List<Map<String, String>>> type = new GenericType<List<Map<String,String>>>(){};
      return (response.getStatus() == 200)
             ? response.readEntity(type)
             : handleError(response);
   }

   private List<Map<String,String>> handleError(Response response)
   {
      throw new IllegalStateException(response.getStatusInfo().getReasonPhrase());
   }
}
