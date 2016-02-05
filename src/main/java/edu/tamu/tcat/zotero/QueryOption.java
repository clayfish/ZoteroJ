package edu.tamu.tcat.zotero;

public enum QueryOption
{
   /** Retrieve all collections or items within a given parent context. This will recursively
    *  descend sub-collections. */
   ALL,

   /** Retrieve only the top-level collections or items within a given parent context. */
   TOP,

   /** Retrieve children of this collection or item. For collections, this
    *  will return sub-collections. */
   CHILDREN;
}
