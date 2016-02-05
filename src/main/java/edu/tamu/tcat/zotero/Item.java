package edu.tamu.tcat.zotero;

import java.util.List;
import java.util.Set;

import edu.tamu.tcat.zotero.types.ItemFieldType;
import edu.tamu.tcat.zotero.types.ItemType;

public interface Item
{
   /**
    * @return The unique id of this item
    */
   String getId();

   /**
    * Returns the version of this item. Items are versioned by Zotero using monotonically
    * increasing integer ids. In general, content returned by an instance may be queried
    * directly from the remote Zotero server. Typically, item implementations will retrieve
    * data from the server on demand and cache that data as needed. Consequently, the data
    * returned by a method (such as {@link #getItems()}) may reflect a later state of the
    * item than the version returned by this method.
    *
    * TODO is this correct?
    *
    * @return The version of this item.
    */
   int getVersion();

   /**
    * @return The declared type of this item. Zotero items can be an attachment, boot, or an article.
    *    Each type will have a preset of defined fields
    */
   ItemType getItemType();

   /**
    * @return A list of people or organizations who played a role in the creation of this item.
    */
   List<ItemCreator> getCreators();

   /**
    * @param field The field whose value is returned. Must be one of the fields defined for
    *       this type of bibliographic item {@link ItemType#getFields()}.
    * @return The value of the identified field. May be {@code null} or the empty string
    *       depending on the value set for this field.
    * @throws IllegalArgumentException If the supplied field is not defined for this type of
    *       bibliographic item.
    */
   String getFieldValue(ItemFieldType field) throws IllegalArgumentException;

   /**
    * TODO it is unclear what this is. Zotero seems to use a parent/child notation for
    *      attachments and comments. These however are semantically different structures and
    *      should be represented as such within our API.
    *
    * @return The parent of this item. May be {@code null} if this item does not have a parent.
    * @throws ZoteroRestException
    */
   Item getParent() throws ZoteroRestException;

   /**
    * @return An {@code ItemSet} containing all items that are children to this item. May
    *    be empty.
    */
   ItemSet getChildren();

   /**
    * @return A list of all collections that this item is a part of. May be an empty list,
    *    will not be {@code null}.
    */
   List<ZoteroCollection> getCollections();

   /**
    * @return A set of all tags used for this item.
    */
   Set<String> getTags();
}
