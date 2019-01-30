package game.view;

import java.io.*;
import java.util.ArrayList;
/**
*Keeps track of completed levels in game by saving progress to a .txt
*/
public class Log{
	private int numLevels;
	private int numHighScores = 5;
	private boolean[] levelsComp;
	private int[] highScores;
	private static BufferedReader in = null;
	private static PrintWriter out = null;
	
	/**
	*@param numLevels Number of levels in game.
	*/
	
	public Log(int numLevels){
		this.numLevels=numLevels;
		levelsComp=getLevelsCompleted();
	}
	
	
	/**
	*Reads in completed levels from .txt
	*@return boolean array of completed levels
	*/
	public boolean[] getLevelsCompleted(){
		levelsComp = new boolean[numLevels];
		try{
            in = new BufferedReader(new FileReader("TheWildEscape/src/resources/logs/levelsComp.txt"));
            for (int i=0; i<numLevels; i++){
				if ((in.readLine()).equals("0")){
					levelsComp[i]=false;
				}
				else levelsComp[i]=true;
			}
            in.close();
        }
        catch (IOException e){System.out.println("File not found");}
		
		return levelsComp;
	}
	
	/*public int[] getHighScores(){
		highScores = new int[numHighScores];
		try{
			in = new BufferedReader(new FileReader("TheWildEscape/src/resources/log/highScores.txt"));
			for (int i=0; i<numHighScores; i++){
				while(!in.readLine().equals(null)){//necessary?
					highScores[i] = Integer.parseInt(in.readLine());
				}
			}
		}
		catch (IOException e){System.out.println("File not found");}
		
		return highScores;
	}*/
	
	/**
	*Updates array when level has been completed.
	*@param level Level which has just been completed.
	*/
	public void levelCompleted(int level){
		levelsComp[level]=true;
		printLevelsCompleted();
	}
	
	/**
	*Prints information stored in the array to .txt
	*/
	
	public void printLevelsCompleted(){
		try {
            out = new PrintWriter(new FileWriter("TheWildEscape/src/resources/logs/levelsComp.txt", false));
            for (int i=0; i<numLevels; i++) {
				if (levelsComp[i]){
                out.println("1");
				}
				else out.println("0");
            }
            out.close();
        }
        catch (IOException e){System.out.println("File not found");}
	}
	
	
}