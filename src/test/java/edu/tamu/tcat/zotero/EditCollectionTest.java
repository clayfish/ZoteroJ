package edu.tamu.tcat.zotero;

import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.tamu.tcat.zotero.EditCollectionCommand;
import edu.tamu.tcat.zotero.ZoteroCollection;
import edu.tamu.tcat.zotero.ZoteroLibrary;
import edu.tamu.tcat.zotero.basic.v3.BasicUserAccount;
import edu.tamu.tcat.zotero.basic.v3.ZoteroClientService;

public class EditCollectionTest
{

   private ZoteroClientService client;
   private BasicUserAccount account;
   private ZoteroLibrary userLibrary;
   private final String PRIMARY_NAME = "Marvel Comics";
   private final String SECONDARY_NAME = "Marvel Universe";
   private final String PARENT_NAME = "Super Hero Comics";
   
   @Before
   public void setup()
   {
      client = new ZoteroClientService();
      client.activate();
      account = new BasicUserAccount("2906896", "LrWNAwauPiHHJ8369h3aUylA", client);
      userLibrary = account.getUserLibrary();
   }
   
   @After
   public void tearDown()
   {
      client.close();
   }
   
   @Test
   public void createCollection() throws Exception
   {
      ZoteroCollection collection = create(PRIMARY_NAME);
      
      Assert.assertEquals(PRIMARY_NAME, collection.getName());
      delete(collection.getId());
   }
   
   @Test
   public void editCollection() throws Exception
   {
      
      ZoteroCollection parentColl = create(PARENT_NAME);
      ZoteroCollection childColl = create(PRIMARY_NAME);
      
      
      ZoteroCollection updatedChild = update(parentColl, childColl);
      Assert.assertEquals(childColl.getId(), updatedChild.getId());
      Assert.assertFalse(childColl.getName().equals(updatedChild.getName()));
      Assert.assertEquals(updatedChild.getParent().getId(), parentColl.getId());
      Assert.assertNotEquals(updatedChild.getVersion(), childColl.getVersion());
      
      delete(new String[]{parentColl.getId(), childColl.getId()});
   }
   
   @Test
   public void removeCollection() throws Exception
   {
      ZoteroCollection collection = create(PRIMARY_NAME);
      
      Assert.assertEquals(PRIMARY_NAME, collection.getName());
      delete(collection.getId());
      
      // TODO: The delete above does not seem to complete prior to this next call to retreive 
      //       the same id.
//      try
//      {
//         ZoteroCollection removedColl = userLibrary.getCollection(collection.getId());
//         Assert.fail("excpetion was not thrown");
//      }
//      catch(Exception e)
//      {
//      }
   }
   
   private ZoteroCollection create(String name) throws InterruptedException, ExecutionException
   {
      EditCollectionCommand newCollection = userLibrary.createCollection();
      newCollection.setName(name);
      
      return newCollection.execute().get();
   }
   
   private ZoteroCollection update(ZoteroCollection parent, ZoteroCollection child) throws Exception
   {
      EditCollectionCommand editCollection = userLibrary.editCollection(child);
      editCollection.setName(SECONDARY_NAME)
                    .setParent(parent);
      
      return editCollection.execute().get();
      
   }
   
   private void delete(String... keys) throws Exception
   {
      ZoteroCollection[] collections = new ZoteroCollection[2];
      for (int k=0; k<keys.length; k++)
      {
         if(keys[k] != null)
            collections[k] = userLibrary.getCollection(keys[k]);
      }
      
      userLibrary.deleteCollections(collections);
   }
}
