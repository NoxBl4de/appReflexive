package valetoundi;


import java.io.*;
import java.net.*;

import bri.Service;

// rien à ajouter ici
public class ServiceInversion implements Service {
	
	private final Socket client;
	
	public ServiceInversion(Socket socket) {
		client = socket;
	}
	
	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader (new InputStreamReader(client.getInputStream ( )));
			PrintWriter out = new PrintWriter (client.getOutputStream ( ), true);
			
			out.println("BIENVENUE dans le Service d'inversion de texte !##"
					+ "Veuillez entrer un texte à inverser : ");
		
			String line = in.readLine();		
	
			String invLine = new String (new StringBuffer(line).reverse());
			
			out.println("Voici votre texte inversé :##" + invLine);
		}
		catch (IOException e) {
			//Fin du service d'inversion
			System.err.println("Fin du service d'inversion");
			e.printStackTrace();
		}
	}
	
	protected void finalize() throws Throwable {
		 client.close(); 
	}

	public static String toStringue() {
		return "Inversion de texte";
	}
}
