package edu.tamu.tcat.zotero.basic.v3.commands;

import edu.tamu.tcat.zotero.ZoteroRestException;

public class ZoteroResponseException extends ZoteroRestException
{

   public ZoteroResponseException(String string)
   {
      super(string);
   }

}
