package edu.tamu.tcat.zotero.basic.v3.types;

import java.text.MessageFormat;
import java.time.zone.ZoneRulesException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import edu.tamu.tcat.zotero.ZoteroRestException;
import edu.tamu.tcat.zotero.basic.v3.AdaptingFuture;
import edu.tamu.tcat.zotero.basic.v3.commands.ZoteroCommandExecutor;
import edu.tamu.tcat.zotero.types.ItemFieldType;
import edu.tamu.tcat.zotero.types.ItemType;
import edu.tamu.tcat.zotero.types.ItemTypeInfo;
import edu.tamu.tcat.zotero.types.ItemTypeProvider;

public class ItemTypeProviderService implements ItemTypeProvider
{
   private final long timeout;
   private final TimeUnit timeoutUnits;
   private final ZoteroCommandExecutor executor;

   public ItemTypeProviderService(ZoteroCommandExecutor executor)
   {
      this.executor = executor;
      this.timeout = 10;
      this.timeoutUnits = TimeUnit.SECONDS;
   }

   @Override
   public Set<ItemTypeInfo> getItemTypes() throws ZoteroRestException
   {
      String message = "Failed to retrieve a list of item types from Zotero";
      ZoteroItemTypeCommand cmd = ZoteroItemTypeCommand.getItemTypes(executor);
      Future<List<Map<String,String>>> typeResults = cmd.execute();

      Future<Set<ItemTypeInfo>> itemTypes = new AdaptingFuture<>(typeResults, this::adaptTypes);
      return unwrap(itemTypes, () -> message);
   }

   @Override
   public ItemType getItemType(ItemTypeInfo typeInfo) throws ZoteroRestException, IllegalArgumentException
   {
      String fieldMessage = "Failed to retrieve the fields for the item type of {0}, from Zotero";
      String creatorMessage = "Failed to retrieve the creators for the item type of {0}, from Zotero";

      Future<List<ItemFieldType>> futureITFs = this.getItemTypeFields(typeInfo);
      List<ItemFieldType> itemFieldTypes = unwrap(futureITFs, () -> MessageFormat.format(fieldMessage, typeInfo));

      Future<List<ItemFieldType>> futureITCs = this.getItemTypeCreatorTypes(typeInfo);
      List<ItemFieldType> itemTypeCreators = unwrap(futureITCs, () -> MessageFormat.format(creatorMessage, typeInfo));

      return new BasicItemType(typeInfo, itemFieldTypes, itemTypeCreators);
   }

   public Future<List<ItemFieldType>> getItemTypeFields(ItemTypeInfo info)
   {
      ZoteroItemTypeCommand cmd = ZoteroItemTypeCommand.getItemTypeFields(executor, info.getId());
      Future<List<Map<String,String>>> fieldResults = cmd.execute();
      return new AdaptingFuture<List<Map<String,String>>, List<ItemFieldType>>(fieldResults, this::adaptFields);
   }

   public Future<List<ItemFieldType>> getItemTypeCreatorTypes(ItemTypeInfo info)
   {
      ZoteroItemTypeCommand cmd = ZoteroItemTypeCommand.getItemTypeCreatorTypes(executor, info.getId());
      Future<List<Map<String,String>>> creatorResults = cmd.execute();
      return new AdaptingFuture<List<Map<String,String>>, List<ItemFieldType>>(creatorResults, this::adaptFields);
   }

   private Set<ItemTypeInfo> adaptTypes(List<Map<String,String>> list)
   {
      return list.stream().map(this::toItemType).collect(Collectors.toSet());
   }

   private List<ItemFieldType> adaptFields(List<Map<String,String>> list)
   {
      return list.stream().map(this::toFieldType).collect(Collectors.toList());
   }

   private ItemTypeInfo toItemType(Map<String,String> map)
   {
      String typeId = map.get("itemType");
      String label = map.get("localized");

      return new BasicItemTypeInfo(typeId, label);
   }

   private ItemFieldType toFieldType(Map<String,String> map)
   {
      String id = map.containsKey("field")
                 ?map.get("field")
                 :map.get("creatorType");

      String label = map.get("localized");

      return new BasicItemFieldType(id, label);
   }

   private <X> X unwrap(Future<X> result, Supplier<String> message) throws ZoteroRestException
   {
      try
      {
         return result.get(timeout, timeoutUnits);
      }
      catch (InterruptedException | TimeoutException e)
      {
         throw new IllegalStateException(message.get(), e);
      }
      catch (ExecutionException ex)
      {
         Throwable cause = ex.getCause();
         if (cause instanceof ZoneRulesException)
            throw (ZoteroRestException)cause;

         if  (cause instanceof RuntimeException)
            throw (RuntimeException)cause;

         throw new IllegalStateException(message.get(), cause);
      }
   }

}
