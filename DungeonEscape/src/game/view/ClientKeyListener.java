package game.view;

import java.awt.event.*;

import game.controller.GameClient;

public class ClientKeyListener implements KeyListener{	
		
		private GameClient client;
		private boolean aPressed, dPressed, wPressed, sPressed;
		
		public ClientKeyListener(GameClient client){
			this.client = client;
			
			aPressed = false;
			dPressed = false;
			wPressed = false;
			sPressed = false;
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int id = e.getKeyCode();
			
			if(id== KeyEvent.VK_TAB){
				client.leaderBoard(true);
			}
			
			if (id == KeyEvent.VK_SPACE){
				client.sendKeyPress("SPACE");
			}
			else if (id == KeyEvent.VK_A){
				if(!aPressed){
					client.sendKeyPress("A");
					aPressed = true;
				}
			}
			else if (id == KeyEvent.VK_D){
				if(!dPressed){
					client.sendKeyPress("D");
					dPressed = true;
				}
			}
			else if (id == KeyEvent.VK_W){
				client.sendKeyPress("W");
			}
			else if (id == KeyEvent.VK_S){
				client.sendKeyPress("S");
			}
			else if (id == KeyEvent.VK_U){
				client.sendKeyPress("U");
			}
			else if (id == KeyEvent.VK_Y){
				client.sendKeyPress("Y");
			}
			
		}

		@Override
		public void keyReleased(KeyEvent e) {
			client.sendPlayerPositionMessage();
			int id = e.getKeyCode();
			if(id== KeyEvent.VK_TAB){
				client.leaderBoard(false);
			}
			if (id == KeyEvent.VK_A){
				client.sendKeyRelease("A");
				aPressed = false;
			} 
			else if(id == KeyEvent.VK_D){
				client.sendKeyRelease("D");
				dPressed = false;
				//controller.stop();
			}
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
		}
	
	}