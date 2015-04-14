/* TimeDTeamEnv age de forma similar ao "timeA" porém faz um desvio síncrono e 
** usa 4 threads (uma para cada robô) para simplificar e agilizar o programa. */

package roborescue.examples;

import jason.RoborescueEnv;
import jason.asSyntax.Structure;
import java.rmi.RemoteException;
import robocode.rescue.interfaces.RMIRobotInterface;

public class TimeDTeamEnv extends RoborescueEnv
{
	private static final String nomeTime = "TimeD";
	private RMIRobotInterface[] aliados;
	private char meuLadoCampo;
	private static Robo robos[];

	/**
	 * Verifica qual o lado do campo e cria um array com 
	 * os meus robôs.
	 */
	@Override
	public void setup()
	{
		robos = new Robo[4];
		
		try
		{
			aliados = getServerRef().getTeamInterfaces(nomeTime);
			meuLadoCampo = aliados[0].getRobotInfo().getX() > 200 ? 'e' : 'd';
			
			for(int i = 0, j = 1; j < aliados.length; i++, j++)
				robos[i] = new Robo(aliados[j]);
		}
		catch(RemoteException ex)
		{
			System.out.println(ex.getMessage());
		}
	}

	@Override
	public boolean executeAction(String ag, Structure action)
	{
		return true;
	}

	@Override
	public void end()
	{
		try
		{
			super.getEnvironmentInfraTier().getRuntimeServices().stopMAS();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Registra o time no servidor e cria e inicia 4 threads, uma para cada robô.
	 */
	public static void main(String[] args)
	{
		TimeDTeamEnv team = new TimeDTeamEnv();
		team.init(new String[]{nomeTime, "localhost"});
		
		for(int i = 0; i < robos.length; i++)
			robos[i].start();
	}

	/**
	 * Classe que comanda o robô.
	 */
	private class Robo extends Thread
	{
		private final RMIRobotInterface aliado;

		public Robo(RMIRobotInterface aliado)
		{
			super();
			this.aliado = aliado;
		}

		/**
		 * Executa até que o programa seja finalizado.
		 */
		@Override
		public void run()
		{
			while(true)
			{
				try
				{
					mainLoop();
					Thread.sleep(20);
				}
				catch(RemoteException ex)
				{
					System.out.println(ex.getMessage());
				}
				catch(InterruptedException ex)
				{
					System.out.println(ex.getMessage());
				}
			}
		}
		
		/**
		 * Se nenhum robô tiver capturado o refém manda ele até uma distância
		 * de 50 pixels do refém.
		 * Se algum robô já tiver o refém ele volta para o seu lado do campo.
		 * Se o robô estiver com velocidade 0 (parado), faz o robô desviar.
		 */
		public void mainLoop() throws RemoteException, InterruptedException
		{
			double diferencaX, diferencaY;

			if(aliados[0].isFollowing() == 0)
			{
				diferencaX = aliado.getRobotInfo().getX() - aliados[0].getRobotInfo().getX();
				diferencaY = aliado.getRobotInfo().getY() - aliados[0].getRobotInfo().getY();

				if(diferencaX < -50 || diferencaX > 50)
				{
					if(aliado.getRobotInfo().getX() > aliados[0].getRobotInfo().getX())
						virarPara(aliado, 270);
					else if(aliado.getRobotInfo().getX() < aliados[0].getRobotInfo().getX())
						virarPara(aliado, 90);

					aliado.setAhead(Math.abs(diferencaX));
				}
				else if(diferencaY < -50 || diferencaY > 50)
				{
					if(aliado.getRobotInfo().getY() > aliados[0].getRobotInfo().getY())
						virarPara(aliado, 180);
					else if(aliado.getRobotInfo().getY() < aliados[0].getRobotInfo().getY())
						virarPara(aliado, 0);

					aliado.setAhead(Math.abs(diferencaY));
				}

				aliado.execute();
			}
			else if(aliados[0].isFollowing() != 0)
			{
				if(meuLadoCampo == 'e')
					virarPara(aliado, 270);
				else if(meuLadoCampo == 'd')
					virarPara(aliado, 90);

				aliado.setAhead(Double.POSITIVE_INFINITY);
				aliado.execute();
			}

			/*Dependendo da máquina e da velocidade do jogo o 
			valor de espera para verificar se houve colisão
			pode ser muito ou pode ser pouco*/
			Thread.sleep(50);
			
			if(aliado.getDistanceRemaining() == 0 && aliado.getRobotInfo().getVelocity() == 0)
				desviar();
		}

		/**
		 * Vira o robô para uma posição específica.
		 */
		private void virarPara(RMIRobotInterface aliado, double anguloDesejado) throws RemoteException
		{
			double angulo = aliado.getRobotInfo().getHeading();

			if(angulo != anguloDesejado)
			{
				if(anguloDesejado < angulo)
					aliado.turnLeft(angulo - anguloDesejado);
				else if(anguloDesejado > angulo)
					aliado.turnRight(anguloDesejado - angulo);

				aliado.execute();
			}
		}
		
		/**
		 * Faz um desvio síncrono, pois um assíncrono iria falhar porque 
		 * não da tempo ao robô de virar antes de andar, então continuaria 
		 * travado.
		 */
		public void desviar() throws RemoteException
		{
			aliado.turnRight(90);
			aliado.execute();
			aliado.back(72);
			aliado.execute();
			aliado.turnLeft(90);
			aliado.execute();
		}
	}
}
