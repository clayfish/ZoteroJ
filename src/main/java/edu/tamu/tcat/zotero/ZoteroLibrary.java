package edu.tamu.tcat.zotero;

import java.util.Map;

import edu.tamu.tcat.zotero.search.ItemQuery;
import edu.tamu.tcat.zotero.search.ItemQueryBuilder;

/**
 *  A collection of bibliographic items. Items within a collection may be organized into one
 *  or more  organized into hierarchical collections.
 *
 *  <p>
 *  Zotero manages bibliographic records in libraries. Each user account has exactly one
 *  associated user library. Users may also have access to (that is, be members of) zero or
 *  more group libraries, which they may or may not have permission to edit.
 */
public interface ZoteroLibrary
{
   enum Type
   {
      /** Bibliographic library for an individual. */
      User,

      /** Bibliographic library for a group account that may be accessible to multiple users. */
      Group;
   }

   /**
    * @return Zotero's internal identifier for this library.
    */
   String getId();

   /**
    * @return The version number for the last time this library was updated.
    */
   int getVersion();

   /**
    * @return The library type of account. May be public open, public closed or private.
    */
   ZoteroLibrary.Type getType();   // FIXME the returned type does not correspond to API

   /**
    * Returns a specific item.
    *
    * @param id The id of item
    * @return the requested bibliographic item.
    *
    * @throws ZoteroRestException If errors were encountered retrieving the indicated item. This
    *       could be due to access restrictions, authentication problems or the object not
    *       being found.
    * @throws IllegalStateException For errors related to network access and other problems.
    */
   Item getItem(String id) throws ZoteroRestException, IllegalStateException;

   /**
    * Retrieve multiple bibliographic items by their id.
    *
    * @param ids The ids of the items to retrieve.
    * @return A map from item id to the retrieved item. Values will not be {@code null}.
    *
    * @throws ZoteroRestException If there are errors retrieving one or more of the requested
    *       items. This could be due to access restrictions, authentication problems or the
    *       items not being found.
    * @throws IllegalStateException For errors related to network access and other problems.
    */
   Map<String, Item> getItems(String... ids) throws ZoteroRestException, IllegalStateException;

   /**
    * Retrieves the identified collection.
    *
    * @param id The id of the collection to retrieve.
    * @return The identified collection.
    * @throws ZoteroRestException If there are errors retrieving the requested collection
    *       This could be due to access restrictions, authentication problems or the
    *       collection not being found.
    * @throws IllegalStateException For errors related to network access and other problems.
    */
   ZoteroCollection getCollection(String id) throws ZoteroRestException;

   // TODO return set of all collections as a tree structure

   /**
    * Constructs an {@link ItemQueryBuilder} for use in searching this items within this library.
    *
    * @return The item query builder.
    */
   ItemQueryBuilder makeItemQueryBuilder();

   /**
    * Constructs a new {@link ItemQueryBuilder} from a previously tokenized query.
    *
    * @param token A token created using {@link #tokenize(ItemQuery)}
    * @return The restored query.
    * @throws IllegalArgumentException If the supplied token was not created by this library.
    */
   ItemQueryBuilder restoreQuery(String token);

   /**
    * Creates a token from a previously built token for serialization purposes. The query
    * can be restored and re-issued using {@link #restoreQuery(String)}.
    *
    * @param q The query to tokenize.
    * @return A tokenized version of the supplied query.
    * @throws IllegalArgumentException If the supplied token was not created by this library.
    */
   String tokenize(ItemQuery q);

   /**
    * Constructs an {@link EditItemCommand} to use to create a new item within this library.
    * The command will only take effect upon execution, meaning that it can be abandoned without
    * leaking internal resources or leaving partially created data in the database.
    *
    * @return A command for use to create a new bibliographic item.
    * @throws UnsupportedOperationException If the account associated with this library
    *       instance does not have permission to edit items.
    */
   EditItemCommand createItem() throws UnsupportedOperationException;

   /**
    * Constructs an {@link EditItemCommand} to use to edit an existing item within this library.
    * The command will only take effect upon execution, meaning that it can be abandoned
    * without leaking internal resources or leaving partially created data in the database.
    *
    * @param item The item to be edited.
    * @return A command to be used to edit the supplied item.
    * @throws IllegalArgumentException If the supplied item is not defined for this collection.
    * @throws UnsupportedOperationException If the account associated with this library
    *       instance does not have permission to edit items.
    */
   EditItemCommand editItem(Item item) throws IllegalArgumentException, UnsupportedOperationException;

   /**
    * Deletes one or more items from this library.
    *
    * @param items The items to be deleted
    * @return A future that will resolve once the supplied items have been removed from the library.
    * @throws UnsupportedOperationException If the account associated with this library
    *       instance does not have permission to edit items.
    */
   void deleteItems(Item... items) throws UnsupportedOperationException; // FIXME not a string

   /**
    * Constructs an {@link EditCollectionCommand} for use in creating a new collection within
    * this library.
    *
    * @return An {@code EditCollectionCommand} to create a new collection
    * @throws UnsupportedOperationException If the account associated with this library
    *       instance does not have permission to edit collections.
    */
   EditCollectionCommand createCollection() throws UnsupportedOperationException;;

   /**
    * Constructs an {@link EditCollectionCommand} for use in creating a new collection within
    * this library.
    *
    * @param collection The collection to be edited.
    * @return An {@code EditCollectionCommand} to update the supplied collection
    * @throws IllegalArgumentException If the supplied collection is not defined for this collection.
    * @throws UnsupportedOperationException If the account associated with this library
    *       instance does not have permission to edit collections.
    */
   EditCollectionCommand editCollection(ZoteroCollection collection) throws IllegalArgumentException, UnsupportedOperationException;

   /**
    * Deletes the collections provided.
    *
    * @param collections The collection, or collections to be deleted from this library.
    * @return A {@code Future<String>} on the success of failure of the deletion.
    * @throws UnsupportedOperationException If the account associated with this library
    *       instance does not have permission to edit collections.
    */
   void deleteCollections(ZoteroCollection... collections) throws UnsupportedOperationException;
}
