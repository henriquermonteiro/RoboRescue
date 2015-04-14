
package jason;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface RoborescueEnvInterface extends Remote {
  
  void start() throws RemoteException;
  void setSide(int side) throws RemoteException;
  void end() throws RemoteException;
  
}
