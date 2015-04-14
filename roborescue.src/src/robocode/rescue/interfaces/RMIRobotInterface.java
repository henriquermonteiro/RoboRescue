
package robocode.rescue.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import robocode.rescue.RobotInfo;


public interface RMIRobotInterface extends Remote {
  
  RobotInfo getRobotInfo() throws RemoteException;
  
  void execute() throws RemoteException;
  
  void ahead(double distance) throws RemoteException;
  
  void back(double distance) throws RemoteException;
  
  void turnRight(double angle) throws RemoteException;
  
  void turnLeft(double angle) throws RemoteException;
  
  void setAhead(double distance) throws RemoteException;
  
  void setBack(double distance) throws RemoteException;
  
  void setTurnRight(double angle) throws RemoteException;
  
  void setTurnLeft(double angle) throws RemoteException;
  
  void setMaxTurnRate(double rate) throws RemoteException;
  
  int isFollowing() throws RemoteException;
  
  double getDistanceRemaining() throws RemoteException;
  
  double getTurnRemaining() throws RemoteException;
  
  void stop() throws RemoteException;
          
}
