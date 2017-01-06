package myRouter;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

import router.DataPacket;
import router.IpPacket;
import router.NetworkLayer;

public class Router implements Runnable {
	
	private ArrayList<RoutingTables> routingTable = new ArrayList<RoutingTables>();
	private NetworkLayer networkLayer;

	
	public Router(String filepath) throws SocketException{
		routingTable = RoutingTables.createRoutingTable(new File(filepath));
		networkLayer = new NetworkLayer(3131);
	}

	@Override
	public void run() {
//		 while (true) {
			 try {
				 System.out.println("Wait");
				IpPacket ipPacket = networkLayer.getPacket();
                System.out.println("Received packet from " 
                        + ipPacket.getNextHopIp().getHostAddress() + "/" + ipPacket.getNextHopPort());
                System.out.println("  " + ipPacket);
                if (ipPacket.getType() == IpPacket.Header.Data) {
                	ipPacket.setNextPort(3142);
                	networkLayer.sendPacket(ipPacket);
                    
                }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		 }
		
	}

}
