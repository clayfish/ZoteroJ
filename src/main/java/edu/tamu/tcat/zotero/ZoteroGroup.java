package edu.tamu.tcat.zotero;

import java.net.URI;
import java.time.Instant;

/**
 * Represents information about a Zotero group.
 */
public interface ZoteroGroup
{
   /**
    * @return Zotero's internal identifier for this library.
    */
   int getId();

   /**
    * @return The version number for the last time this library was updated.
    */
   int getVersion();

   /**
    * @return The number of bibliographic items within the library. May be less than <code>0</code>
    *       if the number of items cannot be determined.
    */
   int getNumberOfItems();

   /**
    * @return The date this library was created.
    */
   Instant getCreated();

   /**
    * @return The date this library was last modified.
    */
   Instant getLastModified();

   /**
    * @return The name of this library, suitable for display.
    */
   String getName();

   /**
    * @return A brief description of this library. May be an empty string, will not be
    *       {@code null}.
    */
   String getDescription();

   /**
    * @return The URL of a website linked to this library. This is a user-supplied website
    *    rather than one affiliated with the Zotero library. May be {@code null} if no value
    *    has been supplied. Alternatively, implementations may elect to return the URL of the
    *    Zotero page associated with this account.
    */
   URI getWebsite();

   /**
    * @return The Zotero id of the user account that owns this library.
    */
   int getOwnerId();
}
