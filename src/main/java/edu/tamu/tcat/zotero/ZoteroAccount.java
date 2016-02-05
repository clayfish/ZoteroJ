package edu.tamu.tcat.zotero;

import java.util.Set;

public interface ZoteroAccount
{
   // TODO provide mechanism to define an load account credentials.

   /**
    * @return The unique id for this account.
    */
   String getId();

   /**
    * Returns the library associated with this account. Note that in Zotero, each
    * user's account is associated with exactly one library of bibliographic items.
    * Users may also create or join groups if they wish to have additional collections.
    * These groups may have multiple members and support different access policies.
    *
    * @return The library associated with this account.
    */
   ZoteroLibrary getUserLibrary();

   Set<ZoteroGroup> getGroups();

}
