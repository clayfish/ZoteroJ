package edu.tamu.tcat.zotero.basic.v3.search;

import java.util.List;

import edu.tamu.tcat.zotero.ItemSet;
import edu.tamu.tcat.zotero.ZoteroRestException;
import edu.tamu.tcat.zotero.basic.v3.BasicZoteroLibrary;
import edu.tamu.tcat.zotero.basic.v3.model.BasicItemSet;
import edu.tamu.tcat.zotero.search.ExecutableItemQuery;
import edu.tamu.tcat.zotero.search.ItemQueryBuilder;
import edu.tamu.tcat.zotero.search.ItemQueryBuilder.QueryMode;
import edu.tamu.tcat.zotero.search.ItemQueryBuilder.SortDirection;
import edu.tamu.tcat.zotero.search.ItemQueryBuilder.SortField;
import edu.tamu.tcat.zotero.search.TagFilter;

public class BasicItemQuery implements ExecutableItemQuery
{

   private final ItemQueryData data;
   private final BasicZoteroLibrary library;

   public BasicItemQuery(ItemQueryData data, BasicZoteroLibrary library)
   {
      this.data = data;
      this.library = library;
   }

   public BasicItemQuery(BasicItemQuery q)
   {
      this.data = new ItemQueryData(q.data);
      this.library = q.library;
   }

   @Override
   public ItemQueryBuilder createBuilder()
   {
      ItemQueryData q = new ItemQueryData(data);
      return new BasicSearchBuilder(q, library);
   }

   @Override
   public String getCollectionId()
   {
      return data.collectionId;
   }

   @Override
   public boolean isRecursive()
   {
      return data.recursive;
   }

   @Override
   public String getQueryText()
   {
      return data.q;
   }

   @Override
   public QueryMode getQueryMode()
   {
      return data.qmode;
   }

   @Override
   public SortField getSortBy()
   {
      return data.sortBy;
   }

   @Override
   public SortDirection getSortDirection()
   {
      return data.sortDir;
   }

   @Override
   public List<String> getItemTypes()
   {
      return data.itemTypes;
   }

   @Override
   public TagFilter getTagFilter()
   {
      return data.tagFilter;
   }

   @Override
   public int getStart()
   {
      return data.start;
   }

   @Override
   public int getLimit()
   {
      return data.limit;
   }

   @Override
   public int getVersion()
   {
      return data.version;
   }

   @Override
   public ItemSet execute() throws ZoteroRestException
   {
      return new BasicItemSet(library, data);
   }

   /**
    * @return A copy of the raw {@code ItemQueryData} object.
    */
   public ItemQueryData getQueryData()
   {
      return new ItemQueryData(data);
   }
}
