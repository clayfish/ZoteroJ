package edu.tamu.tcat.zotero.examples;

import static java.text.MessageFormat.format;

import java.net.URI;
import java.util.stream.IntStream;

import edu.tamu.tcat.zotero.Item;
import edu.tamu.tcat.zotero.ItemSet;
import edu.tamu.tcat.zotero.ZoteroAccount;
import edu.tamu.tcat.zotero.ZoteroClient;
import edu.tamu.tcat.zotero.ZoteroLibrary;
import edu.tamu.tcat.zotero.ZoteroRestException;
import edu.tamu.tcat.zotero.basic.v3.ZoteroClientService;
import edu.tamu.tcat.zotero.basic.v3.commands.ZoteroCommandExecutor;
import edu.tamu.tcat.zotero.search.ExecutableItemQuery;
import edu.tamu.tcat.zotero.types.ItemType;

public class SimpleExample
{
    /**
     * Setup the ZoteroClient. The ZoteroClientService is designed to be run as an OSGi service.
     * In this example, we manually stitch the required services and activate the client implementation.
     */
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
