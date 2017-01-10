package utility;

import java.net.Inet6Address;
import java.util.List;

public class LongPrefixMatch {
	
	/**
	 * 
	 * @param currentAddress
	 * @param routingList
	 * @return
	 */
	public static Routing getLongPrefixMatch(Inet6Address currentAddress, List<Routing> routingList){
		
		Routing match = null;
        int cnt = 0;
        for (Routing route : routingList) {
            int tmp = getMatchScore(currentAddress, route.getDestinationAddress());
            if (tmp > cnt) {
                cnt = tmp;
                match = route;
            }
        }
		return match;
	}
	
	/**
	 * 
	 * @param currentAddress
	 * @return
	 */
    private static int getMatchScore(Inet6Address currentAddress, Inet6Address compareAddress) {

        String currentBinaryString = Inet6AddressToBinary(currentAddress);
        String compareWith  = Inet6AddressToBinary(compareAddress);
        int cnt = 0;
        for(int i = 0; i < compareWith.length() && i < currentBinaryString.length(); i ++){
        	if(compareWith.indexOf(i) == currentBinaryString.indexOf(i)){
        		cnt = cnt +1;
        	}else{
        		break;
        	}
        }
    	return cnt;
    }
    
    /**
     * 
     * @param ipv6ad
     * @return
     */
    private static String Inet6AddressToBinary(Inet6Address ipv6ad) {
    	char charArray[] = ipv6ad.getHostAddress().replace(":","").toCharArray();
        String result = "";
        for(int i = 0; i < charArray.length; i++) {
//        	TODO//
            result += String.format("%4s", Integer.toBinaryString(charArray[i])).replace(" ", "0");
        }
        return result;
    }


}
