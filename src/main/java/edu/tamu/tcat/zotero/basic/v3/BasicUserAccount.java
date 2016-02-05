package edu.tamu.tcat.zotero.basic.v3;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.client.Invocation;

import edu.tamu.tcat.zotero.ZoteroAccount;
import edu.tamu.tcat.zotero.ZoteroGroup;
import edu.tamu.tcat.zotero.ZoteroLibrary;
import edu.tamu.tcat.zotero.ZoteroRestException;
import edu.tamu.tcat.zotero.basic.v3.RestApiV3.ZoteroGroupLibrary;
import edu.tamu.tcat.zotero.basic.v3.commands.GetAssociatedGroupsCommand;
import edu.tamu.tcat.zotero.basic.v3.commands.ZoteroCommandExecutor;
import edu.tamu.tcat.zotero.types.ItemTypeProvider;

public class BasicUserAccount implements ZoteroAccount
{
   private final String id;
   private final String authToken;
   private final ZoteroClientService client;

   public BasicUserAccount(String id, String token, ZoteroClientService client)
   {
      this.id = id;
      this.authToken = token;
      this.client = client;
   }

   @Override
   public String getId()
   {
      return id;
   }

   @Override
   public ZoteroLibrary getUserLibrary()
   {
      return new BasicZoteroLibrary(this);
   }

   @Override
   public Set<ZoteroGroup> getGroups()
   {
      // TODO provide in-memory caching and retrieve only those groups that have been modified since the latest update.
      GetAssociatedGroupsCommand cmd = new GetAssociatedGroupsCommand(this);
      try
      {
         List<ZoteroGroupLibrary> groupData = client.getExecutor().unwrap(cmd.execute(), () -> "Failed to retrieve groups for account " + id);
         return groupData.stream().map(BasicZoteroGroup::new).collect(Collectors.toSet());

      }
      catch (ZoteroRestException e)
      {
         throw new IllegalStateException("Failed to retrieve groups. This is likely due to a bad account id or invalid authentication credentials.", e);
      }
   }

   public ZoteroLibrary getLibrary(ZoteroGroup group)
   {
      return new BasicZoteroLibrary(this, group);
   }

   /**
    * Intended for internal use only.
    * @return
    */
   public ZoteroCommandExecutor getExecutor()
   {
      return client.getExecutor();
   }

   public ItemTypeProvider getItemTypeProvider()
   {
      return client.getTypeProvider();
   }

   /**
    * Indicates whether an authentication token is available for this account. This does not
    * check the validity of that token or provide other guarantees about whether that token
    * has been revoked or if the associated user has explicitly logged into the system during
    * a particular time-span.
    *
    * @return <code>true</code> if an authentication token is available.
    */
   public boolean hasAuthenticationToken()
   {
      return authToken != null;
   }

   /**
    * Appends headers to support Zotero authorization bearer token, if available for this
    * account. If not authentication header is available, the account can still be used to
    * access publicly visible libraries and other resources that do not require authentication.
    * All requests that require authentication, however, will fail with a
    * {@link ZoteroRestException}.
    *
    * @param builder The invocation builder being used to construct the HTTP request.
    * @return The supplied invocation builder with any required header information. Note that
    *       this may return a different instance than the supplied builder. Callers should use
    *       the returned builder for all subsequent calls.
    */
   public Invocation.Builder authenticate(Invocation.Builder builder)
   {
      return (authToken != null) ? builder.header("Authorization", "Bearer " + authToken) : builder;
   }
}
