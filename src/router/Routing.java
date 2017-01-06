package router;

import com.googlecode.ipv6.IPv6Address;
import com.googlecode.ipv6.IPv6Network;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Routing {

    private Inet6Address destinationAddress;
    private IPv6Network destinationNetwork;
    private Inet6Address hopAdress;
    private int hopPort;
    



    public Routing(String destinationAddress, String destinationAddressNetMask, String hopAdress, int hopPort) {
        this.destinationAddress = stringToIPv6(destinationAddress);
        this.hopAdress = stringToIPv6(hopAdress);
        this.hopPort = hopPort;
        this.destinationNetwork = IPv6Network.fromString(destinationAddress + "/" + destinationAddressNetMask);
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

    public int getMatchScore(Inet6Address destination) {
        if(destinationNetwork.contains(IPv6Address.fromInetAddress(destination))) {
            String destBinary = ipv6ToBinaryString(getDestinationAddress());
            String compBinary = ipv6ToBinaryString(destination);
            return compareBits(compBinary, destBinary);
        } else {
            return 0;
        }

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


    private String ipv6ToBinaryString(Inet6Address ipv6ad) {
        IPv6Address address = IPv6Address.fromInetAddress(ipv6ad);
        String longString = address.toLongString().replace(":","");
        String s = "";
        for(int i = 0; i < longString.length(); i++) {
            char c = longString.toCharArray()[i];
            s += String.format("%4s", Integer.toBinaryString(c)).replace(" ", "0");
        }
        return s;
    }

    private Integer compareBits(String a, String b) {
        int count = 0;
        for (int i = 0; (i < Math.min(a.length(), b.length())) &&  i < destinationNetwork.getNetmask().asPrefixLength(); i++) {
            if (!(a.indexOf(i) == b.indexOf(i))) return count;
            count++;
        }
        return count;
    }

    public IPv6Network getDestinationNetwork() {
        return destinationNetwork;
    }



}