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

			//on recupere le login et le password entrés pour les tester
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
					new URL(p.getAdrFTP())}); // modifiée plus tard au cas où un changement d'adresse serait signalé

			// pour eviter d'altérer les données lors d'un accès concurrenciel
			synchronized(this) { 
				
				while (true){
					out.println("##Que voulez vous faire ? ##" +
							"1 : Fournir un service | 2 : Démarrer un service | 3 : Arrêter un service##" +
							"4 : Mettre à jour un service | 5 : Changer son adresse FTP | 6 : Desintaller un service##" + 
							"7 : Quitter");

					int choix = Integer.parseInt(in.readLine());

					switch(choix) {
					case 1: // fournir un nouveau service
						try {
							out.println("Veuillez entrer le nom du service à fournir : ");
							String classeName =  in.readLine();
							
							// chargement de la classe et déclaration au ServiceRegistry p.getLogin()+"."+
							ServiceRegistry.addService(urlcl.loadClass(classeName));
							out.print("Service ajouté !");
						} catch (Exception e) {
							System.out.println(e);
							out.print("Service introuvable !");
						}
						break;

					case 2: //démarrage d'un service
						try {
							// affichage des services disponibles
							out.println(ServiceRegistry.toStringueProg() + 
									"##Veuillez entrer le n° du service à démarrer :");
							//Enregistrement du choix et changement de statut du service
							int choice = Integer.parseInt(in.readLine());
							ServiceRegistry.getActives().set(choice-1, true);
							out.print("Service démarré !");
						} catch (Exception e) {
							System.out.println(e);
							out.print("Service introuvable !");
						}
						break;

					case 3: //Arret d'un service
						try {
							// affichage des services disponibles et indisponibles pour le programmeur
							out.println(ServiceRegistry.toStringueProg() + 
									"##Veuillez entrer le n° du service à arrêter :");
							//Enregistrement du choix et changement de statut du service
							int choice = Integer.parseInt(in.readLine());
							ServiceRegistry.getActives().set(choice-1, false);
							out.print("Service arrêté !");
						} catch (Exception e) {
							System.out.println(e);
							out.print("Service introuvable !");
						}
						break;

					case 4: // mise à jour d'un service
						try {
							// affichage des services disponibles et indisponibles pour le programmeur
							out.println(ServiceRegistry.toStringueProg() + 
									"##Veuillez entrer le n° du service à mettre à jour :");
							//Enregistrement du choix et changement de statut du service
							int choice = Integer.parseInt(in.readLine());

							String className = ServiceRegistry.getServiceClass(choice).getSimpleName();

							// on supprime l'ancienne classe et on la remplace par la nouvelle
							
							ServiceRegistry.getServicesClasses().remove(choice-1);
							ServiceRegistry.getActives().remove(choice-1);
							System.gc();
							//on recharge un nouveau classLoader pour effectuer la màj
							
							URLClassLoader urlcl1 = new URLClassLoader(new URL[] {
									new URL(p.getAdrFTP())});
							
							//on ajoute la classe à liste des classes du ServiceRegistry 
							ServiceRegistry.addService(urlcl1.loadClass(p.getLogin()+"."+className));
							out.print("Service mis à jour !");
						} catch (Exception e) {
							System.out.println(e);
							out.print("Le service n'a pas pu être mis à jour !");
						}
						break;

					case 5: // changement d'adresse FTP
						out.println("Veuillez entrer votre nouvelle adresse FTP :");
						String newAddress = in.readLine();
						p.setAdrFTP(newAddress);
						urlcl = new URLClassLoader(new URL[]{new URL(newAddress)});
						break;

					case 6: // Désinstallation du service
						try {
							// affichage des services disponibles et indisponibles pour le programmeur
							out.println(ServiceRegistry.toStringueProg() + 
									"##Veuillez entrer le n° du service à desinstaller :");
							//Enregistrement du choix et retrait de la liste des services
							int choice = Integer.parseInt(in.readLine());
							
							// supression de la liste des services
							ServiceRegistry.getServicesClasses().remove(choice-1);
							 // suppression dans la listes des actives également
							ServiceRegistry.getActives().remove(choice-1);
							
							// on provoque le garbage collector pour effacer toute trace de la classe
							System.gc();
							
							out.print("Service désinstallé !");
						} catch (Exception e) {
							System.out.println(e);
							out.print("Service introuvable !");
						}
						break;

					case 7: // Quitter la connexion
						out.print("Realiser une entrée clavier pour finir la connexion");
						client.close();
						break;

					default:
						out.print("Mauvaise entrée lors du choix de l'option !");
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
