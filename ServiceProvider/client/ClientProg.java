package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ClientProg {
	private final static int PORT_PROG = 3000;
	private final static String HOST = "localhost"; 

	public static void main(String[] args) {
		Socket s = null;		
		try {
			s = new Socket(HOST, PORT_PROG);

			BufferedReader sin = new BufferedReader (new InputStreamReader(s.getInputStream ( )));
			PrintWriter sout = new PrintWriter (s.getOutputStream ( ), true);
			BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in));

			System.out.println("Connecté au serveur " + s.getInetAddress() + " : "+ s.getPort());
			
			//indentification login/mdp
			System.out.println(sin.readLine());
			
			sout.println(clavier.readLine());

			//entrée de l'adresse ftp du programmeur
			System.out.println(sin.readLine());

			sout.println(clavier.readLine());
			
			while(true) {
				//choix de l'action à réaliser
				System.out.println(sin.readLine().replaceAll("##", "\n"));
				
				sout.println(clavier.readLine());
				
				//entrée de l'élément à mettre à jour ou de la commande pour quitter
				System.out.println(sin.readLine().replaceAll("##", "\n"));
				
				sout.println(clavier.readLine());
			} 

		}
		catch (IOException e) { System.err.println("Fin de la connexion"); }
		// Refermer dans tous les cas la socket
		try { if (s != null) s.close(); } 
		catch (IOException e2) { ; }		
	}
}
