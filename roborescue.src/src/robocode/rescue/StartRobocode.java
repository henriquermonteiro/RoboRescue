package robocode.rescue;

import util.ConstantesExecucao;
import util.Transferencia;

public class StartRobocode {

    public static void main(String[] args) {
        //ConstantesExecucao.start("server");
        //Transferencia.transfere();
        BattleManager battle;
        battle = new BattleManager(1);
        if(args.length > 0) {
          battle.start("TimeA", "TimeB", Long.parseLong(args[0]));
        } else  {
          battle.start("TimeA", "TimeB", 0);
        }
        
    }
}
