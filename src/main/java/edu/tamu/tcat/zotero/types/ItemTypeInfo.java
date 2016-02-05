package edu.tamu.tcat.zotero.types;

/**
 * Provides basic information about an {@link ItemType} such as a Book, Article or
 * Conference Paper.
 */
public interface ItemTypeInfo
{

   /**
    * @return The unique identifier for this type.
    */
   String getId();

   /**
    * @return A label for display.
    */
   String getLabel();
}
