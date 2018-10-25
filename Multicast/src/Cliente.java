import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class Cliente {
	JFrame window = null;
	JTextArea area = null;
	JPanel container_area = null;
	JScrollPane scroll = null;
	String input = null;

	// Variables Multicast
	private InetAddress group; // Grupo para unirse al multicast
	protected MulticastSocket multicast_socket = null;
	protected byte[] receiver_buf = new byte[256]; // Recibe los datos enviados por Multicast

	// Variables cliente-servidor
	Socket socket = null;
	BufferedReader reader = null; // Permite leer mensajes
	PrintWriter writer = null; // Permite enviar mensajes
	
	//Paramentros de cliente
	String server_id = "192.168.1.5";
	String variables = "111"; //Variables solicitadas, luego comparar con estas para saber cuales mostrar
	String previous_measurements = "1"; //Historial solicitado
	Boolean finished_history = false; //Indica si finalizo el envio del historial

	public Cliente() {
		Interface();
	}

	public void Interface() {
		window = new JFrame("Cliente");
		area = new JTextArea(20, 4);
		scroll = new JScrollPane(area);
		input = JOptionPane.showInputDialog("Ingrese IP Mediciones Historial");
		container_area = new JPanel();
		container_area.setLayout(new GridLayout(1, 1));
		container_area.add(scroll);
		window.setLayout(new BorderLayout());
		window.add(container_area, BorderLayout.NORTH);
		window.setSize(500, 350);
		window.setVisible(true);
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Input [ip_multicast, mediciones, historial]
		area.append(input);
		String parsed_input[] = input.split(" ");
		if(parsed_input[2].equals("1")) {
			try {
				socket = new Socket(server_id, 9000);
				write();
				read();
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		
		while(finished_history) {
			//Esperar
		}
		
		// Thread principal
		Thread init = new Thread(new Runnable() {
			public void run() {
				try {
					group = InetAddress.getByName("230.0.0.4");
					multicast_socket = new MulticastSocket(10033);
					multicast_socket.joinGroup(group);
					while (true) {
						DatagramPacket packet = new DatagramPacket(receiver_buf, receiver_buf.length);
						multicast_socket.receive(packet);
						String received = new String(packet.getData(), 0, packet.getLength());
						area.append(received);
						if ("end".equals(received)) {
							break;
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		init.start();
	}

	public void read() {
		Thread thread_reader = new Thread(new Runnable() {
			public void run() {
				try {
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					while (true) {
						String msg_received = reader.readLine();
						if(msg_received == "Finished history") {
							finished_history = true;
							//Cierro las conexiones para permitir a otro cliente solicitar historial
							reader.close();
							writer.close();
							socket.close();
						}
						else {
							area.append(msg_received + "\n");	
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		thread_reader.start();
	}
	
	public void write() {
		try {
			area.append("Se solicita historial: " + socket.isConnected() + "\n");
			writer = new PrintWriter(socket.getOutputStream(), true);
			writer.println("Historial");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		
		new Cliente();
	}

}
