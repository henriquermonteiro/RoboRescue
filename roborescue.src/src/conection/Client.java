package conection;

import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import conection.interfaces.ServerInterface;


public class Client {

    private int port;
    private String host;
    private ServerInterface serverRef;

    public Client(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public void init() {

        ConnectServer cv = new ConnectServer(this, this.port, this.host);
        cv.start();

        try {
            cv.join();
        } catch (InterruptedException ex) {
            System.err.println("Err: Join in Client.initClient()");
        }
    }

    public ServerInterface getServerRef() {
        return serverRef;
    }

    public void setServerRef(ServerInterface serverRef) {
        this.serverRef = serverRef;
    }
}
class ConnectServer extends Thread {

    private int maxTry = 20;
    private ServerInterface serverRef;
    private int port;
    private String host;
    Client client;

    public ConnectServer(Client client, int port, String host) {
        this.client = client;
        this.port = port;
        this.host = host;
    }

    @SuppressWarnings("static-access")
    public void run() {

        int tryCounter = 0;
        boolean finished = false;

        System.out.println("[Client] Initializing conection");

        while (!finished && tryCounter < maxTry) {

            try {
                tryCounter++;
                this.connect();
                finished = true;
                client.setServerRef(this.serverRef);
                System.out.println("[Client] End of conection");
            } catch (RemoteException ex) {
                System.err.println("[Client] Conection error with servers. Try:" + tryCounter + ". host:" + host + ". Port:" 
                        + port + "\nRemaining tries:" + (this.maxTry - tryCounter));
            } catch (NotBoundException ex) {
                System.err.println("[Client] Conection error with servers. Try:" + tryCounter + ". host:" + host + ". Port:" 
                        + port);
            }

            try {
                this.sleep(2000);
            } catch (InterruptedException ex) {
                System.err.println("[Client] Error in wait of Thread: ConnectServer");
            }
        }
    }

    public void connect() throws RemoteException, NotBoundException {
        /*if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }*/
        Registry referenciaRegistry = LocateRegistry.getRegistry(host, port);
        this.serverRef = (ServerInterface) referenciaRegistry.lookup("RoboRescue");
    }
}