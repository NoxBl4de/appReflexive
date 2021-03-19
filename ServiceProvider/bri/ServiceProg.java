package bri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;

class ServiceProg implements Runnable {

	private Socket client;

	ServiceProg(Socket socket) {
		client = socket;
	}

	@SuppressWarnings("resource")
	public void run() {
		// on creer un nouveau compte pour tester la connexion
		Programmeur p = new Programmeur("valetoundi", "azerty4"); 

		try {
			BufferedReader in = new BufferedReader (new InputStreamReader(client.getInputStream ( )));
			PrintWriter out = new PrintWriter (client.getOutputStream ( ), true);
			out.println("Veuillez entrer votre login et mot de passe : (valetoundi azerty4)");

			//on recupere le login et le password entr�s pour les tester
			String[] str = in.readLine().split(" ");
			String login = str[0]; String pass = str[1];


			//System.out.println(login + " " + pass);
			if (!(login.equals(p.getLogin()) && (pass.equals(p.getPassword()))))
					throw new Exception("Le login ou le mot de passe est incorrect !");
			out.print("Bienvenue "+ p.getLogin() + " ! ");

			out.println("Entrez l'adresse de votre serveur ftp : ");

			String serveurFTP = in.readLine(); // on set l'adresse du programmeur

			p.setAdrFTP(serveurFTP);

			URLClassLoader urlcl = new URLClassLoader(new URL[] {
					new URL(p.getAdrFTP())}); // modifi�e plus tard au cas o� un changement d'adresse serait signal�

			// pour eviter d'alt�rer les donn�es lors d'un acc�s concurrenciel
			synchronized(this) { 
				
				while (true){
					out.println("##Que voulez vous faire ? ##" +
							"1 : Fournir un service | 2 : D�marrer un service | 3 : Arr�ter un service##" +
							"4 : Mettre � jour un service | 5 : Changer son adresse FTP | 6 : Desintaller un service##" + 
							"7 : Quitter");

					int choix = Integer.parseInt(in.readLine());

					switch(choix) {
					case 1: // fournir un nouveau service
						try {
							out.println("Veuillez entrer le nom du service � fournir : ");
							String classeName =  in.readLine();
							
							// chargement de la classe et d�claration au ServiceRegistry p.getLogin()+"."+
							ServiceRegistry.addService(urlcl.loadClass(classeName));
							out.print("Service ajout� !");
						} catch (Exception e) {
							System.out.println(e);
							out.print("Service introuvable !");
						}
						break;

					case 2: //d�marrage d'un service
						try {
							// affichage des services disponibles
							out.println(ServiceRegistry.toStringueProg() + 
									"##Veuillez entrer le n� du service � d�marrer :");
							//Enregistrement du choix et changement de statut du service
							int choice = Integer.parseInt(in.readLine());
							ServiceRegistry.getActives().set(choice-1, true);
							out.print("Service d�marr� !");
						} catch (Exception e) {
							System.out.println(e);
							out.print("Service introuvable !");
						}
						break;

					case 3: //Arret d'un service
						try {
							// affichage des services disponibles et indisponibles pour le programmeur
							out.println(ServiceRegistry.toStringueProg() + 
									"##Veuillez entrer le n� du service � arr�ter :");
							//Enregistrement du choix et changement de statut du service
							int choice = Integer.parseInt(in.readLine());
							ServiceRegistry.getActives().set(choice-1, false);
							out.print("Service arr�t� !");
						} catch (Exception e) {
							System.out.println(e);
							out.print("Service introuvable !");
						}
						break;

					case 4: // mise � jour d'un service
						try {
							// affichage des services disponibles et indisponibles pour le programmeur
							out.println(ServiceRegistry.toStringueProg() + 
									"##Veuillez entrer le n� du service � mettre � jour :");
							//Enregistrement du choix et changement de statut du service
							int choice = Integer.parseInt(in.readLine());

							String className = ServiceRegistry.getServiceClass(choice).getSimpleName();

							// on supprime l'ancienne classe et on la remplace par la nouvelle
							
							ServiceRegistry.getServicesClasses().remove(choice-1);
							ServiceRegistry.getActives().remove(choice-1);
							System.gc();
							//on recharge un nouveau classLoader pour effectuer la m�j
							
							URLClassLoader urlcl1 = new URLClassLoader(new URL[] {
									new URL(p.getAdrFTP())});
							
							//on ajoute la classe � liste des classes du ServiceRegistry 
							ServiceRegistry.addService(urlcl1.loadClass(p.getLogin()+"."+className));
							out.print("Service mis � jour !");
						} catch (Exception e) {
							System.out.println(e);
							out.print("Le service n'a pas pu �tre mis � jour !");
						}
						break;

					case 5: // changement d'adresse FTP
						out.println("Veuillez entrer votre nouvelle adresse FTP :");
						String newAddress = in.readLine();
						p.setAdrFTP(newAddress);
						urlcl = new URLClassLoader(new URL[]{new URL(newAddress)});
						break;

					case 6: // D�sinstallation du service
						try {
							// affichage des services disponibles et indisponibles pour le programmeur
							out.println(ServiceRegistry.toStringueProg() + 
									"##Veuillez entrer le n� du service � desinstaller :");
							//Enregistrement du choix et retrait de la liste des services
							int choice = Integer.parseInt(in.readLine());
							
							// supression de la liste des services
							ServiceRegistry.getServicesClasses().remove(choice-1);
							 // suppression dans la listes des actives �galement
							ServiceRegistry.getActives().remove(choice-1);
							
							// on provoque le garbage collector pour effacer toute trace de la classe
							System.gc();
							
							out.print("Service d�sinstall� !");
						} catch (Exception e) {
							System.out.println(e);
							out.print("Service introuvable !");
						}
						break;

					case 7: // Quitter la connexion
						out.print("Realiser une entr�e clavier pour finir la connexion");
						client.close();
						break;

					default:
						out.print("Mauvaise entr�e lors du choix de l'option !");
						break;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
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
