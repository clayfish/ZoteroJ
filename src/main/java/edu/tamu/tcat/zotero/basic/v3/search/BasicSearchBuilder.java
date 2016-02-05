package edu.tamu.tcat.zotero.basic.v3.search;

import java.util.ArrayList;
import java.util.Arrays;

import edu.tamu.tcat.zotero.ZoteroCollection;
import edu.tamu.tcat.zotero.basic.v3.BasicZoteroLibrary;
import edu.tamu.tcat.zotero.basic.v3.search.TagFilterFactory.WebTargetTagFilter;
import edu.tamu.tcat.zotero.search.ItemQueryBuilder;
import edu.tamu.tcat.zotero.search.TagFilter;
import edu.tamu.tcat.zotero.search.TagFilter.DisjunctiveTagFilter;

public class BasicSearchBuilder implements ItemQueryBuilder
{
   private final ItemQueryData query;
   private final BasicZoteroLibrary library;

   public BasicSearchBuilder(BasicZoteroLibrary library)
   {
      this(new ItemQueryData(), library);
   }

   public BasicSearchBuilder(ItemQueryData query, BasicZoteroLibrary library)
   {
      this.query = query;
      this.library = library;
   }

   @Override
   public BasicSearchBuilder searchWithin(ZoteroCollection parent)
   {
      ItemQueryData q = new ItemQueryData(query);
      q.collectionId = parent != null ? parent.getId() : null;

      return new BasicSearchBuilder(q, library);
   }

   @Override
   public BasicSearchBuilder recursive(boolean recursive)
   {
      ItemQueryData q = new ItemQueryData(query);
      q.recursive = recursive;

      return new BasicSearchBuilder(q, library);
   }

   @Override
   public ItemQueryBuilder query(String q)
   {
      return query(q, QueryMode.TITLE_CREATOR_YEAR);
   }

   @Override
   public BasicSearchBuilder query(String qStr, QueryMode mode)
   {
      ItemQueryData q = new ItemQueryData(query);
      q.q = qStr;
      q.qmode = mode;

      return new BasicSearchBuilder(q, library);
   }

   @Override
   public ItemQueryBuilder sort(SortField field)
   {
      return sort(field, null);
   }

   @Override
   public BasicSearchBuilder sort(SortField field, SortDirection direction)
   {
      ItemQueryData q = new ItemQueryData(query);
      q.sortBy = field;
      q.sortDir = direction;

      return new BasicSearchBuilder(q, library);
   }

   @Override
   public BasicSearchBuilder ofType(String... itemTypes)
   {
      ItemQueryData q = new ItemQueryData(query);
      q.itemTypes = Arrays.asList(itemTypes);

      return new BasicSearchBuilder(q, library);
   }

   @Override
   public BasicSearchBuilder excludeType(String itemType)
   {

      ItemQueryData q = new ItemQueryData(query);
      q.itemTypes = new ArrayList<>();
      q.itemTypes.add("-" + itemType);

      return new BasicSearchBuilder(q, library);
   }

   @Override
   public BasicSearchBuilder hasTag(String tag)
   {
      return filter(TagFilterFactory.filter(tag));
   }

   @Override
   public BasicSearchBuilder hasAllTags(String... tags)
   {
      if (tags.length == 0)
         return this;

      return filter(TagFilterFactory.and(tags));
   }

   @Override
   public BasicSearchBuilder hasAnyTags(String... tags)
   {
      if (tags.length == 0)
         return this;

      return filter(TagFilterFactory.or(tags));
   }

   @Override
   public BasicSearchBuilder filter(TagFilter filter)
   {
      if (!WebTargetTagFilter.class.isInstance(filter))
         throw new IllegalArgumentException("Invalid type of tag filter.");

      ItemQueryData q = new ItemQueryData(query);
      q.tagFilter = (WebTargetTagFilter)filter;

      return new BasicSearchBuilder(q, library);
   }

   @Override
   public DisjunctiveTagFilter makeTagFilterBuilder(String tag)
   {
      return new OrTagClause(tag);
   }

   @Override
   public BasicSearchBuilder start(int start)
   {
      if (start < 0)
         start = 0;

      ItemQueryData q = new ItemQueryData(query);
      q.start = start;

      return new BasicSearchBuilder(q, library);
   }

   @Override
   public BasicSearchBuilder limit(int limit)
   {
      if (limit < 1)
         limit = 1;
      if (limit > 100)
         limit = 100;

      ItemQueryData q = new ItemQueryData(query);
      q.limit = limit;

      return new BasicSearchBuilder(q, library);
   }

   @Override
   public BasicSearchBuilder since(int version)
   {
      ItemQueryData q = new ItemQueryData(query);
      q.version = version;

      return new BasicSearchBuilder(q, library);
   }

   @Override
   public BasicItemQuery build()
   {
      return new BasicItemQuery(query, library);
   }

}
