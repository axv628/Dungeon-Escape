package game.view;

import game.controller.GameClient;

public class LobbyThread extends Thread{
	
	private WindowManager manager;
	private GameClient client;
	
	private boolean inLobby;
	
	public LobbyThread(WindowManager manager, GameClient client){
		this.manager = manager;
		this.client = client;
		inLobby = true;
		System.out.println("LobbyThread started");
	}
	
	public void run(){
		while(inLobby){
			System.out.println("LT loop");
			manager.lobbyRepaint();
			try{
				sleep(1000);
			}catch(InterruptedException e){
				System.out.println("Interrupted LobbyThread");
			}
			inLobby = !client.isGameStarted();
			//Periodically update the clientlistlabel in the HostingMenu or JoinMenu dependant on some shit
		}
		if((manager.getGameState()).equals("Join Server")){
			manager.startGameAsClient(client);
		}
		System.out.println("LT ended");
	}

}