/* Este programa eh modelo para ser utilizado na tarefa de busca A* x LRTA*
 da disciplina de Sistemas Inteligentes 1.
 O time TarBuscaA vai salvar seu aliado que está como refém do inimigo. 
 */
package roborescue.impl;

import atuador.AtuadorAssincrono;
import jason.RoborescueEnv;
import jason.asSyntax.Structure;
import java.awt.Point;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import robocode.rescue.RobotInfo;
import robocode.rescue.interfaces.RMIRobotInterface;

public class TimeLRTAStar extends RoborescueEnv {

    private static final String nomeTime = "LRTA* HMRJ RL";
    private final int numRobos = 5;
    private RMIRobotInterface[] aliados;
    private RobotInfo[] inimigos;
    private char meuLadoCampo;
    private RobotInfo[] robos;
    private AtuadorAssincrono atuador;
    private Boolean primeiraVez = true;

    private Point objetivo;
    private Double raioObjetivo_px = 100.0;
    private Double areaDeSeguranca_px = 50.0;

    private PersistentMap map;

    //Para inicializacoes necessarias
    @Override
    public void setup() {
        try {
            aliados = getServerRef().getTeamInterfaces(nomeTime);
            meuLadoCampo = aliados[0].getRobotInfo().getX() > 200 ? 'e' : 'd';
            atuador = new AtuadorAssincrono();

            /* O aliados[0] do time A eh o refem e inicia posicionado na 
             ** extremidade oposta do campo em relacao aos seus companheiros.
             ** Este robô deve ser resgatado.
             */
            System.out.println(nomeTime + ": pos robo REFEM X="
                    + aliados[0].getRobotInfo().getX()
                    + " Y=" + aliados[0].getRobotInfo().getY());

            /* o robo[1] do time A eh o salvador do refem
             ** vc pode escolher entre mexer ou nao nao posicao inicial dele
             */
            System.out.println(nomeTime + ": pos robo SALVADOR X="
                    + aliados[1].getRobotInfo().getX()
                    + " Y=" + aliados[1].getRobotInfo().getY());

            /* TO DO - posicionar os robos aleatoriamente para o A* ou 
             ** em posicoes fixas quando for testar sucessivas vezes com o LRTA*
             ** Abaixo, exemplo de posicionamento fixo.
             */
            if (meuLadoCampo == 'e') {
                aliados[2].setTurnRight(45);
                aliados[2].setAhead(500);
                aliados[2].execute();

                aliados[3].setTurnRight(10);
                aliados[3].setAhead(1000);
                aliados[3].execute();

                aliados[4].setTurnRight(-45);
                aliados[4].setAhead(500);
                aliados[4].execute();
            } else {
                aliados[4].setTurnRight(45);
                aliados[4].setAhead(500);
                aliados[4].execute();

                aliados[3].setTurnRight(10);
                aliados[3].setAhead(1000);
                aliados[3].execute();

                aliados[2].setTurnRight(-45);
                aliados[2].setAhead(500);
                aliados[2].execute();
            }

            /* Obtem informacoes dos robos do time inimigo - as posicoes dos 
             ** robos inimigos devem ser fixadas no outro time - o que eh
             ** importante para executar os algoritmos de busca
             */
            inimigos = new RobotInfo[numRobos];

            // observar que o arg eh o proprio nome do time para impedir trapacas
            inimigos = getServerRef().getEnemyTeamInfo(nomeTime);

            System.out.println("*** Inimigos de " + myTeam + " ***");
            for (int i = 0; i < numRobos; i++) {
                System.out.println(" [" + i + "]: X=" + inimigos[i].getX()
                        + " Y=" + inimigos[i].getY());
            }

            map = new PersistentMap();
            map.loadProperties();

        } catch (RemoteException ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Override
    public boolean executeAction(String ag, Structure action) {
        return true;
    }

    public void mainLoop() throws RemoteException, IOException {
        robos = getServerRef().getMyTeamInfo(myTeam);

        RobotInfo refem = robos[0];
        double xRefem = refem.getX();
        double yRefem = refem.getY();
        RMIRobotInterface[] teamRef = getTeamRef();

        System.out.println("salvador " + (int) teamRef[1].getRobotInfo().getX() + ", "
                + (int) teamRef[1].getRobotInfo().getY());
        System.out.println("   refem " + (int) xRefem + ", " + (int) yRefem);

        // aguarda o robo aliado 3 acabar seu movimento pois eh o que vai
        // mais longe
        if (teamRef[3].getDistanceRemaining() <= 0.1) {
            if (primeiraVez) {
                inimigos = getServerRef().getEnemyTeamInfo(nomeTime);

                Double posicoes[][] = new Double[(inimigos.length + aliados.length) - 2][2];

                int y = 0;
                for (RobotInfo info : inimigos) {
                    posicoes[y][0] = info.getX();
                    posicoes[y][1] = info.getY();

                    y++;
                }

                for (RMIRobotInterface info : aliados) {
                    if (info.getRobotInfo().getRobotIndex() > 1) {
                        posicoes[y][0] = info.getRobotInfo().getX();
                        posicoes[y][1] = info.getRobotInfo().getY();

                        y++;
                    }
                }

                map.setPosicoes(posicoes);

                // Criação do plano:
                objetivo = Map.translateToDiscrete(aliados[0].getRobotInfo().getX(), aliados[0].getRobotInfo().getY());
                map.setObjective(objetivo);

                primeiraVez = false;
            }

            if (teamRef[1].getDistanceRemaining() <= 0.1) {

                // Checa estado corrente
//        Point current = new Point(((Double)teamRef[1].getRobotInfo().getX()).intValue(), ((Double)teamRef[1].getRobotInfo().getY()).intValue());
                State<Point> current = map.getStateForPointDiscrete(teamRef[1].getRobotInfo().getX(), teamRef[1].getRobotInfo().getY());

                if (checkObjetivo(current.getState())) {
                    map.saveProperties();
                    System.out.println("Finalizado");
                    return;
                }

                // Para cada ação do estado corrente
                Double cheapAct_cost = -1.0;
                ArrayList<Action<Point>> cheapAct_list = new ArrayList<>();
                for (Action<Point> act : current.avaliableStates()) {
                    //carregar custo da heurística:
                    act.getDestino().setOverwriteHeuristic(map.getHeuristica(act.getDestino().getState()));
                    
                    // calcular o custo (custo do caminho + heurística) para todo vizinho
                    Double fN = act.getCostFor() + act.pai.getPathCost() + act.getDestino().getHeuristicCost();
                    if (cheapAct_cost < 0 || cheapAct_cost > fN) {
                        cheapAct_cost = fN;
                        cheapAct_list = new ArrayList<>();
                        cheapAct_list.add(act);
                    } else {
                        if (fN == cheapAct_cost) {
                            cheapAct_list.add(act);
                        }
                    }
                    // fim para cada
                }

                //Atualizar o custo heurística do nó corrente com o menor custo total dos vizinhos
                current.setOverwriteHeuristic(cheapAct_cost);
                map.setCostForPoint(cheapAct_cost, current.getState());

                // Escolher a ação que leva ao menor custo, se houver impate escolher aleatoriamente;
                Action<Point> toDo = chooseFromList(cheapAct_list);

                //move para a ação escolhida
                moverParaEstado(aliados[1], toDo.getDestino().getState());

            }
        }

    }

    private void moverParaEstado(RMIRobotInterface rmiRobotInterface, Point pop) throws RemoteException {
        Point x_y = Map.translateToPx(pop);

        atuador.irPara(rmiRobotInterface, x_y.x, x_y.y);
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
        TimeLRTAStar team = new TimeLRTAStar();
        team.init(new String[]{nomeTime, "localhost"});

        while (true) {
            try {
                //Lógica do sistema;
                team.mainLoop();
                //Dar tempo para outro time realizar seus movimentos:
                Thread.sleep(20);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                Logger.getLogger(TimeLRTAStar.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private Action<Point> chooseFromList(ArrayList<Action<Point>> cheapAct_list) {
        if (cheapAct_list.isEmpty()) {
            return null;
        }

        return cheapAct_list.get((int) (Math.random() * cheapAct_list.size()));
    }

    private boolean checkObjetivo(Point state) {
        if (map.distance_px(state, objetivo) <= raioObjetivo_px) {
            return true;
        }

        return false;
    }

}
