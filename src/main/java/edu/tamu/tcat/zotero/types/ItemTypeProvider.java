package edu.tamu.tcat.zotero.types;

import java.util.Set;

import edu.tamu.tcat.zotero.ZoteroRestException;

/**
 *  Provides access to a set of {@link ItemType}s that will be used to define the fields
 *  associated with bibliographic items of various types. In general, bibliographic items
 *  can be of many different types (e.g., Books, Journal Articles, Blog Entries, Films, etc).
 *  Bibliographic information about these items is structured by an ordered set of fields. For
 *  example, a Film might have information about it's running time while a journal article may
 *  supply the name of journal it was published in.
 *
 *  The {@code ItemTypeProvider} is intended to define the bibliographic schema used by a
 *  particular reference management database (e.g., EndNote, Mendely, etc).
 */
public interface ItemTypeProvider
{
   /**
    * @return All bibliographic item types defined by this provider.
    * @throws ZoteroRestException
    */
   Set<ItemTypeInfo> getItemTypes() throws ZoteroRestException;

   /**
    * @param typeId The id of the type to return.
    * @return The identified bibliographic item type.
    * @throws IllegalArgumentException If the requested item type is not defined by this
    *       provider.
    */
   ItemType getItemType(ItemTypeInfo typeId) throws ZoteroRestException, IllegalArgumentException;

   /**
    * @param typeId The id of the type to return.
    * @return The identified bibliographic item type.
    * @throws IllegalArgumentException If the requested item type is not defined by this
    *       provider.
    */
   default ItemType getItemType(String typeId) throws ZoteroRestException, IllegalArgumentException
   {
      // HACK: seems backward. we really need the type id inside here to do the lookup. No need to
      //       convert to full type info instance
      ItemTypeInfo type = getItemTypes().stream()
            .filter(info -> info.getId().equalsIgnoreCase(typeId))
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("No item type defined for '" + typeId + "'"));

      return this.getItemType(type);
   }

}
