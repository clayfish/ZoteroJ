package edu.tamu.tcat.zotero.types;

/**
 *  Defines a single unit of bibliographic information, such as a title, publication date
 *  or journal.
 *
 *  <p>
 *  Note both general bibliographic fields and creator roles are represented by this class.
 *  In general, authors are associated with a specific role and require special handling to
 *  represent and format their names as required by a particular citation style.
 * 
 *  <p>
 *  ItemFieldTypes are intended to be used as keys and maps and other structures. Implementations
 *  must be immutable and override {@link Object#equals(Object)} and {@link Object#hashCode()}.
 */
public interface ItemFieldType
{

   /**
    * @return An identifier for this field type.
    */
   String getId();

   /**
    * @return The name of this field type, suitable for display.
    */
   String getLabel();

}
