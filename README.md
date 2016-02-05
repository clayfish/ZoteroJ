# ZoteroJ: Zotero Client SDK
ZoteroJ provides a Java SDK to interface with the Zotero REST API.

Zotero is a widely used tool for managing personal collections of bibliographic references.

## Background and Motivation
ZoteroJ was developed in order to allow digital scholarship projects, such as the
[Special Divine Action](http://specialdivineaction.org), to integrate robust collection
management tools into Java-based web applications. A long-term goal of this effort
is that ZoteroJ will serve as one pluggable module for a broader reference management system
that enables the project's software developers and administrators to integrate multiple
third-party reference management systems including tools like Mendeley, BibSonomy or one of the
[many other tools](https://en.wikipedia.org/wiki/Comparison_of_reference_management_software)
currently in use.

Gathering and organizing bibliographic material and notes is a critical task for scholars
and most have established tools and work processes. Rather than forcing visitors to learn and
use custom built tools, leaving their data locked into a system that they may use for only a
small portion of their research, these integrations will visitors' to explore the project's
Website and seemlessly interface with their own personal bibliographic management tools -
both adding content to their personal collections and drawing on their collections to enhance
the content of the main project.

## Current Status
This client is currently functional, but very much a work in progress. Key features to be
added include:

 * Bug fixes and consistency checks
 * Comprehensive initial unit tests
 * Local caching of items and ongoing synchronization
 * Improved support for document attachments
 * Faceted search over key fields such as journals, publishers and locations to support type-ahead data entry

## Quick Start
To start working with ZoteroJ, you will first need to create a Zotero account
and obtain a user id and authentication token. Please see the
[Zotero documentation](https://www.zotero.org/support/dev/web_api/v3/basics#authentication)
for more details.

For a starting point into the API, consider the SimpleExample class.

```Java
ppublic class SimpleExample
{
    /** Setup the ZoteroClient. The ZoteroClientService is designed to be run as an OSGi service.
     *  In this example, we manually stitch the required services and activate the client implementation. */
    public static ZoteroClient initClient(URI apiEndpoint)
    {
       URI rootUri = URI.create("https://api.zotero.org");
       ZoteroCommandExecutor executor = new ZoteroCommandExecutor(rootUri, 5);

       ZoteroClientService service = new ZoteroClientService();
       service.bind(executor);
       service.activate();

       return service;
    }

    /** Handle command line arguments to load the ZoteroAccount instance. */
    private static ZoteroAccount loadAccount(String[] args, ZoteroClient client) throws ZoteroRestException
    {
        if (args.length != 2)
        {
            System.out.println("Unexpected number of arguments.");
            System.out.println("  Usage: java -jar zoteroj.jar <userId> <token>");
            System.exit(1);
        }

        String userId = args[0];
        String token = args[1];

        System.out.println(format("Loading Zotero Account for user {0}", userId));
        return client.getUserAccount(userId, token);
    }

    /** Convenience method to get the value of a known field from a bibliographic item.  */
    private static String getFieldValue(Item item, String field)
    {
        ItemType type = item.getItemType();
        return type.getFields().stream()
              .filter(f -> "title".equalsIgnoreCase(f.getId()))
              .findAny()
              .map(fType -> item.getFieldValue(fType))
              .orElse(format("Undefined field [{0}]", field));
    }

    public static void main(String[] args)
    {
        ZoteroClient client = initClient(URI.create("https://api.zotero.org"));

        try
        {
            ZoteroAccount account = loadAccount(args, client);
            ZoteroLibrary library = account.getUserLibrary();
            ExecutableItemQuery query = library.makeItemQueryBuilder().limit(10).build();

            System.out.println("Loading items . . . ");
            ItemSet items = query.execute();
            IntStream.range(0, 10)
                .mapToObj(items::get)
                .forEach(item-> {
                    String title = getFieldValue(item, "title");
                    System.out.println(format("[{0}]\t{1}", item.getItemType().getLabel(), title));
                });

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}

```
