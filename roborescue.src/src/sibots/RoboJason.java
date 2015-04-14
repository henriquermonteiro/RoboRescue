
package sibots;

import robocode.RoundEndedEvent;
import robocode.WinEvent;
import robocode.rescue.RMIRobot;
import robocode.rescue.RobotAction;

public class RoboJason extends RMIRobot {

    public RoboJason() {
        //Nao Utiliza nenhum metodo extendido da classe TeamRobot aqui se Nao o robo explode
        //Se precisar, utilize no mainLoop() ou depois
    }
    
    //O setup e rodado antes do mainLoop(), caso precise fazer alguma configuracao fora do loop principal
    @Override
    public void setup() {
        // TODO Auto-generated method stub
    }

    @Override
    public void mainLoop() {
      
        System.out.println(">> mainLoop <<");
        for (int i = 0; i < actionsList.size(); i++) {
          RobotAction action = actionsList.get(i);
          
          if (action.acao.equals(RobotAction.MOVE_AHEAD)) {
            if (action.asynchronous) {
              System.out.println(">> setAhead " + action.distance);
              setAhead(action.distance);
            } else {
              System.out.println(">> ahead " + action.distance);
              ahead(action.distance);
            }
          } else if (action.acao.equals(RobotAction.MOVE_BACK)) {
            if (action.asynchronous) {
              System.out.println(">> setBack " + action.distance);
              setBack(action.distance);
            } else {
              System.out.println(">> back " + action.distance);
              back(action.distance);
            }
          } else if (action.acao.equals(RobotAction.TURN_RIGHT)) {
            if (action.asynchronous) {
              System.out.println(">> setTurnRight " + action.turnAng);
              setTurnRight(action.turnAng);
            } else {
              System.out.println(">> turnRight " + action.turnAng);
              turnRight(action.turnAng);
            }
          } else if (action.acao.equals(RobotAction.TURN_LEFT)) {
            if (action.asynchronous) {
              System.out.println(">> setTurnLeft " + action.turnAng);
              setTurnLeft(action.turnAng);
            } else {
              System.out.println(">> turnLeft " + action.turnAng);
              turnLeft(action.turnAng);
            }
          } else if (action.acao.equals(RobotAction.RESUME)) {
            System.out.println(">> maxTurnRate " + action.maxTurnRate);
            setMaxTurnRate(action.maxTurnRate);
            
          }
        }
        
        //execute();
        actionsList.clear();
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
