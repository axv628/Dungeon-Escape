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


public class PauseMenu extends JDialog{

	private Game parent;
	private GamePanel game;
	
	private BufferedImage image;

	private JButton resume = new JButton();
	private JButton mainMenu = new JButton();
	private JPanel mpane;
	
	private static final int width = 360;
	private static final int height = 306;
	
	/**
	* Menu that appears when the user uses the P key in singleplayer, has choices to resume or leave the game.
	* Reads in image and paints it onto the main panel of the pause menu.
	* Sets location relative to main game frame.
	*/

	public PauseMenu(Game parent, String title, GamePanel game){
		super(parent, title);
		
		this.parent = parent;
		this.game = game;
		game.setPaused(true);
		setUndecorated(true);
		setSize(width, height);
		setFocusable(true);
		requestFocus();
		
		try {
			image = ImageIO.read(new File("TheWildEscape/src/resources/menus/paused.png")); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		mpane = new JPanel(){
			protected void paintComponent(Graphics g) {
			super.paintComponent(g);
		
			g.drawImage(image, 0, 0, width, height, null);
			
			mainMenu.setBounds(width/2-90, height/2+65, 180, 60);
			addImageToButton(mainMenu, "mainMenu");
			resume.setBounds(width/2-90, height/2, 180, 60);
			addImageToButton(resume, "resume");
			
		
			}
		};
		
		mpane.setSize(width, height);
		Point p = game.getLocationOnScreen();
		setLocation(p.x + game.width/2 - 180, p.y + game.height/2 - 200);
		initButtons();
		getContentPane().add(mpane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		
	}
	
	/**
	* Sets action listeners for buttons and add them to the main panel.
	*/
	
	private void initButtons(){
		resume.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){
				game.setPaused(false);
				setVisible(false);
				
				dispose();
				
			}			
		});
		
		mpane.add(resume);
				
		mainMenu.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){
				setVisible(false);
				dispose();
				parent.changeState("MenuPanel");
				game.setRunning(false);
			}
		});
		
		mpane.add(mainMenu);
	}
	
	/**
	* Gets image and adds it to JButton
	* @param button JButton to have image added
	* @param buttonName name of button image .png
	*/
	
	public static void addImageToButton(JButton button, String buttonName){
		Image image;
		try {
			image = ImageIO.read(new File("TheWildEscape/src/resources/buttons/" + buttonName + ".png"));
			ImageIcon icon1 = new ImageIcon(image.getScaledInstance(button.getWidth(),button.getHeight(),Image.SCALE_DEFAULT));
			button.setIcon(icon1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
