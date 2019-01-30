package game.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MenuPanel extends JPanel {
	private JButton singlePlayerButton;
	private JButton multiPlayerButton;
	private JButton instructionsButton;
	private JButton muteButton;
	private JLabel title;

	
	private WindowManager manager;
	private boolean multiplayer = false;
	
	float interpolation;
	
	private int width, height;
	
	public MenuPanel(WindowManager manager){
		super();
		this.manager = manager;
		
		
		width = manager.getWidth();
		height = manager.getHeight();
		
		setLayout(null);
		initButtons();
		this.setSize(width, height);
		this.setVisible(true);
	}
	
	private void initButtons(){
		
		singlePlayerButton= new JButton();
		singlePlayerButton.setBounds((int) (width/2-160),(int)(3*height/5), 320, 100);
		addImageToButton(singlePlayerButton,"TheWildEscape/src/resources/buttons/singleplayer.png");
		singlePlayerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeState("level map");
			}	
		});
		add(singlePlayerButton);
		
		multiPlayerButton=new JButton();
		multiPlayerButton.setBounds((int) (width/2-160),(int)(3*height/5+100), 320, 100);
		addImageToButton(multiPlayerButton,"TheWildEscape/src/resources/buttons/multiplayer.png");
		multiPlayerButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				changeState("Multiplayer");
			}
		});
		add(multiPlayerButton);
		
		
		instructionsButton=new JButton();
		instructionsButton.setBounds((int) (width/2-120),(int)(3*height/5+205), 240, 80);
		addImageToButton(instructionsButton,"TheWildEscape/src/resources/buttons/singleplayer.png"); 
		instructionsButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				changeState("Instructions");
			}
		});
		add(instructionsButton);
		
		muteButton = new JButton();
		muteButton.setBounds((int) (0.83 * width),(int)(0.8*height),(int) (0.1 * width),(int)(0.1*height));
		addImageToButton(muteButton,"TheWildEscape/src/resources/buttons/soundOn.png");
		manager.muted = false;
		muteButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!manager.muted){
					addImageToButton(muteButton,"TheWildEscape/src/resources/buttons/soundOff.png");
					manager.muteSong();
				}
				else {
					addImageToButton(muteButton,"TheWildEscape/src/resources/buttons/soundOn.png");
					manager.muted = false;
					manager.unmuteSong();
					}
				}
		});
		add(muteButton);
				
	}
	
	public static void addImageToButton(JButton button, String imageLocation){
		Image image;
		try {
			image = ImageIO.read(new File(imageLocation));
			ImageIcon icon1 = new ImageIcon(image.getScaledInstance(button.getWidth(),button.getHeight(),Image.SCALE_DEFAULT)); 
			button.setIcon(icon1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		try {
			BufferedImage bg = ImageIO.read(new File("TheWildEscape/src/resources/backgrounds/menu.png")); 
			BufferedImage title = ImageIO.read(new File("TheWildEscape/src/resources/buttons/title.png"));
			width= this.getWidth();
			height=this.getHeight();
			g.drawImage(bg, 0, 0, width, height, null);
			g.drawImage(title, width/2-350, height/9, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		addImageToButton(singlePlayerButton,"TheWildEscape/src/resources/buttons/singleplayer.png");
		
		addImageToButton(multiPlayerButton,"TheWildEscape/src/resources/buttons/multiplayer.png");
		
		addImageToButton(instructionsButton,"TheWildEscape/src/resources/buttons/controls.png"); 
    }
	
	private void changeState(String newState){
		manager.changeState(newState);
	}
}