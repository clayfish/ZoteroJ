package edu.tamu.tcat.zotero.basic.v3.editor;

/**
 *  BasicRelations are a required object to be sent to the Zotero API, when creating an item.
 *  Currently the only way to create a relation is using the Zotero desktop software.
 *
 *  https://www.zotero.org/support/related - In addition to collections and tags, a third way
 *  to express relationships between items is by setting up “relations”. Relations can set up
 *  between any pair of items in a library (it is not possible to relate items from different
 *  libraries).
 *
 *  Example
 *  "relations" : {
 *                 "owl:sameAs" : "http://zotero.org/groups/1/items/JKLM6543",
 *                 "dc:relation" : "http://zotero.org/groups/1/items/PQRS6789",
 *                 "dc:replaces" : "http://zotero.org/users/1/items/BCDE5432"
 *                }
 *
 */
public class BasicRelations
{
   // TODO: Relations will be added later on.
}
