/* Este programa eh modelo para ser utilizado na tarefa de busca A* x LRTA*
 da disciplina de Sistemas Inteligentes 1.
 O time TarBuscaA vai salvar seu aliado que está como refém do inimigo. 
 */
package roborescue.impl;

import atuador.AtuadorAssincrono;
import jason.RoborescueEnv;
import jason.asSyntax.Structure;
import java.awt.Point;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import robocode.rescue.RobotInfo;
import robocode.rescue.interfaces.RMIRobotInterface;

public class TimeAStar extends RoborescueEnv {

    private static final String nomeTime = "A* HMRJ RL";
    private final int numRobos = 5;
    private RMIRobotInterface[] aliados;
    private RobotInfo[] inimigos;
    private char meuLadoCampo;
    private RobotInfo[] robos;
    private AtuadorAssincrono atuador;
    private Boolean primeiraVez = true;

    private Point objetivo;
    private PriorityQueue<SearchNode<Point>> fronteira;
    private Double raioObjetivo_px = 50.0;
    private Stack<Point> solucao;

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

        } catch (RemoteException ex) {
            System.out.println(ex.getMessage());
        }

    }

    @Override
    public boolean executeAction(String ag, Structure action) {
        return true;
    }

    public void makePlan() throws RemoteException {
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

        Map.getInstance().setPosicoes(posicoes);

        // Criação do plano:
        objetivo = Map.translateToDiscrete(aliados[0].getRobotInfo().getX(), aliados[0].getRobotInfo().getY());
        Map.getInstance().setObjective(objetivo);
//        Map.getInstance().setPosicoes(getPosicoes());

        // Nó inicial
        SearchNode<Point> position = new SearchNode<>(null, Map.getInstance().getStateForPointDiscrete(aliados[1].getRobotInfo().getX(), aliados[1].getRobotInfo().getY()), 0.0);

        //Fronteira (Fila de prioridades ordenada pelo custo do caminho+heurística;
        fronteira = new PriorityQueue<SearchNode<Point>>(new Comparator<SearchNode<Point>>() {

            @Override
            public int compare(SearchNode<Point> t, SearchNode<Point> t1) {
                return (int) (t.getCostF() - t1.getCostF());
            }
        });

        fronteira.add(position);
        //Explorado (conjunto dos nós já explorados
        Collection<SearchNode<Point>> explorados = new Vector<SearchNode<Point>>();
        //Loop
        boolean hasMore = true;
        while (hasMore) {
            // Se a fronteira estiver vazia retorne falha
            if (fronteira.isEmpty()) {
                break;
            }
            // position <- POP(fronteira)
            position = fronteira.poll();
            //se position for o estado objetivo, retorna Solução
            if (checkObjetivo(position.getState().getState(), objetivo)) {
                solucao = new Stack<>();
                while (position != null) {
                    solucao.add(position.getState().getState());
                    position = position.getFather();
                }
                break;
            }
            // adiciona o estado à Explorado
            explorados.add(position);
            //para cada ação possível no estado position
            for (Action<Point> act : position.getState().avaliableStates()) {
                //new child <- Estado resultande da ação
                State<Point> child = act.getDestino();
                //se o estado de child não está em explorado e nem na fronteira
                if (!explorados.contains(child) && !fronteira.contains(child)) {
                    //adiciona child à fronteira
                    fronteira.add(new SearchNode<>(position, child, act.getCostFor()));
                } else {
                    //senão

                    //se child está na fronteira, mas com um custo de caminho+heurística maior que o atual
                    boolean change = false;
                    if (fronteira.contains(child)) {
                        for (SearchNode<Point> s : fronteira) {
                            if (child.equals(s)) {
                                if (child.getHeuristicCost() + child.getPathCost() < s.getCostF()) {
                                    change = true;
                                    break;
                                }
                            }
                        }
                        // substitui o nó na fronteira por child;
                        if (change) {
                            fronteira.remove(child);
                            fronteira.add(new SearchNode<>(position, child, act.getCostFor() + act.pai.getPathCost()));
                        }
                    }
                }
                // fim para cada
            }
            //fim while
        }

        //fim: Criação do plano;
        ready_go = true;
    }

    boolean ready_go = false;

    public void mainLoop() throws RemoteException {
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

            // para entao mandar o salvador se movimentar (evita colisoes)
            if (primeiraVez) {
                primeiraVez = false;
                // manda o salvador em direcao ao refem sem se preocupar com obstaculos
//                atuador.irPara(teamRef[1], (int) xRefem, (int) yRefem);
                makePlan();
            }

            if (teamRef[1].getDistanceRemaining() <= 0.1 && ready_go) {
                if (!solucao.empty()) {
                    moverParaEstado(teamRef[1], solucao.pop());
                }else{
                    System.out.println("Fim da execução.");
                }
            }
        }

        //se terminou de agir
        // Executa próximo passo do plano;
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
        TimeAStar team = new TimeAStar();
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
            }
        }
    }

    private boolean checkObjetivo(Point position, Point objetivo) {
        if (Map.distance_px(position, objetivo) < raioObjetivo_px) {
            return true;
        }

        return false;
    }

    private Double[][] getPosicoes() {
        try {
            RobotInfo[] infos = getServerRef().getMyTeamInfo(nomeTime);
            RobotInfo[] infosEn = getServerRef().getEnemyTeamInfo(nomeTime);

            Double[][] posicoes = new Double[infos.length + infosEn.length][2];
            int k = 0;

            for (RobotInfo info : infos) {
                posicoes[k][0] = info.getX();
                posicoes[k][1] = info.getY();

                k++;
            }
            for (RobotInfo info : infosEn) {
                posicoes[k][0] = info.getX();
                posicoes[k][1] = info.getY();

                k++;
            }
        } catch (RemoteException ex) {
            Logger.getLogger(TimeAStar.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new Double[0][0];
    }

    private void moverParaEstado(RMIRobotInterface rmiRobotInterface, Point pop) throws RemoteException {
        Point x_y = Map.translateToPx(pop);

        atuador.irPara(rmiRobotInterface, x_y.x, x_y.y);
    }

}
