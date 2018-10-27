import java.net.*;

public class ClientMulticast extends Thread{
	//Instancia interface
	Interface viewport;
	String variables;
	//Si finaliza en envio de historial comienza a recibir del multicast
	Boolean finished;
	
	//Instanciar Variables Multicast
	private InetAddress group; // Grupo para unirse al multicast
	protected MulticastSocket multicast_socket1 = null;
	protected MulticastSocket multicast_socket2 = null;
	protected MulticastSocket multicast_socket3 = null;
	DatagramPacket packet;
	protected byte[] receiver_buf = new byte[256]; // Recibe los datos enviados por Multicast

	public ClientMulticast(Interface viewport, String variables, Boolean finished) {
		this.viewport = viewport;
		this.variables = variables;
		this.finished = finished;
	}

	public void run() {
		try {
			ClientMulticastHandler();
			while (true) {
				if(finished) {
					DatagramPacket packet = new DatagramPacket(receiver_buf, receiver_buf.length);
					if(multicast_socket1 != null) {
						multicast_socket1.receive(packet);
						String received = new String(packet.getData(), 0, packet.getLength());
						viewport.screenwrite("> Variaci�n de Acidez: " + received + "\n");
					}
					if(multicast_socket2 != null) {
						multicast_socket2.receive(packet);
						String received = new String(packet.getData(), 0, packet.getLength());
						viewport.screenwrite("> Variaci�n de Temperatura: " + received + "\n");
					}
					if(multicast_socket3 != null) {
						multicast_socket3.receive(packet);
						String received = new String(packet.getData(), 0, packet.getLength());
						viewport.screenwrite("> Variaci�n de Humedad: " + received + "\n");
					}
				}
			}
		} catch (Exception ex) { ex.printStackTrace(); }
	}
	
	public void ClientMulticastHandler () {
		try {
			group = InetAddress.getByName("230.0.0.4");
			char[] characters = variables.toCharArray();
			if(characters[0] == '1') {
				multicast_socket1 = new MulticastSocket(10033);
				multicast_socket1.joinGroup(group);
			}
			if(characters[1] == '1') {
				multicast_socket2 = new MulticastSocket(10034);
				multicast_socket2.joinGroup(group);
			}
			if(characters[2] == '1') {
				multicast_socket3 = new MulticastSocket(10035);
				multicast_socket3.joinGroup(group);
			}
		} catch (Exception ex) { ex.printStackTrace(); }
	}
}
