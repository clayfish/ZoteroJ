package edu.tamu.tcat.zotero.basic.v3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import edu.tamu.tcat.zotero.EditItemCommand;
import edu.tamu.tcat.zotero.Item;
import edu.tamu.tcat.zotero.ItemCreator;
import edu.tamu.tcat.zotero.ItemSet;
import edu.tamu.tcat.zotero.ZoteroCollection;
import edu.tamu.tcat.zotero.ZoteroRestException;
import edu.tamu.tcat.zotero.types.ItemFieldType;
import edu.tamu.tcat.zotero.types.ItemType;

public class BasicEditItemCommand implements EditItemCommand
{
   private final BasicZoteroLibrary library;

   private EditItemMutator itemMutator;
   private Map<String,Object> itemTypeData;

   public BasicEditItemCommand(BasicZoteroLibrary library)
   {
      this.library = library;
      this.itemMutator = new EditItemMutator();
   }

   public BasicEditItemCommand(BasicZoteroLibrary library, Item item)
   {
      this.library = library;
      this.itemMutator = new EditItemMutator(item);
   }

   @Override
   public EditItemCommand setItemType(ItemType item)
   {
      itemMutator.setItemType(item);
      return this;
   }

   @Override
   public EditItemCommand setCreators(List<ItemCreator> creators)
   {
      itemMutator.setCreators(creators);
      return this;
   }

   @Override
   public EditItemCommand setFields(Map<ItemFieldType, String> fields) throws IllegalArgumentException
   {
      fields.forEach((f,v) -> itemMutator.setField(f,v));
      return this;
   }


   @Override
   public EditItemCommand setField(ItemFieldType field, String value) throws IllegalArgumentException
   {
      itemMutator.setField(field, value);
      return this;
   }


   @Override
   public EditItemCommand clearField(ItemFieldType field)
   {
      itemMutator.clearField(field);
      return this;
   }

   @Override
   public EditItemCommand setCollections(String... ids)
   {
      itemMutator.setCollections(new ArrayList<>());
      return this;
   }

   @Override
   public EditItemCommand setTags(String... tags)
   {
      itemMutator.setTags(new HashSet<>());
      return this;
   }

   @Override
   public Future<Item> execute()
   {
      try
      {
         return itemMutator.getKey() == null || itemMutator.getKey().isEmpty()
                ? library.creatItem(itemMutator)
                : library.updateItem(itemMutator);
      }
      catch (ZoteroRestException e)
      {
         throw new IllegalStateException("An error occured while attempting to create or edit the Item:" + itemTypeData.get("title"), e);
      }
   }
   
   public class EditItemMutator
   {
      private String key;
      private int version;
      private ItemType itemType;
      private List<ItemCreator> creators = new ArrayList<>();;
      private List<ZoteroCollection> collections = new ArrayList<>();;
      private Map<String,String> fields = new HashMap<>();
      private Set<String> tags = new HashSet<>();
      private Item parent;
      private ItemSet children;
      private HashMap<String, String> relations = new HashMap<>();
      
      public EditItemMutator()
      {
      }
      
      public EditItemMutator(Item item)
      {
         
         setKey(item.getId());
         setVersion(item.getVersion());
         setItemType(item.getItemType());
         
         setCreators(item.getCreators());
         item.getItemType().getFields().forEach(f -> setField(f, item.getFieldValue(f)));
         
         setCollections(item.getCollections());
         setTags(item.getTags());
//         setChildren(item.getChildren());
         
         try
         {
            setParentItem(item.getParent());
         }
         catch (ZoteroRestException e)
         {
            e.printStackTrace();
         }
      }
      
      public void setKey(String key)
      {
         this.key = key;
      }
      
      public void setVersion(int version)
      {
         this.version = version;
      }
      
      public void setItemType(ItemType itemType)
      {
         this.itemType = itemType;
      }
      
      public void setCreators(List<ItemCreator> creators)
      {
         this.creators = new ArrayList<>(creators);
      }
      
      public void setField(ItemFieldType type, String value)
      {
         fields.put(type.getId(), value);
      }
      
      public void clearField(ItemFieldType type)
      {
         fields.put(type.getId(), "");
      }
      
      public void setCollections(List<ZoteroCollection> collections)
      {
         this.collections = new ArrayList<>(collections);
      }
      
      public void setTags(Set<String> tags)
      {
         this.tags = new HashSet<>(tags);
      }
      
      public void setRelations(HashMap<String, String> relations)
      {
         this.relations = new HashMap<>(relations);
      }
      
      public void setParentItem(Item parent)
      {
         this.parent = parent;
      }
      
      public void setChildren(ItemSet children)
      {
         this.children = children;
      }
      
      public String getKey()
      {
         return this.key;
      }
      
      public int getVersion()
      {
         return this.version;
      }
      
      public ItemType getItemType()
      {
         return this.itemType;
      }
      
      public List<ItemCreator> getCreators()
      {
         return this.creators;
      }
      
      public Map<String,String> getFields()
      {
         return this.fields;
      }
      
      public List<ZoteroCollection> getCollections()
      {
         return this.collections;
      }
      
      public Set<String> getTags()
      {
         return this.tags;
      }
      
      public HashMap<String,String> getRelations()
      {
         return this.relations;
      }
      
      public Item getParent()
      {
         return this.parent;
      }
      
      public ItemSet getChildren()
      {
         return this.children;
      }
   }
}
