//URL- https://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
/*
 * @author-Shivam Kumar Sareen
 * @Student Id- 1001751987
 * */
import java.net.*;
import java.io.*;
import java.util.*;

import javax.swing.JOptionPane;

/*
 * The Client that can be run both as a console or a GUI
 */
public class Client  {

	// for I/O
	public ObjectInputStream sInput;		// to read from the socket
	public ObjectOutputStream sOutput;		// to write on the socket
	private Socket socket;

	// if I use a GUI or not
	private ClientGUI clnt_gui;
	
	// the server, the port and the username
	private String server;
	static private int port=9928;
	static private String username;
	/*
	 *  Constructor called by console mode
	 *  server: the server address
	 *  port: the port number
	 *  username: the username
	 */
	/*Client(String server, int port, String username) {
		
		// which calls the common constructor with the GUI set to null
	//	this(server, port, username, null);
		System.out.println("inside client constructor console");
	}*/

	/*
	 * Constructor call when used from a GUI
	 * in console mode the ClienGUI parameter is null
	 */
	Client(String server,int port,  ClientGUI clnt_gui) {
		
		this.server = server;
		this.port = port;
	//	this.username = username;
		// save if we are in GUI mode or not
		this.clnt_gui = clnt_gui;
		System.out.println("inside client constructor gui");
		
		try {
			System.out.println("inside client, port"+port);
			socket = new Socket(server, port);
		//	JOptionPane.showMessageDialog(clnt_gui, username+" has connected");
			//clnt_gui.lblClnm.setText("User Name: "+username);
		} 
		catch(ConnectException ce){
		//display("Error connectiong to server:" + ce);
		//System.out.println(ce);
		JOptionPane.showMessageDialog(clnt_gui, "Server Down!.Please check if the Server is running ");
		clnt_gui.tfInputMsg.setText("");
		System.exit(0);
		}
		catch(Exception ec) {
			//display("Error connectiong to server:" + ec);
			System.out.println(ec);
		//	JOptionPane.showMessageDialog(clnt_gui, "Server Down!.Please check if the Server is running ");
			
		}
		
		
		try
		{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("I/O stream created");
		}
		catch (IOException eIO) {
			System.out.println("Exception creating new Input/output Streams: " + eIO);
			display("Exception creating new Input/output Streams: " + eIO);
			
		}
	}
	
	/*
	 * To start the dialog
	 */
	public boolean start() {
		// try to connect to the server
		
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);
	
		/* Creating both Data Stream */
		
	/*	try
		{
			//String cl_status="reject";
			
			
		}
		catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}*/
		// creates the Thread to listen from the server 
		new ListenFromServer().start();
		// Send our username to the server this is the only message that we
		// will send as a String. All other messages will be ChatMessage objects
		
		// success we inform the caller that it worked
		return true;
	}

	/*
	 * To send a message to the console or the GUI
	 */
	private void display(String msg) {
		if(clnt_gui == null)
			System.out.println(msg);      // println in console mode
		else
			clnt_gui.append(msg + "\n");		// append to the ClientGUI JTextArea (or whatever)
	}
	
	/*
	 * To send a message to the server
	 */
	void sendMessage(ChatMessage msg) {
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	/*
	 * When something goes wrong
	 * Close the Input/Output streams and disconnect not much to do in the catch clause
	 */
	private void disconnect() {
		try { 
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {} // not much else I can do
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {} // not much else I can do
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {} // not much else I can do
		
		// inform the GUI
		if(clnt_gui != null)
			clnt_gui.connectionFailed();
			
	}

	public static void main(String[] args) {
		// default values
		int portNumber = port;
		String serverAddress = "localhost";
		String userName = username;
		//ClientGUI clnt=new ClientGUI();
	//	System.out.println("Inside main for userName" + username+ " of client="+clnt);

		// depending of the number of arguments provided we fall through
	/*	switch(args.length) {
			
			case 3:
				serverAddress = args[2];
			// > javac Client username portNumber
			case 2:
				try {
					portNumber = Integer.parseInt(args[1]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
					return;
				}
			// > javac Client username
			case 1: 
				userName = args[0];
			// > java Client
			case 0:
				break;
			// invalid number of arguments
			default:
				System.out.println("Usage is: > java Client [username] [portNumber] {serverAddress]");
			return;
		}*/
		
	//	Client client = new Client(serverAddress, port, userName);
		// create the Client object
	//	Client client = new Client(serverAddress, port, userName,clnt);
		// test if we can start the connection to the Server if it failed nothing we can do
	//	if(!client.start())
		//	return;
		
		
	/*	
		// wait for messages from user
		Scanner scan = new Scanner(System.in);
		// loop forever for message from the user
		while(true) {
			System.out.print("> ");
			// read message from user
			String msg = scan.nextLine();
			// logout if message is LOGOUT
			if(msg.equalsIgnoreCase("LOGOUT")) {
				client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
				// break to do the disconnect
				break;
			}
			// message WhoIsIn
			else if(msg.equalsIgnoreCase("ONLINEUSER")) {
				client.sendMessage(new ChatMessage(ChatMessage.ONLINEUSER, ""));				
			}
			else {				// default to ordinary message
				client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
			}
		}*/
		// done disconnect
	//	client.disconnect();	
	}

	/*
	 * a class that waits for the message from the server and append them to the JTextArea
	 */
	class ListenFromServer extends Thread {

		public void run() {
			while(true) {
				try {
					String msg = (String) sInput.readObject();
					// if console mode print the message and add back the prompt
					if(clnt_gui == null) {
						System.out.println(msg);
						System.out.print("> ");
					}
					else {
						clnt_gui.append(msg);
					}
				}
				catch(IOException e) {
					display("Client " +username+" has disconnected from the connection: ");
					if(clnt_gui != null) 
						clnt_gui.connectionFailed();
					break;
				}
				// can't happen with a String object but need the catch anyhow
				catch(ClassNotFoundException e2) {
				}
			}
		}
	}
}
