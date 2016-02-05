package edu.tamu.tcat.zotero.basic.v3.model;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.tamu.tcat.zotero.Item;
import edu.tamu.tcat.zotero.ZoteroCollection;
import edu.tamu.tcat.zotero.basic.v3.BasicEditCollectionCommand.ZoteroCollectionMutator;
import edu.tamu.tcat.zotero.basic.v3.BasicZoteroLibrary;
import edu.tamu.tcat.zotero.basic.v3.RestApiV3;
import edu.tamu.tcat.zotero.basic.v3.RestApiV3.Tag;

public class BasicCollection implements ZoteroCollection
{

//   <userOrGroupPrefix>/collections/<collectionKey>/collections The set of subcollections within a specific collection in the library
//   <userOrGroupPrefix>/collections/<collectionKey>/items The set of all items within a specific collection in the library
//   <userOrGroupPrefix>/collections/<collectionKey>/items/top   The set of top-level items within a specific collection in the library
//   <userOrGroupPrefix>/collections/<collectionKey>/tags  The set of tags within a specific collection in the library
   private final String key;
   private final int version;

   private final String name;
   private final String parentId;

   // TODO replace with ZoteroLibrary?
   private final BasicZoteroLibrary library;

   public BasicCollection(BasicZoteroLibrary library, RestApiV3.Collection dto)
   {
      this.library = library;
      // TODO should free this of a dependency on RestApiV3?
      // TODO test adaptation with 'dummy' data
      this.key = dto.key;
      this.version = dto.version;
      this.name = dto.data.name;
      
      if (dto.data.parentCollection.equals("false"))
         this.parentId = "";
      else
         this.parentId = dto.data.parentCollection;

   }

   public BasicCollection(BasicZoteroLibrary library, ZoteroCollectionMutator mutator)
   {
      this.library = library;
      this.key = mutator.getId();
      this.version = mutator.getVersion();
      this.name = mutator.getName();
      String parent = mutator.getParent();
      if (parent.equals("false"))
         this.parentId = "";
      else
         this.parentId = parent;
   }

   @Override
   public String getId()
   {
      return key;
   }

   @Override
   public int getVersion()
   {
      return version;
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public boolean hasParent()
   {
      if (parentId == null || parentId.trim().isEmpty())
         return false;
      else
         return true;
   }

   @Override
   public ZoteroCollection getParent()
   {
      if (!hasParent())
         throw new UnsupportedOperationException("This collection has no parent. " + this);

      try
      {
         return library.getCollection(parentId);
      }
      catch (Exception ex)
      {
         throw new IllegalStateException("Failed to retrieve parent of " + this + "\n\tParent id: " + parentId, ex);
      }
   }

   @Override
   public List<Item> getItems()
   {
      throw new UnsupportedOperationException();
//      // TODO need to add filtered/sorted variant
//      // TODO can/should we do paging
//      // <userOrGroupPrefix>/collections/<collectionKey>/items/top
//      try
//      {
//         return client.getItemWithinCollection(this.key, QueryOption.TOP);
//      }
//      catch (Exception ex)
//      {
//         throw new IllegalStateException("Failed to retrieve parent of " + this + "\n\tParent id: " + parentId, ex);
//      }
   }

   @Override
   public List<Item> getAllItems()
   {
      throw new UnsupportedOperationException();
//      // <userOrGroupPrefix>/collections/<collectionKey>/items
//      try
//      {
//         return client.getItemWithinCollection(this.key, QueryOption.ALL);
//      }
//      catch (Exception ex)
//      {
//         throw new IllegalStateException("Failed to retrieve parent of " + this + "\n\tParent id: " + parentId, ex);
//      }
   }

   @Override
   public List<ZoteroCollection> getSubCollections()
   {
      // <userOrGroupPrefix>/collections/<collectionKey>/collections
      try
      {
         return library.getSubCollections(this.key);
      }
      catch (Exception ex)
      {
         throw new IllegalStateException("Failed to retrieve parent of " + this + "\n\tParent id: " + parentId, ex);
      }
   }

   @Override
   public Set<Tag> getTags()
   {
      return Collections.emptySet();
   }

   // TODO format items
   // TODO search within a collection

   @Override
   public String toString()
   {
      // TODO add library information?
      String msg = "Zotero Collection \"{0}\" [id: {1}]";
      return MessageFormat.format(msg, name, key);
   };
}
