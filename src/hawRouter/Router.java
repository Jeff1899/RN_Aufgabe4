package hawRouter;


import router.ControlPacket;
import router.IpPacket;
import router.NetworkLayer;
import utility.LongPrefixMatch;
import utility.Routing;


import java.io.File;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * 
 * @author Jeff & Biraj
 *
 */
public class Router implements Runnable {

    private NetworkLayer networkLayer;
    private String routerName;
    private List<Routing> routingList;
    private boolean running = true;

    /**
     * Initialisiert den Router
     * @param port
     * @param name
     * @param routingTableFilepath
     * @param routerAddress
     */
    public Router(int port,String name, String routingTableFilepath, String routerAddress) {

        try {
            networkLayer = new NetworkLayer(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.routerName = name;
        this.routingList = Routing.createRoutingTable(new File(routingTableFilepath));
    }

    /**
     * Leitet das Packet zum nächsten Hop und dekrimiert HopLimit
     * 
     * @param sendPackage
     * @param tableEntry
     * @throws IOException
     */
    private void sendPackage(IpPacket sendPackage, Routing tableEntry) throws IOException {
        sendPackage.setHopLimit(sendPackage.getHopLimit() - 1);
        sendPackage.setNextHopIp(tableEntry.getHopAddress());
        sendPackage.setNextPort(tableEntry.getHopPort());
        networkLayer.sendPacket(sendPackage);
    }

    /**
     * 
     * @param receivedIpPackage
     * @param type
     * @throws IOException
     */
    private void sendErrorReturnPackage(IpPacket receivedIpPackage, ControlPacket.Type type) throws IOException {
        System.out.println("Sending Error return Package of type " + type);

        if(!isControllOrInvalidSourcePackage(receivedIpPackage)) {
        	Routing bestMatch = LongPrefixMatch.getLongPrefixMatch(receivedIpPackage.getDestinationAddress(), routingList);
            receivedIpPackage.setDestinationAddress(receivedIpPackage.getSourceAddress());
            receivedIpPackage.setNextHopIp(bestMatch.getHopAddress());
            receivedIpPackage.setNextPort(bestMatch.getHopPort());
            receivedIpPackage.setHopLimit(255);
            ControlPacket timeExceeded = new ControlPacket(type, receivedIpPackage.getBytes());
            receivedIpPackage.setControlPayload(timeExceeded.getBytes());
            networkLayer.sendPacket(receivedIpPackage);
        }
    }

    /**
     * 
     * @param packageToCheck
     * @return
     */
    private boolean isControllOrInvalidSourcePackage(IpPacket packageToCheck) {
        return packageToCheck.getType() == IpPacket.Header.Control
                || packageToCheck.getSourceAddress() == null;
    }

    /**
     * 
     * @param ipp
     * @return
     */
    private boolean isRouteAvailable(Inet6Address destination) {
        for (Routing route : routingList) {
        	if(route.getDestinationAddress().toString().equals(destination.toString())){
        		 return true;
        	}
        }
        return false;
    }

    /**
     * 
     */
	@Override
	public void run() {

        while (running) {
        	try {
	        	IpPacket receivedIpPackage = networkLayer.getPacket();
	            System.out.println( routerName + " empfängt Packet " + receivedIpPackage);
	            if(!isRouteAvailable(receivedIpPackage.getDestinationAddress())){
	                sendErrorReturnPackage(receivedIpPackage, ControlPacket.Type.DestinationUnreachable);
	            } else {
	                if (receivedIpPackage.getHopLimit() > 1) {
	                	
	                	
	                	sendPackage(receivedIpPackage, LongPrefixMatch.getLongPrefixMatch(receivedIpPackage.getDestinationAddress(), routingList));
	                } else {
	                    sendErrorReturnPackage(receivedIpPackage, ControlPacket.Type.TimeExceeded);
	                }
	            }
        	}
        	catch (IOException e) {
        		e.printStackTrace();
			 }
        }
	} 	
}