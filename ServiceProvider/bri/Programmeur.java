package bri;

class Programmeur {

	private String login, password;
	private String adrFTP;
	
	public Programmeur(String log, String mdp) {
		setLogin(log); setPassword(mdp);
	}

	public String getAdrFTP() {
		return adrFTP;
	}

	public void setAdrFTP(String adrFTP) {
		this.adrFTP = adrFTP;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
