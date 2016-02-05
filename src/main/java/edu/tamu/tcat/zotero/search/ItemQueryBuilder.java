package edu.tamu.tcat.zotero.search;

import edu.tamu.tcat.zotero.ZoteroCollection;
import edu.tamu.tcat.zotero.search.TagFilter.DisjunctiveTagFilter;

/**
 * A builder for use in constructing a new {@link ItemQuery}. All methods provide sensible
 * defaults so it is possible to obtain a builder and immediately build and execute the query.
 * Methods on the builder allow callers to refine their search criteria.
 *
 * <p>
 * All methods return a new instance of the query builder leaving the current instance
 * unchanged.
 *
 * @apiNote This uses a builder pattern rather than a command to allow the client to build a
 * prototype query and then revise that query to generate other related queries.
 */
public interface ItemQueryBuilder
{
   enum QueryMode
   {
      TITLE_CREATOR_YEAR,
      EVERYTHING;
   }

   /**
    *  The field to be used to sort entries.
    */
   enum SortField
   {
      dateAdded,
      dateModified,     // default
      title,
      creator,
      type,
      date,
      publisher,
      publicationTitle,
      journalAbbreviation,
      language,
      accessDate,
      libraryCatalog,
      callNumber,
      rights,
      addedBy,
      numItems;      // only applicable when retrieving tags (?) May need to guard use
   }

   /**
    * Specifies the sorting direction of the field to be sorted. The default sort order
    * varies by {@link SortField}.
    */
   enum SortDirection
   {
      asc, desc;
   }

   /**
    * Specifies that this should search only for items within the given collection. If
    * <code>null</code>, this will search for all items within a given library.
    *
    * @param parent The collection to search within.
    * @return The new {@link ItemQueryBuilder}.
    */
   ItemQueryBuilder searchWithin(ZoteroCollection parent);

   /**
    * Indicates whether the search should descend all sub-directories recursively or only look
    * within the current scope (library or collection).
    *
    * @param recursive <code>true</code> if sub-collections should be searched recursively,
    *       <code>false</code> to search only within items at the top level of the collection.
    * @return The new {@link ItemQueryBuilder}.
    */
   ItemQueryBuilder recursive(boolean recursive);

   /**
    * Specify a phrase to search for. By default, this searches titles and individual creator
    * fields. To change the search behavior, set the {@link QueryMode} directly using the
    * {@link #query(String, QueryMode)}. Zotero currently supports phrase searching only.
    *
    * @param q The query string to search for.
    * @return The new {@link ItemQueryBuilder}.
    */
   ItemQueryBuilder query(String q);

   /**
    * Specify a phrase to search for, providing the query mode explicitly. Zotero currently
    * supports phrase searching only.
    *
    * @param q The query string to search for.
    * @param mode Specifies the fields to search over and other search characteristics
    * @return The new {@link ItemQueryBuilder}.
    */
   ItemQueryBuilder query(String q, QueryMode mode);

   /**
    * Specify the field to sort results by. This will apply the Zotero-defined default sort
    * direction associated with this field.
    *
    * @param field The field to sort results by.
    * @return The new {@link ItemQueryBuilder}.
    */
   ItemQueryBuilder sort(SortField field);

   /**
    * Specify the field to sort results by and the direction to sort them in.
    *
    * @param field The field to sort results by.
    * @return The new {@link ItemQueryBuilder}.
    */
   ItemQueryBuilder sort(SortField field, SortDirection direction);

   // TODO Use the ItemType concrete types?

   /**
    * Filters the results to show only results that match one of the supplied item types.
    *
    * @param itemType One or more item types to match.
    * @return A new QueryBuilder
    */
   ItemQueryBuilder ofType(String... itemTypes);

   /**
    * Excludes items of a specific type.
    *
    * @param itemType The type to exclude.
    * @return A new QueryBuilder
    */
   ItemQueryBuilder excludeType(String itemType);

   /**
    * Filters results to include only items that have the specified tag. Like the other tag
    * filter methods, this resets any pre-existing tag filters defined for this query.
    *
    * @param tag The tag to filter for.
    * @return A new QueryBuilder
    */
   ItemQueryBuilder hasTag(String tag);

   /**
    * Filters results to include only items that have all of the specified tags. Like the other
    * tag filter methods, this resets any pre-existing tag filters defined for this query.
    *
    * @param tags The tags to filter for.
    * @return A new QueryBuilder
    */
   ItemQueryBuilder hasAllTags(String... tags);

   /**
    * Filters results to include only items that have any of the specified tags. Like the other
    * tag filter methods, this resets any pre-existing tag filters defined for this query.
    *
    * @param tags The tags to filter for.
    * @return A new QueryBuilder
    */
   ItemQueryBuilder hasAnyTags(String... tags);

   /**
    * Filters results to include only items that have the specified tag. Like the other tag
    * filter methods, this resets any pre-existing tag filters defined for this query.
    *
    * @param tag The tag filter to apply. Must be an instance derived from the type returned by
    *       {@link #makeTagFilterBuilder()}.
    * @return A new QueryBuilder
    * @throws IllegalArgumentException If the supplied filter was not derived from this
    *       query builder.
    */
   ItemQueryBuilder filter(TagFilter filter) throws IllegalArgumentException;

   /**
    * @param tag The tag to initialize this
    * @return A {@link DisjunctiveTagFilter} for use in constructing a {@code TagFilter} for
    *       use with this {@code ItemQueryBuilder}
    */
   TagFilter.DisjunctiveTagFilter makeTagFilterBuilder(String tag);

   /**
    * Sets the index of the first result. Combine with {@link #limit(int)} to select a slice
    * of the available results. This value will default to zero. Negative values will be
    * coerced to zero.
    *
    * @param start The start index.
    * @return The new {@link ItemQueryBuilder}.
    */
   ItemQueryBuilder start(int start);

   /**
    * The maximum number of results to return with a single request.
    *
    * @param limit The number of results to return.
    * @return The new {@link ItemQueryBuilder}.
    */
   ItemQueryBuilder limit(int limit);

   /**
    * Return only objects modified after the specified library version.
    *
    * @param version The library version.
    * @return The new {@link ItemQueryBuilder}.
    */
   ItemQueryBuilder since(int version);

   /**
    * Construct an {@link ItemQuery} based on the current state of this builder. May be called
    * multiple times.
    *
    * @return The constructed {@code ItemQuery}.
    */
   ExecutableItemQuery build();

}
