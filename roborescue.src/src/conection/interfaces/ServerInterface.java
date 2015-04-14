package conection.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import robocode.rescue.RobotInfo;


public interface ServerInterface extends Remote {

	boolean isStarted()throws RemoteException;
    RobotInfo[] getMyTeamInfo(String teamName)throws RemoteException;
    RobotInfo[] getEnemyTeamInfo(String teamName)throws RemoteException;
	RobotInfo getRobotInfo(String teamName, int robot)throws RemoteException;
    /*
	public RobotInfo[] getTeamInfo(String TeamName)throws RemoteException;
    */
	
}
