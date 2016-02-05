package edu.tamu.tcat.zotero.basic.v3;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import edu.tamu.tcat.zotero.EditCollectionCommand;
import edu.tamu.tcat.zotero.EditItemCommand;
import edu.tamu.tcat.zotero.Item;
import edu.tamu.tcat.zotero.ZoteroCollection;
import edu.tamu.tcat.zotero.ZoteroGroup;
import edu.tamu.tcat.zotero.ZoteroLibrary;
import edu.tamu.tcat.zotero.ZoteroRestException;
import edu.tamu.tcat.zotero.basic.v3.BasicEditCollectionCommand.ZoteroCollectionMutator;
import edu.tamu.tcat.zotero.basic.v3.BasicEditItemCommand.EditItemMutator;
import edu.tamu.tcat.zotero.basic.v3.RestApiV3.Collection;
import edu.tamu.tcat.zotero.basic.v3.RestApiV3.CollectionList;
import edu.tamu.tcat.zotero.basic.v3.commands.DeleteCollectionsCommand;
import edu.tamu.tcat.zotero.basic.v3.commands.DeleteItemCommand;
import edu.tamu.tcat.zotero.basic.v3.commands.GetCollectionCommand;
import edu.tamu.tcat.zotero.basic.v3.commands.GetCollectionsCommand;
import edu.tamu.tcat.zotero.basic.v3.commands.GetItemCommand;
import edu.tamu.tcat.zotero.basic.v3.commands.SaveCollectionCommand;
import edu.tamu.tcat.zotero.basic.v3.commands.SaveItemCommand;
import edu.tamu.tcat.zotero.basic.v3.commands.UpdateCollectionCommand;
import edu.tamu.tcat.zotero.basic.v3.commands.UpdateItemCommand;
import edu.tamu.tcat.zotero.basic.v3.model.BasicCollection;
import edu.tamu.tcat.zotero.basic.v3.model.BasicItem;
import edu.tamu.tcat.zotero.basic.v3.search.BasicItemQuery;
import edu.tamu.tcat.zotero.basic.v3.search.BasicSearchBuilder;
import edu.tamu.tcat.zotero.basic.v3.search.ItemQueryData;
import edu.tamu.tcat.zotero.search.ItemQuery;
import edu.tamu.tcat.zotero.search.ItemQueryBuilder;

public class BasicZoteroLibrary implements ZoteroLibrary
{
   private final long timeout;
   private final TimeUnit timeoutUnits;
   // HACK: cannot be null. May be anonymous, but cannot be null
   private final BasicUserAccount account;

   private final Type type;
   private final String id;
   private final int version;
   private final String name;

   public BasicZoteroLibrary(BasicUserAccount account)
   {
      this.type = ZoteroLibrary.Type.User;
      this.account = account;
      this.id = account.getId();

      // TODO: need to obtain these or else update them based on retrieved data.
      this.version = -1;
      this.name = "User library for " + this.id;

      // HACK: make these configurable
      this.timeout = 10;
      this.timeoutUnits = TimeUnit.SECONDS;
   }


   public BasicZoteroLibrary(BasicUserAccount account, ZoteroGroup group)
   {
      this.type = ZoteroLibrary.Type.Group;

      this.account = account;
      this.id = Integer.toString(group.getId());
      this.version = group.getVersion();
      this.name = group.getName();

      // HACK: make these configurable
      this.timeout = 10;
      this.timeoutUnits = TimeUnit.SECONDS;
   }

   /**
    *
    * @return The user account for which access to this library is authorized. This may
    *       <code>null</code> for publicly accessible libraries.
    */
   public BasicUserAccount getAccount()
   {
      return account;
   }

   @Override
   public String getId()
   {
      return id;
   }

   @Override
   public int getVersion()
   {
      return version;
   }


   @Override
   public ZoteroLibrary.Type getType()
   {
      return type;
   }

   @Override
   public Item getItem(String id) throws ZoteroRestException, IllegalStateException
   {
      String message = "Could not retrieve bibliographic item for id {0} from library {1}";
      return unwrap(getItemAsync(id), () -> MessageFormat.format(message, id, this.name));
   }

   @Override
   public Map<String, Item> getItems(String... ids) throws ZoteroRestException, IllegalStateException
   {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException();
   }

   @Override
   public EditItemCommand createItem()
   {
      return new BasicEditItemCommand(this);
   }

   @Override
   public EditItemCommand editItem(Item item) throws IllegalArgumentException
   {
      return new BasicEditItemCommand(this, item);
   }

   @Override
   public void deleteItems(Item... item) throws IllegalArgumentException
   {
      Set<String> deleteItems = new HashSet<>();
      for(int i=0; i<item.length; i++)
      {
         deleteItems.add(item[i].getId());
      }
      removeItems(deleteItems);
   }

   @Override
   public EditCollectionCommand createCollection()
   {
      return new BasicEditCollectionCommand(this);
   }


   @Override
   public EditCollectionCommand editCollection(ZoteroCollection collection)
   {
      return new BasicEditCollectionCommand(this, collection);
   }


   @Override
   public void deleteCollections(ZoteroCollection... collections)
   {
      Set<String> deleteItems = new HashSet<>();
      for(int i=0; i<collections.length; i++)
      {
         if (collections[i] != null)
            deleteItems.add(collections[i].getId());
      }
      removeCollection(deleteItems);
   }

   public Future<Item> creatItem(EditItemMutator itemData) throws ZoteroRestException, IllegalStateException
   {
      SaveItemCommand cmd = new SaveItemCommand(this, itemData);
      Future<edu.tamu.tcat.zotero.basic.v3.RestApiV3.Item> results = cmd.execute();
      return new AdaptingFuture<RestApiV3.Item, Item>(results, this::adapt);
   }

   public Future<Item> updateItem(EditItemMutator itemData) throws ZoteroRestException, IllegalStateException
   {
      UpdateItemCommand cmd = new UpdateItemCommand(this, itemData);
      Future<EditItemMutator> updatedItem = cmd.execute();
      return new AdaptingFuture<EditItemMutator, Item>(updatedItem, this::adapt);
   }

   public Future<Void> removeItems(Set<String> deleteItems)
   {
      return new DeleteItemCommand(this, deleteItems).execute();
   }

   public Future<ZoteroCollection> addCollection(ZoteroCollectionMutator collection) throws ZoteroRestException, IllegalStateException
   {
      SaveCollectionCommand cmd = new SaveCollectionCommand(this, collection);
      Future<Collection> result = cmd.execute();
      return new AdaptingFuture<RestApiV3.Collection, ZoteroCollection>(result, this::adapt);
   }

   public Future<ZoteroCollection> updateCollection(ZoteroCollectionMutator collection) throws ZoteroRestException, IllegalStateException
   {
      UpdateCollectionCommand cmd = new UpdateCollectionCommand(this, collection);
      Future<ZoteroCollectionMutator> updatedCollection = cmd.execute();
      return new AdaptingFuture<ZoteroCollectionMutator, ZoteroCollection>(updatedCollection, this::adapt);
   }

   public void removeCollection(Set<String> collectionKey)
   {
      new DeleteCollectionsCommand(this, collectionKey).execute();
   }

   @Override
   public ItemQueryBuilder makeItemQueryBuilder()
   {
      return new BasicSearchBuilder(this);
   }

   @Override
   public ItemQueryBuilder restoreQuery(String token)
   {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException();
   }

   @Override
   public String tokenize(ItemQuery q)
   {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException();
   }

   @Override
   public ZoteroCollection getCollection(String id) throws ZoteroRestException
   {
      String message = "Could not retrieve collection for id {0} from library {1}";
      return unwrap(getCollectionAsync(id), () -> MessageFormat.format(message, id, this.name));
   }

   public Future<Item> getItemAsync(String itemId) throws ZoteroRestException, IllegalStateException
   {
      GetItemCommand command = new GetItemCommand(this);
      command.setItemId(itemId);

      Future<RestApiV3.Item> result = command.execute();
      return new AdaptingFuture<RestApiV3.Item, Item>(result, this::adapt);
   }

   public Future<ZoteroCollection> getCollectionAsync(String id) throws ZoteroRestException
   {
      GetCollectionCommand command = new GetCollectionCommand(this);
      command.setCollectionId(id);

      Future<RestApiV3.Collection> result = command.execute();
      return new AdaptingFuture<RestApiV3.Collection, ZoteroCollection>(result, this::adapt);
   }


   public ItemQueryBuilder makeItemQueryBuilder(ItemQuery q)
   {
      if (q instanceof BasicItemQuery)
      {
         BasicItemQuery query = (BasicItemQuery)q;
         ItemQueryData data = query.getQueryData();

         return new BasicSearchBuilder(data, this);
      }

      // FIXME violates substitution principle. Need to clarify in JavaDoc and/or provide
      //       alternative approach to create this.
      throw new IllegalArgumentException("The supplied item query must be an instance of " + BasicItemQuery.class.getName());
   }

   public List<ZoteroCollection> getSubCollections(String parentId) throws ZoteroRestException
   {
      return unwrap(getSubCollectionsAsync(parentId),
            () -> MessageFormat.format("Failed to retrieve sub collections for {0}", parentId));
   }

   public Future<List<ZoteroCollection>> getSubCollectionsAsync(String parentId)
   {
      GetCollectionsCommand cmd = new GetCollectionsCommand(this);
      cmd.setParentCollection(parentId);
      Future<CollectionList> results = cmd.execute();
      return new AdaptingFuture<RestApiV3.CollectionList, List<ZoteroCollection>>(results, this::adapt);
   }

   private Item adapt(RestApiV3.Item dto)
   {
      return new BasicItem(this, account.getItemTypeProvider(), dto);
   }

   private ZoteroCollection adapt(RestApiV3.Collection dto)
   {
      return new BasicCollection(this, dto);
   }

   private List<ZoteroCollection> adapt(RestApiV3.CollectionList dtos)
   {
      List<ZoteroCollection> collection = new ArrayList<>();
      dtos.collections.forEach((i)->{
         collection.add(adapt(i));
      });
      return collection;
   }
   
   private Item adapt(EditItemMutator mutator)
   {
      return new BasicItem(this, mutator);
   }
   
   private ZoteroCollection adapt(ZoteroCollectionMutator mutator)
   {
      return new BasicCollection(this, mutator);
   }

   private <X> X unwrap(Future<X> result, Supplier<String> message) throws ZoteroRestException
   {
      try
      {
         return result.get(timeout, timeoutUnits);
      }
      catch (InterruptedException | TimeoutException e)
      {
         throw new IllegalStateException(message.get(), e);
      }
      catch (ExecutionException ex)
      {
         Throwable cause = ex.getCause();
         if (cause instanceof ZoteroRestException)
            throw (ZoteroRestException)cause;

         if  (cause instanceof RuntimeException)
            throw (RuntimeException)cause;

         throw new IllegalStateException(message.get(), cause);
      }
   }
}
