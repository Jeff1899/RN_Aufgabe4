package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * 
 * @author Jeff & Biraj
 *
 */
public class Routing {

    private Inet6Address destinationAddress;
    private Inet6Address hopAdress;
    private int hopPort;
    
    /**
     * 
     * @param destinationAddress
     * @param destinationAddressNetMask
     * @param hopAdress
     * @param hopPort
     */
    public Routing(String destinationAddress, String destinationAddressNetMask, String hopAdress, int hopPort) {
        this.destinationAddress = stringToIPv6(destinationAddress);
        this.hopAdress = stringToIPv6(hopAdress);
        this.hopPort = hopPort;
    }
    
    /**
     * 
     * @param accounts
     * @return
     */
	public static ArrayList<Routing> createRoutingTable(File accounts){
		   ArrayList<Routing> routeTable = new ArrayList<Routing>();
	        try {
	            BufferedReader rdr = new BufferedReader(new FileReader(accounts));
	            String input = "";
	            while ((input = rdr.readLine()) != null) {
	                String[] routeEntry = input.split(";");
	                String target[] = routeEntry[0].split("/");
	                routeTable.add(new Routing(target[0], target[1], routeEntry[1], Integer.parseInt(routeEntry[2])));
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return routeTable;
	}

//	/**
//	 * 
//	 * @param destination
//	 * @return
//	 */
//    public int getMatchScore(Inet6Address destination) {
//        String target = changeToBinary(destinationAddress);
//        String compare = changeToBinary(destination);
//        int cnt = 0;
//        for(int i = 0; i < target.length() && i < compare.length(); i ++){
//        	if(target.indexOf(i) == compare.indexOf(i)){
//        		cnt = cnt +1;
//        	}else{
//        		break;
//        	}
//        }
//    	return cnt;
//    }

    /**
     * 
     * @param address
     * @return
     */
    private Inet6Address stringToIPv6 (String address) {
        Inet6Address nextHopAddress = null;
        try {
            nextHopAddress = (Inet6Address) InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return nextHopAddress;
    }
    


    /**
     * 
     * @return
     */
    public Inet6Address getDestinationAddress() {
        return destinationAddress;
    }

    /**
     * 
     * @return
     */
    public Inet6Address getHopAddress() {
        return hopAdress;
    }

    /**
     * 
     * @return
     */
    public int getHopPort() {
        return hopPort;
    }



}