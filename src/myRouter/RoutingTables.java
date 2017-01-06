package myRouter;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class RoutingTables {

		private String target;
		private String nextHop;
		private int port;
		
		public RoutingTables(String target, String nextHop, int port){
			this.target = target;
			this.nextHop = nextHop;
			this.port = port;
		}
		
		@Override
		public String toString(){
			return this.target + " " + this.nextHop + " " + this.port;
			
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

		public String getTarget() {
			return target;
		}

		public String getNextHop() {
			return nextHop;
		}

		public int getPort() {
			return port;
		}
		
	}
