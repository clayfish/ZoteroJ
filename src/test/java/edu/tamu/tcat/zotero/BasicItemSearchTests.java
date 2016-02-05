package edu.tamu.tcat.zotero;

import java.net.URI;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.tamu.tcat.zotero.ZoteroApiCommandTests.SimpleCredentials;
import edu.tamu.tcat.zotero.basic.v3.ZoteroClientService;
import edu.tamu.tcat.zotero.basic.v3.ZoteroUserCredentials;
import edu.tamu.tcat.zotero.basic.v3.commands.ZoteroCommandExecutor;
import edu.tamu.tcat.zotero.search.ItemQueryBuilder;

public class BasicItemSearchTests
{

   private ZoteroCommandExecutor executor;
   private ZoteroUserCredentials credentials;

   @Before
   public void setup()
   {
      URI rootUri = URI.create("https://api.zotero.org");
      executor = new ZoteroCommandExecutor(rootUri, 5);

      credentials = new SimpleCredentials("userId", "token");
   }

   @After
   public void tearDown()
   {
      executor.close();
   }

   public void testSearchAll()
   {

   }

   public static ZoteroClient initClient(URI apiEndpoint)
   {
       URI rootUri = URI.create("https://api.zotero.org");
       ZoteroCommandExecutor executor = new ZoteroCommandExecutor(rootUri, 5);

       ZoteroClientService service = new ZoteroClientService();
       service.bind(executor);
       service.activate();

       return service;
   }
   @Test
   public void testBasicCollectionSearch() throws InterruptedException, ExecutionException, ZoteroRestException
   {
      String cId = "NTF5FGZC";

      ZoteroClientService service = new ZoteroClientService();
      service.bind(executor);
      service.activate();

      ZoteroClient client = service;
      ZoteroAccount account = client.getUserAccount(credentials.getUserId(), credentials.getToken());
      ZoteroLibrary library = account.getUserLibrary();
      try
      {
         ZoteroCollection collection = library.getCollection(cId);

         library.makeItemQueryBuilder();
         ItemQueryBuilder builder = library.makeItemQueryBuilder();
         ItemSet items = builder.searchWithin(collection)
               .build()
               .execute();
      } finally {
         service.close();
      }
   }

   public void testCollectionDescendantSearch()
   {

   }

   public void testBasicTagSearch()
   {

   }

   public void testExcludeTagSearch()
   {

   }

   public void testOrTagSearch()
   {

   }

   public void testAndTagSearch()
   {

   }

   public void testAuthorNameSearch()
   {

   }

   public void testTitleSearch()
   {

   }
}
