package StartUp;

import java.net.SocketException;

import myRouter.Router;

public class main {

	public static void main(String[] args) throws SocketException {
		Thread router1 = new Thread(new Router("../RN_Aufgabe4/RoutingTables/table_1.csv"));
		router1.run();
//		Router  router2 = new Router("../RN_Aufgabe4/RoutingTables/table_2.csv");
	}

}
