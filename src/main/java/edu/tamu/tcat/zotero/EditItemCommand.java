package edu.tamu.tcat.zotero;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import edu.tamu.tcat.zotero.types.ItemFieldType;
import edu.tamu.tcat.zotero.types.ItemType;

/**
 *  Edits a new or existing bibliographic item. See {@link ZoteroLibrary#createItem()} and
 *  {@link ZoteroLibrary#editItem(Item)}. All changes made via this command take effect
 *  only upon the invocation of the {@link #execute()} method. The command may be freely
 *  abandoned until this method has been called.
 */
public interface EditItemCommand
{
   // TODO check for errors prior to execution.

   /**
    * @param item The type of item to be created. Zotero defines a set of bibliographic items
    *       that may be used in all libraries. These item types define the various fields that
    *       may be set for a bibliographic item. This value must be supplied.
    *
    * @return A reference to this edit command
    */
   EditItemCommand setItemType(ItemType item);

   /**
    * @param ids An array of ids that the item is a part of.
    * @return A reference to this edit command
    */
   EditItemCommand setCollections(String...ids);

   /**
    * @param tags A set of tags a range of tags for this item.
    * @return A reference to this edit command
    */
   EditItemCommand setTags(String...tags);

   /**
    * Sets the people or institutions who authored or otherwise contributed to the creation of
    * this item.
    *
    * Note that types of creator roles (e.g., author, contributor, editor) types for a
    * particular type of bibliographical item are defined by the {@link ItemType} definition.
    * The creators supplied to this method are not restricted to the roles defined by the
    * associated type, however, formatting of creators with custom types may not be well
    * supported.
    *
    * @param creators The creators of this work
    * @return A reference to this edit command
    */
   EditItemCommand setCreators(List<ItemCreator> creators);

   /**
    * Sets the bibliographic properties of this item. Valid fields are defined by the
    * {@link ItemType} associated with this entry. Note that, when editing an existing item,
    * this will update all fields supplied in the associated map, leaving existing values for
    * all other fields unmodified.
    *
    * @param fields The bibliographic properties of this item.
    *
    * @return A reference to this edit command
    * @throws IllegalArgumentException If one of the supplied field types is not defined for
    *       this type of bibliographic item.
    */
   EditItemCommand setFields(Map<ItemFieldType, String> fields) throws IllegalArgumentException;

   /**
    *
    * @param field The field whose value should be set.
    * @param value The value to be set.
    * @return A reference to this edit command
    * @throws IllegalArgumentException If the supplied field is not defined for this type
    *       of bibliographic item.
    */
   EditItemCommand setField(ItemFieldType field, String value) throws IllegalArgumentException;

   /**
    * Clears the value of a field associated with this item.
    *
    * @param field the field to be cleared.
    * @return A reference to this edit command
    */
   EditItemCommand clearField(ItemFieldType field);

   /**
    * @return Returns the future id of the newly created {@code Item}.
    * @throws IllegalStateException
    * @throws ZoteroRestException
    */
   Future<Item> execute();
}
