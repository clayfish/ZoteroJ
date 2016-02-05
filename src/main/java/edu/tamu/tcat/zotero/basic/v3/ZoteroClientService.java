package edu.tamu.tcat.zotero.basic.v3;

import java.util.Objects;

import edu.tamu.tcat.zotero.ZoteroAccount;
import edu.tamu.tcat.zotero.ZoteroClient;
import edu.tamu.tcat.zotero.ZoteroRestException;
import edu.tamu.tcat.zotero.basic.v3.commands.ZoteroCommandExecutor;
import edu.tamu.tcat.zotero.basic.v3.types.ItemTypeProviderService;
import edu.tamu.tcat.zotero.types.ItemTypeProvider;

public class ZoteroClientService implements ZoteroClient
{
   private ZoteroCommandExecutor exec;

   private ItemTypeProvider typeProvider;

   public ZoteroClientService()
   {
      exec = new ZoteroCommandExecutor();
   }

   public void bind(ZoteroCommandExecutor exec)
   {
      this.exec = exec;
   }

   public void activate()
   {
      Objects.requireNonNull(exec, "No command executor is available");

      typeProvider = new ItemTypeProviderService(exec);
   }

   @Override
   public void close()
   {

   }

   public ZoteroCommandExecutor getExecutor()
   {
      return exec;
   }

   public ZoteroAccount getAnonymousAcount()
   {
      return new BasicUserAccount(Integer.toString(-1), null, this);
   }

   public ZoteroAccount getUnauthenticatedAccount(String userId)
   {
      return new BasicUserAccount(userId, null, this);
   }

   /**
    * @implNote This does not provide any validation of the supplied account or attendant
    *       library.
    */
   @Override
   public ZoteroAccount getUserAccount(String userId, String authToken)
         throws ZoteroRestException, IllegalStateException
   {
      // TODO provide local caching of results
      return new BasicUserAccount(userId, authToken, this);
   }

   @Override
   public ItemTypeProvider getTypeProvider()
   {
      return typeProvider;
   }

}
