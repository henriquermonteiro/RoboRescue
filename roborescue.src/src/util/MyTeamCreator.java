package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


import net.sf.robocode.io.Logger;
import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.repository.RepositoryManager;
import net.sf.robocode.settings.ISettingsManager;
import net.sf.robocode.settings.SettingsManager;

public class MyTeamCreator {

	/**
	 * @param args
	 */
	private String[] robotSelection;
	private String teamName;
	private IRepositoryManager repositoryManager;
	private ISettingsManager st;
	
	public MyTeamCreator(String[] robotSelection, String teamName){
		this.teamName = teamName;
		this.robotSelection = robotSelection;
		st = new SettingsManager();
		
		repositoryManager = new RepositoryManager(st);
		
	}
	public static void main(String[] args) {
		String pack = "sibots.";
		String[] robos = new String[5];
		String teamName = JOptionPane.showInputDialog("Digite o nome do time");
		robos[0] = pack + "RoboRefem*";
		for ( int i =1; i < 5; i++){
			robos[i] = pack + JOptionPane.showInputDialog("Digite o nome do robo " + i) + "*";
		}
		
		MyTeamCreator mt = new MyTeamCreator(robos, teamName);
		try {
			mt.createTeam();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public int createTeam() throws IOException {
		System.out.println(repositoryManager.getRobotsDirectory());
		File f = new File(ConstantesExecucao.caminhoRobocode+"/robots", "/sibots/" + teamName + "Team" + ".team");
		OutputStream out = new  FileOutputStream(f);
		String teamString = "team.members= ";
		for ( String s : robotSelection){
			teamString += s + ",";
		}
		teamString.substring(0, teamString.length() -2);
		out.write(teamString.getBytes());
		out.close();
		return 0;
	}

}
