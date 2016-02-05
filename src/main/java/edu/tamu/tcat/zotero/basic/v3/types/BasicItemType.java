package edu.tamu.tcat.zotero.basic.v3.types;

import java.util.Collections;
import java.util.List;

import edu.tamu.tcat.zotero.types.ItemFieldType;
import edu.tamu.tcat.zotero.types.ItemType;
import edu.tamu.tcat.zotero.types.ItemTypeInfo;

public class BasicItemType implements ItemType
{

   private ItemTypeInfo type;
   private final List<ItemFieldType> fields;
   private final List<ItemFieldType> creators;

   public BasicItemType(ItemTypeInfo type, List<ItemFieldType> fields, List<ItemFieldType> creators)
   {
      this.type = type;
      this.fields = fields;
      this.creators = creators;
   }

//   public static BasicItemType adapt(RestApiV3.ItemType itemType)
//   {
//      ItemTypeInfo type = new BasicItemTypeInfo(itemType.itemType.itemType, itemType.itemType.localized);
//      List<ItemFieldType> fields = itemType.fields.stream()
//            .map(BasicItemFieldType::adapt)
//            .collect(Collectors.toList());
//
//      List<ItemFieldType> creators = itemType.creators.stream()
//            .map(BasicItemFieldType::adapt)
//            .collect(Collectors.toList());
//
//      return new BasicItemType(type, fields, creators);
//   }

   @Override
   public String getId()
   {
      return this.type.getId();
   }

   @Override
   public String getLabel()
   {
      return this.type.getLabel();
   }

   @Override
   public List<ItemFieldType> getFields()
   {
      return Collections.unmodifiableList(this.fields);
   }

   @Override
   public List<ItemFieldType> getCreatorRoles()
   {
      return Collections.unmodifiableList(this.creators);
   }

}
