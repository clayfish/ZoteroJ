package edu.tamu.tcat.zotero.search;

import java.util.List;

import edu.tamu.tcat.zotero.search.ItemQueryBuilder.QueryMode;
import edu.tamu.tcat.zotero.search.ItemQueryBuilder.SortDirection;
import edu.tamu.tcat.zotero.search.ItemQueryBuilder.SortField;

/**
 *  Represents a query for a set of bibliographic items from Zotero. Instances are constructed
 *  using an {@link ItemQueryBuilder}.
 */
public interface ItemQuery
{
   /**
    * Creates a query builder that is initialized to the state of this query.
    *
    * @return The created builder.
    */
   ItemQueryBuilder createBuilder();

   /**
    * @return The id of the collection to search within. Will be <code>null</code> if
    *    no collection is specified.
    */
   String getCollectionId();

   /**
    * @return <code>true</code> if the query will recursively descend sub-collections or
    *    <code>false</code> if only the immediate children of the current collection of library
    *    will be returned.
    */
   boolean isRecursive();

   /**
    * @return A phrase to search for within items. How this phrase is matched agaist items
    *    is defined by the value of {@link #getQueryMode()}. May be <code>null</code> if no
    *    query phase is supplied.
    */
   String getQueryText();

   /**
    * @return The mode for matching the supplied query text. May be <code>null</code> if the
    *    default query mode is to be used.
    */
   QueryMode getQueryMode();

   /**
    * @return The field by which results should be sorted. May be <code>null</code> if no
    *    explicit sort criteria is supplied.
    */
   SortField getSortBy();

   /**
    * @return The sort direction to apply. May be <code>null</code> if the per-field default
    *    (as defined by Zotero) should be used.
    */
   SortDirection getSortDirection();

   /**
    * @return A list of item types to be returned. May be an empty list if no item type
    *    restrictions have been applied.
    */
   List<String> getItemTypes();

   /**
    * @return The tag filter to be applied to the results. May be <code>null</code> if no
    *    tag filtering is to be applied.
    */
   TagFilter getTagFilter();

   /**
    * @return The index of the first item to be returned.
    */
   int getStart();

   /**
    * @return The maximum number of items to be returned. Will be in the range of
    *    <code>[0..100]</code>.
    */
   int getLimit();

   /**
    * @return The version of the library to query against. Results will be restricted to
    *       those that have been modified since this version of the library.
    */
   int getVersion();

}
