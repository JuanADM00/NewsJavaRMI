import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Server {
	private static final int PORT = 1802;

	public Server() {
		try {
			ConexionDB service = new ConexionDB();
			LocateRegistry.createRegistry(PORT);
			Naming.rebind("//127.0.0.1:" + Integer.toString(PORT) + "/service",service);
			System.out.println("Servidor escuchando en el puerto " + String.valueOf(PORT));
		} catch (RemoteException|MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Server();
	}
}