package hawRouter;

public class StartRouter {

	public static void main(String[] args) {		
		Thread r1 = new Thread(new Router(3131,"Router_1", "C:/Users/Jeff/Desktop/Projekte/Studium/WS_16/RN_Aufgabe4/tables/Router_1.csv", "::99"));
		Thread r2 = new Thread(new Router(3132,"Router_2", "C:/Users/Jeff/Desktop/Projekte/Studium/WS_16/RN_Aufgabe4/tables/Router_2.csv", "::55"));
		r1.start();
		r2.start();
	}
}