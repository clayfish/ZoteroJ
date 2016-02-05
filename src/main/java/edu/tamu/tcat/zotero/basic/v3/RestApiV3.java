package edu.tamu.tcat.zotero.basic.v3;

import java.util.List;
import java.util.Map;

/**
 * Represents the REST data vehicles supplied by V3 of the Zotero rest API.
 */
public class RestApiV3
{
   public static class ItemList
   {
      public List<Item> items;
   }

   public static class Item
   {
      public String key;
      public int version;
      public ZoteroLibraryRef library;
      public Map<String, SimpleLink> links;
      public ItemMeta meta;
      public Map<String, Object> data;

      public static class ItemMeta
      {
         public String creatorSummary = "";
         public String parsedDate = "";
         public int numChildren = 0;
      }

      /** Structure of tags returned with data.tags []. */
      public static class Tag
      {
         public String tag;

         // unclear what this is, type info is not always present, examples show type: 1
         public int type;
      }

   }

   public static class ZoteroLibraryRef
   {
      public String type;
      public String id;
      public String name;
      public Map<String, SimpleLink> links;
   }

   public static class ZoteroGroupLibrary
   {
      public int id;
      public int version;
      public Map<String, SimpleLink> links;
      public Meta meta;
      public Data data;

      public static class Meta
      {
         public String created;
         public String lastModified;
         public int numItems;
      }

      public static class Data
      {
         public int id;
         public int version;
         public int owner;
         public String name;
         public String description;
         public String type;
         public String url;
         public String libraryEditing;
         public String libraryReading;
         public String fileEditing;
      }
   }

   public static class SimpleLink
   {
      public String href;
      public String type;
      public String title;
      public int length;

      @Override
      public String toString()
      {
         return href + " [" + type + "]";
      }
   }


   public static class CollectionList
   {
      public List<Collection> collections;
   }

   public static class Collection
   {
      public String key;
      public int version;
      public ZoteroLibraryRef library;
      public Map<String, SimpleLink> links;
      public CollectionMeta meta;
      public CollectionData data;
   }

   public static class CollectionMeta
   {
      public int numCollections;
      public int numItems;
   }

   public static class CollectionData
   {
      public String key;
      public int version;
      public String name;
      public String parentCollection;

      // TODO don't know the structure of this yet
      public Map<String, Object> relations;

   }

   public static class Tag
   {
      public String tag;
      public Map<String, SimpleLink> links;
      public TagMetadata meta;
   }

   public static class TagMetadata
   {
      public int type;
      public int numItems;
   }

   public static class ItemTypeInfo
   {
      public String itemType;
      public String localized;
   }

   public static class ItemTypeField
   {
      public String field;
      public String localized;
   }

   public static class ItemType
   {
      public ItemTypeInfo itemType;
      public List<ItemTypeField> fields;
      public List<ItemTypeField> creators;
   }

   public static class ZoteroLibrary
   {
      /** Unique identifier for this library. */
      public int id;

      /** Current version number for this library. */
      public int version;

      /** Links for finding information about this library. Will include 'self' that references
       *  the API (type=application/json) and 'alternate' for a public web URL (type=text/html)
       */
      public Map<String, SimpleLink> links;

      /** Summary information about the library. */
      public ZoteroLibraryMeta meta;

      /** Detailed information about the library. */
      public ZoteroLibraryData data;
   }

   public static class ZoteroLibraryMeta
   {
      public String created;
      public String lastModified;
      public int numItems;
   }

   public static class ZoteroLibraryData
   {
      public int id;
      public int version;
      public String name;
      public int owner;
      public String type;
      public String description;
      public String url;
      public String libraryEditing;
      public String libraryReading;
      public String fileEditing;
   }

   public static class ZoteroItemResponse
   {
      public Map<String,Item> successful;
      public Map<String,String> success;
      public Map<String,String> unchanged;
      public Map<String,String> failed;

//    {   successful={},
//          success={},
//          unchanged={},
//          failed={0={code=400, message='firstName' and 'name' creator fields are mutually exclusive}}}
   }
   
   public static class ZoteroCollectionResponse
   {
      public Map<String,Collection> successful;
      public Map<String,String> success;
      public Map<String,String> unchanged;
      public Map<String,String> failed;
   }



//   https://api.zotero.org/users/2536190/collections/EK6F98V3
//   {
//      "key": "EK6F98V3",
//      "version": 20,
//      "library": {
//          "type": "user",
//          "id": 2536190,
//          "name": "neal.audenaert",
//          "links": {
//              "alternate": {
//                  "href": "https://www.zotero.org/neal.audenaert",
//                  "type": "text/html"
//              }
//          }
//      },
//      "links": {
//          "self": {
//              "href": "https://api.zotero.org/users/2536190/collections/EK6F98V3",
//              "type": "application/json"
//          },
//          "alternate": {
//              "href": "https://www.zotero.org/neal.audenaert/collections/EK6F98V3",
//              "type": "text/html"
//          },
//          "up": {
//              "href": "https://api.zotero.org/users/2536190/collections/NTF5FGZC",
//              "type": "application/atom+xml"
//          }
//      },
//      "meta": {
//          "numCollections": 0,
//          "numItems": 2
//      },
//      "data": {
//          "key": "EK6F98V3",
//          "version": 20,
//          "name": "Topic Modeling",
//          "parentCollection": "NTF5FGZC",
//          "relations": {}
//      }
//  }


//      https://api.zotero.org/users/2536190/items/D8R4WZQ4
//      {
//          "key": "D8R4WZQ4",
//          "version": 18,
//          "library": {
//              "type": "user",
//              "id": 2536190,
//              "name": "neal.audenaert",
//              "links": {
//                  "alternate": {
//                      "href": "https://www.zotero.org/neal.audenaert",
//                      "type": "text/html"
//                  }
//              }
//          },
//          "links": {
//              "self": {
//                  "href": "https://api.zotero.org/users/2536190/items/D8R4WZQ4",
//                  "type": "application/json"
//              },
//              "alternate": {
//                  "href": "https://www.zotero.org/neal.audenaert/items/D8R4WZQ4",
//                  "type": "text/html"
//              }
//          },
//          "meta": {
//              "creatorSummary": "Matloff",
//              "parsedDate": "2011",
//              "numChildren": 0
//          },
//          "data": {
//              "key": "D8R4WZQ4",
//              "version": 18,
//              "itemType": "book",
//              "title": "The Art of R Programming: A Tour of Statistical Software Design",
//              "creators": [
//                  {
//                      "creatorType": "author",
//                      "firstName": "Norman",
//                      "lastName": "Matloff"
//                  }
//              ],
//              "abstractNote": "",
//              "series": "",
//              "seriesNumber": "",
//              "volume": "",
//              "numberOfVolumes": "",
//              "edition": "",
//              "place": "",
//              "publisher": "No Starch Press",
//              "date": "2011",
//              "numPages": "373",
//              "language": "",
//              "ISBN": "978-1-9327-384-2",
//              "shortTitle": "",
//              "url": "",
//              "accessDate": "",
//              "archive": "",
//              "archiveLocation": "",
//              "libraryCatalog": "",
//              "callNumber": "",
//              "rights": "",
//              "extra": "",
//              "dateAdded": "2015-07-15T15:45:43Z",
//              "dateModified": "2015-07-15T15:45:43Z",
//              "tags": [],
//              "collections": [],
//              "relations": {}
//          }
//      }

}
