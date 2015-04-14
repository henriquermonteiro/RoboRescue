package conection;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import robocode.rescue.RobotAction;
import robocode.rescue.RobotInfo;

import conection.interfaces.JasonInterface;
import conection.interfaces.RobocodeInterface;
import jason.RoborescueEnvInterface;
import java.awt.geom.Rectangle2D;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import robocode.rescue.interfaces.RMIRobotInterface;


@SuppressWarnings("serial")
public class ServerImpl extends UnicastRemoteObject
        implements JasonInterface, RobocodeInterface {

  private final int teamSize = 5;
  private final ServerWindow window;
  
  private int counterTeamA;
  private int counterTeamB;
  private RMIRobotInterface[] aRobots;
  private RMIRobotInterface[] bRobots;
  private boolean robotsReady;
  private boolean teamAReady;
  private boolean teamBReady;
  private String teamAName;
  private String teamBName;
  private HashMap<String,RoborescueEnvInterface> teamsMap;
  
  // Remover!!!
  private double battlefieldWidth = 2427;
  private double battlefieldHeight = 1500;
  private Rectangle2D.Double aRescueArea = new Rectangle2D.Double(0, 0, 200, battlefieldHeight);
  private Rectangle2D.Double bRescueArea = new Rectangle2D.Double(battlefieldWidth - 200, 0, 200, battlefieldHeight);

  
  public ServerImpl(ServerWindow window) throws RemoteException {
    super();
    this.window = window;
    teamsMap = new HashMap<String,RoborescueEnvInterface>();
  }
  
  /*
   * Métodos acessados pelo servidor
   */
  
  public void initServer(int port) throws RemoteException {
    Registry registry = LocateRegistry.createRegistry(port);
    registry.rebind("RoboRescue", this);
    System.out.println("[Server] Server started in port " + port);
  }
  
  public boolean setup(String teamAName, String teamBName) {
    boolean initOk = true;
    
    counterTeamA = 0;
    counterTeamB = 0;
    aRobots = new RMIRobotInterface[teamSize];
    bRobots = new RMIRobotInterface[teamSize];
    robotsReady = false;
    teamAReady = false;
    teamBReady = false;
    this.teamAName = teamAName;
    this.teamBName = teamBName;
    System.out.println("[Server] " + teamAName + " vs " + teamBName);
    
    RoborescueEnvInterface teamARef = teamsMap.get(teamAName);
    RoborescueEnvInterface teamBRef = teamsMap.get(teamBName);
    if (teamARef == null || teamBRef == null ) {
      initOk = false;
    }
    
    if (initOk) {
      try {
        teamARef.setSide(0);
        teamBRef.setSide(1);
      } catch (RemoteException ex) {
        initOk = false;
      }
    }
    
    return initOk;
  }

  /*
   * Métodos acessados pelos robos
   */
  
  @Override
  public void registerRobot(RMIRobotInterface robotRef, int index, String name) throws RemoteException {
        
    if (name.contains(teamAName)) {
      System.out.println("[Server] Add " + name);
      if (aRobots[index] == null) {
        counterTeamA++;
      }
      aRobots[index] = robotRef;
    } else if (name.contains(teamBName)) {
      System.out.println("[Server] Add " + name);
      if (bRobots[index - teamSize] == null) {
        counterTeamB++;
      }
      bRobots[index - teamSize] = robotRef;
    } else {
      System.out.println("[Server] Team of robot " + name + " not found.");
    }

    if (counterTeamA == teamSize && counterTeamB == teamSize) {
      robotsReady = true;
      System.out.println("[Server] Robots ready");
      window.setRobotsReady();
      //System.out.println("[Server] Waiting for players");
    }
    if (robotsReady && teamAReady && teamBReady) {
      System.out.println("[Server] START!");
    }
  }
  
  public boolean start() {
    boolean startOk = true;
    
    RoborescueEnvInterface teamARef = teamsMap.get(teamAName);
    RoborescueEnvInterface teamBRef = teamsMap.get(teamBName);
    if (teamARef == null || teamBRef == null ) {
      startOk = false;
    }
    
    if (startOk) {
      try {
        teamARef.start();
        teamBRef.start();
      } catch (RemoteException ex) {
        startOk = false;
      }
    }
    
    return startOk;
  }
  
  public boolean stop() {
    boolean stopOk = true;
    
    RoborescueEnvInterface teamARef = teamsMap.get(teamAName);
    RoborescueEnvInterface teamBRef = teamsMap.get(teamBName);
    if (teamARef == null || teamBRef == null ) {
      stopOk = false;
    }
    
    if (stopOk) {
      try {
        teamARef.end();
      } catch (RemoteException ex) {
        stopOk = false;
      }
      try {
        teamBRef.end();
      } catch (RemoteException ex) {
        stopOk = false;
      }
    }
    
    return stopOk;
  }
  
  
  /*
   * Métodos acessados pelo ambiente
   */
  
  @Override
  public void registerTeam(RoborescueEnvInterface teamRef, String teamName) throws RemoteException {
    if (teamsMap.containsKey(teamName)) {
      teamsMap.remove(teamName);
    }
    window.addTeam(teamName);
    teamsMap.put(teamName, teamRef);
  }

  @Override
  public RMIRobotInterface[] getTeamInterfaces(String teamName)  throws RemoteException {
    if (robotsReady && teamName.equals(teamAName)) {
      return aRobots;
    } else if (robotsReady && teamName.equals(teamBName)) {
      return bRobots;
    } else {
      return null;
    }
  }
  
  @Override
  public boolean isStarted() throws RemoteException {
    return (robotsReady && teamAReady && teamBReady);
  }
  
  
  @Override
  public void setReady(String teamName) throws RemoteException {
    if (teamName.equals(teamAName)) {
      teamAReady = true;
      System.out.println("[Server] " + teamName + " ready");
    } else if (teamName.equals(teamBName)) {
      teamBReady = true;
      System.out.println("[Server] " + teamName + " ready");
    }
    if (robotsReady && teamAReady && teamBReady) {
      System.out.println("[Server] READY!");
    }
  }
  
  @Override
  public double getBattlefieldHeight() throws RemoteException {
    return battlefieldHeight;
  }
  
  @Override
  public double getBattlefieldWidth() throws RemoteException {
    return battlefieldWidth;
  }
  
  @Override
  public Rectangle2D.Double getRescueArea(String teamName) throws RemoteException {
    if (teamName.equals(teamAName)) {
      return aRescueArea;
    } else if (teamName.equals(teamBName)) {
      return bRescueArea;
    } else {
      return null;
    }
  }
  
  @Override
  public RobotInfo[] getMyTeamInfo(String teamName) throws RemoteException {
    RobotInfo[] team = new RobotInfo[teamSize];
    if (teamName.equals(teamAName)) {
      for (int i = 0; i < teamSize; i++)
        team[i] = aRobots[i].getRobotInfo();      
    } else if (teamName.equals(teamBName)) {
      for (int i = 0; i < teamSize; i++)
        team[i] = bRobots[i].getRobotInfo();      
    }
    return team;
  }
  
  @Override
  public RobotInfo[] getEnemyTeamInfo(String teamName) throws RemoteException {
    RobotInfo[] team = new RobotInfo[teamSize];
    if (teamName.equals(teamAName)) {
      for (int i = 0; i < teamSize; i++)
        team[i] = bRobots[i].getRobotInfo();      
    } else if (teamName.equals(teamBName)) {
      for (int i = 0; i < teamSize; i++)
        team[i] = aRobots[i].getRobotInfo();      
    }
    return team;
  }
  
  @Override
  public RobotInfo getRobotInfo(String teamName, int robot) throws RemoteException {
    if (teamName.equals(teamAName)) {
      return aRobots[robot].getRobotInfo();      
    } else if (teamName.equals(teamBName)) {
      return bRobots[robot-teamSize].getRobotInfo();      
    }
    return null;
  }
  
  /*
   * Remover !!!!
   */
  /*
  @Override
  public void setRoboInfo(String TeamName, int robo, double x, double y,
          double heading, double velocity, int state) throws RemoteException {
    RobotInfo roboI = getTeam(TeamName)[robo];
    roboI.setX(x);
    roboI.setY(y);
    roboI.setHeading(heading);
    roboI.setVelocity(velocity);
    synchronized (roboI.getAction()) {
      if (roboI.getAction().state != RobotAction.NEW) {
        roboI.getAction().state = state;
      }
    }
  }

  @Override
  public RobotInfo getRoboInfo(String TeamName, int robo) throws RemoteException {
    RobotInfo info = getTeam(TeamName)[robo];
    return info;
  }

  private RobotInfo[] getTeam(String TeamName) {
    if (TeamName.startsWith(ConstantesExecucao.nomeTeamA)) {
      return aRobots;
    } else if (TeamName.startsWith(ConstantesExecucao.nomeTeamB)) {
      return bRobots;
    } else {
      System.err.println("[Server] Time " + TeamName + " nao encontrado");
      return null;
    }
  }
  

  @Override
  public boolean isRefemFollowing(String TeamName) throws RemoteException {
    if (TeamName.startsWith(ConstantesExecucao.nomeTeamA)) {
      if (aHostageFollowing == -1) {
        return false;
      } else {
        return true;
      }
    } else if (TeamName.startsWith(ConstantesExecucao.nomeTeamB)) {
      if (bHostageFollowing == -1) {
        return false;
      } else {
        return true;
      }
    } else {
      System.err.println("[Server] Time " + TeamName + " nao encontrado");
      return false;
    }
  }

  @Override
  public RobotInfo getRefemFollowing(String TeamName) throws RemoteException {
    if (TeamName.startsWith(ConstantesExecucao.nomeTeamA)) {
      if (isRefemFollowing(TeamName)) {
        return aRobots[aHostageFollowing];
      } else {
        return null;
      }
    } else if (TeamName.startsWith(ConstantesExecucao.nomeTeamB)) {
      if (isRefemFollowing(TeamName)) {
        return bRobots[bHostageFollowing];
      } else {
        return null;
      }
    } else {
      System.err.println("[Server] Time " + TeamName + " nao encontrado");
      return null;
    }
  }

  @Override
  public void setFollowing(String TeamName, int robo) throws RemoteException {
    if (TeamName.startsWith(ConstantesExecucao.nomeTeamA)) {
      aHostageFollowing = robo;
    } else if (TeamName.startsWith(ConstantesExecucao.nomeTeamB)) {
      bHostageFollowing = robo;
    } else {
      System.err.println("[Server] Time" + TeamName + "nao encontrado");
    }
  }


  @Override
  public void setLimits(double height, double width) throws RemoteException {
    battlefieldHeight = height;
    battlefieldWidth = width;
  }

  @Override
  public double[] getLimits() throws RemoteException {
    return (new double[]{battlefieldHeight, battlefieldWidth});
  }

  @Override
  public RobotAction getAction(String TeamName, int robo) throws RemoteException {
    RobotInfo roboI = getTeam(TeamName)[robo];
    synchronized (roboI.getAction()) {
      if (roboI.getAction().state == RobotAction.NEW) {
        roboI.getAction().state = RobotAction.FETCH;
      }
    }
    return roboI.getAction();
  }

  @Override
  public void setAction(String TeamName, int robo, RobotAction action) throws RemoteException {
    RobotInfo roboI = getTeam(TeamName)[robo];
    synchronized (roboI.getAction()) {
      roboI.setAction(action);
    }
  }

  @Override
  public void setStart(String TeamName) throws RemoteException {
    if (TeamName.startsWith(ConstantesExecucao.nomeTeamA)) {
      teamAReady = true;
      System.out.println("[Server] " + ConstantesExecucao.nomeTeamA + " joined");
    } else if (TeamName.startsWith(ConstantesExecucao.nomeTeamB)) {
      teamBReady = true;
      System.out.println("[Server] " + ConstantesExecucao.nomeTeamB + " joined");
    }
    if (robotsReady && teamAReady && teamBReady) {
      System.out.println("[Server] START!");
    }
  }
  */
}
