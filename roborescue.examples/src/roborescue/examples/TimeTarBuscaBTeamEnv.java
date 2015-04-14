/* Este programa eh modelo para ser utilizado na tarefa de busca A* x LRTA*
 da disciplina de Sistemas Inteligentes 1.
 O time TarBuscaB contem robos que deverao ser posicionados no campo (eles nao
 se mexem apos este posicionamento inicial)
 */
package roborescue.examples;

import atuador.AtuadorAssincrono;
import jason.RoborescueEnv;
import jason.asSyntax.Structure;
import java.rmi.RemoteException;
import robocode.rescue.RobotInfo;
import robocode.rescue.interfaces.RMIRobotInterface;

public class TimeTarBuscaBTeamEnv extends RoborescueEnv {

    private static final String nomeTime = "TimeTarBuscaB";
    private final int numRobos = 5;
    private RMIRobotInterface[] aliados;
    private RobotInfo[] inimigos;
    private char meuLadoCampo;
    private RobotInfo[] robos;
    private AtuadorAssincrono atuador;
    private Boolean primeiraVez = true;

    //Para inicializacoes necessarias
    @Override
    public void setup() {
        try {
            aliados = getServerRef().getTeamInterfaces(nomeTime);
            meuLadoCampo = aliados[0].getRobotInfo().getX() > 200 ? 'e' : 'd';
            atuador = new AtuadorAssincrono();

            /* Posiciona os robos arbitrariamente para servirem de barreira para
             ** para o time oponente
             */
            if (meuLadoCampo == 'e') {
                aliados[1].setAhead(300);
                aliados[1].execute();

                aliados[2].setAhead(300);
                aliados[2].execute();

                aliados[3].setAhead(100);
                aliados[3].execute();

                aliados[4].setAhead(200);
                aliados[4].execute();
            } else {
                aliados[1].setTurnLeft(10);
                aliados[1].setAhead(300);
                aliados[1].execute();

                aliados[2].setAhead(500);
                aliados[2].execute();

                aliados[3].setAhead(700);
                aliados[3].execute();

                aliados[4].setAhead(900);
                aliados[4].execute();
            }
        } catch (RemoteException ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Override
    public boolean executeAction(String ag, Structure action) {
        return true;
    }

    public void mainLoop() throws RemoteException {

        // este time nao faz nada - fica parado
    }

    @Override
    public void end() {
        try {
            super.getEnvironmentInfraTier().getRuntimeServices().stopMAS();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        TimeTarBuscaBTeamEnv team = new TimeTarBuscaBTeamEnv();
        team.init(new String[]{nomeTime, "localhost"});

        while (true) {
            try {
                team.mainLoop();
                Thread.sleep(20);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

}
