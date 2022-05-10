
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Set;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.WebLink;
import org.eclipse.californium.core.network.CoapEndpoint;

/**
 * @author ebruno
 */
public class CoapDiscovery {
    public static void main(String[] args) throws Exception {
        InetAddress addr = InetAddress.getByName("224.0.1.187");
        InetSocketAddress bindToAddress = new InetSocketAddress(addr, 5863);
        CoapEndpoint multicast = 
                CoapEndpoint.builder()
                        .setInetSocketAddress(bindToAddress)
                        .setPort(5863)
                        .build();
        CoapServer server = new CoapServer();
        server.addEndpoint(multicast);
        
        discovery("coap://localhost:5683/.well-known/core");
        discoverResources("coap://localhost:5683");

        discovery("coap://10.0.1.193:5683/.well-known/core");
        discoverResources("coap://10.0.1.193:5683");

        discovery("coap://FF05::FD:5683/");
        discoverResources("coap://FF05::FD:5683");

        discovery("coap://FF05::FD:5683/.well-known/core");

        discovery("coap://224.0.1.187:5683/");
        discoverResources("coap://224.0.1.187::5683");

    }
    
    public static void discoverResources(String uri)  {
        System.out.println("\ndiscoverResources() for address: " + uri);
        try {
            CoapClient client = new CoapClient(uri);
            client.useNONs();
            Set<WebLink> resources = client.discover();
            if ( resources != null ) {
                for ( WebLink link : resources ) {
                    System.out.println(link);
                }
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public static void discovery(String uri) {
        System.out.println("\ndiscovery() for address: " + uri);
        try {
            CoapClient client = new CoapClient(uri);
            client.useNONs();
            CoapResponse response = client.get();
            if ( response != null ) {
                System.out.println("Response code: " + response.getCode());
                System.out.println("Resources: " + response.getResponseText());
                System.out.println("Options: " + response.getOptions());

                InetSocketAddress saddr = response.advanced().getLocalAddress();
                InetSocketAddress addr = response.advanced().getSourceContext().getPeerAddress();
                System.out.println(
                        "Source address: " + 
                        addr.getAddress() + 
                        ":" + addr.getPort());

                //InetAddress addr = response.advanced().getSource();
                //int port = response.advanced().getSourcePort();
                //System.out.println("Source address: " + addr +":"+port);
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    
}
