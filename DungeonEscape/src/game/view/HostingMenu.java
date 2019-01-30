package game.view;

import java.awt.*;
import javax.swing.*;

import game.controller.GameServer;
import game.controller.GameClient;

import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class HostingMenu extends JPanel{
	
	private JButton startButton, backButton, addAI;
	private JLabel label, displayIP, boxLabel;
	private JComboBox<String> levelSelect;
	private JTextArea playerListLabel;
	
	private LobbyThread lobbyThread;
	
	private WindowManager manager;
	private int width, height;
	
	private GameServer server;
	private GameClient client;
	private int selectedLevel;
	private int numAIPlayers = 0;
	
	public HostingMenu(WindowManager manager, String username){
		this.manager = manager;
		width = manager.getWidth();
		height = manager.getHeight();
		
		server = new GameServer();
		server.start();
		
		client = new GameClient(server.getAddress(), username);
		client.start();
		
		setLayout(null);
		
		
		initLevelSelect();
		initLabel();
		initButtons();
		lobbyThread = new LobbyThread(manager, client);
		lobbyThread.start();
	}
	
	public HostingMenu(WindowManager manager){
		this.manager = manager;
		width = manager.getWidth();
		height = manager.getHeight();
		
		server = new GameServer();
		server.start();
		
		client = new GameClient(server.getAddress());
		client.start();
		
		setLayout(null);
		initLabel();
		initButtons();
		initLevelSelect();
		
		lobbyThread = new LobbyThread(manager, client);
		lobbyThread.start();
	}
	
	private void initButtons(){
		startButton = new JButton();
		add(startButton);
		
		startButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				startGame();
			}
		});
		
		backButton = new JButton();
		add(backButton);
		
		backButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				manager.changeState("Multiplayer");
			}
			
		});
		
		addAI = new JButton();
		add(addAI);
		
		addAI.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				numAIPlayers++;
			}
			
		});
	}
	
	private void initLabel(){
		displayIP = new JLabel("IP: " + server.getIPString());
		displayIP.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
		add(displayIP);
		
		playerListLabel = new JTextArea();
		playerListLabel.setFont( new Font(Font.SANS_SERIF, Font.BOLD, 20));
		playerListLabel.setText(getClientListText());
		playerListLabel.setEditable(false);
		add(playerListLabel);
	}
	
	private String getClientListText(){
		ArrayList<String> clientList = client.getConnectedClients();
		String labelText = "\n";
		
		for(String clientID: clientList){
			labelText = labelText + clientID + "\n";
		}
		labelText = labelText + "Number of AI players: " + String.valueOf(numAIPlayers);
		return labelText;
	}
	/**
	 * initialises the JComboBox for selecting a multiplayer level
	 * assigns the level number to the selectedLevel variable
	 */
	private void initLevelSelect(){
		String[] levelFiles = {"Map 1", "Map 2", "Map 3"};
		levelSelect = new JComboBox<String>(levelFiles);
		//levelSelect.setSelectedIndex(0);
		levelSelect.setFont(new Font(Font.SANS_SERIF,Font.BOLD, 30));
		levelSelect.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				selectedLevel = levelSelect.getSelectedIndex();
				
			}
			
		});
		selectedLevel = levelSelect.getSelectedIndex();
		//System.out.println("you selected " + selectedLevel);
		add(levelSelect);
	}
	
	public void startGame(){
		//Needs to initialise the world information BEFORE changing the window and starting the game loops.
		System.out.println(selectedLevel);
		if(server.startGame(selectedLevel, numAIPlayers)){
			lobbyThread.interrupt();
			manager.startGameWithServer(server, client);
		}else{
			JOptionPane.showMessageDialog(null, "Not enough players connected", "Error", JOptionPane.WARNING_MESSAGE);
		}
		
		//Called when the startButton is pressed and clients are connected
		
	}
	
	public static void addImageToButton(JButton button, String imageLocation){
		Image image;
		try {
			image = ImageIO.read(new File("TheWildEscape/src/resources/buttons/"+imageLocation+".png"));
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
			BufferedImage bg = ImageIO.read(new File("TheWildEscape/src/resources/backgrounds/hosting.png")); 
			BufferedImage title = ImageIO.read(new File("TheWildEscape/src/resources/buttons/waiting.png")); 
			width= this.getWidth();
			height=this.getHeight();
			g.drawImage(bg, 0, 0, width, height, null);
			g.drawImage(title, width/2-400, (int)(0.1*height), 800, 200, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		levelSelect.setBounds((int)(0.7*width), (int)(height/2), 200, 50);
		startButton.setBounds((int)(0.7*width), height/2 + 135, 225, 75);
		addImageToButton(startButton, "start");
		addAI.setBounds((int)(0.7*width), height/2 + 55, 225, 75);
		addImageToButton(addAI, "addAI");
		backButton.setBounds(width/10, height/10, 100, 50);
		addImageToButton(backButton, "back");
		displayIP.setBounds((int) (width/4) ,(int) (0.75*height) + 64, (int)( 0.4*width), (int)( 0.09*height));
		
		playerListLabel.setOpaque(false);
		playerListLabel.setBounds((int)(width/4), (int)(height/3), 400, 500);
		playerListLabel.setText(getClientListText());
		
	}
}
