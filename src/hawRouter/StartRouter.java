package hawRouter;

public class StartRouter {

	public static void main(String[] args) {		
		Thread r1 = new Thread(new Router(3131, "C:/Users/Jeff/Desktop/Projekte/Studium/WS_16/RN_Aufgabe4/asserts/configdata.csv", "::10"));
		Thread r2 = new Thread(new Router(3132, "C:/Users/Jeff/Desktop/Projekte/Studium/WS_16/RN_Aufgabe4/asserts/configdata2.csv", "::1"));
		r1.start();
		r2.start();
	}
}