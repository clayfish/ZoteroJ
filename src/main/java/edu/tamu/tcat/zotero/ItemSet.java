package edu.tamu.tcat.zotero;

import edu.tamu.tcat.zotero.search.ItemQuery;

public interface ItemSet extends Iterable<Item>
{

   int size();

   Item get(int ix) throws IndexOutOfBoundsException;

   ItemQuery getQuery();


}
