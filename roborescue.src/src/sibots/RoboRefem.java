package sibots;

import static robocode.util.Utils.normalAbsoluteAngle;
import static robocode.util.Utils.normalRelativeAngle;

import java.awt.Color;
import java.awt.Point;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.RoundEndedEvent;
import robocode.WinEvent;
import robocode.rescue.RMIRobot;
import robocode.rescue.RobotInfo;

public class RoboRefem extends RMIRobot {

    private List<String> deadRobots;

    public RoboRefem() {
    }
    
    @Override
    public void setup() {
        deadRobots = new ArrayList<String>();
    }    

    @Override
    public void mainLoop() {
        try {
            if (target != null) {
                target = serverRef.getRobotInfo(myTeam, target.getRobotIndex());
                double distancia = Point.distance(target.getX(), target.getY(), getX(), getY());
                if (distancia > 150) {
                    target = null;
                    //setRobotColor();
                    //serverRef.setFollowing(myTeam, -1);
                }
            }
            if (target == null) {
                RobotInfo[] robots = serverRef.getMyTeamInfo(myTeam);
                for (RobotInfo ri : robots) {
                    if (ri.getRobotIndex() != this.getRobotIndex() && !deadRobots.contains(ri.getName())) {
                        double distance = Point.distance(ri.getX(), ri.getY(), getX(), getY());
                        System.out.println(ri.getRobotIndex() + " ; " + this.getRobotIndex() + ":   " + distance);
                        if (distance <= 150) {
                            target = ri;
                            System.out.println("target:" + target.getName());
                            //serverRef.setFollowing(myTeam, ri.getRobotIndex());
                            break;
                        }
                    }
                }
            }
            if (target != null) {
                seguetarget();
            } else {
                System.out.println("doNothing");
                setVelocity(0);
                doNothing();
            }
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void seguetarget() {
        System.out.println("target:" + target.getName());
        double angle = normalAbsoluteAngle(Math.atan2(target.getX() - getX(), target.getY() - getY()));
        double turn = normalRelativeAngle(angle - getGunHeadingRadians()) * 180.0 / Math.PI;
        System.out.println("Angulo:" + turn);
        if (turn >= 0 && turn <= 180) {
            setTurnRight(10);
        }
        if (turn < 0 && turn >= -180) {
            setTurnLeft(10);
        }

        //waitFor(new TurnCompleteCondition(this));
        setVelocity(Math.abs(target.getVelocity()));
        if (Point.distance(target.getX(), target.getY(), getX(), getY()) < 60) {
            setVelocity(0);
            System.out.println("Velocidade: 0.0 (dist < 60)");
        } else {
            System.out.println("Velocidade: " + target.getVelocity());
        }
    }

    @Override
    public void setRobotColor() {
        if (getRobotIndex() < 5) {
            setColors(Color.RED, Color.BLUE, Color.BLACK);
        } else {
            setColors(Color.RED, Color.GREEN, Color.BLACK);
        }
    }
    
    private void setVelocity(double velocityRate) {
        setMaxVelocity(velocityRate);
        if (velocityRate > 0) {
            setAhead(Double.POSITIVE_INFINITY);
        } else if (velocityRate < 0) {
            setBack(Double.POSITIVE_INFINITY);
        } else {
            setAhead(0);
        }
    }

    @Override
    public void onHitWall(HitWallEvent e) {
        setMaxVelocity(8);
        double angulo = e.getBearing();
        if (angulo > -90 && angulo <= 90) {
            setBack(100);
        } else {
            setAhead(100);
        }
        execute();
    }
    
    @Override
    public void onHitRobot(HitRobotEvent e) {
        double angulo = e.getBearing();
        if (angulo > -90 && angulo <= 90) {
            setBack(100);
        } else {
            setAhead(100);
        }
        execute();
    }
    
     @Override
    public void onRobotDeath(RobotDeathEvent event) {
      String sender = event.getName();
      out.println("\n" +sender + " DIED!\n");
      if (target != null) {
        if (sender.equals(target.getName())) {
          deadRobots.add(target.getName());
          target = null;
        }
      }
    }
    
    @Override
    public void onWin(WinEvent event) {
        setAhead(0);
        turnRight(3600);
    }
    
    @Override
    public void onRoundEnded(RoundEndedEvent event) {
        battleEnded = true;
    }
    
}
