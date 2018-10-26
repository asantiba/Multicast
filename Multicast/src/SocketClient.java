import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketClient extends Thread{
	// Instanciar interface
	Interface viewport;

	// Variables cliente-servidor
	Socket socket;
	BufferedReader reader; // Permite leer mensajes
	PrintWriter writer; // Permite enviar mensajes
	
	//Paramentros de cliente
	String server_id;
	String variables; //Variables solicitadas

	public SocketClient(Interface viewport, String server_id, String variables) {
		this.viewport = viewport;
		this.server_id = server_id;
		this.variables = variables;
	}

	public void run() {
		viewport.screenwrite("> Variables: " + variables + "\n");
		char[] characters = variables.toCharArray();
		String text = "";
		if(characters[0] == '1') {
			text = text + "Acidez ";
		}
		if(characters[1] == '1') {
			text = text + "Temperatura ";
		}
		if(characters[2] == '1') {
			text = text + "Humedad";
		}
		viewport.screenwrite("> Solicitar historial de: " + text + "\n");
		try {
			socket = new Socket(server_id, 9000);
			write();
			read();
			socket.close();
		}catch(Exception ex) {ex.printStackTrace();}
	}

	public void write() {
		try {
			writer = new PrintWriter(socket.getOutputStream(), true);
			writer.println(variables);
		} catch (Exception ex) { ex.printStackTrace();}
	}

	public void read() {
		try{
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (true) {
				String msg_received = reader.readLine();
				if(msg_received == "Finished history") {
					//Cierro las conexiones para permitir a otro cliente solicitar historial
					reader.close();
					writer.close();
					socket.close();
					break;
				}
				else {
					viewport.screenwrite(msg_received + "\n");
				}
			}
		} catch (Exception ex) { ex.printStackTrace();}
	}
}
