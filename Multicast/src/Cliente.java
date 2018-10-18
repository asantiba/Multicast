import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class Cliente {
	JFrame window = null;
	JTextArea area = null;
	JPanel container_area = null;
	JScrollPane scroll = null;
	// Variables socket uni
	Socket socket = null;
	BufferedReader reader = null; //Permite leer mensajes
	PrintWriter writer = null; //Permite enviar mensajes
	
	public Cliente() {
		Interface();
	}
	
	public void Interface() {
		window = new JFrame("Cliente");
		area = new JTextArea(20,4);
		scroll = new JScrollPane(area);
		container_area = new JPanel();
		container_area.setLayout(new GridLayout(1,1));
		container_area.add(scroll);
		window.setLayout(new BorderLayout());
		window.add(container_area, BorderLayout.NORTH);
		window.setSize(500, 350);
		window.setVisible(true);
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Thread principal
		Thread init = new Thread(new Runnable() {
			public void run() {
				try {
					socket = new Socket("localhost", 9000);
					read();
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		init.start();
	}
	
	public void read() {
		Thread thread_reader = new Thread(new Runnable() {
			public void run() {
				try {
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					while(true) {
						String msg_received = reader.readLine();
						area.append(msg_received);
					}
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		thread_reader.start();
	}

	public static void main(String[] args) {
		new Cliente();
	}

}
