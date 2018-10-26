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
	ArrayList<Socket> socket_list;
	//Thread encargados del envio del historial.
	SocketHandler handler;
	//Arreglo de DataHandlers, cada uno con medidores diferentes.
	DataHandler[] datas;
	
	//Init
	public SocketServer(Interface viewport, DataHandler[] datas){
		this.viewport = viewport; 
		this.datas = datas;
		this.socket_list = new ArrayList<Socket>();
		try {
			server = new ServerSocket(port);
			viewport.screenwrite("> Socket historial en Puerto: "+this.port+"\n");
		} catch(Exception ex) {ex.printStackTrace();}
		
	}
	
	public void run() {
		try {
			while(true) {
				//Supondremos, que desde el cliente nunca se solicitara este socket si "Historial != 1"
				Socket s = server.accept();
				socket_list.add(s);
				viewport.screenwrite("\n> Conexion nueva solicita historial\n");
				if((handler == null) || !handler.isAlive() ) {
					PrintWriter writer = new PrintWriter(socket_list.get(0).getOutputStream(), true);
					handler = new SocketHandler(datas, writer, socket_list.get(0));
					viewport.screenwrite("> Mandando Historial\n\n");
					handler.start();
					socket_list.remove(0);
				}
			}
		}catch(Exception ex) {ex.printStackTrace();}
	}
}

//Funcionalidades del Socket de historial.
class SocketHandler extends Thread{
	DataHandler[] datas;
	PrintWriter writer;
	String variables; //variables solicitadas
	Socket s;
	
	public SocketHandler(DataHandler[] datas, PrintWriter writer, Socket s){
		this.datas = datas;
		this.writer = writer;
		this.s = s;
	}
	
	//Lee el buffer del socket s, luego retorna una lista de string
	public void run(){
		try { //Se intenta obtener la informacion del socket.
			BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
			while (true) {
				variables = reader.readLine();
				if(variables.length() > 1) {
					write();
					variables = "";
				}
			}
		} catch(Exception ex) {ex.printStackTrace();}
	}

	//Escribe en el output, envia valores en una lista de string al socket.
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