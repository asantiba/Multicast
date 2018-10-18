import javax.swing.*;
import java.awt.*;
import java.net.*; // Socket
import java.io.*; // Recibe y envia mensajes
//import java.awt.event.*; // Manejo de eventos
import java.util.Timer; // Timer para enviar variaciones
import java.util.TimerTask; //Ejecuta tarea cuando se cumple un Timer
import java.util.ArrayList; //Metodos en listas
import java.util.Arrays;

public class Servidor {
	// Variables para interfaz
	JFrame window = null;
	JTextArea area = null;
	JScrollPane scroll = null;
	JPanel container_area = null;
	// Variables servidor
	ServerSocket server = null;
	Socket socket = null;
	BufferedReader reader = null; //Permite leer mensajes
	PrintWriter writer = null; //Permite enviar mensajes
	
	// constructor: Aquí se inicializa todo
	public Servidor() {
		Interface();
	}
	
	public void Interface() {
		window = new JFrame("Servidor"); //Ventana con nombre Servidor
		area = new JTextArea(20,4);	//Area para insertar texto
		scroll = new JScrollPane(area); //Barra para desplazar hacia abajo
		container_area = new JPanel();
		container_area.setLayout(new GridLayout(1,1));
		container_area.add(scroll);
		window.setLayout(new BorderLayout());
		window.add(container_area, BorderLayout.NORTH);
		window.setSize(500, 350);
		window.setVisible(true);
		window.setResizable(false); //Bloquea el cambio de tamaño 
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Thread principal
		Thread init = new Thread(new Runnable() {
			public void run() {
				try {
					server = new ServerSocket(9000);
					while(true) {
						socket = server.accept();
						read();
						timer();
					}
				}catch(Exception ex) {
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
					while(true) {
						String msg_received = reader.readLine();
						area.append("Cliente " + msg_received + " solicita historial\n");
					}
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		thread_reader.start();
	}
	
	public void write(String number, String elemento) {
		try {
			writer = new PrintWriter(socket.getOutputStream(),true);
			writer.println("Servidor: Variación de " + elemento + ": " + number + "\n");
			String line = "Variacion de " + elemento + ": " + number + "\n";
			area.append(line);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void timer() {
		Thread thread_timer = new Thread(new Runnable() {
			public void run() {
				try {
					// Datos NO2
					ArrayList<String> NO2 = new ArrayList<String>();
					String[] otherList = new String[] {"22.9", "16.5", "18.6", "11.2", "4.5", "20.1", "21.4", "14.7", "21.6", "12.6"};
					NO2.addAll(Arrays.asList(otherList));
					// Datos CO
					ArrayList<String> CO = new ArrayList<String>();
					String[] otherList2 = new String[] {"0.37", "0.34", "0.59", "0.2", "0.16", "0.28", "0.31", "0.24", "0.55", "0.25"};
					CO.addAll(Arrays.asList(otherList2));
					// Datos O3
					ArrayList<String> O3 = new ArrayList<String>();
					String[] otherList3 = new String[] {"0.023", "0.016", "0.017", "0.023", "0.04", "0.042", "0.039", "0.029", "0.038", "0.028"};
					O3.addAll(Arrays.asList(otherList3));
					//Timer
					Timer timer = new Timer();
					//Tarea a ejecutar
					TimerTask task = new TimerTask() {
						@Override
						public void run() {
							int number = (int) (Math.random() * 10);
							write(NO2.get(number), "NO2");
							number = (int) (Math.random() * 10);
							write(CO.get(number), "CO");
							number = (int) (Math.random() * 10);
							write(O3.get(number), "O3");
						}
					};
					timer.schedule(task, 0, 2000);
				}catch(Exception ex) {
					ex.printStackTrace();
				}				
			}
		});
		thread_timer.start();
	}
	
	public static void main(String[] args) {
		new Servidor();
	}

}
