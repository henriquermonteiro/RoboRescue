package jason;

import jason.JasonException;

import java.rmi.RemoteException;

public class Jason {

	public Jason() throws JasonException {

		String arg[] = new String[1];
		arg[0] = "masMain.mas2j";
		jason.infra.centralised.RunCentralisedMAS.main(arg);
	}

	public static void main(String[] args) throws JasonException, RemoteException {

		new Jason();
	}
}
