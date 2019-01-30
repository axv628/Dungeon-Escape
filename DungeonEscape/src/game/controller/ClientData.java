package game.controller;

import java.net.InetAddress;

/**
*Stores information about client communication for use by the server
*/
public class ClientData{
	
	/**
	*Stores the client machine's address
	*/
	private InetAddress address;
	
	/**
	*The client's port on which the server will communicate
	*/
	private int port;
	
	
	/**
	*Creates a new ClientData with the given address and port.
	*/
	public ClientData(InetAddress address, int port){
		this.address = address;
		this.port = port;
	}
	
	
	/**
	*Gets the ip address of the client
	*@return this client's ip address
	*/
	public InetAddress getAddress(){
		return address;
	}
	
	/**
	*Gets the port on which the server will communicate to the client
	*@return this client's port
	*/
	public int getPort(){
		return port;
	}
}