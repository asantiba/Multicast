import javax.swing.*;
import java.awt.*;
import java.net.*; // Socket
//import java.awt.event.*; // Manejo de eventos

//Funcion Main, inicializa el Servidor.
public class Servidor{
	//Para escribir en pantalla viewport.screenwrite("string")
	public static Interface viewport;
	public static DataHandler data1,data2,data3;
	
	public static void main(String[] args){
		viewport = new Interface();
		data1 = new DataHandler("Acidez");
		data2 = new DataHandler("Temperatura");
		data3 = new DataHandler("Humedad");
		DataHandler[] datas = {data1, data2, data3};
		
		viewport.screenwrite("> Inicializando servidor\n");
		
		
		InetAddress target_ip;
		MulticastSocket multi_socket;
		
		try {
			target_ip = InetAddress.getByName("230.0.0.4");
			multi_socket = new MulticastSocket();
			
			MultisocketServer multi_server_1 = new MultisocketServer(multi_socket,target_ip, 10033, data1, viewport);
			MultisocketServer multi_server_2 = new MultisocketServer(multi_socket,target_ip, 10034, data2, viewport);
			MultisocketServer multi_server_3 = new MultisocketServer(multi_socket,target_ip, 10035, data3, viewport);
			SocketServer server_t = new SocketServer(viewport, datas);
			
			multi_server_1.start();
			multi_server_2.start();
			multi_server_3.start();
			server_t.start();
			
			viewport.screenwrite("> Server ON\n");	
		}catch(Exception e) {e.printStackTrace();}
	}
}

//Thread del Servidor del historial, llama al SocketHandler para realizar cualquier operacion en un socket
//y DataHandler para obtener cualquier dato. Cuando se socilita historial, se llama a DataThread (ESTO NO ESTA IMPLEMENTADO AUN)
//Para poder enviar el historial y seguir podiendo recibir otros sockets.


	







