
package conection;


public class TeamStatistics {
  
  private String teamName;
  private int wins;
  private int loses;
  private int draws;
  
  public TeamStatistics(String teamName) {
    this.teamName = teamName;
    wins = 0;
    loses = 0;
    draws = 0;
  }
  
  public String getName() {
    return teamName;
  }
  public int getWins() {
    return wins;
  }
  public int getLoses() {
    return loses;
  }
  public int getDraws() {
    return draws;
  }
  public int addWin() {
    wins++;
    return wins;
  }
  public int addLose() {
    loses++;
    return loses;
  }
  public int addDraw() {
    draws++;
    return draws;
  }
  
}
