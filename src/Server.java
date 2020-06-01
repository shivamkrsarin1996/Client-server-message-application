//URL-https://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
/*
 * @author-Shivam Kumar Sareen
 * @Student Id- 1001751987
 * */
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JOptionPane;

//import Server.ClientThread;

/*
 * The server that can be run both as a console application or a GUI
 */
public class Server {
	// a unique ID for each connection
	private static int uniqueId;
	// an ArrayList to keep the list of the Client
	private ArrayList<ClientThread> al;
	// if I am in a GUI
	private ServerGUI sg;
	// to display time
	private SimpleDateFormat sdf;
	// the port number to listen for connection
	private int port;
	// the boolean that will be turned of to stop the server
	private boolean keepGoing;
	public static ArrayList<String> client_names;
	static int srvrPort=9928;
	
	public static void removeClientnames(String user)
	{			System.out.println("Inside removeClient");
		if (client_names.contains(user)) 
		{
			client_names.remove(user);
			System.out.println(user+" removed");
		}
	}

	public static void addClientnames(String user)
	{			System.out.println("Inside addClient");
		if (!client_names.contains(user)) {
			client_names.add(user);
		}
	}
	
	public static boolean isClientOnline(String user) {
		System.out.println("Inside isClientOnline");
		return client_names.contains(user);
	}
	
/*	public static String ArraytoString(ArrayList<String> client_list)
	{
			String value="";
		if (client_list.size()!=0)
		{
			 value=client_list.toString();
			System.out.println("In arrtostring value="+value);
		}
		return null;
		
	}*/
	/*
	 *  server constructor that receive the port to listen to for connection as parameter
	 *  in console
	 */
	public Server(int port)
	{
		this(port, null);
	}
	
	public Server(int port, ServerGUI sg) 
	{
		// GUI or not
		this.sg = sg;
		// the port
		this.port = port;
		// to display hh:mm:ss
		sdf = new SimpleDateFormat("HH:mm:ss");
		// ArrayList for the Client list
		al = new ArrayList<ClientThread>();
		client_names=new ArrayList<String>();
	}
	
	
	public void start() 
	{
		System.out.println("Inside server start");
		keepGoing = true;
		/* create socket server and wait for connection requests */
		try 
		{
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);

			display("Server waiting for Clients on port " + port + ".");
			
			// infinite loop to wait for connections
			while(keepGoing) 
			{
				// format message saying we are waiting
				//display("Server waiting for Clients on port " + port + ".");
				
				Socket socket = serverSocket.accept();  	// accept connection
				// if I was asked to stop
				if(!keepGoing)
					break;
				ClientThread t = new ClientThread(socket);  // make a thread of it
			//	System.out.println("Adding t to al, size of al="+al.size());
				al.add(t);									// save it in the ArrayList
	
				//		System.out.println("Added t to al, size of al="+al.size() +"content =");
		//		System.out.println("will go to run method now");
				
				t.start();
			}
			// I was asked to stop
			try {
				serverSocket.close();
				for(int i = 0; i < al.size(); ++i) {
					ClientThread tc = al.get(i);
					try {
					tc.sInput.close();
					tc.sOutput.close();
					tc.socket.close();
					}
					catch(IOException ioE) {
						// not much I can do
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		// something went bad
			catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}
	
	
    /*
     * For the GUI to stop the server
     */
	protected void stop() 
	{
		keepGoing = false;
		// connect to myself as Client to exit statement 
		// Socket socket = serverSocket.accept();
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
			// nothing I can really do
		}
	}
	
	
	 // Display an event (not a message) to the console or the GUI
	private void display(String msg) 
	{
		String time = sdf.format(new Date()) + " " + msg;
		if(sg == null)
			System.out.println(time);
		else
			sg.appendEvent(time + "\n");
	}
	
	
	
//URL- https://github.com/abhi195/Chat-Server/blob/master/src/Server.java	
	 // to broadcast a message to all Clients
	private synchronized boolean broadcast(String message) 
	{
		// add HH:mm:ss and \n to the message
		String time = sdf.format(new Date());
		String messageLf = time + " " + message + "\n";
		
		// display message on console or GUI
		if(sg == null)
			System.out.print(messageLf);
		else
			sg.appendRoom(messageLf);     // append in the room window

//my logic for multicast-start
		
// to check if message is private i.e. client to client message
			if(message.contains("@")==true)
				{	
		
					String[] w = message.split(" ",3);
					System.out.println("w="+w);
					boolean isPrivate = false;
					if(w[1].charAt(0)=='@') 	//check if at index 0 character is @
						isPrivate=true;
	//w[0] contains the sender name				
	//w[1] contains the list of recipients	
	//w[2] contains the message				
					
	//unicast- if private message, send message to mentioned username only
	//multicast mentioned with a multiple users separated using ","
					if(isPrivate==true)
					{	
//multicast					
						if(w[1].contains(","))
						{	
							System.out.println("will be a multicast");
							String str_for_multi_users=w[1].substring(1);
						
						//creating an arraylist to store users for multicast
							ArrayList<String> listof_multicast_users= new ArrayList<String>();
							String[] recipient=str_for_multi_users.split(",");
							System.out.println("recipient's list="+recipient);
							System.out.println("No of users in reciepient="+recipient.length);
							
							for (int i=0;i<recipient.length;i++)
							{
								System.out.println("Users="+ recipient[i]);
								
								
								boolean found=false;
																
								// we loop in reverse order to find the mentioned username
								for(int y=al.size(); --y>=0;)
								{
									ClientThread ct1=al.get(y);
									String check1=ct1.username;
									System.out.println("from ct1.username="+check1);
									String check=ct1.getUsername();
									System.out.println("from getUsername()="+check);
									if(check.equals(recipient[i]))
									{
										// try to write to the Client if it fails remove it from the list
										if(!ct1.writeMsg(messageLf))
										{
											al.remove(y);
											display("Disconnected Client " + ct1.username + " removed from list.");
										}
										// username found and delivered the message
										found=true;
										break;
									}
									
								}
								// mentioned user not found, return false
								if(found!=true)
								{
									return false; 
								}
								
							}

						}
						
	//unicast						
						if (!w[1].contains(","))
						{
							System.out.println("will be a unicast");
							
							String tocheck=w[1].substring(1, w[1].length());
							
							message=w[0]+w[2];
							System.out.println("w[0]="+w[0]+",w[1]="+w[1]+",w[2]="+w[2]+",message="+message);
							//String messageLf = time + " " + message + "\n";
							
							
							boolean found=false;
							
							
							// we loop in reverse order to find the mentioned username
							for(int y=al.size(); --y>=0;)
							{
								ClientThread ct1=al.get(y);
								String check1=ct1.username;
								System.out.println("from ct1.username="+check1);
								String check=ct1.getUsername();
								System.out.println("from getUsername()="+check);
								if(check.equals(tocheck))
								{
									// try to write to the Client if it fails remove it from the list
									if(!ct1.writeMsg(messageLf))
									{
										al.remove(y);
										display("Disconnected Client " + ct1.username + " removed from list.");
									}
									// username found and delivered the message
									found=true;
									break;
								}
								
							}
							// mentioned user not found, return false
							if(found!=true)
							{
								return false; 
							}
		//logic ends		
						} //end unicast
						

					}	// end if isprivate		
						
				}         // end- if msg contains @ -unicast/multicast
// end of unicast/multicast
			
// if message is a broadcast message
				else
				{
					//String messageLf = time + " " + message + "\n";
						// display message
						System.out.print(messageLf);
					
					// we loop in reverse order in case we would have to remove a Client
					// because it has disconnected
					for(int i = al.size(); --i >= 0;) {
						ClientThread ct = al.get(i);
						// try to write to the Client if it fails remove it from the list
						if(!ct.writeMsg(messageLf)) {
							al.remove(i);
							display("Disconnected Client " + ct.username + " removed from list.");
						}
					}
				}
				return true;
				
				
			}
		


	// for a client who logoff using the LOGOUT message
	synchronized void remove(int id)
	{
		// scan the array list until we found the Id
		for(int i = 0; i < al.size(); ++i)
		{
			ClientThread ct = al.get(i);
			// found it
			if(ct.id == id)
			{
				al.remove(i);
				return;
			}
		}
	}

		
		
	/*
	 *  To run as a console application just open a console window and: 
	 * > java Server
	 * > java Server portNumber
	 */ 
	public static void main(String[] args) 
	{
		// start server on PortNumber that is specified 
		int portNumber = srvrPort;

		// create a server object and start it
		Server server = new Server(portNumber);
		server.start();
	}

	/** One instance of this thread will run for each client */
	class ClientThread extends Thread {
		// the socket where to listen/talk
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		// my unique id (easier for deconnection)
		int id;
		// the Username of the Client
		String username;
		// the only type of message a will receive
		ChatMessage cm;
		// the date I connect
		String date;

		// Constructor
		ClientThread(Socket socket) {
			
			//ClientGUI cnt_gui;
			id = ++uniqueId;		// a unique id
			this.socket = socket;
			/* Creating both Data Stream */
			System.out.println("Thread trying to create Object Input/Output Streams");
			try
			{	
				// create output first
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				System.out.println("I/O stream created for Server");
				
				String status="reject";
			
			while(true)
			{
						System.out.println("Status="+status);
						// read the username
						username = (String) sInput.readObject();
						System.out.println("User name from server in Cl thread="+username);
			//Calling the method to check if the username entered is unique or not
						if(Server.isClientOnline(username))
						{
							sOutput.writeUTF("Reject");
							sOutput.flush();
							//continue;
						}
						else 
						{
							Server.addClientnames(username);
							sOutput.writeUTF("Accept");
							sOutput.flush();
							break;
						}
						
					
				} //end-while
			display(username + " just connected.");
			}
			catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
			catch (ClassNotFoundException e) {
			}
            date = new Date().toString() + "\n";
		}// end of constructor
		
		
		public String getUsername()
		{
			return username;
		}
		
		

		// what will run forever
		public void run() {
			System.out.println("Inside server run");

			// to loop until LOGOUT
			boolean keepGoing = true;
			while(keepGoing) {
				// read a String (which is an object)
				try {
					cm = (ChatMessage) sInput.readObject();
				}
				catch (IOException e) {
					display(username + " Exception reading Streams: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {
					break;
				}
				// the messaage part of the ChatMessage
				String message = cm.getMessage();

				// Switch on the type of message receive
				switch(cm.getType()) {

				case ChatMessage.MESSAGE:
					boolean confirmation = broadcast(username + ": " + message);
					if(confirmation==false){
						String msg =  "Sorry. No such user exists.\n" ;
						writeMsg(msg);
					
					}
					break;
				case ChatMessage.LOGOUT:
					display(username + " disconnected with a LOGOUT message.");
					Server.removeClientnames(username);
					keepGoing = false;
					break;
				case ChatMessage.ONLINEUSER:
					writeMsg("\n");
					writeMsg("\n **************List of the users connected at " + sdf.format(new Date()) + "**********\n");
					// scan al the users connected
					for(int i = 0; i < al.size(); ++i) {
						ClientThread ct = al.get(i);
						writeMsg((i+1) + ") " + ct.username + " since " + ct.date);
					}
					writeMsg("\n");
					break;
				}
			}
			// remove myself from the arrayList containing the list of the
			// connected Clients
			remove(id);
			close();
		}
		
		// try to close everything
		private void close() {
			// try to close the connection
			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}

		/*
		 * Write a String to the Client output stream
		 */
		private boolean writeMsg(String msg) {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				sOutput.writeObject(msg);
			}
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}
	}
}

