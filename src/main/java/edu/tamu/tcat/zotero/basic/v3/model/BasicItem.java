package edu.tamu.tcat.zotero.basic.v3.model;

import java.net.URI;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import edu.tamu.tcat.zotero.Item;
import edu.tamu.tcat.zotero.ItemCreator;
import edu.tamu.tcat.zotero.ItemSet;
import edu.tamu.tcat.zotero.SimpleItemCreator;
import edu.tamu.tcat.zotero.ZoteroCollection;
import edu.tamu.tcat.zotero.ZoteroLibrary;
import edu.tamu.tcat.zotero.ZoteroRestException;
import edu.tamu.tcat.zotero.basic.v3.BasicEditItemCommand.EditItemMutator;
import edu.tamu.tcat.zotero.basic.v3.RestApiV3;
import edu.tamu.tcat.zotero.types.ItemFieldType;
import edu.tamu.tcat.zotero.types.ItemType;
import edu.tamu.tcat.zotero.types.ItemTypeInfo;
import edu.tamu.tcat.zotero.types.ItemTypeProvider;

public class BasicItem implements Item
{
   /** The authoritative link to this item. */
   private static final String LINK_SELF = "self";

   /** Link to the webpage for this item. */
   private static final String LINK_ALTERNATE = "alternate";

   private final static Logger logger = Logger.getLogger(BasicItem.class.getName());

   public static final String ERR_MISSING_LINK = "Failed to retrieve item link [rel={1}] for item {0}.]";
   public static final String ERR_MALFORMED_DATE =
         "Failed to parse value for date {0}. "
               + "\n\tItem: {1} [{2}]"
               + "\n\tURI:  {3}";
   public static final String ERR_MALFORMED_LINK =
         "Failed to parse item link [rel={1}] for item {0}. "
         + "\n\tURL:       {2}"
         + "\n\tException: {3}";

   public static final String ERR_LIBRARY_MISMATCH =
         "Library ids do not match for item {0}."
         + "\n\tItem Library:     {1}"
         + "\n\tSupplied Library: {2}";

   /** A reference to the library this item belongs to. Connect the item instance to the remote services. */
   private final ZoteroLibrary library;

   /** Unique identifier for this item within a the context of the library it is a member of. */
   private final String id;

   /** Canonical URI for identifying this item. For use in RDF relationships, etc. */
   private final URI uri;

   /** Reference to the API endpoint for this item. */
   private final URI apiRef;

   /** The version of the associated library for which this item was last modified. */
   private final int version;

   /** The bibliographic type of this item (such as book, article). */
   private final ItemType type;

   /** The title of this bibliographic item. */
   private final String title;

   /** A simplified format of the creator information for display. */
   private final String creator;

   /** A simplified representation of the date this item was created. */
   private final String date;

   private final String parent;

   private final Map<String, String> fieldValues;

   private final List<ItemCreator> creators;

   private final List<RestApiV3.Item.Tag> tags;
   private final Set<String> collections;
//   private final List<String> relations;

   private final Instant dateAdded;
   private final Instant dateModified;





//   "data": {
//      "key": "ABCD2345",
//      "version": 1,
//      "itemType": "webpage",
//      "title": "Zotero Quick Start Guide",
//      "creators": [
//          {
//              "creatorType": "author",
//              "name": "Center for History and New Media"
//          }
//      ],
//      "abstractNote": "",
//      "websiteTitle": "Zotero",
//      "websiteType": "",
//      "date": "",
//      "shortTitle": "",
//      "url": "https://www.zotero.org/support/quick_start_guide",
//      "accessDate": "2014-06-12T21:28:55Z",
//      "language": "",
//      "rights": "",
//      "extra": "",
//      "dateAdded": "2014-06-12T21:28:55Z",
//      "dateModified": "2014-06-12T21:28:55Z",
//      "tags": [],
//      "collections": [],
//      "relations": {}
//    }

   public BasicItem(ZoteroLibrary library, ItemTypeProvider types, RestApiV3.Item dto)
   {
      this.library = library;
      if (!Objects.equals(dto.library.id, library.getId()))
      {
         String msg = MessageFormat.format(ERR_LIBRARY_MISMATCH, dto.key, dto.library.id, library.getId());
         throw new IllegalStateException(msg);
      }

      this.apiRef = parseLink(dto, LINK_SELF);
      this.uri = parseLink(dto, LINK_ALTERNATE);

      this.creator = dto.meta.creatorSummary;
      this.date = dto.meta.parsedDate;

      Map<String, Object> data = dto.data;
      this.id = (String)data.get("key");
      this.version = ((Integer)data.get("version")).intValue();
      this.title = (String)data.get("title");

      this.type = getItemType(types, data);
      this.fieldValues = this.type.getFields().stream()
                           .map(field -> field.getId())
                           .collect(Collectors.toMap(
                                 Function.identity(),
                                 fieldId -> parseValue(fieldId, data)));


      @SuppressWarnings("unchecked")  // hoping the data format is correct
      List<Map<String, String>> rawCreators = (List<Map<String, String>>)data.get("creators");
      this.creators = rawCreators.stream().map(this::toCreator).collect(Collectors.toList());

      this.parent = (String)data.get("parentItem");
//      library.getItem(getFieldValue("parentItem"));


      this.dateAdded = parseDate((String)data.get("dateAdded"));
      this.dateModified = parseDate((String)data.get("dateModified"));

      this.tags = parseTags(dto);

      @SuppressWarnings("unchecked") // defined by data API to be list of strings
      List<String> collectionIds = (List<String>)data.get("collections");
      this.collections = new HashSet<>(collectionIds);

      // TODO parse relations. Should a Map<String, String> with RDF verb,object.
      //      data.get("relations");
      // TODO generate RDF triples for these relns?
   }

   public BasicItem(ZoteroLibrary library, EditItemMutator mutator)
   {
      this.library = library;

      this.apiRef = null;
      this.uri = null;
      this.creator = "";
      this.date = "";

      this.id = mutator.getKey();
      this.version = mutator.getVersion();
      Map<String, String> fields = mutator.getFields();

      this.title = fields.get("title");

      this.type = mutator.getItemType();
      this.fieldValues = new HashMap<>(fields);
      this.creators = new ArrayList<>(mutator.getCreators());

      this.parent = mutator.getParent() != null
                       ? mutator.getParent().getId()
                       : "";
      this.tags = new ArrayList<>(); // mutator.getTags();
      this.collections = new HashSet<>(); //mutator.getCollections();
      this.dateAdded = null;
      this.dateModified = null;
   }

   /**
    * Forces a value from the data map to be a string value. May be <code>null</code> if the
    * data map does not contain a value for the supplied key.
    */
   private String parseValue(String key, Map<String, Object> data)
   {
      if (!data.containsKey(key))
         return null;

      Object value = data.get(key);
      if (value == null)
         return null;

      if (String.class.isInstance(value))
         return (String)value;

      if (Number.class.isInstance(value))
         return value.toString();

      String ERR_UNPARSABLE_VALUE =
            "Failed to parse value for item field {0}. Found instance of {1}."
            + "\n\tString Value: {2}";
      logger.warning(MessageFormat.format(ERR_UNPARSABLE_VALUE, key, value.getClass(), value));

      return "";
   }

   private SimpleItemCreator toCreator(Map<String, String> raw)
   {
      String type = raw.get("creatorType");
      String name = raw.get("name");
      String first = raw.get("firstName");
      String last = raw.get("lastName");
      String ERR_UNKNOWN_CREATOR_ROLE = "Could not restore author role {0}";

      // TODO simly ignore unknown roles or force to author.
      ItemFieldType role = this.type.getCreatorRoles().stream()
            .filter(r -> r.getId().equalsIgnoreCase(type))
            .findAny()
            .orElseThrow(() -> new IllegalStateException(MessageFormat.format(ERR_UNKNOWN_CREATOR_ROLE, type)));
      return (name != null && !name.trim().isEmpty())
                  ? new SimpleItemCreator(role, name)
                  : new SimpleItemCreator(role, first, last);
   }

   private ItemType getItemType(ItemTypeProvider types, Map<String, Object> data)
   {
      // FIXME push this into the ItemTypeProvider

      String itemType = (String)data.get("itemType");
      try
      {
         ItemTypeInfo typeInfo = types.getItemTypes().stream()
               .filter(info -> info.getId().equals(itemType))
               .findAny()
               .orElseThrow(() -> new IllegalStateException());
         return types.getItemType(typeInfo);
      }
      catch (Exception ex)
      {
         throw new IllegalArgumentException("Unsupported item type: " + itemType);
      }
   }

   private static RestApiV3.Item.Tag createTag(Map<String, Object> tag)
   {
      RestApiV3.Item.Tag sTag = new RestApiV3.Item.Tag();
      sTag.tag = (String)tag.get("tag");
      Integer tagType = (Integer)tag.get("type");
      sTag.type = tagType != null ? tagType.intValue() : 0;

      return sTag;
   }

   private List<RestApiV3.Item.Tag> parseTags(RestApiV3.Item dto)
   {
      @SuppressWarnings("unchecked")      // defined by data API
      List<Map<String, Object>> rawTags = (List<Map<String, Object>>)dto.data.get("tags");
      return rawTags.stream()
                    .map(BasicItem::createTag)
                    .collect(Collectors.toList());

   }

   private URI parseLink(RestApiV3.Item dto, String rel)
   {
      RestApiV3.SimpleLink link = dto.links.get(rel);
      if (link == null)
      {
         logger.fine(() -> MessageFormat.format(ERR_MISSING_LINK, this.id, rel));
         return null;
      }

      try
      {
         return URI.create(link.href);
      }
      catch (Exception ex)
      {
         logger.warning(() -> MessageFormat.format(ERR_MALFORMED_LINK, this.id, rel, link.href, ex.getMessage()));
         return null;
      }
   }

   /**
    * Used to safely parse (or not) the date added/date modified fields.
    */
   private Instant parseDate(String value)
   {
      if (value == null)
         return null;

      try
      {
         return Instant.parse(value);
      }
      catch (Exception ex)
      {
         String msg = "Failed to parse value for created/modified date {0}. "
               + "\n\tItem: {1} [{2}]"
               + "\n\tURI:  {3}";
         logger.log(Level.WARNING, MessageFormat.format(msg, value, this.title, this.id, this.apiRef), ex);
         return null;
      }
   }

   @Override
   public String getId()
   {
      return this.id;
   }

   public ZoteroLibrary getLibrary()
   {
      return library;
   }

   public URI getUri()
   {
      return apiRef;
   }

   @Override
   public int getVersion()
   {
      return this.version;
   }

   public String getTitle()
   {
      return title;
   }

   public String getCreatorDescription()
   {
      return creator;
   }

   public String getDate()
   {
      return date;
   }

   public Instant getDateAdded()
   {
      return dateAdded;
   }

   public Instant getDateModified()
   {
      return dateModified;
   }


   @Override
   public ItemType getItemType()
   {
      return this.type;
   }

   @Override
   public List<ItemCreator> getCreators()
   {
      return Collections.unmodifiableList(creators);
   }

   @Override
   public String getFieldValue(ItemFieldType field) throws IllegalArgumentException
   {
      if (!fieldValues.containsKey(field.getId()))
         throw new IllegalArgumentException(MessageFormat.format("Invalid item field type {0}.", field.getId()));

      return this.fieldValues.get(field.getId());
   }

   @Override
   public List<ZoteroCollection> getCollections()
   {
      // cache the dynamically loaded collection
      return collections.stream()
                        .map(this::getCollectionById)
                        .collect(Collectors.toList());

   }

   private ZoteroCollection getCollectionById(String id)
   {
      try {
         return library.getCollection(id);
      } catch (ZoteroRestException e) {
         throw new IllegalArgumentException("Invalid collection id: " + id, e);
      }
   }

   @Override
   public Item getParent() throws IllegalStateException, ZoteroRestException
   {
      if (this.parent == null)
         return null;

      return library.getItem(this.parent);
   }

   @Override
   public ItemSet getChildren()
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public Set<String> getTags()
   {
      return tags.stream().map(tag -> tag.tag).collect(Collectors.toSet());
   }
}
