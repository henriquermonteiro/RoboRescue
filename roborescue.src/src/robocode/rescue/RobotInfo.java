package robocode.rescue;

import java.io.Serializable;

public class RobotInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final int Refem = 0;
    public static final int Robo1 = 1;
    public static final int Robo2 = 2;
    public static final int Robo3 = 3;
    public static final int Robo4 = 4;
    
    private double x;
    private double y;
    private double velocity;
    private double heading;
    private String name;
    private int robotIndex;
    private RobotAction action;

    public RobotInfo(String name) {
        this.name = name;
        action = new RobotAction();
    }
    
    public RobotInfo(String name, int robotIndex, double x, double y, double velocity, double heading) {
      this.name = name;
      this.robotIndex = robotIndex;
      this.x = x;
      this.y = y;
      this.velocity = velocity;
      this.heading = heading;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public RobotAction getAction() {
        return action;
    }

    public void setAction(RobotAction action) {
        this.action.acao = action.acao;
        this.action.maxTurnRate = action.maxTurnRate;
        this.action.maxVelocity = action.maxVelocity;
        if (!action.acao.equals(RobotAction.RESUME)) {
            this.action.asynchronous = action.asynchronous;
            this.action.distance = action.distance;
            this.action.turnAng = action.turnAng;
            this.action.state = action.state;
        }
    }

    public String getName() {
        return name;
    }

    public int getRobotIndex() {
        return robotIndex;
    }

    public void setRobotIndex(int robotIndex) {
        this.robotIndex = robotIndex;
    }
}