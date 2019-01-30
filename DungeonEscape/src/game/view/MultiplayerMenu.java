package game.view;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class MultiplayerMenu extends JPanel{
	
	private WindowManager manager;
	
	private int width, height;
	
	private JLabel label;
	private JTextField usernameField;
	
	private JButton hostButton;
	private JButton joinButton;
	private JButton backButton;
	
	public MultiplayerMenu(WindowManager manager){
		this.manager = manager;
		width = manager.getWidth();
		height = manager.getHeight();
		
		setLayout(null);
		initLabel();
		initButtons();
		initTextField();
	}
	
	private void initButtons(){
		hostButton = new JButton();
		hostButton.setBounds(width/3 - 100, height/2 - 25, 200, 50);
		hostButton.setFont(new Font("Algerian", Font.ROMAN_BASELINE, 15));
		hostButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				goToHostingMenu();
			}
		});
		addImageToButton(hostButton,"TheWildEscape/src/resources/buttons/hostserver.png");
		add(hostButton);
		
		joinButton = new JButton();
		joinButton.setBounds(2*width/3 - 100, height/2 - 25, 200, 50);
		joinButton.setFont(new Font("Algerian", Font.ROMAN_BASELINE, 15));
		joinButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				manager.changeState("Join Server");
			}
		});
		addImageToButton(joinButton,"TheWildEscape/src/resources/buttons/joinserver.png");
		add(joinButton);
		
		backButton = new JButton();
		backButton.setBounds(width/10, height/10, 100, 50);
		backButton.setFont(new Font("Algerian", Font.ROMAN_BASELINE, 15));
		backButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				manager.changeState("MenuPanel");
			}
			
		});
		addImageToButton(backButton,"TheWildEscape/src/resources/buttons/back.png");
		add(backButton);
	}
	
	private void initLabel(){
		label = new JLabel("Multiplayer");
		label.setBounds((int) (0.25*width) ,(int) (0.2*height), (int)(700 ), (int)( 150));
		label.setFont(new Font("Algerian",Font.BOLD, 70));
		add(label);
	}
	
	private void initTextField(){
		usernameField = new JTextField();
		usernameField.setBounds(width/3 - 100, height/2 + 75, 200, 50);
		usernameField.setFont(new Font("Algerian", Font.BOLD, 22));
		add(usernameField);
	}
	
	private void goToHostingMenu(){
		if(usernameField.getText().isEmpty()){
			JOptionPane.showMessageDialog(null, "Please Enter a username", "Error", JOptionPane.WARNING_MESSAGE);
			return;
		}
		manager.goToHostingMenu((usernameField.getText()).trim());
	}
	
	public static void addImageToButton(JButton button, String imageLocation){
		Image image;
		try {
			image = ImageIO.read(new File(imageLocation));
			ImageIcon icon1 = new ImageIcon(image.getScaledInstance(
					button.getWidth(),button.getHeight(),Image.SCALE_DEFAULT)); 
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
			
			width= this.getWidth();
			height=this.getHeight();

			g.drawImage(bg, 0, 0, width, height, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		hostButton.setBounds(width/3 - 100, height/2 - 25,(int) (width*0.15),(int)(height*0.1));
		joinButton.setBounds(2*width/3 - 100, height/2 - 25,(int) (width*0.15),(int)(height*0.1));
		usernameField.setBounds(width/3 - 100, height/2 + 75, 200, 50);
		addImageToButton(hostButton,"TheWildEscape/src/resources/buttons/hostserver.png");
		
		addImageToButton(joinButton,"TheWildEscape/src/resources/buttons/joinserver.png");
		
		backButton.setBounds(width/10, height/10, 100, 50);
		label.setBounds((int) (0.3*width) ,(int) (0.2*height), (int)( 700), (int)( 150));
	}
}