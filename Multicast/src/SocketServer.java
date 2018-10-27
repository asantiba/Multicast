import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class SocketServer extends Thread{
	Interface viewport;
	ServerSocket server;
	int port = 9000;
	
	//Lista de sockets esperando.
	ArrayList<SocketHandler> handler_list;
	//Thread encargados del envio del historial.
	SocketHandler handler;
	//Arreglo de DataHandlers, cada uno con medidores diferentes.
	DataHandler[] datas;
	
	//Init
	public SocketServer(Interface viewport, DataHandler[] datas){
		this.viewport = viewport; 
		this.datas = datas;
		this.handler_list = new ArrayList<SocketHandler>();
		try {
			server = new ServerSocket(port);
			viewport.screenwrite("> Socket historial en Puerto: "+this.port+"\n");
		} catch(Exception ex) {ex.printStackTrace();}
		
	}
	
	public void run() {
		try {
			while(true) {
				//Supondremos, que desde el cliente nunca se solicitara este socket si "Historial != 1"	
				if (handler_list.isEmpty() || handler_list.get(0).isAlive()) {
					Socket s = server.accept();
					viewport.screenwrite("\n> Conexion nueva solicita historial\n");
					handler_list.add(new SocketHandler(datas, s));
					if(handler_list.get(0).getState() == Thread.State.NEW) {
						handler_list.get(0).start();
					}
					else if (handler_list.get(0).getState() == Thread.State.TERMINATED){
						handler_list.remove(0);
						if(handler_list.size() != 0) {
							handler_list.get(0).start();
						}
					}
				}
			}
		}catch(Exception ex) {ex.printStackTrace();}
	}
}

//Funcionalidades del Socket de historial.
class SocketHandler extends Thread{
	DataHandler[] datas;
	PrintWriter writer;
	BufferedReader reader;
	String variables; //variables solicitadas
	Socket s;
	
	public SocketHandler(DataHandler[] datas, Socket s){
		this.datas = datas;
		this.s = s;
		try {
			this.writer = new PrintWriter(s.getOutputStream(), true);
			this.reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch(Exception ex) {ex.printStackTrace();}
		
	}
	
	public void run(){
		try { //Se intenta obtener la informacion del socket.
				variables = reader.readLine();
				if(variables.length() > 1) {
					write();
					variables = "";
				
			}
		} catch(Exception ex) {ex.printStackTrace();}
	}
	
	public void write() {
		try {
			for(int i = 0; i < datas[0].elements.size(); i++) {
				char[] characters = variables.toCharArray();
				for(int j = 0; j < characters.length; j++) {
					if(characters[j] == '1') {
						writer.println("> Variación de "+datas[j].indicator+": " + datas[j].elements.get(i));
					}
				}
			}
			writer.println("Finished history");
		}catch(Exception ex) {ex.printStackTrace();}
	}
}