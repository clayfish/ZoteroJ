package edu.tamu.tcat.zotero.basic.v3.commands;

import static java.util.stream.Collectors.toList;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import edu.tamu.tcat.zotero.basic.v3.BasicZoteroLibrary;
import edu.tamu.tcat.zotero.basic.v3.RestApiV3;
import edu.tamu.tcat.zotero.basic.v3.model.BasicItem;
import edu.tamu.tcat.zotero.basic.v3.model.BasicItemSet.ItemSetPageData;
import edu.tamu.tcat.zotero.basic.v3.search.ItemQueryData;
import edu.tamu.tcat.zotero.types.ItemTypeProvider;

public class GetItemQueryPageCommand extends ZoteroCommandAdapter<ItemSetPageData>
   {
      // FIXME should return ItemSetData that can be used to populate an item set.
      //       the ItemSet itself will need to poke into this and get the underlyign results
      //       re-sort, etc.
      private static final String API_ITEMS = "items";
      private static final String API_COLLECTIONS = "collections";

      private ItemQueryData data;

      public GetItemQueryPageCommand(BasicZoteroLibrary library)
      {
         super(library);
      }

      /**
       *
       * @param data The item query to execute. The command will maintain an internal copy of
       *       this query so that the supplied query data will not be modified and changes to
       *       the supplied query will not be reflected within the copy held by the command.
       */
      public void setQuery(ItemQueryData data)
      {
         setQuery(data, 0);
      }

      /**
       *
       * @param data The item query to execute. The command will maintain an internal copy of
       *       this query so that the supplied query data will not be modified and changes to
       *       the supplied query will not be reflected within the copy held by the command.
       * @param page The index of the set of paged data to be returned. For queries that return
       *       more than <code>data.limit</code> items, multiple pages of results can be
       *       returned by incrementing the value of <code>data.start</code>. This causes the
       *       index of the first item returned to be set appropriately.
       */
      public void setQuery(ItemQueryData data, int page)
      {
         if (page < 0)
            page = 0;

         this.data = new ItemQueryData(data);
         this.data.start = page * this.data.limit;
      }

      @Override
      protected Invocation buildInvocation(WebTarget apiRoot)
      {
         if (data == null)
         {
            data = new ItemQueryData();      // default 'query all items, recursively
            data.recursive = true;
         }

         WebTarget target = configureBaseUri(apiRoot);
         target = apply(target);

         return appendHeaders(target.request(MediaType.APPLICATION_JSON)).buildGet();
      }

      /**
       * Parses a string value that is expected to be an integer. Traps and ignores any
       * exceptions and returns the supplied default value.
       *
       * @param expecedInt The value to parse.
       * @param defaultValue A default value to supply if parsing fails.
       * @return The parsed integer or the default value
       */
      private int parseIntSafe(String expecedInt, int defaultValue)
      {
         if (expecedInt == null || expecedInt.trim().isEmpty())
            return defaultValue;

         try
         {
            return Integer.parseInt(expecedInt);
         }
         catch (Exception ex)
         {
            // HACK: fails silently, should log warning.
            return defaultValue;
         }
      }

      @Override
      protected ItemSetPageData handleResponse(Response response)
      {
         if (response.getStatus() != 200)
            throw handleError(response);

         GenericType<List<RestApiV3.Item>> type = new GenericType<List<RestApiV3.Item>>(){};
         String numResults = response.getHeaderString("Total-Results");
         String lastVersion = response.getHeaderString("Last-Modified-Version");

         ItemSetPageData page = new ItemSetPageData();
         List<RestApiV3.Item> items = response.readEntity(type);
         BasicZoteroLibrary library = getLibrary();
         ItemTypeProvider typeProvider = library.getAccount().getItemTypeProvider();
         page.data = new ItemQueryData(data);
         page.items = items.stream()
                           .map(dto -> new BasicItem(library, typeProvider, dto))
                           .collect(Collectors.toList());
         page.totalResults = parseIntSafe(numResults, -1);
         page.totalResults = parseIntSafe(lastVersion, 0);

         return page;

//          TODO parse link data
//            String link = response.getHeaderString("Link");
//            Link: <https://api.zotero.org/users/12345/items?limit=30&start=30>; rel="next",
//               <https://api.zotero.org/users/12345/items?limit=30&start=5040>; rel="last",
//               <https://www.zotero.org/users/12345/items>; rel="alternate"
//            new BasicItemSet();
      }

      private IllegalStateException handleError(Response response)
      {
         // TODO find correct type to throw here
         return new IllegalStateException(response.getStatusInfo().getReasonPhrase());
      }

      private WebTarget configureBaseUri(WebTarget apiRoot)
      {
         WebTarget target = apiRoot;
         target = (data.collectionId != null && !data.collectionId.trim().isEmpty())
            ? target.path(API_COLLECTIONS).path(data.collectionId.trim()).path(API_ITEMS)
            : target.path(API_ITEMS);

         if (!data.recursive)
            target = target.path("top");

         return target;
      }

      /**
       * Applies query parameters to the WebTarget that is being constructed.
       * @param target
       * @return
       */
      private WebTarget apply(WebTarget target)
      {
         target = target.queryParam("start", data.start)
                        .queryParam("limit", data.limit);

         if (data.version >= 0)
            target = target.queryParam("since", data.version);

         if (!data.itemTypes.isEmpty())
         {
            List<String> types = data.itemTypes.stream()
                  .map(GetItemQueryPageCommand::encode)
                  .collect(toList());
            target = target.queryParam("itemType=", String.join(" || ", types));
         }

         if (data.q != null && !data.q.trim().isEmpty())
         {
            target = target.queryParam("q", data)
                           .queryParam("qmode", data.qmode.toString());
         }

         if (data.tagFilter != null)
            target = data.tagFilter.apply(target);

         if (data.sortBy != null)
         {
            target = target.queryParam("sort", data.sortBy.toString());
            if (data.sortDir != null)
               target = target.queryParam("direction", data.sortDir.toString());
         }

         return target;
      }

      private static String encode(String tag)
      {
         try
         {
            return URLEncoder.encode(tag, "UTF-8");
         }
         catch (UnsupportedEncodingException e)
         {
            throw new IllegalStateException("Cannot find UTF-8 charset.", e);
         }
      }

   }