package edu.tamu.tcat.zotero.search;

import edu.tamu.tcat.zotero.ItemSet;
import edu.tamu.tcat.zotero.ZoteroRestException;

public interface ExecutableItemQuery extends ItemQuery
{
   ItemSet execute() throws ZoteroRestException;
}
