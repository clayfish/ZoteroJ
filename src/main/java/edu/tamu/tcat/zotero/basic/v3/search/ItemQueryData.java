package edu.tamu.tcat.zotero.basic.v3.search;

import java.util.ArrayList;
import java.util.List;

import edu.tamu.tcat.zotero.basic.v3.search.TagFilterFactory.WebTargetTagFilter;
import edu.tamu.tcat.zotero.search.ItemQueryBuilder.QueryMode;
import edu.tamu.tcat.zotero.search.ItemQueryBuilder.SortDirection;
import edu.tamu.tcat.zotero.search.ItemQueryBuilder.SortField;

/**
 * JSON serializable representation of a search.
 */
public class ItemQueryData
{
   // NOTE this is intended to support serialization of searches for local caching and
   //      incremental updates. For the sake of being explicit, some parameters that have
   //      valid defaults are defined here explicitly.

   public String collectionId;
   public boolean recursive;

   public String q = null;
   public QueryMode qmode = QueryMode.TITLE_CREATOR_YEAR;
   public SortField sortBy;
   public SortDirection sortDir;
   public List<String> itemTypes = new ArrayList<>();
   public WebTargetTagFilter tagFilter;                        // TODO make JSONable

   public int start = 0;
   public int limit = 100;
   public int version = -1;

   public ItemQueryData()
   {
   }

   public ItemQueryData(ItemQueryData source)
   {
      collectionId = source.collectionId;
      recursive = source.recursive;

      q = source.q;
      qmode = source.qmode;

      sortBy = source.sortBy;
      sortDir = source.sortDir;
      itemTypes = new ArrayList<>(source.itemTypes);
      tagFilter = source.tagFilter;

      start = source.start;
      limit = source.limit;
      version = source.version;
   }
}