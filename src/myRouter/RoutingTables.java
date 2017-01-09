package myRouter;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class RoutingTables {

		private Inet6Address  target;
		private Inet6Address  nextHop;
		private int port;
		
		public RoutingTables(String target, String nextHop, int port) throws UnknownHostException{
			this.target = (Inet6Address) InetAddress.getByName(target);
			this.nextHop = (Inet6Address) InetAddress.getByName(nextHop);
			this.port = port;
		}
		
		@Override
		public String toString(){
			return this.target + " " + this.nextHop + " " + this.port;
			
		}
		
		public RoutingTables longestPrefixMatch(){
		  //für die weiterleitung in andere Router.
		  return null;
		}

		public Inet6Address getTarget() {
			return target;
		}

		public Inet6Address getNextHop() {
			return nextHop;
		}

		public int getPort() {
			return port;
		}
		
	}
