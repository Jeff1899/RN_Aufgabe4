package router;

import com.googlecode.ipv6.IPv6Address;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Router {

    private NetworkLayer networkLayer;
    private Inet6Address ownRouterAddress;
    private List<Routing> routingList;
    private boolean running = true;

    /*
    Program arguments for two instances:
        5000 Router1 ::1
        5001 Router2 ::1
     */


    public Router(int port, String routingTableFilepath, String selfAddress) {

        try {
            networkLayer = new NetworkLayer(port);
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("ERROR: Initialisierung des NetworkLayer fehlgeschlagen!");
        }
        this.routingList = parseRoutingList(routingTableFilepath);

        try {
            this.ownRouterAddress = (Inet6Address) InetAddress.getByName(selfAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println(routingTableFilepath + " Listening on port " + port + " with local adress" + ownRouterAddress + "");
    }

    private List<Routing> parseRoutingList(String filepath) {
        String line;
        String cvsSplitBy = ";";
        List<Routing> result = new ArrayList<>(3);
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {

            while ((line = br.readLine()) != null) {
                String[] staticRouteLink = line.split(cvsSplitBy);

                String destinationAddressNet[] = staticRouteLink[0].split("/");
                String destinationAddress = destinationAddressNet[0];
                String netMask = destinationAddressNet[1];

                Routing routing = new Routing(destinationAddress, netMask, staticRouteLink[1], Integer.parseInt(staticRouteLink[2]));
                result.add(routing);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void sendAndReceiveMessage() throws IOException {

        while (running) {
            IpPacket receivedIpPackage = receiveMessage();
            System.out.println("received Message " + receivedIpPackage);

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
    }

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
        // && packageToCheck.getSourceAddress() != Inet6Address.getLoopbackAddress();
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


    public static void main(String[] args) {
      if(args.length != 1){
        System.out.println("Usage: Router <Path>");
        System.out.println("Path: Path of the Configuration Data");
        return;
      }
        int networkLayerPort = Integer.parseInt(args[0]);
        String listFilePath = args[1];
        String routerAddress = args[2];
        Router router = new Router(networkLayerPort, listFilePath, routerAddress);

        try {
            router.sendAndReceiveMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
  
  