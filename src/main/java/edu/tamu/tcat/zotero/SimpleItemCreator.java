package edu.tamu.tcat.zotero;

import edu.tamu.tcat.zotero.types.ItemFieldType;

public class SimpleItemCreator implements ItemCreator
{
   private final String role;
   private final String name;
   private final String given;
   private final String family;

   public SimpleItemCreator(ItemFieldType role, String name)
   {
      this.role = role.getId();
      this.name = name;
      this.given = "";
      this.family = "";
   }

   public SimpleItemCreator(ItemFieldType role, String given, String family)
   {
      this.role = role.getId();
      this.given = given;
      this.family = family;
      this.name = "";
   }

   @Override
   public String getRole()
   {
      return role;
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public String getGivenName()
   {
      return given;
   }

   @Override
   public String getFamilyName()
   {
      return family;
   }
}
