package game.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import game.controller.GameLoop;

/** 
* Menu that will appear upon the game ending, shows relevant information according to the outcome of the game
* and the player's own status.
*/

public class EndMenu extends JDialog{ //MENU FOR WHEN PLAYER HAS WON/LOST A GAME, WILL BE DIFFERENT FOR MULTIPLAYER/SINGLEPLAYER

	Game parent;
	GamePanel game;
	private BufferedImage image;

	private JPanel mpane;
	private JLabel i;
	private JButton restart = new JButton();
	private JButton haunt = new JButton();
	private JButton mainMenu = new JButton();
	private JButton nextLevel = new JButton();
	
	private static final int width = 360;
	private static final int height = 450;
	
	private int mode;//SP or MP
	private int SP = 0;
	private int MP = 1;
	
	private String info;//DIED, LOST, WON, LOST winnerID
	private String winnerID = "";
	
	private boolean playersLeft;
	
	/**
	* Pauses player's gameLoop.
	* Reads in relevant panel skin dependant on what has happened. e.g player died, winner, game over and paints to panel
	* Set bounds for buttons
	*@param parent parent frame, what the menu appears in relation to
	*@param info Game logic information, what has happened/outcome of game
	*@param mode singleplayer/multiplayer
	*@param game access to game panel to pause/unpause loop
	*/
	
	public EndMenu(Game parent, String info, int mode, GamePanel game){
		super(parent, info);
		
		this.parent = parent;
		this.mode = mode;
		this.info = info;
		this.game = game;
		game.setPaused(true);
		setUndecorated(true);
		setSize(width, height);
		setFocusable(true);
		requestFocus();
		
		
		if (info.equals("DIED")){
			try {
			image = ImageIO.read(new File("TheWildEscape/src/resources/menus/died.png")); 
			} catch (IOException e) {
			e.printStackTrace();
		}
		}
		else if (info.equals("WON")){
			if (mode == 1){
			try {
				image = ImageIO.read(new File("TheWildEscape/src/resources/menus/winner.png")); 
				} catch (IOException e) {
				e.printStackTrace();
				}
			}
			if (mode == 0){
				try {
				image = ImageIO.read(new File("TheWildEscape/src/resources/menus/escaped.png")); 
				} catch (IOException e) {
				e.printStackTrace();
			}
			}
		}
		else if (info.equals("LOST")){
			try {
			image = ImageIO.read(new File("TheWildEscape/src/resources/menus/gameoOverSP.png")); 
			} catch (IOException e) {
			e.printStackTrace();
			}
		}
		else {
			try {
			image = ImageIO.read(new File("TheWildEscape/src/resources/menus/gameoOver.png")); 
			} catch (IOException e) {
			e.printStackTrace();
			}
			String[] s = info.split(" ");
			winnerID = s[1];
		}
		
		
		mpane = new JPanel(){
			protected void paintComponent(Graphics g) {
			super.paintComponent(g);
		
			setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
			g.drawImage(image, 0, 0, width, height, null);
			g.drawString(winnerID, width/2-winnerID.length()-10, 175);
			
			mainMenu.setBounds(width/2-90, height/2+50, 180, 60);
			addImageToButton(mainMenu, "mainMenu");
			restart.setBounds(width/2-90, height/2+115, 180, 60);
			addImageToButton(restart, "tryAgain");
			nextLevel.setBounds(width/2-90, height/2+115, 180, 60);
			addImageToButton(nextLevel, "nextLevel");
			haunt.setBounds(width/2-90, height/2+115, 180, 60);
			addImageToButton(haunt, "haunt");
		
			}
		};

		mpane.setSize(width, height);
		Point p = game.getLocationOnScreen();
		setLocation(p.x + game.width/2 - 180, p.y + game.height/2 - 250);
		initButtons();
		getContentPane().add(mpane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		
	}
	
	/**
	* Initialises buttons and button actions depending on 'info' given.
	*/
	
	private void initButtons(){
		//MAIN MENU
		mainMenu.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){
				setVisible(false);
				dispose();
				parent.changeState("MenuPanel");
				game.setRunning(false);
			}
			
		});
		
		mpane.add(mainMenu);
		
		//TRYAGAIN
		if (((mode == SP) && info.equals("DIED"))||((mode==SP) && info.equals("LOST"))){
			restart.addActionListener(new ActionListener(){
			
				public void actionPerformed(ActionEvent e){
					setVisible(false);
					dispose();
					parent.changeState("level map");
					game.setRunning(false);
				}
			
			});
			
			mpane.add(restart);
			
		}
		
		//NEXTLEVEL
		if (mode == SP && info.equals("WON")){
			nextLevel.addActionListener(new ActionListener(){
			
				public void actionPerformed(ActionEvent e){
					setVisible(false);
					dispose();
					parent.changeState("level map");
					game.setRunning(false);
				}
			
			});
			
			mpane.add(nextLevel);
		}
		
		//HAUNT
		if (mode == MP && info == "DIED"){
			haunt.addActionListener(new ActionListener(){
			
				public void actionPerformed(ActionEvent e){
					setVisible(false);
					dispose();
					game.setPaused(false);
				}
			
			});
			
			mpane.add(haunt);
		}
		
	}
	
	/**
	* Adds image to button
	* @param button JButton to be skinned
	* @param buttonName file name of button image .png
	*/
	
	public static void addImageToButton(JButton button, String buttonName){
		Image image;
		try {
			
			image = ImageIO.read(new File("TheWildEscape/src/resources/buttons/"+buttonName+".png"));
			ImageIcon icon1 = new ImageIcon(image.getScaledInstance(button.getWidth(),button.getHeight(),Image.SCALE_DEFAULT)); 
			button.setIcon(icon1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
