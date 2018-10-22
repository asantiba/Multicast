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
	public static Interface viewport;
	public static DataHandler data;

	public static void main(String[] args){
		viewport = new Interface();
		data = new DataHandler();
		viewport.screenwrite("> Inicializando servidor\n");
		
		MultisocketThread multi_server_t = new MultisocketThread(viewport, data);
		ServerThread server_t = new ServerThread(viewport, data);
		
		multi_server_t.start();
		server_t.start();
		
		viewport.screenwrite("> Server ON\n");
		
		
		
	}
}

//Thread del Servidor, llama al SocketHandler para realizar cualquier operacion en un socket.
//y data handler para obtener cualquier dato.
class ServerThread extends Thread{
	ServerSocket server;
	int port = 9000;

	ArrayList<Socket> socket_list;
	SocketHandler handler;
	Interface viewport;
	DataHandler data;
	
	
	public ServerThread(Interface viewport, DataHandler data){
		this.viewport = viewport; 
		this.data = data;
		try {
			server = new ServerSocket(port);
			handler = new SocketHandler();
			viewport.screenwrite("> Socket historial en Puerto: "+this.port+"\n");	
		} catch(Exception ex) {ex.printStackTrace();}
		
	}
	
	public void run() {
		try {
			while(true) {
				//Supondremos, que desde el cliente nunca se solicitara este socket si "Historial != 1"
				socket_list.add(server.accept());
				viewport.screenwrite("> Conexion nueva solicita historial\n");
				String[] read = handler.read(socket_list.get(0));
				String[] mediciones = read[1].split("");
				for(int i = 0; i < mediciones.length; i++) {
					if(mediciones[i].equals("1")) {
						data.get_array(i);
					}
				
				}
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
	
	public void input(String[] string, Socket s, DataHandler data){
		try {
			
			
		} catch(Exception ex) {ex.printStackTrace();}
	}
	
	//Escribe en el output, envia valores en una lista de string al socket.
	public void write(Socket s, ArrayList<String> values) {
		try {
			PrintWriter writer = new PrintWriter(s.getOutputStream(), true);
			for(int i = 0; i < values.size(); i++) {
				writer.println("Iniciando el Historial:\n");
				writer.println("Variación de NO2: " + values.get(i));
			}
			writer.println("Finished history");
		}catch(Exception ex) {ex.printStackTrace();}
	}
	
}

//Configuracion del server del socket
class MultisocketThread extends Thread{
	InetAddress group;
	int port = 10033;
	MulticastSocket multi_socket;
	MultisocketHandler handler;
	Interface viewport;
	DataHandler data;
	
	public MultisocketThread(Interface viewport, DataHandler data) {
		this.viewport = viewport;
		this.handler = new MultisocketHandler();
		this.data = data;
		try {
			this.group = InetAddress.getByName("230.0.0.4");
			this.multi_socket = new MulticastSocket(port);
			this.multi_socket.joinGroup(group);
		} catch(Exception ex) {ex.printStackTrace();}
		viewport.screenwrite("> Multicast IP: "+this.group+" - Puerto: "+this.port+"\n");

	}
	
	public void run(){
		try {
			TimerTask timerTask = new TimerTask(){
				public void run(){ 
					 //Calculamos un conjunto de valores, y los guardamos en la lista de datos.
		             data.compute();
		             //Los mandamos por el socket.
		             handler.write(data.get_lastValues(), multi_socket, group, port);
		         } 
		     }; 
		      // Aquí se pone en marcha el timer cada segundo. 
		     Timer timer = new Timer(); 
		     // Dentro de 0 milisegundos avísame cada 1000 milisegundos 
		     timer.scheduleAtFixedRate(timerTask, 0, 1000);
		} catch(Exception ex) {ex.printStackTrace();}
	}	
}

//Realiz los envios via el socket
class MultisocketHandler{
	public void write(String string, MulticastSocket multi_socket, InetAddress group, int port) {
		try{
			byte[] buffer = string.getBytes();
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
			multi_socket.send(packet);
		}catch (Exception ex) {ex.printStackTrace();}
	}
}


//Crea los datos y los guarda
class DataHandler{
	//Los valores del arreglo se acceden facilmente con estos arreglos
	ArrayList<String> NO2 = new ArrayList<String>();
	ArrayList<String> O3 = new ArrayList<String>();
	ArrayList<String> CO = new ArrayList<String>();	
	String[] lastValues;
	
	//Init
	public DataHandler() {
		String[] otherList;
		otherList = new String[] { "22.9", "16.5", "18.6", "11.2", "4.5", "20.1", "21.4", "14.7", "21.6", "12.6" };	
		NO2.addAll(Arrays.asList(otherList));
		otherList = new String[] { "0.37", "0.34", "0.59", "0.2", "0.16", "0.28", "0.31", "0.24", "0.55", "0.25" };
		CO.addAll(Arrays.asList(otherList));
		otherList = new String[] { "0.023", "0.016", "0.017", "0.023", "0.04", "0.042", "0.039", "0.029", "0.038", "0.028" };
		O3.addAll(Arrays.asList(otherList));
	}
	
	public void appender(String[] values){
		NO2.add(values[0]);
		CO.add(values[1]);
		O3.add(values[2]);
	}
	
	public String random(double n) {	 		
		return String.valueOf((Math.random() * n));
	}
	
	public void compute() {
		String[] values = {random(30), random(0.5), random(0.040)};
		appender(values);
		this.lastValues = values;
	}
	
	public String get_lastValues() {
		return ("NO2 = "+lastValues[0]+"\nCO = "+lastValues[1]+"\nO3 = "+lastValues[2]+"\n");
	}
	
	public ArrayList<String> get_array(int index){
		if(index == 0) {return NO2;}
		if(index == 1) {return CO;}
		else {return O3;}
	}
	
	public String get_arrayname(int index) {
		if(index == 0) {return "NO2";}
		if(index == 1) {return "CO";}
		else {return "O3";}
	}
}
	



class Interface{
	JFrame window;
	JTextArea area;
	JScrollPane scroll;
	JPanel container_area;
	
	public Interface() {
		window = new JFrame("Servidor2"); // Ventana con nombre Servidor
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



