package edu.tamu.tcat.zotero;

/**
 * Thrown to indicate that an error response was returned by the Zotero REST API. Typically
 * this will be due to a lack of user access permissions or a resource that cannot be found.
 */
public class ZoteroRestException extends Exception
{

   public ZoteroRestException(String string, Throwable e)
   {
      super(string, e);
   }

   public ZoteroRestException(String string)
   {
      super(string, null);
   }


}
