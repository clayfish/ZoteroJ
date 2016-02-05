package edu.tamu.tcat.zotero;

import java.util.List;
import java.util.Set;

import edu.tamu.tcat.zotero.basic.v3.RestApiV3.Tag;

public interface ZoteroCollection
{
   /**
    * @return The unique id of this collection.
    */
   String getId();

   /**
    * @return The version of this collection. Collections are versioned by Zotero using
    *    monotonically increasing integer ids. In general, content returned by an instance may
    *    be queried directly from the remote Zotero server. Typically, collection
    *    implementations will retrieve data from the server on demand and cache that data as
    *    needed. Consequently, the data returned by a method (such as {@link #getItems()}) may
    *    reflect a later state of the collection than the version returned by this method.
    */
   int getVersion();

   /**
    * @return The name of this collection for display purposes.
    */
   String getName();

   /**
    * @return All items that are immediate children of this collection. Note that this will not
    *       descend into sub-collections. May be an empty list, will not be {@code null}.
    */
   List<Item> getItems();

   /**
    * @return All items that are descendants of this collection including all sub-collections.
    *       May be an empty list, will not be {@code null}.
    */
   List<Item> getAllItems();

   /**
    * @return {@code true} if this collection has a parent collection.
    */
   boolean hasParent();

   /**
    * @return The parent of this collection. Will throw an exception if this collection
    *       does not have a parent as indicated by {@link #hasParent()}.
    */
   ZoteroCollection getParent();

   /**
    * @return A list of all sub-collections of this collection. May be an empty list, will
    *       not be {@code null}.
    */
   List<ZoteroCollection> getSubCollections();

   /**
    * @return A set of all tags used within this collection.
    */
   Set<Tag> getTags();
}
