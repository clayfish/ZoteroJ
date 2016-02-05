package edu.tamu.tcat.zotero.basic.v3;

import java.net.URI;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

import edu.tamu.tcat.zotero.ZoteroGroup;

public class BasicZoteroGroup implements ZoteroGroup
{
   private final int id;
   private final int version;
   private final int numItems;
   private final Instant modified;
   private final Instant created;
   private final String description;
   private final String name;
   private final URI url;
   private int ownerId;

   public static class Context
   {

   }


   public BasicZoteroGroup(RestApiV3.ZoteroGroupLibrary group)
   {
      this.id = group.id;
      this.version = group.version;
      this.numItems = group.meta.numItems;

      this.created = parseDateSafely(group.meta.created);
      this.modified = parseDateSafely(group.meta.lastModified);

      this.name = group.data.name;
      this.description = group.data.description;
      this.url = URI.create(group.data.url);
      this.ownerId = group.data.owner;
   }

   private static Instant parseDateSafely(String timestamp)
   {
      if (timestamp == null || timestamp.trim().isEmpty())
         return null;

      try
      {
         return DateTimeFormatter.ISO_INSTANT.parse(timestamp, Instant::from);
      }
      catch (Exception ex)
      {
         // TODO log
         return null;
      }
   }

   private static URI parseUriSafely(String uri)
   {
      if (uri == null || uri.trim().isEmpty())
         return null;

      try
      {
         return URI.create(uri);
      }
      catch (Exception ex)
      {
         // TODO log
         return null;
      }
   }

   @Override
   public int getId()
   {
      return id;
   }

   @Override
   public int getVersion()
   {
      return version;
   }

   @Override
   public int getNumberOfItems()
   {
      return numItems;
   }

   @Override
   public Instant getCreated()
   {
      return created;
   }

   @Override
   public Instant getLastModified()
   {
      return modified;
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public String getDescription()
   {
      return description;
   }

   @Override
   public URI getWebsite()
   {
      return url;
   }

   @Override
   public int getOwnerId()
   {
      return ownerId;
   }
}
