package edu.tamu.tcat.zotero;

import edu.tamu.tcat.zotero.types.ItemFieldType;
import edu.tamu.tcat.zotero.types.ItemType;

/**
 *  An individual or organization that play a role in the creation of a bibliographic item, for
 *  example, an author, editor, director, or contributor.
 */
public interface ItemCreator
{
   /**
    * @return The role this individual played in the creation of a work. Typical roles
    *       associated with a particular type of bibliographic item are defined by
    *       the {@link ItemType}.
    */
   String getRole();

   /**
    * @return A single-valued name for this creator. Some creators such as corporations are
    *       referenced by a single name rather than a given/family name pair.
    */
   String getName();

   /**
    * @return The given or first name of this creator.
    */
   String getGivenName();

   /**
    * @return The family or last name of this creator. More formally, this is the name by which
    *       this individual will be sorted in lists of bibliographic citations. Note that this
    *       may be null or the empty string if a single-valued name is used (e.g., for a
    *       corporation).
    */
   String getFamilyName();

   /**
    * Constructs an {@link ItemCreator} with the supplied role and name. Intended for
    * institutional creators or others whose name cannot be separated into first and last parts.
    *
    * @param role The role of this creator. Expected to be a value returned by
    *       {@link ItemType#getCreatorRoles()}. The id of the supplied role will be used as the
    *       role of the creator.
    * @param name The name of this creator.
    *
    * @return An {@link ItemCreator} instance.
    */
   static ItemCreator create(ItemFieldType role, String name)
   {
      return new SimpleItemCreator(role, name);
   }

   /**
    * Constructs an {@link ItemCreator} with the supplied role and name.
    *
    * @param role The role of this creator. Expected to be a value returned by
    *       {@link ItemType#getCreatorRoles()}. The id of the supplied role will be used as the
    *       role of the creator.
    * @param given The given, or first, name of the creator.
    * @param family the family, or last, name of the creator.
    *
    * @return An {@link ItemCreator} instance.
    */
   static ItemCreator create(ItemFieldType role, String given, String family)
   {
      return new SimpleItemCreator(role, given, family);
   }

}
