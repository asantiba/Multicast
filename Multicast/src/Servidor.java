import java.net.*; // Socket

//Funcion Main, inicializa el Servidor.
public class Servidor{
	//Intanciar interfaz
	public static Interface viewport;
	
	public static DataHandler data1, data2, data3;
	
	public static void main(String[] args){
		//Iniciar interfaz con el nombre de la ventana
		viewport = new Interface("Servidor Multicast");
		data1 = new DataHandler("Acidez");
		data2 = new DataHandler("Temperatura");
		data3 = new DataHandler("Humedad");
		DataHandler[] datas = {data1, data2, data3};
		
		viewport.screenwrite("> Inicializando servidor\n");
		
		InetAddress target_ip = null;
		MulticastSocket multi_socket = null;
		
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
			
			viewport.screenwrite("> Server ON\n\n");	
		}catch(Exception e) {e.printStackTrace();}
	}
}