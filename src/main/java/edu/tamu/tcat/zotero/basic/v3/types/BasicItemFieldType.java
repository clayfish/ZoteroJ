package edu.tamu.tcat.zotero.basic.v3.types;

import java.util.Objects;

import edu.tamu.tcat.zotero.basic.v3.RestApiV3;
import edu.tamu.tcat.zotero.types.ItemFieldType;

public class BasicItemFieldType implements ItemFieldType
{
   private final String id;
   private final String label;

   public BasicItemFieldType(String id, String label)
   {
      this.id = id;
      this.label = label;
   }

   public static BasicItemFieldType adapt(RestApiV3.ItemTypeField dto)
   {
      return new BasicItemFieldType(dto.field, dto.localized);
   }

   @Override
   public String getId()
   {
      return this.id;
   }

   @Override
   public String getLabel()
   {
      return this.label;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (!ItemFieldType.class.isInstance(obj))
         return false;
      
      ItemFieldType field = (ItemFieldType)obj;
      
      return Objects.equals(id, field.getId());
   }
   
   @Override
   public int hashCode()
   {
      return id.hashCode();
   }
}
