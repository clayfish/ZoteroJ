package edu.tamu.tcat.zotero;

import java.util.concurrent.Future;

/**
 *  A command for use in creating a new collection or editing an existing collection within
 *  a ZoteroLibrary.
 */
public interface EditCollectionCommand
{
   /**
    * @param name The name of the collection. This value must be supplied.
    * @return A reference to this edit command
    */
   EditCollectionCommand setName(String name);

   /**
    * Sets the parent of this collection for hierarchically structured collections. Collections
    * for which no parent is set will be attached to the root of the library.
    *
    * @param collection The parent of this collection
    * @return A reference to this edit command
    */
   EditCollectionCommand setParent(ZoteroCollection collection);

   /**
    * @return A future that resolves to the id of the new or updated collection once the
    *       edits have been fully executed. The supplied future will propagate any exceptions
    *       that occurred during executions as either {@link ZoteroRestException} (for
    *       authentication, authorization or other preventable errors updating the remote
    *       library) or {@link IllegalStateException} (for network access and other errors that
    *       are outside the control of the client application).
    */
   Future<ZoteroCollection> execute();
}
