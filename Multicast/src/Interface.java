import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.*;

class Interface{
	JFrame window;
	JTextArea area;
	JScrollPane scroll;
	JPanel container_area;
	
	public Interface(String name) {
		window = new JFrame(name); // Ventana con nombre Servidor
		area = new JTextArea(20, 40); // Area para insertar texto
		scroll = new JScrollPane(area); // Barra para desplazar hacia abajo
		container_area = new JPanel();
		config();
	}
	
	//Configuraci�n de la interfaz
	public void config() {	
		container_area.setLayout(new GridLayout(1, 1));
		container_area.add(scroll);
		window.setLayout(new BorderLayout());
		window.add(container_area, BorderLayout.NORTH);
		window.setSize(500, 350);
		window.setVisible(true);
		window.setResizable(false); // Bloquea el cambio de tama�o
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	//Para escribir en pantalla viewport.screenwrite("string")
	public void screenwrite(String string) {
		area.append(string);
	}
}