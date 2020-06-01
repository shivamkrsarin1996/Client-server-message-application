**********
**About:**
**********

It is a multithreaded client/server ChatServer based on console which uses Java Socket programming. 
A server listens for connection requests from clients across the network or even from the same machine. 
Clients know how to connect to the server via an IP address and port number. 
After connecting to the server, the client gets to choose his/her username on the chat room. 
The client sends a message, the message is sent to the server using ObjectOutputStream in java. 
After receiving the message from the client, the server broadcasts the message if it is not a private message. 
And if it is a private message which is detect using ‘@’ followed by a valid username, then send the message only to that user. 
Java object serialization to transfer the messages.

*****************
**Instructions:**
*****************

**Server**
1. Run Server_GUI.java file
2.The Server will start on the configured localhost and port

**Client**
Run Client_GUI.java file
The Client will connect to the server , if the server is server was previously started.
Enter the Username for the client to connect to the Server
If the username is unique, the client will start the connection with the server.

To implement multiple client chat , re run the Client_GUI.java file


**Chat**

While in client console:
1. Simply type the message to send broadcast to all active clients
2. Type '@username<space>yourmessage' without quotes to send message to desired client
3. Type 'WHOISIN' without quotes to see list of active clients
4. Type 'LOGOUT' without quotes to logoff from server
