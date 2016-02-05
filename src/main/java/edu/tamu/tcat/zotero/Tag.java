package edu.tamu.tcat.zotero;

/**
 *  Structured representation of a tag. In general, tags may be referred to simply by their
 *  name, however various components of the API return additional information that is
 *  captured by this data type.
 *
 */
@Deprecated // probably need to simplify this and just use a string.
public interface Tag
{
   String getName();

   int getType();       // replace with enum

   int getNumItems();
}
