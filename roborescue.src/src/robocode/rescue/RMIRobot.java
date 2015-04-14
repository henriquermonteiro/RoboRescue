
package robocode.rescue;

import java.awt.Color;
import java.rmi.RemoteException;

import robocode.TeamRobot;
import conection.Client;
import conection.interfaces.RobocodeInterface;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import robocode.rescue.interfaces.RMIRobotInterface;


public abstract class RMIRobot extends TeamRobot {

    protected Client client;
    protected RobocodeInterface serverRef;
    protected RMIRobotImpl robotRef;
    protected String myTeam;
    protected String enemyTeam;
    protected RobotInfo target;
    protected boolean battleEnded;
    protected ArrayList<RobotAction> actionsList;
    

    public RMIRobot() {
      try {
        System.out.println("Conecting with RMI server");

        client = new Client(1313, "localhost");
        client.init();
        serverRef = (RobocodeInterface) client.getServerRef();
        robotRef = new RMIRobotImpl(this);
        battleEnded = false;
        actionsList = new ArrayList<RobotAction>();
      } catch (RemoteException ex) {
        System.out.println("RMI error");
        ex.printStackTrace();
      }
    }

    public void run() {
        try {
            setupRobot();
        } catch (RemoteException e) {
            e.printStackTrace();
            System.out.println("RemoteException: setup: " + e.getMessage());
            return;
        }

        //while (!battleEnded && getEnergy() != 0) {
        while (true) {
            try {
                if (!serverRef.isStarted()) {
                    doNothing();
                    execute();
                } else {
                  if (getRobotIndex() % 5 != 0) {
                    actionsList = robotRef.waitUntilReady();
                    if (actionsList != null)
                      mainLoop();
                    robotRef.unblockExecute();
                  } else {
                    mainLoop();
                  }
                  execute();
                  //Thread.sleep(10);
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException ex) {
                Logger.getLogger(RMIRobot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void setupRobot() throws RemoteException {
      
      serverRef.registerRobot((RMIRobotInterface) robotRef, getRobotIndex(), getName());
      setRobotColor();
      
      String name = getName();
      int num = getRobotIndex();
      if (getRobotIndex() % 5 != 0) {
        myTeam = name.substring(0, name.indexOf('*'));
      } else {
        myTeam = name.substring(0, name.indexOf("Refem"));
      }
      System.out.println(name + " (" + myTeam + "): index = " + num);
      
      setup();
    }
    
    public int isFollowing() {
      if (target != null) {
        return (target.getRobotIndex() % 5);
      } 
      return 0;
    }

    public void setRobotColor() {
        if (getRobotIndex() < 5) {
            setColors(Color.BLUE, Color.BLUE, Color.BLACK);
        } else {
            setColors(Color.GREEN, Color.GREEN, Color.BLACK);
        }
    }

    public abstract void mainLoop();

    public abstract void setup();

    /*
    public RobotInfo[] getTeamInfo() {

        try {
            RobotInfo[] teamInfo = referenciaServidor.getTeamInfo(team);
            return teamInfo;
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public RobotInfo getTeammate(int robo) {
        try {
            RobotInfo teammate = referenciaServidor.getRoboInfo(team, robo);
            return teammate;
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public void getRoboAction() {
        try {
            RobotAction action = referenciaServidor.getAction(team, info.getNumRobo());
            info.setAction(action);

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setRoboInfo() {

        //System.out.println("dist rem: " + getDistanceRemaining() +
        //        "\nturn rem: " + getTurnRemaining());
        info.setHeading(getHeading());
        info.setX(getX());
        info.setY(getY());
        info.setVelocity(getVelocity());
        try {
            referenciaServidor.setRoboInfo(team, info.getNumRobo(), info.getX(),
                    info.getY(), info.getHeading(), info.getVelocity(), info.getAction().state);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    */
}
