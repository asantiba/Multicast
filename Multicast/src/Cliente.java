import java.net.*; // Socket

public class Cliente {
	
	public static Interface viewport;
	SocketClient client;

	public Cliente(String server_id, String variables, String previous_measurements) {
		viewport = new Interface("Cliente");
		viewport.screenwrite("> Inicializando cliente\n");

		if(previous_measurements == "1") {
			SocketClient client = new SocketClient(viewport, server_id, variables);
			client.start();
		}
	}

	public static void main(String[] args) {
		new Cliente("localhost", "101", "1");
		//oe apura
	}

}
