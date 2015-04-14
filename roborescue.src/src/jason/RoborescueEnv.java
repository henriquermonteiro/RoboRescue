package jason;

import jason.asSyntax.Structure;
import jason.environment.Environment;

import java.rmi.RemoteException;

import conection.Client;
import conection.interfaces.JasonInterface;
import java.util.concurrent.atomic.AtomicBoolean;
import robocode.rescue.interfaces.RMIRobotInterface;


public abstract class RoborescueEnv extends Environment {

    public final int LEFT_SIDE = 0;
    public final int RIGHT_SIDE = 1;
    
    private Client client;
    private JasonInterface serverRef;
    private RMIRobotInterface[] teamRef;
    private RoborescueEnvInterface envRef;
    private final AtomicBoolean hasSide = new AtomicBoolean(false);
    private final AtomicBoolean isStarted = new AtomicBoolean(false);
    protected String myTeam;
    protected int side;

    @Override
    public void init(String[] args) {

      try {
        myTeam = args[0];
        envRef = new RoborescueEnvImpl(this);
        client = new Client(1313, args[1]);
        client.init();
        System.out.println("[Client] Connection established with " + args[1] + ":1313");
        serverRef = (JasonInterface) client.getServerRef();
        serverRef.registerTeam(envRef, myTeam);
        
        waitASide(); 
                
        serverRef.setReady(myTeam);
        
        System.out.println("[Client] Waiting for the game start");
        waitStart();
        System.out.println("[Client] START!");
                
        teamRef = serverRef.getTeamInterfaces(myTeam);
        
        setup();
        
      } catch (RemoteException e) {
        e.printStackTrace();
        //System.exit(1);
      } catch (InterruptedException ex) {
        ex.printStackTrace();
        System.exit(1);
      }

    }
    
    public void setSide(int side) {
      this.side = side;
      synchronized(hasSide) {
        hasSide.set(true);
        hasSide.notify();
      }
    }
    
    public void start() {
      synchronized(isStarted) {
        isStarted.set(true);
        isStarted.notify();
      }
    }
    
    private void waitASide() throws InterruptedException{
      synchronized(hasSide) {
        while (!hasSide.get()) {
          hasSide.wait();
        }
      }
    }
    
    private void waitStart() throws InterruptedException {
      synchronized(isStarted) {
        while (!isStarted.get()) {
          isStarted.wait();
        }
      }
    }
    
    public RMIRobotInterface[] getTeamRef() {
        return teamRef;
    }
    
    public JasonInterface getServerRef() {
        return serverRef;
    }

    public abstract void setup();

    @Override
    public abstract boolean executeAction(String ag, Structure action);
    
    public abstract void end();

}