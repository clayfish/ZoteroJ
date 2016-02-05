package edu.tamu.tcat.zotero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.tamu.tcat.zotero.EditItemCommand;
import edu.tamu.tcat.zotero.Item;
import edu.tamu.tcat.zotero.ItemCreator;
import edu.tamu.tcat.zotero.SimpleItemCreator;
import edu.tamu.tcat.zotero.ZoteroLibrary;
import edu.tamu.tcat.zotero.basic.v3.BasicUserAccount;
import edu.tamu.tcat.zotero.basic.v3.ZoteroClientService;
import edu.tamu.tcat.zotero.basic.v3.model.BasicItem;
import edu.tamu.tcat.zotero.basic.v3.types.BasicItemFieldType;
import edu.tamu.tcat.zotero.basic.v3.types.BasicItemTypeInfo;
import edu.tamu.tcat.zotero.basic.v3.types.ItemTypeProviderService;
import edu.tamu.tcat.zotero.types.ItemFieldType;
import edu.tamu.tcat.zotero.types.ItemType;
import edu.tamu.tcat.zotero.types.ItemTypeInfo;

public class EditItemTest
{

   private ZoteroClientService client;
   private ItemTypeProviderService service;
   private BasicUserAccount account;
   private ZoteroLibrary userLibrary;
   private ItemType itemType;
   
   private final String TITLE = "Harriet Tubman, the heroine in ebony";
   private final String AUTHOR = "Taylor, Robert W.";
   private final String PUBLISHER = "Boston";
   private final String LANGUAGE = "English";
   private final String DATE = "1901";
   private final String NUM_PAGES = "16";
   
   @Before
   public void setup()
   {
      client = new ZoteroClientService();
      client.activate();
      account = new BasicUserAccount("2906896", "LrWNAwauPiHHJ8369h3aUylA", client);
      userLibrary = account.getUserLibrary();
      try
      {
         service = new ItemTypeProviderService(client.getExecutor());
         ItemTypeInfo bookInfo = new BasicItemTypeInfo("book","Book");
         itemType = service.getItemType(bookInfo);
      }
      catch (Exception e)
      {
         Assert.fail("Failed to retreive ItemTypes!");
      }
   }

   @Test
   public void createItem() throws Exception
   {
      Item createdItem = create();
      checkValues(createdItem);
      delete(createdItem);
   }
   
   @Test
   public void editItem() throws Exception
   {
      Item createdItem = create();
      Item updatedItem = update(createdItem);
      checkValues(updatedItem);
      delete(new Item[]{createdItem, updatedItem});
   }
   
   public void checkValues(Item original)
   {
      List<ItemFieldType> fields = itemType.getFields();
      
      ItemFieldType numPages = fields.stream()
                                     .filter(f -> f.getId().equals("numPages"))
                                     .findAny()
                                     .orElseThrow(() -> new IllegalArgumentException("Number of Pages not found", null));
      
      ItemFieldType title = fields.stream()
                                  .filter(f -> f.getId().equals("title"))
                                  .findAny()
                                  .orElseThrow(() -> new IllegalArgumentException("Title not found", null));
      
      ItemFieldType publisher = fields.stream()
                                      .filter(f -> f.getId().equals("publisher"))
                                      .findAny()
                                      .orElseThrow(() -> new IllegalArgumentException("Publisher not found", null));
      
      ItemFieldType language = fields.stream()
                                     .filter(f -> f.getId().equals("language"))
                                     .findAny()
                                     .orElseThrow(() -> new IllegalArgumentException("Lanuage not found", null));
      ItemFieldType date = fields.stream()
                                 .filter(f -> f.getId().equals("date"))
                                 .findAny()
                                 .orElseThrow(() -> new IllegalArgumentException("Date not found", null));
      
      Assert.assertEquals(TITLE, original.getFieldValue(title));
      Assert.assertEquals(PUBLISHER, original.getFieldValue(publisher));
      Assert.assertEquals(LANGUAGE, original.getFieldValue(language));
      Assert.assertEquals(DATE, original.getFieldValue(date));
      if (!original.getFieldValue(numPages).isEmpty())
         Assert.assertEquals(NUM_PAGES, original.getFieldValue(numPages));
   }
   
   private BasicItem create() throws Exception
   {
      List<ItemFieldType> fields = itemType.getFields();
      EditItemCommand editItemCmd = userLibrary.createItem();
      editItemCmd.setItemType(itemType)
                 .setCreators(addCreators(itemType.getCreatorRoles()))
                 .setFields(setFields(fields));
      
      return (BasicItem)editItemCmd.execute().get();
   }
   
   private Item update(Item item) throws Exception
   {
      EditItemCommand editItem = userLibrary.editItem(item);
      List<ItemFieldType> fieldType = getFieldType("numPages");
      editItem.setField(fieldType.get(0), NUM_PAGES);
      return editItem.execute().get();
   }
   
   private void delete(Item... item) throws Exception
   {
      userLibrary.deleteItems(item);
   }
   
   private List<ItemFieldType> getFieldType(String field)
   {
      List<ItemFieldType> fields = itemType.getFields();
      FieldTypePredicates pred = new FieldTypePredicates();
      return fields.stream().filter(pred.hasField(field)).collect(Collectors.toList());
   }
   
   private class FieldTypePredicates
   {
      public Predicate<ItemFieldType> hasField(String fieldName)
      {
         return p -> p.getId().equals(fieldName);
      }
   }

   private Map<ItemFieldType, String> setFields(List<ItemFieldType> fieldTypes)
   {
      Map<ItemFieldType, String> fieldMap = new HashMap<>();
      
      fieldTypes.stream().forEach(f -> {
         switch(f.getId())
         {
            case "title": 
               fieldMap.put(f, TITLE);
               break;
            case "language":
               fieldMap.put(f, LANGUAGE);
               break;
            case "publisher":
               fieldMap.put(f, PUBLISHER);
               break;
            case "date":
               fieldMap.put(f, DATE);
               break;
            default:
               break;
         }
      });
      
      return fieldMap;
   }
   
   private List<ItemCreator> addCreators(List<ItemFieldType> roles)
   {
      List<ItemCreator> creators = new ArrayList<>();
      SimpleItemCreator creator = new SimpleItemCreator(
            roles.stream()
                 .filter(f -> f.getId().equals("author"))
                 .findAny()
                 .orElse(new BasicItemFieldType("author","Auhtor")), AUTHOR);
      creators.add(creator);
      return creators;
   }
}
