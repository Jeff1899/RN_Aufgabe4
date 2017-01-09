package myRouter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.SocketException;
import java.util.ArrayList;

import router.ControlPacket;
import router.ControlPacket.Type;
import router.IpPacket;
import router.NetworkLayer;

public class Router implements Runnable {

  private ArrayList<RoutingTables> routingTable = new ArrayList<RoutingTables>();
  private NetworkLayer networkLayer;

  public Router(String filepath) throws SocketException {
    routingTable = createRoutingTable(new File(filepath));
    networkLayer = new NetworkLayer(3131);
  }

  @Override
  public void run() {
    while (true) {
      System.out.println("Wait");
      IpPacket ipPacket=null;
      try {
        ipPacket = networkLayer.getPacket();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      System.out.println(
          "Received packet from " + ipPacket.getNextHopIp().getHostAddress() + "/" + ipPacket.getNextHopPort());
      System.out.println("  " + ipPacket);
      if (!destinationRechable(ipPacket)) {
        sendErrorMessage(ipPacket, ControlPacket.Type.DestinationUnreachable);
      }
      if (ipPacket.getHopLimit() > 1) {
        Inet6Address destinationAddress = ipPacket.getDestinationAddress();
        RoutingTables bestMatch = findBestMatch(destinationAddress);
        sendPackagefurther(ipPacket, bestMatch);
      } else {
        sendErrorMessage(ipPacket, ControlPacket.Type.TimeExceeded);
      }
    }

  }

  private RoutingTables findBestMatch(Inet6Address destinationAddress) {
    //find the best match routing
    return  null;
  }

  private void sendPackagefurther(IpPacket packageToSend, RoutingTables routing) {
    packageToSend.setHopLimit(packageToSend.getHopLimit() - 1);
    packageToSend.setNextHopIp(routing.getNextHop());
    packageToSend.setNextPort(routing.getPort());
    try {
      networkLayer.sendPacket(packageToSend);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private boolean destinationRechable(IpPacket ipPacket) {
    return true;
  }

  private void sendErrorMessage(IpPacket ipPacket, Type type)  {
    System.out.println("Sending Error return Package of type " + type);

    RoutingTables bestMatch = findBestMatch(ipPacket.getSourceAddress());
    ipPacket.setDestinationAddress(ipPacket.getSourceAddress());
    ipPacket.setNextHopIp(bestMatch.getNextHop());
    ipPacket.setNextPort(bestMatch.getPort());
    ipPacket.setHopLimit(255);
    ControlPacket timeExceeded = new ControlPacket(type, ipPacket.getBytes());
    ipPacket.setControlPayload(timeExceeded.getBytes());
    try {
      networkLayer.sendPacket(ipPacket);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
  
  public static ArrayList<RoutingTables> createRoutingTable(File file){
    ArrayList<RoutingTables> routingList = new ArrayList<RoutingTables>();
       try {
           BufferedReader rdr = new BufferedReader(new FileReader(file));
           String input = "";
           while ((input = rdr.readLine()) != null) {
               String[] userdata = input.split(";");                   
               routingList.add(new RoutingTables(userdata[0], userdata[1], Integer.parseInt(userdata[2])));
           }
       } catch (IOException e) {
           e.printStackTrace();
       }
       return routingList;
}

}
