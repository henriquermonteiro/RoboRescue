
package jason;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class RoborescueEnvImpl extends UnicastRemoteObject implements RoborescueEnvInterface {

  RoborescueEnv env;
  
  public RoborescueEnvImpl(RoborescueEnv env) throws RemoteException {
    super();
    this.env = env;
  }
  
  
  @Override
  public void start() throws RemoteException {
    env.start();
  }
  
  @Override
  public void setSide(int side) throws RemoteException {
    env.setSide(side);
  }

  @Override
  public void end() throws RemoteException {
    env.end();
  }
  
}
