
public class Cliente {
	//Instanciar interface
	//public static Interface viewport;
	//Instanciar socket cliente
	SocketClient client;
	ClientMulticast client_multicast;

	public Cliente(String server_id, String variables, String previous_measurements) {
		//viewport = new Interface("Cliente");
		//viewport.screenwrite("> Inicializando cliente\n");
		System.out.println("> Inicializando cliente");

		if(previous_measurements == "1") {
			try {
				SocketClient client = new SocketClient(server_id, variables);
				client.start();
			} catch(Exception ex) {ex.printStackTrace();}
		}
		try {
			ClientMulticast client_multicast = new ClientMulticast(variables, true);
			client_multicast.start();
		} catch(Exception ex) {ex.printStackTrace();}
	}

	public static void main(String[] args) {
		new Cliente(args[0], args[1], args[2]);
		
	}

}
