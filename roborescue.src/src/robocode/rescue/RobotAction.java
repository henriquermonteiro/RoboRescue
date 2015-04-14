package robocode.rescue;

import java.io.Serializable;
import robocode.Rules;

public class RobotAction implements Serializable {

    private static final long serialVersionUID = 3L;
    public static final String DO_NOTHING = "N";
    public static final String DO_BOTH = "X";
    public static final String TURN_LEFT = "L";
    public static final String TURN_RIGHT = "R";
    public static final String MOVE_AHEAD = "A";
    public static final String MOVE_BACK = "B";
    public static final String RESUME = "C";
    public static final int NEW = 0;
    public static final int FETCH = 1;
    public static final int RUN = 2;
    public static final int DONE = 3;
    
    public double maxVelocity;
    public double maxTurnRate;
    public boolean asynchronous;
    public double turnAng;
    public double distance;
    public String acao;
    public int state;
    
    public RobotAction() {
        acao = DO_NOTHING;
        turnAng = 0.0;
        distance = 0.0;
        asynchronous = true;
        state = NEW;
        maxTurnRate = Rules.MAX_TURN_RATE;
        maxVelocity = Rules.MAX_VELOCITY;
    }

    @Override
    public String toString() {
        String s = acao + ": " + distance + " L" + turnAng + " async = " + asynchronous;
        return s;
    }
}
