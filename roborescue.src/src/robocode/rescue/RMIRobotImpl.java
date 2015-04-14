
package robocode.rescue;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import robocode.rescue.interfaces.RMIRobotInterface;


public class RMIRobotImpl extends UnicastRemoteObject implements RMIRobotInterface {
  
  private final RMIRobot robot;
  private ArrayList<RobotAction> actionsList;
  private final AtomicBoolean waitingAction = new AtomicBoolean(true);
  
  public RMIRobotImpl(RMIRobot robot) throws RemoteException {
    this.robot = robot;
    actionsList = new ArrayList<RobotAction>();
  }
  
  public ArrayList<RobotAction> waitUntilReady() throws InterruptedException {
    /*synchronized(waitingAction) {
      while (waitingAction.get()) {
          waitingAction.wait();
      }
      System.out.println("actionsList = " + actionsList.size());
    }*/
    if (!waitingAction.get()) {
      ArrayList<RobotAction> actionsCopy = (ArrayList<RobotAction>) actionsList.clone();
      actionsList.clear();
      return actionsCopy;
    } else {
      return null;
    }
  }
  
  public void unblockExecute() {
    synchronized(waitingAction) {
      waitingAction.set(true);
      waitingAction.notify();
    }
  }
  
  @Override
  public RobotInfo getRobotInfo() throws RemoteException {
      RobotInfo info = new RobotInfo(robot.getName(), robot.getRobotIndex(), 
              robot.getX(), robot.getY(), robot.getVelocity(), robot.getHeading());
      return info;
   }

  @Override
  public void execute() throws RemoteException {
    synchronized(waitingAction) {
      try {
        waitingAction.set(false);
        while (!waitingAction.get())
          waitingAction.wait();
      } catch (InterruptedException ex) {
        Logger.getLogger(RMIRobotImpl.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    //System.out.println(">> Execute");
  }

  @Override
  public void ahead(double distance) throws RemoteException {
    RobotAction action = new RobotAction();
    action.asynchronous = false;
    action.acao = RobotAction.MOVE_AHEAD;
    action.distance = distance;
    actionsList.add(action);
  }

  @Override
  public void back(double distance) throws RemoteException {
    RobotAction action = new RobotAction();
    action.asynchronous = false;
    action.acao = RobotAction.MOVE_BACK;
    action.distance = distance;
    actionsList.add(action);
  }

  @Override
  public void turnRight(double angle) throws RemoteException {
    RobotAction action = new RobotAction();
    action.asynchronous = false;
    action.acao = RobotAction.TURN_RIGHT;
    action.turnAng = angle;
    actionsList.add(action);
  }

  @Override
  public void turnLeft(double angle) throws RemoteException {
    RobotAction action = new RobotAction();
    action.asynchronous = false;
    action.acao = RobotAction.TURN_LEFT;
    action.turnAng = angle;
    actionsList.add(action);
  }

  @Override
  public void setAhead(double distance) throws RemoteException {
    RobotAction action = new RobotAction();
    action.asynchronous = true;
    action.acao = RobotAction.MOVE_AHEAD;
    action.distance = distance;
    actionsList.add(action);
    //robot.setAhead(distance);
    //System.out.println(">> setAhead");
  }

  @Override
  public void setBack(double distance) throws RemoteException {
    RobotAction action = new RobotAction();
    action.asynchronous = true;
    action.acao = RobotAction.MOVE_BACK;
    action.distance = distance;
    actionsList.add(action);
  }

  @Override
  public void setTurnRight(double angle) throws RemoteException {
    RobotAction action = new RobotAction();
    action.asynchronous = true;
    action.acao = RobotAction.TURN_RIGHT;
    action.turnAng = angle;
    actionsList.add(action);
    //robot.setTurnRight(angle);
    //ystem.out.println(">> setTurnRight");
  }

  @Override
  public void setTurnLeft(double angle) throws RemoteException {
    RobotAction action = new RobotAction();
    action.asynchronous = true;
    action.acao = RobotAction.TURN_LEFT;
    action.turnAng = angle;
    actionsList.add(action);
  }
  
  @Override
  public void setMaxTurnRate(double rate) throws RemoteException {
    RobotAction action = new RobotAction();
    action.acao = RobotAction.RESUME;
    action.maxTurnRate = rate;
    actionsList.add(action);
  }

  @Override
  public int isFollowing() throws RemoteException {
    return robot.isFollowing();
  }

  @Override
  public double getDistanceRemaining() throws RemoteException {
    return robot.getDistanceRemaining();
  }

  @Override
  public double getTurnRemaining() throws RemoteException {
    return robot.getTurnRemaining();
  }

  @Override
  public void stop() throws RemoteException {
    robot.stop();
  }
  
}
