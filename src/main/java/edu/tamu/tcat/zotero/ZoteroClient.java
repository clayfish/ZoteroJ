package edu.tamu.tcat.zotero;

import edu.tamu.tcat.zotero.types.ItemTypeProvider;

/**
 *  The ZoteroClient is the primary point of access for interfacing with Zotero user accounts,
 *  groups and libraries.
 *
 *  <p>Applications are responsible for managing
 *
 *  FIXME this JavaDoc is out of date.
 *
 *  The main entry point for working with bibliographic items and other resources from
 *  Zotero ({@link http://zotero.org}. A {@code ZoteroClient} is tied to a particular user's
 *  account credentials and provides access to the Zotero server via its REST API.
 *
 *  <p>This interface provides a blocking API to access the remote server. In many common
 *  scenarios a blocking API will be the most convenient and easiest to use. However, given the
 *  nature of accessing a server over REST, the methods of this client may take an exceptionally
 *  long time to complete. Notably, this should not be used in the display thread. In some
 *  circumstances, the non-blocking API provided by
 *
 */
public interface ZoteroClient extends AutoCloseable
{
   /**
    * Loads a user account.
    *
    * @param userId The id for the user whose account should be returned.
    * @param authToken A valid authentication token for this user. Tokens are typically
    *    obtained using OAuth and associated with a user's application account. See
    *    {@link https://www.zotero.org/support/dev/web_api/v3/oauth} for details. Alternatively,
    *    once a user has logged into Zotero, their user id and API keys can be found at
    *    {@link https://www.zotero.org/settings/keys}.
    *
    * @return
    * @throws ZoteroRestException If the requested user does not exist or cannot be
    *    authenticated.
    * @throws IllegalStateException
    */
   ZoteroAccount getUserAccount(String userId, String authToken) throws ZoteroRestException, IllegalStateException;

   /**
    * @return Loads the {@link ItemTypeProvider} that defines the
    */
   ItemTypeProvider getTypeProvider();
}
