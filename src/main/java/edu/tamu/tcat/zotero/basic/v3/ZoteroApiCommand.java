package edu.tamu.tcat.zotero.basic.v3;

import java.util.concurrent.Future;

/**
 *  Calls to the Zotero REST API are issued via implementations of this interface.
 *
 *  @param <T> The type of response data to be returned by the Zotero API
 */
public interface ZoteroApiCommand<T>
{

   Future<T> execute() throws IllegalStateException;

}
