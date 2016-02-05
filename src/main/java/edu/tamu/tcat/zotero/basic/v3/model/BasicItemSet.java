package edu.tamu.tcat.zotero.basic.v3.model;

import java.text.MessageFormat;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import edu.tamu.tcat.zotero.Item;
import edu.tamu.tcat.zotero.ItemSet;
import edu.tamu.tcat.zotero.ZoteroRestException;
import edu.tamu.tcat.zotero.basic.v3.BasicZoteroLibrary;
import edu.tamu.tcat.zotero.basic.v3.commands.GetItemQueryPageCommand;
import edu.tamu.tcat.zotero.basic.v3.commands.ZoteroCommandExecutor;
import edu.tamu.tcat.zotero.basic.v3.search.BasicItemQuery;
import edu.tamu.tcat.zotero.basic.v3.search.ItemQueryData;

public class BasicItemSet implements ItemSet
{
   /**
    * Represents the data associated with a single Zotero API call that will return
    * multiple items. In general, a query for multiple items may return more results that the
    * Zotero API is willing to return to the client. In these cases, the API will respond
    * paged results that consist of a sub-set of the total results along with supporting
    * information about how to retrieve the remaining information.
    *
    */
   public static class ItemSetPageData
   {
      /** The query data used to generate this request. This will be a copy of the data such
       *  that modifications to this object will not affect references to this information
       *  held by others. */
      public ItemQueryData data;

      /** The item data for the current page of results. */
      public List<BasicItem> items;

      /** The index of the first returned result (starting at 0). Same as <code>data.start</code>. */
      public int offset;

      /** The last modified version of the data that was returned. */
      public int lastModifiedVersion;

      /** The total number of results associated with this query. */
      public int totalResults;

      // TODO add returned links
   }

   // TODO add support for local cache.
   // TODO add support for ansync data loading and background pre-fetching

   private ConcurrentHashMap<Integer, ItemSetPageData> dataPages = new ConcurrentHashMap<Integer, ItemSetPageData>();

   private ItemQueryData srcQuery;

   private int size = -1;       // total number of results
   private int version = -1;    // last observed version

   private BasicZoteroLibrary library;

   public BasicItemSet(BasicZoteroLibrary library, ItemQueryData srcQuery)
   {
      this.library = library;
      this.srcQuery = srcQuery;

      getPageData(0);
   }

   @Override
   public Iterator<Item> iterator()
   {
      return IntStream.range(0, size).mapToObj(this::get).iterator();
   }

   @Override
   public int size()
   {
      return size;
   }

   @Override
   public Item get(int ix) throws IndexOutOfBoundsException
   {
      if (ix < 0 || ix >= size)
         throw new IndexOutOfBoundsException();

      int page = ix / srcQuery.limit;
      int offset = ix % srcQuery.limit;

      ItemSetPageData pageData = getPageData(page);
      return pageData.items.get(offset);
   }

   @Override
   public BasicItemQuery getQuery()
   {
      return new BasicItemQuery(srcQuery, library);
   }

   public BasicItemSet reload()
   {
      return new BasicItemSet(library, srcQuery);
   }

   /**
    * Called when a response from the server indicates that the data associated with this
    * item set has been modified since it was last retrieved.
    */
   private void onDataChanged()
   {
      throw new ConcurrentModificationException("The underlying Zotero collection has "
            + "changed since this ItemSet was created. Please call ItemSet.reload() ");
   }

   private ItemSetPageData getPageData(int page)
   {
      return dataPages.computeIfAbsent(Integer.valueOf(page), this::loadPageData);
   }

   private ItemSetPageData loadPageData(int page)
   {
      // NOTE this is a blocking request. Might introduce a non-blocking variant to
      //      support pre-fetching if performance warrents it.
      GetItemQueryPageCommand command = new GetItemQueryPageCommand(library);
      command.setQuery(srcQuery, page);
      Future<ItemSetPageData> future = command.execute();

      ZoteroCommandExecutor executor = library.getAccount().getExecutor();
      String pattern = "Failed to retrieve item set page data {0} for query {1}";
      try
      {
         ItemSetPageData data = executor.unwrap(future,
               () -> MessageFormat.format(pattern, page, srcQuery));

         checkDataChanges(data);

         return data;
      }
      catch (ZoteroRestException zre)
      {
         throw new IllegalStateException(zre.getMessage(), zre);
      }
   }

   /**
    * Checks to see if the data for the item set has changed on the server.
    */
   private void checkDataChanges(ItemSetPageData data)
   {
      if (size == -1)
         size = data.totalResults;
      if (version == -1)
         version = data.lastModifiedVersion;

      if (size != data.totalResults || version != data.lastModifiedVersion)
         onDataChanged();
   }

}
