package bri;


import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;


class ServiceBRi implements Runnable {

	private Socket client;

	ServiceBRi(Socket socket) {
		client = socket;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader (new InputStreamReader(client.getInputStream ( )));
			PrintWriter out = new PrintWriter (client.getOutputStream ( ), true);
			out.println(ServiceRegistry.toStringue()+"##Tapez le num?ro de service d?sir? :");
			int choix = Integer.parseInt(in.readLine());

			if (ServiceRegistry.getActives().get(choix-1) == true) {
				// instancier le service num?ro "choix" en lui passant la socket "client"
				// invoquer run() pour cette instance ou la lancer dans un thread ? part
				synchronized(this) {
					try {
						((Runnable) ServiceRegistry.getServiceClass(choix).getDeclaredConstructor(Socket.class).newInstance(client)).run();
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException | SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

			else out.println("Le service que vous voulez lancer est indisponible ! "
					+ "R?aliser une entr?e clavier et appuyer sur entr?e pour finir la connexion.");

		} catch (IOException e) {
			//Fin du service
		}

		try {client.close();} catch (IOException e2) {}
	}

	protected void finalize() throws Throwable {
		client.close(); 
	}

	// lancement du service
	public void start() {
		(new Thread(this)).start();		
	}

}
