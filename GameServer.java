package game.controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

import game.view.Game;
import game.model.World;
import game.model.objects.Player;

/**
*Maintains connections to the game clients, and runs the multiplayer game, authoratively.
*/
public class GameServer extends Thread {
	
	private ServerGameLoop gameLoop;
	private GameEngine gameEngine;
	private GameController controller;
	
	private DatagramSocket datagramSocket;
	private InetAddress hostIP;
	
	private boolean hasController = false;
	private boolean serverRunning = false;
	private boolean gameRunning = false;
	
	private int knockOutPeriod = 15;

	private HashMap<String, ClientData> clientList;
	
	private World world;
	private String levelPath;

	/**
	*Creates a new game server, initialises all the things and stuff
	*/
	public GameServer() {
		clientList = new HashMap<String, ClientData>();
		
		try {
			datagramSocket = new DatagramSocket(22222);
			datagramSocket.setSoTimeout(5000);
			serverRunning = true;
		} catch (SocketException e) {
			e.printStackTrace();
		}

		try {
			hostIP = InetAddress.getByName(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	*Runs the GameServer Thread
	*Then loops through listening on the DatagramSocket and handling received DatagramPackets appropriately
	*Waits for client connections until the game is started, then manages the game state between the clients and stuff
	*/
	@Override
	public void run() {
		while(serverRunning){
			DatagramPacket message = receiveMessage();
			handleClientInput(message);
		}
		System.out.println("Server closing Socket");
		datagramSocket.close();
	}
	
	private DatagramPacket receiveMessage(){
		byte[] data = new byte[512];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try{
			datagramSocket.receive(packet);
		}catch (IOException e){
			System.out.println("IOX");
			//e.printStackTrace();
		}
		return packet;
	}
	
	private void handleClientConnection(DatagramPacket packet){
		InetAddress newClientAddress = packet.getAddress();
		int newClientPort = packet.getPort();
		String[] message = (new String(packet.getData()).trim()).split(" ");
		
		String command = message[0];
		String newClientID = message[1];
		
		if(command.equals("OP_CONNECT") && !(clientList.containsKey(newClientID))){
			clientList.put(newClientID, new ClientData(newClientAddress, newClientPort));
			sendMessageToAll("OP_CONNECT " + newClientID + " TRUE");
		}else{
			sendMessage("OP_CONNECT " + newClientID + " FALSE", newClientAddress, newClientPort);
		}
		
		String connectedClients = "";
		for(String clientID: clientList.keySet()){
			connectedClients = connectedClients + clientID + " ";
		}
		sendMessageToAll("OP_CONNECTEDLIST " + connectedClients);
	}
	
	private void handleClientInput(DatagramPacket packet){
		String input = (new String(packet.getData())).trim();
		//Takes the message from the client and sends responses to the client/all the clients accordingly.
		String[] sepInput = input.split(" ", 2);
		switch(sepInput[0]){
			case "OP_KEYPRESS":
				handleKeyPressMessage(sepInput[1]);
				//Currently just pings the data back to the client
				sendMessageToAll(input);
			break;
			case "OP_KEYRELEASE":
				handleKeyReleaseMessage(sepInput[1]);
				//Currently just pings the data back to the client
				sendMessageToAll(input);
			break;
			case "OP_PLAYERPOSITION":
				handlePlayerPositionMessage(sepInput[1]);
			break;
			case "OP_CONNECT":
				handleClientConnection(packet);
			break;
		}
	}

	private void handlePlayerPositionMessage(String input){
		String[] inputArray = (input.split(" "));
		String clientID = inputArray[0];
		
		try{
			float updatedX = Float.parseFloat(inputArray[1]);
			float updatedY = Float.parseFloat(inputArray[2]);
			if(hasController){
				controller.dictatePlayerPosition(clientID, updatedX, updatedY);
			}
		}catch(NumberFormatException e){
			System.out.println("NFE");
		}
		
	}
	
	private boolean handleKeyPressMessage(String input){
		String[] inputArray = (input.split(" "));
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
					controller.attack(clientID);
				break;
				case "Y":
					controller.freeze(clientID);
				break;
			}
			return true;
		}else{
			System.out.println("Server does not have controller");
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
	
	private boolean sendMessage(String message, InetAddress address, int port){
		byte[] data = new byte[512];
		data = message.getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
		try {
			datagramSocket.send(packet);
			return true;
		} catch (IOException e) {
			e.printStackTrace(); 
			return false;
		}
	}
	
	private void sendMessageToAll(String message){
		for(String clientID : clientList.keySet()){
			ClientData recipient = clientList.get(clientID);
			sendMessage(message, recipient.getAddress(), recipient.getPort());
		}
	}
	
	/**
	*Starts the game; initialises the world to be played, and tells all the clients the initial information
	*@param selectedLevel Integer representing the chosen level to be played
	*@param numAIPlayers The number of AI players to be added to the game world
	*/
	public boolean startGame(int selectedLevel, int numAIPlayers){
		
		if (selectedLevel == 0){
			levelPath = "TheWildEscape/src/resources/L1M.txt";
		} else if (selectedLevel == 1){
			levelPath = "TheWildEscape/src/resources/L2M.txt";
		}
		else levelPath = "TheWildEscape/src/resources/L3M.txt";
		
		String[] aiPlayers = new String[numAIPlayers];
		for (int i=0; i<numAIPlayers; i++){
			String aiID = "AI" + String.valueOf(i);
			aiPlayers[i] = aiID;
		}
		
		//All clients and the server for now will only run this level
		String[] clients = (clientList.keySet()).toArray(new String[clientList.size()]);
		
		
		if(!(clients.length + aiPlayers.length > 1)){
			return false;
		}
		world = new World(levelPath, clients, aiPlayers);
		
		ConcurrentHashMap<String, Player> playerList = world.getPlayerList();
		
		//Send the players the levelfilepath to their locally stored initial world.
		sendMessageToAll("OP_NEWWORLD " + levelPath);
		
		//Send all the player starting points
		for(String clientID: playerList.keySet()){
			Player p = playerList.get(clientID);
			sendMessageToAll("OP_NEWPLAYER " + clientID + " " + p.getXPos() + " " + p.getYPos());
		}
		
		//Tell all the players the game is starting
		sendMessageToAll("OP_STARTGAME");
		
		gameRunning = true;
		return true;
	}
	
	/**
	*Updates all the connected clients of the game state; primarily player positions
	*/
	public void update(){
		ConcurrentHashMap<String, Player> playerList = world.getPlayerList();
		for(String clientID : playerList.keySet()){
			Player p = playerList.get(clientID);
			float xPos = p.getXPos();
			float yPos = p.getYPos();
			if(p.getAI()){
				System.out.println(clientID + " " + xPos + ", " + yPos);
			}
			sendMessageToAll("OP_PLAYERPOSITION " + clientID + " " + xPos + " " + yPos);
		}
	}
	
	/**
	*Returns the IP address of the server
	*@return The server's IP address
	*/
	public InetAddress getAddress(){
		return hostIP;
	}
	
	/**
	*Starts the server's game loop
	*/
	public void startGameLoop(){
		gameEngine = new GameEngine(world);
		this.controller = gameEngine.getController();
		hasController = true;
		gameLoop = new ServerGameLoop(gameEngine, this);
	}
	
	private void endGameLoop(){
		gameLoop.end();
	}
	
	/**
	*Returns the server's IP address as a string
	*@return The ip address as a string
	*/
	public String getIPString(){
		return hostIP.getHostAddress();
	}
	
	/**
	*Sends a time update to all the clients for managing their game state
	*@param time Integer representing the time in the game running
	*/
	public void timeUpdate(int time){//CALLED EVERY SECOND
		sendMessageToAll("OP_TIME " + time);
		if (time%knockOutPeriod == 0){//DEDUCTS LIFE FROM LOWEST SCORER EVERY 20 SECS
			String lowestScorer = "";
			int lowestScore = 50;
			ConcurrentHashMap<String, Player> pList = world.getPlayerList();
			for(String id : pList.keySet()){
				Player p = pList.get(id);
				if(p.getIsAlive()){
					int score = p.getScore();
					if (score < lowestScore){
						lowestScore = score;
						lowestScorer = id;
					}
				}
			}
			Player lowest = pList.get(lowestScorer);
			lowest.deductLife();
			sendMessageToAll("OP_PLAYERDAMAGE " + lowestScorer);
		}
		if (time == 0){//ENDS GAME WHEN TIME IS UP
			gameRunning = false;
			gameLoop.end();
		}
		
		if (getNumPlayersAlive()<2){//ENDS GAME WHEN ONE PLAYER REMAINS
			String winner = getAlivePlayers().get(0);
			sendMessageToAll("OP_GAMEOVER " + winner);
			gameRunning = false;
			serverRunning = false;
			gameLoop.end();
			
			interrupt();
			datagramSocket.close();
		}
	}
	
	private ArrayList<String> getAlivePlayers(){
		return world.getAlivePlayers();
	}
	
	/**
	*Returns the number of players left alive in the game
	*@return Integer representing the number of players left alive in the multiplayer game running
	*/
	public int getNumPlayersAlive(){
		ArrayList<String> list = getAlivePlayers();
		return list.size();
	}
}
