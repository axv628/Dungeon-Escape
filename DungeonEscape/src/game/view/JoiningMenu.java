package game.view;

import java.awt.*;
import javax.swing.*;

import game.controller.GameClient;

import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

public class JoiningMenu extends JPanel{
	
	private JButton joinButton;
	private JButton backButton;
	private JTextField ipField, usernameField;
	private JTextArea playerListLabel;
	
	private WindowManager manager;
	private int width, height;
	
	private JLabel label, ipLabel, usernameLabel;
	
	private boolean hasClient = false;
	
	private GameClient client;
	
	public JoiningMenu(WindowManager manager){
		//Will have 2 text fields to fill in for the username and ip address to connect to.
		//Clicking joinButton will call a function that tries to connect to the server dependant on the contents of the text fields.
		
		this.manager = manager;
		width = manager.getWidth();
		height = manager.getHeight();
		
		
		setLayout(null);
		initLabels();
		initTextFields();
		initButtons();
	}
	
	private void initTextFields(){
		 ipField = new JTextField();
		 ipField.setBounds(width/2 , height/2 -25 , 200, 50);
		 ipField.setFont(new Font("Algerian",Font.BOLD, 22));
		 add(ipField);
		 
		 usernameField = new JTextField();
		 usernameField.setBounds(width/2 , height/2 - 125 , 200, 50);
		 usernameField.setFont(new Font("Algerian", Font.BOLD, 22));
		 add(usernameField);
	}
	private void initLabels(){
		label = new JLabel("Join a server");
		label.setBounds((int) (0.4*width) ,(int) (0.2*height),
				(int)( 700), (int)( 150));
		label.setFont(new Font("Algerian",Font.BOLD, 50));
		add(label);
		
		ipLabel = new JLabel("Enter Address of server:");
		ipLabel.setBounds(width/4 - 250 , height/2 - 25, 350, 50);
		ipLabel.setFont(new Font("Algerian",Font.BOLD, 22));
		add(ipLabel);
		
		usernameLabel = new JLabel("Enter username: ");
		usernameLabel.setBounds(width/4 - 250 , height/2 - 125, 350, 50);
		usernameLabel.setFont(new Font("Algerian",Font.BOLD, 22));
		add(usernameLabel);
		
		playerListLabel = new JTextArea();
		playerListLabel.setBounds((int)(width/5), (int)(height/5), 200, 400);
		playerListLabel.setText(getClientListText());
		add(playerListLabel);
	}
	
	private String getClientListText(){
		if(hasClient){
			ArrayList<String> clientList = client.getConnectedClients();
			String labelText = "List of connected clients: \n";
		
			for(String clientID: clientList){
				labelText = labelText + clientID + "\n";
			}
			return labelText;	
		}
		return "";
		
	}
	
	private void initButtons(){
		joinButton = new JButton();
		
		joinButton.setBounds(width/3 , height/2 - 25, 200, 50);
		joinButton.setFont(new Font("Algerian", Font.ROMAN_BASELINE, 15));
		add(joinButton);
		
		joinButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				joinGame();
			}
		});
		
		backButton = new JButton("Back");
		backButton.setBounds(width/10, height/10, 100, 50);
		backButton.setFont(new Font("Algerian", Font.ROMAN_BASELINE, 15));
		add(backButton);
		
		backButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				manager.changeState("Multiplayer");
			}
			
		});
	}

	private void joinGame(){
		//Tries to join the server
		
		if(ipField.getText().isEmpty()){
			JOptionPane.showMessageDialog(null, "Please Enter Address of the server", "Error", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if(usernameField.getText().isEmpty()){
			JOptionPane.showMessageDialog(null, "Please Enter a username", "Error", JOptionPane.WARNING_MESSAGE);
			return;
		}
		try{
			client = new GameClient(InetAddress.getByName((ipField.getText()).trim()), (usernameField.getText()).trim()); 	//entered ip putted here
			client.start();
			try{
				Thread.sleep(250);
				if(client.isConnected()){
					hasClient = true;
					LobbyThread thread = new LobbyThread(manager, client);
					thread.start();
				}
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}catch(UnknownHostException e){
			e.printStackTrace();
		}
	}
	
	protected void paintComponent(Graphics g) {
		System.out.println("paintComponent called in joiningmenu");
		super.paintComponent(g);
		try {
			BufferedImage bg = ImageIO.read(new File("TheWildEscape/src/resources/backgrounds/hosting.png")); 
			width= this.getWidth();
			height=this.getHeight();
			g.drawImage(bg, 0, 0, width, height, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 ipField.setBounds(width/2 , height/2 -25 , 200, 50);
		 ipLabel.setBounds(width/4 - 250 , height/2 - 25, 350, 50);
		 joinButton.setBounds(width/3 , height/2 - 25, 200, 50);
		 backButton.setBounds(width/10, height/10, 100, 50);
		 label.setBounds((int) (0.4*width) ,(int) (0.2*height),
					(int)( 700), (int)( 150));
		playerListLabel.setBounds((int)(width/5), (int)(height/5), 200, 400);
		playerListLabel.setText(getClientListText());
		MenuPanel.addImageToButton(joinButton,"TheWildEscape/src/resources/buttons/joinserver.png");
	}
}