package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Routing {

    private Inet6Address destinationAddress;
    private Inet6Address hopAdress;
    private int hopPort;
    
    public Routing(String destinationAddress, String destinationAddressNetMask, String hopAdress, int hopPort) {
        this.destinationAddress = stringToIPv6(destinationAddress);
        this.hopAdress = stringToIPv6(hopAdress);
        this.hopPort = hopPort;
    }
    
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


    public int getMatchScore(Inet6Address destination) {
        String target = changeToBinary(destinationAddress);
        String compare = changeToBinary(destination);
        int cnt = 0;
        for(int i = 0; i < target.length() && i < compare.length(); i ++){
        	if(target.indexOf(i) == compare.indexOf(i)){
        		cnt = cnt +1;
        	}else{
        		break;
        	}
        }
    	return cnt;
    }

    private Inet6Address stringToIPv6 (String address) {
        Inet6Address nextHopAddress = null;
        try {
            nextHopAddress = (Inet6Address) InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return nextHopAddress;
    }
    
    private String changeToBinary(Inet6Address ipv6ad) {
    	char charArray[] = ipv6ad.getHostAddress().replace(":","").toCharArray();
        String result = "";
        for(int i = 0; i < charArray.length; i++) {
            result += String.format("%4s", Integer.toBinaryString(charArray[i])).replace(" ", "0");
        }
        return result;
    }

    public Inet6Address getDestinationAddress() {
        return destinationAddress;
    }

    public Inet6Address getHopAddress() {
        return hopAdress;
    }

    public int getHopPort() {
        return hopPort;
    }



}