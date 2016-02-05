package edu.tamu.tcat.zotero.basic.v3;

import java.util.concurrent.Future;

import edu.tamu.tcat.zotero.EditCollectionCommand;
import edu.tamu.tcat.zotero.ZoteroCollection;

public class BasicEditCollectionCommand implements EditCollectionCommand
{

   private final BasicZoteroLibrary library;
   private ZoteroCollectionMutator collection;
   
   public BasicEditCollectionCommand(BasicZoteroLibrary library)
   {
      this.library = library;
      this.collection = new ZoteroCollectionMutator();
   }
   
   public BasicEditCollectionCommand(BasicZoteroLibrary library, ZoteroCollection collection)
   {
      this.library = library;
      this.collection = new ZoteroCollectionMutator(collection);
   }

   @Override
   public EditCollectionCommand setName(String name)
   {
      collection.setName(name);
      return this;
   }

   @Override
   public EditCollectionCommand setParent(ZoteroCollection collection)
   {
      this.collection.setParent(collection.getId());
      return this;
   }

   @Override
   public Future<ZoteroCollection> execute()
   {
      try
      {
         return collection.getId() == null || collection.getId().isEmpty() 
                ? library.addCollection(collection)
                : library.updateCollection(collection);
      }
    catch (Exception e)
    {
       throw new IllegalStateException("An error occured while attempting to create or edit the collection:" + collection.getName(), e);
    }
   }
   
   public class ZoteroCollectionMutator
   {
      private  String key;
      private  int version;
      private  String name;
      private  String parentId;
      
      public ZoteroCollectionMutator()
      {
      }
      
      public ZoteroCollectionMutator(ZoteroCollection orig)
      {
         setId(orig.getId());
         setVersion(orig.getVersion());
         setName(orig.getName());
         if (orig.hasParent())
            setParent(orig.getParent().getId());
      }

      public void setId(String id)
      {
         this.key = id;
      }
      
      public void setVersion(int ver)
      {
         this.version = ver;
      }
      
      public void setName(String name)
      {
         this.name = name;
      }
      
      public void setParent(String parentId)
      {
         this.parentId = parentId;
      }

      public String getId()
      {
         return this.key;
      }

      public int getVersion()
      {
         return this.version;
      }

      public String getName()
      {
         return this.name;
      }

      public String getParent()
      {
         return this.parentId;
      }

   }

}
