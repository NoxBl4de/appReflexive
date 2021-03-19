package appli;

import bri.ServeurBRi;

public class BRiLaunch {
	private final static int PORT_PROG = 3000;
	private final static int PORT_AMA = 3100;


	public static void main(String[] args) {

		System.out.println("Bienvenue dans votre gestionnaire dynamique d'activité BRi");
		System.out.println("Pour ajouter une activité, celle-ci doit être présente sur votre serveur ftp");
		System.out.println("A tout instant, en tapant le nom de la classe, vous pouvez l'intégrer");
		System.out.println("Les amateurs se connectent au serveur 3100 pour lancer une activité");
		System.out.println("Les programmeurs se connectent au serveur 3000");

		new Thread(new ServeurBRi(PORT_AMA)).start();
		new Thread(new ServeurBRi(PORT_PROG)).start();
		
			
	}
}
