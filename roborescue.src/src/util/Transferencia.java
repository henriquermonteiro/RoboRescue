package util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Compila e copia os arquivos .class para a pasta correta e executa o ambiente
 * robocode -Ddebug=true -Djava.security.main -DJava.security.policy=policy
 * -Xmx512M -DNOSECURITY=true -Ddebug=true -DNOSECURITY=true
 *
 * Rodar o jason:
 *
 * - Main class (normal): jason.mas2j.parser.mas2j
 *
 * - Argumentos (arquivo mars.mas2j):
 * "C:/Users/Emerson/workspace/jasonum/mars.mas2j" run
 *
 *
 */
public class Transferencia {

    @SuppressWarnings("unused")
    public static void transfere() {

        //copia os arquivos para o diretorio padrao do robocode
        String pProjeto = ConstantesExecucao.caminhoDiretorioClasses;

        String pRobocode = ConstantesExecucao.caminhoRobocode;

        String pastaOrigem = pProjeto + "/sibots/";
        String pastaDestino = pRobocode + "/robots/sibots/";


        List<String> arqOr = new ArrayList<String>();
        List<String> arqDes = new ArrayList<String>();

        for (String robo : FuncoesGerais.ListarArquivos(pastaOrigem)) {
            arqOr.add(pastaOrigem + robo);
            arqDes.add(pastaDestino + robo);
        }
        
        for (int i = 0; i < arqOr.size(); i++) {
            if (!arqOr.get(i).equals("") && !arqDes.get(i).equals("")) {
                try {
                    FuncoesGerais.CriarPastas(arqDes.get(i));
                    FuncoesGerais.copy(new File(arqOr.get(i)), new File(arqDes.get(i)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
