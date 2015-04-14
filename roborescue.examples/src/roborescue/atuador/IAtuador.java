package atuador;

import java.rmi.RemoteException;
import robocode.rescue.interfaces.RMIRobotInterface;

public interface IAtuador
{
	public static final char FRENTE			= 'F';
	public static final char TRAS			= 'T';
	public static final char DIREITA		= 'D';
	public static final char ESQUERDA		= 'E';
	public static final char PARAR			= 'P';
	public static final char IGNORAR		= 'I';
	public static final int POSICAO_ATUAL	= -1;
	
	/**
	 * Dada uma coordenada X Y esse método se encarrega de virar o robô na direção
	 * certa e o move em linha reta até o objetivo.
	 */
	public void irPara(RMIRobotInterface robo, int x2, int y2) throws RemoteException;
	
	/**
	 * Executa a ação passado por parâmetro pela distância em pixels 
	 * especificadas usando o comando passado.
	 */
	public void mover(RMIRobotInterface robo, char comando, int distancia) throws RemoteException;
	
	/**
	 * Vira o robô para o ângulo desejado.
	 */
	public boolean virar(RMIRobotInterface robo, int rotacao) throws RemoteException;
}
