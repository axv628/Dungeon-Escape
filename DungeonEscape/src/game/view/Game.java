package game.view;

import javax.swing.*;

import game.controller.GameClient;
import game.controller.GameServer;

import java.awt.*;

public class Game extends JFrame{

	public static boolean isServer = false;
	private static boolean multiplayer = false;
	public static GameServer gameserver;
	public static GameClient gameclient;
	private Dimension screenSize;
	
	public static int width;
	public static int height;
	
	private JPanel currentPanel; 
	
	private WindowManager manager;
	
	public Game(){
		super();
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    setSize((int)(screenSize.width*0.75), (int) (screenSize.height*0.95));
//		setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		setResizable(true);
		setMinimumSize(new Dimension(890,700));
		
		setVisible(true);
		Dimension dimension = this.getContentPane().getSize();
		width = getWidth();
		height = getHeight();
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//this.setFocusable(true);
		//this.requestFocus();
		//this.requestFocusInWindow();
		
		this.setLayout(new GridLayout(1,1));
		
		//Initialises the main menu within manager
		manager = new WindowManager(this);
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
	}
	
	public static void setMultiplayer(boolean multi){
		multiplayer = multi;
	}
	
	public static boolean getMultiplayer(){
		return multiplayer;
	}
	
	public static boolean getServer(){
		return isServer;
	}
	
	public void changeState(String newState){
		manager.changeState(newState);
	}

	public static void main(String[] args){
		Game game = new Game();
	}
	
	public void resize(){
		setSize((int)(screenSize.width*0.75), (int)(screenSize.height*0.95));
		setResizable(true);
	}
}