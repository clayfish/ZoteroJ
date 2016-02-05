package edu.tamu.tcat.zotero.basic.v3;

/**
 *  User account authorization tokens for executing requests against the Zotero REST API.
 */
public interface ZoteroUserCredentials
{

   /**
    * @return The id of the authenticated user.
    */
   String getUserId();

   /**
    * @return An authorization token associated with this user account. Tokens will typically
    *    be provided using an OAuth-based login, although there are other mechanisms for
    *    creating a token.
    */
   String getToken();
}
