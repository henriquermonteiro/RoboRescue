package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConstantesExecucao {

    public static String caminhoRobocode;
    public static String caminhoDiretorioClasses;
    public static String nomeTeamA;
    public static String nomeTeamB;

    public static void start(String user) {
        File arquivo;
        FileInputStream input;
        Properties props;
        
        try {
            if(user.equals("server")) {
                arquivo = new File("caminhos.properties");

                input = new FileInputStream(arquivo);
                props = new Properties();
                props.load(input);

                caminhoRobocode = props.getProperty("robocode");
                caminhoDiretorioClasses = props.getProperty("diretorioClasses");
            }
            
            arquivo = new File("teams.properties");
            input = new FileInputStream(arquivo);
            props = new Properties();
            props.load(input);

            nomeTeamA = props.getProperty("nomeTeamA");
            nomeTeamB = props.getProperty("nomeTeamB");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
