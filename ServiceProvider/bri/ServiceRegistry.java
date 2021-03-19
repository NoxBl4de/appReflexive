package bri;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.util.Vector;

public class ServiceRegistry {
	// cette classe est un registre de services
	// partagée en concurrence par les clients et les "ajouteurs" de services,
	// un Vector pour cette gestion est pratique

	static {
		servicesClasses = new Vector<Class<?>>();
		actives = new Vector<Boolean>();
	}
	
	private static Vector<Class<?>> servicesClasses;
	private static Vector<Boolean> actives;

	// ajoute une classe de service après contrôle de la norme BLTi
	public static void addService(Class<?> classe) throws Exception {
		// vérifier la conformité par introspection
		// si non conforme --> exception avec message clair
		// si conforme, ajout au vector
		boolean conform = false;

		for(Class<?> e : classe.getInterfaces()) {
			if (e.getSimpleName().equals("Service")) { 
				conform = true; 
				break;
			}
			else conform = false;
		}

		if (!Modifier.isAbstract(classe.getModifiers()) && Modifier.isPublic(classe.getModifiers()))
			conform = true;
		else conform = false;

		try {
			if (classe.getConstructor(Socket.class) != null)
				conform = true;
			else conform = false;
		} catch (NoSuchMethodException | SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

		for(Field f : classe.getDeclaredFields()) {
			if (f.getType().getSimpleName().equals("Socket") && Modifier.isPrivate(f.getModifiers()) && Modifier.isFinal(f.getModifiers())) {
				conform = true;
				break;
			}
		}

		for(Method m : classe.getDeclaredMethods()) {
			if (m.getName().equals("toStringue") && Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
				conform = true;
				break;
			}
			else conform = false;
		}

		if (conform == false) throw new Exception("Cette classe n'est pas conforme aux normes BRi !");

		servicesClasses.addElement(classe);
		actives.addElement(true);

	}

	// renvoie la classe de service (numService -1)	
	public static Class<?> getServiceClass(int numService) {
		return servicesClasses.elementAt(numService-1);
	}

	// liste les activités présentes
	public static String toStringue() {
		String result = "Activités présentes :##";

		for (int i = 0; i < servicesClasses.size(); ++i) {
			if (actives.get(i) == true) {
				result += "N°" + String.valueOf(i+1) + " : ";
				result += servicesClasses.get(i).getSimpleName() + "##";
			}
		}
		return result;
	}

	// liste les activités présentes et celles inactives pour le programmeur
	public static String toStringueProg() {
		String result = "Activités présentes :##";

		for (int i = 0; i < servicesClasses.size(); ++i) {
			if (actives.get(i) == true) {
				result += "N°" + String.valueOf(i+1) + " : ";
				result += servicesClasses.get(i).getSimpleName() + "##";
			}
			else result += "Le service n° " + String.valueOf(i+1) + " n'est pas actif##";
		}
		return result;
	}

	// renvoie la liste des classes
	public static Vector<Class<?>> getServicesClasses() {
		return servicesClasses;
	}

	// renvoie la liste des services actifs
	public static Vector<Boolean> getActives() {
		return actives;
	}

}
