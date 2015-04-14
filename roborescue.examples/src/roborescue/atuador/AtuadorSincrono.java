package atuador;

import java.rmi.RemoteException;
import robocode.rescue.interfaces.RMIRobotInterface;

/**
 * Responsável por mover o robô de um ponto a outro em linha reta e rotacioná-lo.
 * Os métodos dessa classe são são bloqueantes, retornando apenas depois que os 
 * movimentos necessários forem concluídos.
 * Essa classe deve ser usada quando precisão é requirida sobre agilidade.
 */
public class AtuadorSincrono implements IAtuador
{
	/**
	 * Dada uma coordenada X Y esse método se encarrega de virar o robô na direção
	 * certa (usando o método virar, que faz uma rotação de no máximo 90 graus)
	 * e o move em linha reta até o objetivo.
	 * Por ser um método síncrono cada movimento é executado totalmente antes do
	 * próximo ser iniciado, porém essa Thread é bloqueada enquanto os comandos
	 * são executados.
	 * Este método aceita a constante Atuador.POSICAO_ATUAL na posição X e Y, 
	 * significando que o robô não se moverá nesse eixo.
	 */
	@Override
	public void irPara(RMIRobotInterface robo, int x2, int y2) throws RemoteException
	{
		int x1, y1, distancia, rotacao;
		
		/*Pára o robô antes de começar */
		mover(robo, PARAR, 0);
		
		x1 = (int) robo.getRobotInfo().getX();
		y1 = (int) robo.getRobotInfo().getY();
		x2 = x2 == POSICAO_ATUAL ? x1 : x2;
		y2 = y2 == POSICAO_ATUAL ? y1 : y2;
		distancia = (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)); /*Teorema de Pitágoras h² = a² + b²*/
		rotacao = (int) robo.getRobotInfo().getHeading();
		
		if(distancia != 0)
		{
			/*http://stackoverflow.com/questions/2676719/calculating-the-angle-between-the-line-defined-by-two-points*/
			rotacao = (int) Math.toDegrees(Math.atan2(x2 - x1, y2 - y1)); /*X e Y trocados de lugar para não precisar converter*/
			rotacao = rotacao >= 0 ? rotacao : rotacao + 360;
		}
		
		if(virar(robo, rotacao))
			mover(robo, FRENTE, distancia);
		else
			mover(robo, TRAS, distancia);
	}
	
	/**
	 * Executa a ação passado por parâmetro pela distância em pixels 
	 * especificadas usando o comando passado. O comando só retorna
	 * após o robô terminar de se mover.
	 * O comando é um char que resulta em 5 ações:
	 *  - F: Frente (ahead).
	 *  - T: Trás (back).
	 *  - D: Virar a direita (turnRight).
	 *  - E: Virar a esquerda (turnLeft).
	 *  - P: Parar (stop).
	 *  - I: Não faz nada.
	 */
	@Override
	public void mover(RMIRobotInterface robo, char comando, int distancia) throws RemoteException
	{
		switch(Character.toUpperCase(comando))
		{
		case FRENTE:
			robo.ahead(distancia);
			break;
		case TRAS:
			robo.back(distancia);
			break;
		case DIREITA:
			robo.turnRight(distancia);
			break;
		case ESQUERDA:
			robo.turnLeft(distancia);
			break;
		case PARAR:
			robo.ahead(0);
			break;
		case IGNORAR:
			break;
		default:
			System.out.println("Comando não reconhecido");
		}

		robo.execute();
	}
	
	/**
	 * Vira o robô para o ângulo desejado.
	 * O robô é virado em no máximo 90 graus para a esquerda ou para a direita, 
	 * fazendo com que ele fique virado de frente ou de trás para o objetivo.
	 * Se o robô ficar virado contra o objetivo será necessário ele ir de ré
	 * até lá.
	 * Ao final da é retornado um boolean para indicar se o movimento executado
	 * está rotacionou o robô para o ângulo desejado ou se ele está virado.
	 * Este método é bloqueante, então há um aumento de precisão e uma perda
	 * de agilidade.
	 */
	@Override
	public boolean virar(RMIRobotInterface robo, int rotacao) throws RemoteException
	{
		boolean posicaoExata = true;
		char lado = PARAR;
		int angulo = 0;
		int posicaoAtual = (int) robo.getRobotInfo().getHeading();
		int distancia = rotacao - posicaoAtual;
		
		if(distancia == 0 || distancia == 180)
		{
			posicaoExata = distancia == 0 ? true : false;
			lado = PARAR;
			angulo = posicaoAtual;
		}
		else if(distancia > 0) /*Objetivo está a direita*/
		{
			if(distancia <= 90)
			{
				posicaoExata = true;
				lado = DIREITA;
				angulo = distancia;
			}
			else if(distancia > 90)
			{
				posicaoExata = false;
				lado = ESQUERDA;
				angulo = posicaoAtual + 180 - rotacao;
			}
		}
		else if(distancia < 0) /*Objetivo está a esquerda*/
		{
			if(distancia >= -90)
			{
				posicaoExata = true;
				lado = ESQUERDA;
				angulo = Math.abs(distancia);
			}
			else if(distancia < -90)
			{
				posicaoExata = false;
				lado = DIREITA;
				angulo = rotacao + 180 - posicaoAtual;
			}
		}
		mover(robo, lado, angulo);
		
		return posicaoExata;
	}
}
