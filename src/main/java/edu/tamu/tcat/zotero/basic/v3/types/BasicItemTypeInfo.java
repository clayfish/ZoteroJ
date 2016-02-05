package edu.tamu.tcat.zotero.basic.v3.types;

import edu.tamu.tcat.zotero.types.ItemTypeInfo;

public class BasicItemTypeInfo implements ItemTypeInfo
{
   private final String typeId;
   private final String label;

   public BasicItemTypeInfo(String id, String label)
   {
      this.typeId = id;
      this.label = label;
   }

   @Override
   public String getId()
   {
      return this.typeId;
   }

   @Override
   public String getLabel()
   {
      return this.label;
   }
}
