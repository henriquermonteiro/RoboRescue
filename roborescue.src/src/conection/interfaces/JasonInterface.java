package conection.interfaces;

import jason.RoborescueEnvInterface;
import java.awt.geom.Rectangle2D;
import java.rmi.RemoteException;

import robocode.rescue.RobotAction;
import robocode.rescue.RobotInfo;
import robocode.rescue.interfaces.RMIRobotInterface;

public interface JasonInterface extends ServerInterface  {

	//metodos que sao chamados pelo ambiente
	/*
    double[] getLimits()throws RemoteException;
	RobotInfo getRefemFollowing(String teamName)throws RemoteException;
	boolean isRefemFollowing(String teamName)throws RemoteException;
	void setAction(String teamName, int robo, RobotAction action)throws RemoteException;
    */
    
    void registerTeam(RoborescueEnvInterface teamRef, String teamName)throws RemoteException;
	void setReady(String teamName)throws RemoteException;
    double getBattlefieldHeight() throws RemoteException;
    double getBattlefieldWidth() throws RemoteException;
    Rectangle2D.Double getRescueArea(String teamName) throws RemoteException;
    RMIRobotInterface[] getTeamInterfaces(String teamName)  throws RemoteException;
}
