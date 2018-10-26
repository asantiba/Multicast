import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Timer;
import java.util.TimerTask;

//Configuracion del server del socket y ejecuciones del socket.
class MultisocketServer extends Thread{
	InetAddress target_ip;			//Ip a los que se envian los MultiCast
	int target_port;				//Puerto por donde se mandaran los mensajes.
	MulticastSocket multi_socket;	//El Multicast Socket
	Interface viewport;				//De aqui se imprime en pantalla viewport.screenwrite("str")
	DataHandler data;				//De aqui se obtienen los datos.
	
	public MultisocketServer(MulticastSocket multi_socket,InetAddress target_ip, int target_port, DataHandler data, Interface viewport) {
		this.viewport = viewport;
		this.multi_socket = multi_socket;
		this.target_ip = target_ip;
		this.target_port = target_port;
		this.data = data;
		viewport.screenwrite("> Multicast IP mandando "+data.indicator+" a: "+this.target_ip+":"+this.target_port+"\n");

	}
	
	//Escribe un paquete
	public DatagramPacket write(String string) {
		byte[] buffer = string.getBytes();
		return new DatagramPacket(buffer, buffer.length, target_ip, target_port);
	
	}
	
	public void run(){
		try {
			//Crea el tarea programada de los datos.
			TimerTask timerTask = new TimerTask(){
				public void run(){ //Ejecucion	             
					try {
					data.compute(); //Se crean los datos.
		            multi_socket.send(write(data.lastvalue)); //Se crea un paquete con los ultimos datos.
		            String line = "> Variacion de " + data.indicator + ": " + data.lastvalue + "\n";
		            viewport.screenwrite(line);
					}catch(Exception ex) {ex.printStackTrace();}	
				} 
		     };  
		     Timer timer = new Timer(); 
		     //Inicia la tarea, desde 0seg, cada 3seg
		     timer.scheduleAtFixedRate(timerTask, 0, 2000);
		} catch(Exception ex) {ex.printStackTrace();}
	}	
}
