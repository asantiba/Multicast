import java.net.Socket;
import java.util.ArrayList;

//Crea los datos, los guarda y los retorna, posee otras utilidades.
class DataHandler{
	String indicator;	//Nombre de lo que se mide
	ArrayList<String> elements = new ArrayList<String>();
	String lastvalue;
	
	//Init
	public DataHandler(String indicator) {
		this.indicator = indicator;
	}
	//Genera valores nuevos para agregar a los arreglos
	public void compute() {
		String value = random(30);
		elements.add(value);
		this.lastvalue = value;
	}
	//Retorna los ultimos valores creados, como string.
	public String get_lastValues() {
		return (indicator +" = " + lastvalue);
	}
	//Genera un numero entre 0 - n
	public String random(double n) {	 		
		return String.valueOf((Math.random() * n));
	}
}

//Es necesario tener un Thread enviando los datos del historial al Socket,
//para poder seguir aceptando otros sockets pientras se ejecuta.
class DataThread extends Thread{
	Socket s;
	DataThread(DataHandler data, Socket s){
		this.s = s;
	}	
	public void run(){
		try{	
		}catch (Exception ex) {ex.printStackTrace();}
	}
}