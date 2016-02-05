package edu.tamu.tcat.zotero;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.tamu.tcat.zotero.basic.v3.ZoteroClientService;
import edu.tamu.tcat.zotero.basic.v3.commands.ZoteroCommandExecutor;
import edu.tamu.tcat.zotero.basic.v3.types.BasicItemTypeInfo;
import edu.tamu.tcat.zotero.types.ItemType;
import edu.tamu.tcat.zotero.types.ItemTypeInfo;
import edu.tamu.tcat.zotero.types.ItemTypeProvider;

public class ZoteroItemTypeCmdTests
{
   private ZoteroCommandExecutor executor;

   /**
    * All currently supported Zotero Item Types (3/22/2016)
    */
   private final List<String> types = new ArrayList<>(Arrays.asList("artwork","audioRecording","bill","blogPost","book",
         "bookSection","case","computerProgram","conferencePaper",
         "dictionaryEntry","document","email","encyclopediaArticle",
         "film","forumPost","hearing","instantMessage","interview",
         "journalArticle","letter","magazineArticle","manuscript",
         "newspaperArticle","map","note","patent","podcast",
         "presentation","radioBroadcast","report","statute",
         "tvBroadcast","thesis","videoRecording","webpage"));

   /**
    * All currently supported Zotero Field Types for a Item Type of Book
    */
   private final List<String> bookFields = new ArrayList<>(Arrays.asList("title","abstractNote","series","seriesNumber",
         "volume","numberOfVolumes","edition","place",
         "publisher","date","numPages","language","ISBN",
         "shortTitle","url","accessDate","archive",
         "archiveLocation","libraryCatalog","callNumber",
         "rights","extra"));

   /**
    * All currently supported Zotero Creator Types for a Item Type of Book
    */
   private final List<String> bookCreators = new ArrayList<>(Arrays.asList("author","contributor","editor","seriesEditor","translator"));


   @Before
   public void setup()
   {
      executor = new ZoteroCommandExecutor();
   }

   @After
   public void tearDown()
   {
      executor.close();
   }

   @Test
   public void getItemTypes() throws Exception
   {
      ZoteroClientService client = new ZoteroClientService();
      client.bind(executor);
      client.activate();

      ItemTypeProvider service = client.getTypeProvider();
      try
      {
         Set<ItemTypeInfo> itemTypes = service.getItemTypes();

         itemTypes.forEach(t -> {
            if (!types.contains(t.getId()))
               Assert.fail("The item Type: " + t.getLabel() + " was not part of the Zotero Types");
         });

         BasicItemTypeInfo book = null;
         Iterator<ItemTypeInfo> iterator = itemTypes.iterator();
         for (int i=0; i<itemTypes.size(); i++)
         {
            ItemTypeInfo itemType = iterator.next();
            if (itemType.getId().equals("book"))
            {
               book = new BasicItemTypeInfo(itemType.getId(), itemType.getLabel());
               i = itemTypes.size();
            }
         }

         ItemType itemType = service.getItemType(book);

         itemType.getCreatorRoles().forEach(c -> {
            if (!bookCreators.contains(c.getId()))
               Assert.fail("The creator Type: " + c.getLabel() + " was not part of the Zotero Item Type Book");
         });
         itemType.getFields().forEach(f -> {
            if (!bookFields.contains(f.getId()))
               Assert.fail("The field Type: " + f.getLabel() + " was not part of the Zotero Item Type Book");
         });
      }
      finally
      {
         client.close();
      }
   }
}
