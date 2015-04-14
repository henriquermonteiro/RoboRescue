package conection.interfaces;

import java.rmi.RemoteException;

import robocode.rescue.RobotAction;
import robocode.rescue.RobotInfo;
import robocode.rescue.interfaces.RMIRobotInterface;

public interface RobocodeInterface extends ServerInterface {

	//metodos que devem ser chamdos pelo robo
	/*
    public RobotAction getAction(String TeamName, int robo) 
                throws RemoteException;
	public void setRoboInfo(String TeamName, int robo, double x, double y, 
                double heading, double velocity, int state) 
                throws RemoteException;
	public void setLimits(double height, double width) 
                throws RemoteException;
	
	//metodos que sao chamados pelo refem
	public void setFollowing(String TeamName, int robo) 
                throws RemoteException;
    */
    
    
	void registerRobot(RMIRobotInterface robotRef, int index, String name) throws RemoteException;
}
