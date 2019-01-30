package game.controller;

import java.io.IOException;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import game.model.World;
import game.model.objects.Player;


/**
*Connects to a game server, and runs the multiplayer game, receiving updates from the server on the game state dictated by the other clients.
*/
public class GameClient extends Thread {

	private InetAddress ipAddress;
	private DatagramSocket datagramSocket;
	private String clientID;
	private ArrayList<String> connectedClientIDs;
	private boolean establishedConnection = false;
	private boolean gameRunning = false;
	private boolean hasController = false;
	private World world;
	private GameController controller;
	
	 /**
	 *Creates a new GameClient with an InetAddress of the server it is to connect to.
	 *Generates a random clientID string to identify the client
	 */
	public GameClient(InetAddress inetAddress) {
		try {
			datagramSocket = new DatagramSocket();
			//datagramSocket.setSoTimeout(5000);
			ipAddress = inetAddress;
		} catch (SocketException e) {
			e.printStackTrace();
		}
		Random r = new Random();
		clientID = "Player" + Integer.toString(r.nextInt(1000000));
	}
	
	 /**
	 *Creates a new GameClient with 
	 *@param inetAddress Address of the server it is to connect to,
	 *@param clientID Uniquely identifies the GameClient to the server.
	 *				 Must not be null and must not contain any spaces.
	 */
	public GameClient(InetAddress inetAddress, String clientID){
		this.clientID = clientID;
		try {
			datagramSocket = new DatagramSocket();
			datagramSocket.setSoTimeout(5000);
			ipAddress = inetAddress;
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	/**
	*Runs the GameClient Thread
	*Sends a connection message to the server 
	*Then loops through listening on the DatagramSocket and handling received DatagramPackets appropriately
	*/
	@Override
	public void run(){
		sendMessage("OP_CONNECT" + " " + clientID);
		DatagramPacket isConnectedMessage = receiveMessage();
		handleMessage(isConnectedMessage);
		while(establishedConnection){
			DatagramPacket message = receiveMessage();
			handleMessage(message);
		}
		datagramSocket.close();
		System.out.println("GameClient stopped running");
	}
	
	private DatagramPacket receiveMessage(){
		byte[] data = new byte[512];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try{
			datagramSocket.receive(packet);
		}catch (IOException e){
			System.out.println("IOX");
		}
		return packet;
	}

	private void handleMessage(DatagramPacket packet){
		String input = (new String(packet.getData())).trim();
		//Takes the message from the server and sends responses to the client/all the clients accordingly.
		String[] sepInput = input.split(" ", 2);
		switch(sepInput[0]){
			case "OP_KEYPRESS":
				//Message has form: OP_KEYPRESS clientID key
				handleKeyPressMessage(sepInput[1]);
			break;
			case "OP_KEYRELEASE":
				//Message has form: OP_KEYRELEASE clientID key
				handleKeyReleaseMessage(sepInput[1]);
			break;
			case "OP_PLAYERPOSITION":
				//Message has formL OP_PLAYERPOSITION clientID xPos yPos
				handlePlayerPositionMessage(sepInput[1]);
			break;
			case "OP_CONNECT":
				//Message has form: OP_CONNECT clientID TRUE/FALSE
				handleConnectionMessage(sepInput[1]);
			break;
			case "OP_DISCONNECT":
				//handleDisconnectMessage();
			break;
			case "OP_NEWPLAYER":
				//Message form: OP_NEWPLAYER clientID x y
				handleNewPlayerMessage(sepInput[1]);
			break;
			
			case "OP_NEWWORLD":
				//Message form: OP_NEWWORLD levelFileName
				handleNewWorldMessage(sepInput[1]);
			break;
			case "OP_STARTGAME":
				//Message formL OP_STARTGAME
				handleStartGameMessage();
			break;
			case "OP_TIME":
				//Message form: OP_TIME time
				handleTimeUpdateMessage(sepInput[1]);
			break;
			case "OP_PLAYERDAMAGE":
				//Message form: OP_PLAYERDAMAGE playerID
				handlePlayerDamageMessage(sepInput[1]);
			break;
			case "OP_GAMEOVER":
				//Message form: OP_GAMEOVER winnerID
				handleGameOverMessage(sepInput[1]);
			break;
			case "OP_CONNECTEDLIST":
				//Message form: OP_CONNECTEDLIST client[1] client[2] ...
				handleConnectedListMessage(sepInput[1]);
			break;
		}
	}
	
	private boolean handleKeyPressMessage(String k){
		String[] inputArray = (k.split(" "));
		String clientID = inputArray[0];
		String key = inputArray[1];
		if(hasController){
			switch(key){
				case "W":
					controller.upClimb(clientID);
				break;
				case "A":
					controller.left(clientID);
				break;
				case "S":
					controller.downLadder(clientID);
				break;
				case "D":
					controller.right(clientID);
				break;
				case "SPACE":
					controller.jump(clientID);
				break;
				case "U":
					controller.attack(clientID);//clientID will be attacker
				break;
				case "Y":
					controller.freeze(clientID);
				break;
			}
			return true;
		}else{
			System.out.println("Does not have controller");
			return false;
		}
	}
	
	private boolean handleKeyReleaseMessage(String k){
		String[] inputArray = (k.split(" "));
		String clientID = inputArray[0];
		String key = inputArray[1];
		if(hasController){
			if(key.equals("A") || key.equals("D")){
				controller.stop(clientID);
			}
			return true;
		}else{
			return false;
		}
	}
	
	private void handlePlayerPositionMessage(String input){
		String[] inputArray = input.split(" ");
		String clientToUpdate = inputArray[0];
		
		if(!clientToUpdate.equals(clientID)){
			float updatedX = Float.parseFloat(inputArray[1]);
			float updatedY = Float.parseFloat(inputArray[2]);
			if(hasController){
				controller.dictatePlayerPosition(clientToUpdate, updatedX, updatedY);
			}
		}
	}
	
	private boolean handleConnectionMessage(String input){
		String[] inputArray = input.split(" ");
		String connectedClientID = inputArray[0];
		String isConnected = inputArray[1];
		if(connectedClientID.equals(clientID)){
			if(isConnected.equals("TRUE")){
				establishedConnection = true;
				System.out.println(clientID + " established connection");
			}else if(isConnected.equals("FALSE")){
				establishedConnection = false;
				System.out.println("Could not establish connection");
			}
		}else{
			//It's a new client that isn't this client, so display/store their ID somehow.
		}
		
		return establishedConnection; 
	}
	
	private boolean handleNewPlayerMessage(String input){
		String[] inputArray = input.split(" ");
		String newPlayerID = inputArray[0];
		float newX = Float.parseFloat(inputArray[1]);
		float newY = Float.parseFloat(inputArray[2]);
		world.addNewPlayer(newPlayerID, newX, newY);
		return true;
	}
	
	private boolean handleNewWorldMessage(String input){
		String[] inputArray = input.split(" ");
		String levelFileName = inputArray[0].trim();
		world = new World(levelFileName, clientID);
		return true;
	}
	
	private void handleStartGameMessage(){
		gameRunning = true;
	}
	
	public void handleTimeUpdateMessage(String input){
		if(hasController){
			controller.timeUpdate(input);
		}
	}
	
	public void handlePlayerDamageMessage(String input){
		controller.doPlayerDamage(input);
	}

	private void handleGameOverMessage(String winnerID){
		controller.gameOver(winnerID);
	}
	
	private void handleConnectedListMessage(String input){
		String[] clients = input.split(" ");
		connectedClientIDs = new ArrayList<String>(Arrays.asList(clients));
	}
	
	/**
	*Sends a message from the open DatagramSocket
	*@param m Message of the form :
			  "OP_COMMAND clientID data_1 data_2 ... data_n"
	*/
	public boolean sendMessage(String m){
		byte[] data = new byte[512];
		data = m.getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, 22222);
		try {
			datagramSocket.send(packet);
			return true;
		} catch (IOException e) {
			e.printStackTrace(); 
			return false;
		}
	}
	/**
	*Forms a message based on the client's internal game state containing data about the client's player's position in the game
	*Then sends the message @see sendMessage()
	*/
	public void sendPlayerPositionMessage(){
		Player p = world.getPlayer();
		float x = p.getXPos();
		float y = p.getYPos();
		String message = "OP_PLAYERPOSITION " + clientID + " " + x + "f " + y + "f";
		sendMessage(message);
	}
	
	/**
	*Forms a message for communicating a key press on the client's machine,
	*then sends the message @see sendMessage()
	*/
	public void sendKeyPress(String key){
		//Will eventually receive both the key and the client which pressed the key
		String message = "OP_KEYPRESS" + " " + clientID + " " + key;
		sendMessage(message);
	}
	
	/**
	*Forms a message for communicating a key release on the client's machine,
	*then sends the message @see sendMessage()
	*/
	public void sendKeyRelease(String key){
		String message = "OP_KEYRELEASE" + " " + clientID + " " + key;
		sendMessage(message);
	}
	
	/**
	*Gets a boolean representing whether the game is running
	*/
	public boolean isGameStarted(){
		return gameRunning;
	}
	
	/**
	*Does goodness know what
	*/
	public void leaderBoard(boolean bool){
		controller.leaderBoard(bool);
	}
	
	/**
	*Sets the GameController stored. Sets the hasController property to true;
	*/
	public void addController(GameController controller){
		this.controller = controller;
		this.hasController = true;
	}
	
	/**
	*Gets whether the client has successfully connected to the server
	*/
	public boolean isConnected(){
		return establishedConnection;
	}
	
	/**
	*Returns the list of clients connected to the same server as this GameClient
	*@return An ArrayList of strings representing the clients connected to the same server
	*/
	public ArrayList<String> getConnectedClients(){
		return connectedClientIDs;
	}
	
	/**
	*Returns the client's game state
	*@return A World object representing the client's internal game state.
	*/
	public World getWorld(){
		return world;
	}
}
