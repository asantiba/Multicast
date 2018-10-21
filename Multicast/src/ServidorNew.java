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

public class ServidorNew{
	//Para escribir en pantalla viewport.screenwrite("string")
	Interface viewport = new Interface();
	ServerThread server_t = new ServerThread();
	MultisocketThread multi_server_t = new MultisocketThread();
	
	public static void main(){
		
	}
}

//Thread del Servidor, llama al SocketHandler para realizar cualquier operacion en un socket.
//y data handler para obtener cualquier dato.
class ServerThread extends Thread{
	ServerSocket server;
	Socket socket;
	SocketHandler handler;
	
	public ServerThread(){
		try {
			server = new ServerSocket(9000);
			handler = new SocketHandler();
		} catch(Exception ex) {ex.printStackTrace();}
		
	}
	
	public void run() {
		try {
			while(true) {
				socket = server.accept();
				handler.read(socket);
				//handler.write(socket, array);
			}
		} catch(Exception ex) {ex.printStackTrace();}
	}
}

class SocketHandler {
	//Read solo lee el buffer del socket s, luego retorna una lista de string
	public String[] read(Socket s){
		String[] msg_splitted = null;
		try { //Se intenta obtener la informacion del socket.
			BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String msg_received = reader.readLine();
			msg_splitted = msg_received.split(" ");				
		} catch(Exception ex) {ex.printStackTrace();}
		return msg_splitted;
	}
	
	//Escribe en el output, envia valores en una lista de string al socket.
	public void write(Socket s, ArrayList<String> values) {
		try {
			PrintWriter writer = new PrintWriter(s.getOutputStream(), true);
			for(int i = 0; i < values.size(); i++) {
				writer.println("Variación de NO2: " + values.get(i));
			}
			writer.println("Finished history");
		}catch(Exception ex) {ex.printStackTrace();}
	}
}

//Crea los datos y los guarda
class DataHandler extends Thread{
	ArrayList<String> NO2 = new ArrayList<String>();
	ArrayList<String> O3 = new ArrayList<String>();
	ArrayList<String> CO = new ArrayList<String>();	
	String[] otherList;
	
	public DataHandler() {
		otherList = new String[] { "22.9", "16.5", "18.6", "11.2", "4.5", "20.1", "21.4", "14.7", "21.6", "12.6" };	
		NO2.addAll(Arrays.asList(otherList));
		otherList = new String[] { "0.37", "0.34", "0.59", "0.2", "0.16", "0.28", "0.31", "0.24", "0.55", "0.25" };
		CO.addAll(Arrays.asList(otherList));
		otherList = new String[] { "0.023", "0.016", "0.017", "0.023", "0.04", "0.042", "0.039", "0.029", "0.038", "0.028" };
		O3.addAll(Arrays.asList(otherList));
	}

	public int random(int n) {	 		
		return (int) ((Math.random() * n) + 1);
	}
	public void appender(){
		//sd
	}
}
//Configuracion del server del socket
class MultisocketThread extends Thread{
	InetAddress group;
	MulticastSocket multi_socket;
	MultisocketHandler handler;
	
	public MultisocketThread() {
		this.handler = new MultisocketHandler();
	}
	
	public void run(){
		try {
			group = InetAddress.getByName("230.0.0.4");
			multi_socket = new MulticastSocket(10033);
			multi_socket.joinGroup(group);
		} catch(Exception ex) {ex.printStackTrace();}
	}	
}

class MultisocketHandler{
	public void write(String string, MulticastSocket multi_socket, InetAddress group) {
		try{
			DatagramPacket packet = new DatagramPacket(string.getBytes(), string.length(), group, 10033);
			multi_socket.send(packet);
		}catch (Exception ex) {ex.printStackTrace();}
	}
}


class Interface{
	JFrame window;
	JTextArea area;
	JScrollPane scroll;
	JPanel container_area;
	
	public Interface() {
		window = new JFrame("Servidor"); // Ventana con nombre Servidor
		area = new JTextArea(20, 40); // Area para insertar texto
		scroll = new JScrollPane(area); // Barra para desplazar hacia abajo
		container_area = new JPanel();
		config();
	}
	
	public void config() {	
		container_area.setLayout(new GridLayout(1, 1));
		container_area.add(scroll);
		window.setLayout(new BorderLayout());
		window.add(container_area, BorderLayout.NORTH);
		window.setSize(500, 350);
		window.setVisible(true);
		window.setResizable(false); // Bloquea el cambio de tamaño
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void screenwrite(String string) {
		area.append(string);
	}
	
}



