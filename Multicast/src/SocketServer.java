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
				viewport.screenwrite("> Conexion nueva solicita historial\n");
				if(!handler.isAlive()) {
					String[] read = handler.read(socket_list.get(0));
					String[] asked = read[1].split("");		//Mediciones pedidas
					PrintWriter writer = new PrintWriter(socket_list.get(0).getOutputStream(), true);
					handler = new SocketHandler(datas, writer);
					for(int i = 0; i < asked.length; i++) {
						if(asked[i].equals("1")) {
							writer.println("> Iniciando el Historial de:"+datas[i].indicator+"\n");
							handler.start();
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
	
	public SocketHandler(DataHandler[] datas, PrintWriter writer){
		this.datas = datas;
		this.writer = writer;
	}
	
	//Lee el buffer del socket s, luego retorna una lista de string
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
	public void run(PrintWriter writer, DataHandler values) {
		try {
			for(int i = 0; i < values.elements.size(); i++) {
				writer.println("Variación de "+values.indicator+": " + values.elements.get(i)+"\n");
			}
			writer.println("Historial de "+values.indicator+" finalizado\n");
		}catch(Exception ex) {ex.printStackTrace();}
	}
	
}