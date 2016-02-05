package edu.tamu.tcat.zotero;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.tamu.tcat.zotero.basic.v3.RestApiV3;
import edu.tamu.tcat.zotero.basic.v3.ZoteroClientService;
import edu.tamu.tcat.zotero.basic.v3.ZoteroUserCredentials;
import edu.tamu.tcat.zotero.basic.v3.commands.GetCollectionsCommand;
import edu.tamu.tcat.zotero.basic.v3.commands.ZoteroCommandExecutor;

public class ZoteroApiCommandTests
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

   @Test
   public void basicConnectionTest() throws IllegalStateException, ZoteroRestException
   {
      String itemId = "GQFZM88S";

      ZoteroClientService service = new ZoteroClientService();
      service.bind(executor);
      service.activate();

      ZoteroClient client = service;
      ZoteroAccount account = client.getUserAccount(credentials.getUserId(), credentials.getToken());
      ZoteroLibrary library = account.getUserLibrary();
      try
      {
         Item item = library.getItem(itemId);
         Set<String> tags = item.getTags();
         tags.forEach((t)->{
            System.out.println(t);
         });

      } finally {
         service.close();
      }
   }

   @Test
   public void basicCollectionTest() throws ZoteroRestException
   {
      String collectionId = "EK6F98V3";
      ZoteroClientService service = new ZoteroClientService();
      service.bind(executor);
      service.activate();

      ZoteroClient client = service;
      ZoteroAccount account = client.getUserAccount(credentials.getUserId(), credentials.getToken());
      ZoteroLibrary library = account.getUserLibrary();
      try
      {
         ZoteroCollection collection = library.getCollection(collectionId);
         String id = collection.getId();
      } finally {
         service.close();
      }
//      GetCollectionCommand command = executor.createCommand(GetCollectionCommand.class, credentials);
//      command.setCollectionId(collectionId);
//
//      Future<RestApiV3.Collection> result = command.execute();
//
//      RestApiV3.Collection item = result.get();


//      System.out.println(item.key);
   }

   @SuppressWarnings("deprecation")
   @Test
   public void basicCollectionsTest() throws InterruptedException, ExecutionException
   {
      // TODO create well-defined Zotero data set to test
      // TODO test error conditions
      GetCollectionsCommand allCollectionsCmd = executor.createCommand(GetCollectionsCommand.class, credentials);
      Future<RestApiV3.CollectionList> allCollectionsRslts = allCollectionsCmd.execute();
      RestApiV3.CollectionList allCollections = allCollectionsRslts.get();

      GetCollectionsCommand topCollectionsCmd = executor.createCommand(GetCollectionsCommand.class, credentials);
      topCollectionsCmd.setRecursive(false);
      Future<RestApiV3.CollectionList> topCollectionsRslts = topCollectionsCmd.execute();
      RestApiV3.CollectionList topCollections = topCollectionsRslts.get();

      String collectionId = "NTF5FGZC";
      GetCollectionsCommand collectionsByIdCmd = executor.createCommand(GetCollectionsCommand.class, credentials);
      collectionsByIdCmd.setParentCollection(collectionId);
      Future<RestApiV3.CollectionList> collectionsByIdRslts = collectionsByIdCmd.execute();
      RestApiV3.CollectionList collectionsById = collectionsByIdRslts.get();

   }

   @Test
   public void basicItemsTest() throws InterruptedException, ExecutionException
   {
      String collectionId = "NTF5FGZC";
      String itemId = "DBIAH9ST";


//      GetItemsCommand allItemsCmd = executor.createCommand(GetItemsCommand.class, credentials);
//      Future<List<RestApiV3.Item>> allItemResults = allItemsCmd.execute();
//      List<RestApiV3.Item> allItems = allItemResults.get();
//
//      GetItemsCommand allTopItemsCmd = executor.createCommand(GetItemsCommand.class, credentials);
//      allTopItemsCmd.setItemsTop();
//      Future<List<RestApiV3.Item>> topItemsResults = allTopItemsCmd.execute();
//      List<RestApiV3.Item> topItems = topItemsResults.get();
//
//      GetItemsCommand allItemChildrenCmd = executor.createCommand(GetItemsCommand.class, credentials);
//      allItemChildrenCmd.setItemsId(itemId);
//      Future<List<RestApiV3.Item>> itemChildrenRslts = allItemChildrenCmd.execute();
//      List<RestApiV3.Item> childItems = itemChildrenRslts.get();
//
//      GetItemsCommand allCollectionItemsCmd = executor.createCommand(GetItemsCommand.class, credentials);
//      allCollectionItemsCmd.setColledtionItems(collectionId);
//      Future<List<RestApiV3.Item>> collectionItemsResults = allCollectionItemsCmd.execute();
//      List<RestApiV3.Item> collectionItems = collectionItemsResults.get();
//
//      GetItemsCommand allCollTopItemsCmd = executor.createCommand(GetItemsCommand.class, credentials);
//      allCollTopItemsCmd.setColledtionTopItems(collectionId);
//      Future<List<RestApiV3.Item>> collTopResults = allCollTopItemsCmd.execute();
//      List<RestApiV3.Item> collectionTopItems = collTopResults.get();
//      System.out.println(allItems);
   }

   public static class SimpleCredentials implements ZoteroUserCredentials
   {
      private final String token;
      private final String id;

      public SimpleCredentials(String id, String token)
      {
         this.id = id;
         this.token = token;
      }

      @Override
      public String getUserId()
      {
         return id;
      }

      @Override
      public String getToken()
      {
         return token;
      }


   }

}
