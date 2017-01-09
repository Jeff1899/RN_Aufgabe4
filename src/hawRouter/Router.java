package hawRouter;

import com.googlecode.ipv6.IPv6Address;

import router.ControlPacket;
import router.IpPacket;
import router.NetworkLayer;
import utility.Routing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Router implements Runnable {

    private NetworkLayer networkLayer;
    private String routerName;
    private Inet6Address routerAddr;
    private List<Routing> routingList;
    private boolean running = true;

    public Router(int port,String name, String routingTableFilepath, String routerAddress) {

        try {
            networkLayer = new NetworkLayer(port);
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("ERROR: Initialisierung des NetworkLayer fehlgeschlagen!");
        }
        this.routerName = name;
        this.routingList = Routing.createRoutingTable(new File(routingTableFilepath));

        try {
            this.routerAddr = (Inet6Address) InetAddress.getByName(routerAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println(routingTableFilepath + " Listening on port " + port + " with local adress" + routerAddr + "");    }

    private void sendPackageNormally(IpPacket packageToSend, Routing routing) throws IOException {
        packageToSend.setHopLimit(packageToSend.getHopLimit() - 1);
        packageToSend.setNextHopIp(routing.getHopAddress());
        packageToSend.setNextPort(routing.getHopPort());
        networkLayer.sendPacket(packageToSend);
    }

    private void sendErrorReturnPackage(IpPacket receivedIpPackage, ControlPacket.Type type) throws IOException {
        System.out.println("Sending Error return Package of type " + type);

        if(!isControllOrInvalidSourcePackage(receivedIpPackage)) {
            Routing bestMatch = findBestMatch(receivedIpPackage.getSourceAddress());
            receivedIpPackage.setDestinationAddress(receivedIpPackage.getSourceAddress());
            receivedIpPackage.setNextHopIp(bestMatch.getHopAddress());
            receivedIpPackage.setNextPort(bestMatch.getHopPort());
            receivedIpPackage.setHopLimit(255);
            ControlPacket timeExceeded = new ControlPacket(type, receivedIpPackage.getBytes());
            receivedIpPackage.setControlPayload(timeExceeded.getBytes());
            networkLayer.sendPacket(receivedIpPackage);
        }
    }

    private boolean isControllOrInvalidSourcePackage(IpPacket packageToCheck) {
        return packageToCheck.getType() == IpPacket.Header.Control
                || packageToCheck.getSourceAddress() == null;
    }

    private boolean reachableRoute(IpPacket ipp) {
        for (Routing r : routingList) {
            if (r.getDestinationNetwork().contains(IPv6Address.fromInetAddress(ipp.getDestinationAddress()))) {
                return true;
            }
        }
        return false;
    }

    public Routing findBestMatch(Inet6Address destinationAddress) {
        Routing bestMatch = null;
        long bestMatchValue = Integer.MIN_VALUE;
        for (Routing route : routingList) {
            long tmp = route.getMatchScore(destinationAddress);
            if (tmp > bestMatchValue) {
                bestMatchValue = tmp;
                bestMatch = route;
            }
        }
        return bestMatch;
    }

    private IpPacket receiveMessage() throws IOException {
        return networkLayer.getPacket();
    }

	@Override
	public void run() {

        while (running) {
        	try {
	        	IpPacket receivedIpPackage;
				receivedIpPackage = receiveMessage();
	            System.out.println( routerName + " empfängt Packet " + receivedIpPackage);
	            if(!reachableRoute(receivedIpPackage)){
	                sendErrorReturnPackage(receivedIpPackage, ControlPacket.Type.DestinationUnreachable);
	            } else {
	                Inet6Address destinationAddress = receivedIpPackage.getDestinationAddress();
	                Routing bestMatch = findBestMatch(destinationAddress);
	
	                if (receivedIpPackage.getHopLimit() > 1) {
	                    sendPackageNormally(receivedIpPackage, bestMatch);
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