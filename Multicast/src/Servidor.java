import javax.swing.*;
import java.awt.*;
import java.net.*; // Socket
import java.io.*; // Recibe y envia mensajes
//import java.awt.event.*; // Manejo de eventos
import java.util.Timer; // Timer para enviar variaciones
import java.util.TimerTask; //Ejecuta tarea cuando se cumple un Timer
import java.util.ArrayList; //Metodos en listas
import java.util.Arrays;
import java.util.Date; //Obtiene la fecha
import java.text.DateFormat; //Lee parte de la fecha

public class Servidor {
	// Variables para interfaz
	JFrame window = null;
	JTextArea area = null;
	JScrollPane scroll = null;
	JPanel container_area = null;

	// Variables Multicast
	private DatagramSocket datagram_socket; // Datagrama para enviar datos
	private InetAddress group; // Grupo para unirse al multicast
	private byte[] buf; // Buffer para enviar datos
	protected MulticastSocket multicast_socket = null; // Socket Multicast
	protected byte[] receiver_buf = new byte[256]; // Recibe los datos enviados por Multicast

	// Variables cliente-servidor
	ServerSocket server = null;
	Socket socket = null;
	BufferedReader reader = null; // Permite leer mensajes
	PrintWriter writer = null; // Permite enviar mensajes
	
	// Historial de Variables
	ArrayList<String> NO2_history = new ArrayList<String>();
	ArrayList<String> CO_history = new ArrayList<String>();
	ArrayList<String> O3_history = new ArrayList<String>();

	// constructor: Aquí se inicializa todo
	public Servidor() {
		Interface();
	}

	public void Interface() {
		window = new JFrame("Servidor"); // Ventana con nombre Servidor
		area = new JTextArea(20, 40); // Area para insertar texto
		scroll = new JScrollPane(area); // Barra para desplazar hacia abajo
		container_area = new JPanel();
		container_area.setLayout(new GridLayout(1, 1));
		container_area.add(scroll);
		window.setLayout(new BorderLayout());
		window.add(container_area, BorderLayout.NORTH);
		window.setSize(500, 350);
		window.setVisible(true);
		window.setResizable(false); // Bloquea el cambio de tamaño
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Thread para enviar historial
		Thread history = new Thread(new Runnable() {
			public void run() {
				try {
					server = new ServerSocket(9000);
					while(true) {
						socket = server.accept();
						read();
						write();
					}
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		history.start();
		
		// Thread principal
		Thread init = new Thread(new Runnable() {
			public void run() {
				try {
					group = InetAddress.getByName("230.0.0.4");
					multicast_socket = new MulticastSocket(10033);
					multicast_socket.joinGroup(group);
					timer();
					// Al parecer no podemos escuchar los mensajes que llegan al grupo, ME
					// PEGAAAAAAAA EL PC, asi que la primera conexion debe ser por socket normal
					/*
					 * while (true) { // Socket cliente-servidor // socket = server.accept();
					 * 
					 * // Lee los mensajes enviados al grupo multicast read_multicast(); }
					 */
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		init.start();
	}

	public void read_multicast() {
		Thread thread_reader = new Thread(new Runnable() {
			public void run() {
				try {
					DatagramPacket packet = new DatagramPacket(receiver_buf, receiver_buf.length);
					multicast_socket.receive(packet);
					String received = new String(packet.getData(), 0, packet.getLength());
					// Si el mensaje solicita el historial enviarlo
					if (received == "") {
						area.append("Cliente " + received + " solicita historial\n");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		thread_reader.start();
	}

	public void timer() {
		Thread thread_timer = new Thread(new Runnable() {
			public void run() {
				try {
					// Datos NO2
					ArrayList<String> NO2 = new ArrayList<String>();
					String[] otherList = new String[] { "22.9", "16.5", "18.6", "11.2", "4.5", "20.1", "21.4", "14.7", "21.6", "12.6" };
					NO2.addAll(Arrays.asList(otherList));

					// Datos CO
					ArrayList<String> CO = new ArrayList<String>();
					otherList = new String[] { "0.37", "0.34", "0.59", "0.2", "0.16", "0.28", "0.31", "0.24", "0.55", "0.25" };
					CO.addAll(Arrays.asList(otherList));

					// Datos O3
					ArrayList<String> O3 = new ArrayList<String>();
					otherList = new String[] { "0.023", "0.016", "0.017", "0.023", "0.04", "0.042", "0.039", "0.029", "0.038", "0.028" };
					O3.addAll(Arrays.asList(otherList));
					// Timer
					Timer timer = new Timer();
					// Tarea a ejecutar
					TimerTask task = new TimerTask() {
						@Override
						public void run() {
							//NO2
							int number = (int) (Math.random() * 10);
							write_multicast(NO2.get(number), "NO2");
							NO2_history.add(NO2.get(number));
							//CO
							number = (int) (Math.random() * 10);
							write_multicast(CO.get(number), "CO");
							CO_history.add(CO.get(number));
							//O3
							number = (int) (Math.random() * 10);
							write_multicast(O3.get(number), "O3");
							O3_history.add(O3.get(number));
							area.append("\n");
						}
					};
					timer.schedule(task, 0, 10000);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		thread_timer.start();
	}

	public void write_multicast(String number, String elemento) {
		try {
			String line = "Variación de " + elemento + ": " + number + " / Fecha: " + DateFormat.getTimeInstance(DateFormat.MEDIUM).format(new Date()) +"\n";
			buf = line.getBytes();
			datagram_socket = new DatagramSocket(); //Crea un datagrama
			DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 10033); //Inserta el buffer en un datagrama
			datagram_socket.send(packet); //Envia el paquete
			area.append(line); //Agrega la linea al panel area
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void read() {
		Thread thread_reader = new Thread(new Runnable() {
			public void run() {
				try {
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					while (true) {
						String msg_received = reader.readLine();
						if(msg_received == "Historial") {
							area.append("Cliente solicita historial\n\n");
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
		Thread thread_write = new Thread(new Runnable() {
			public void run() {
				try {
					writer = new PrintWriter(socket.getOutputStream(), true);
					for(int i = 0; i < NO2_history.size(); i++) {
						//NO2
						writer.println("Variación de NO2: " + NO2_history.get(i));
						//CO
						writer.println("Variación de CO: " + CO_history.get(i));
						//03
						writer.println("Variación de O3: " + O3_history.get(i));
					}
					writer.println("Finished history");
					area.append("Finished history\n\n");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		thread_write.start();
	}

	public static void main(String[] args) {
		new Servidor();
	}

}
