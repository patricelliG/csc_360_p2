import java.net.*; 
import java.io.*; 
import java.util.*; 

/** 
 * Open an SMTP connection to a mailserver and send one mail. 
 * 
 */ 
public class SMTPConnection { 
    /* The socket to the server */ 
    private Socket connection; 

    /* Streams for reading and writing the socket */ 
    private BufferedReader fromServer; 
    //private DataOutputStream toServer; 
    private PrintStream toServer; 
    private String server = "smtp.tcnj.edu";
    private int portNumber = 25;
    private String hostname = "hostname"; // Change to proper hostname

    private static final int SMTP_PORT = 25; 
    private static final String CRLF = "\r\n"; 

    /* Are we connected? Used in close() to determine what to do. */ 
    private boolean isConnected = false; 

    /* Create an SMTPConnection object. Create the socket and the  
       associated streams. Initialize SMTP connection. */ 
    public SMTPConnection(Envelope envelope) throws IOException { 

        // Attempt to connect to the server
//        try {
//        serverSocket = new Socket (server, portNumber); 
//        fromServer = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
//        toServer = new DataOutputStream(serverSocket.getOutputStream());
          // Temporary for troubleshooting
          fromServer = new BufferedReader(new InputStreamReader(System.in)); 
          toServer = System.out; 
//        }
//        catch (IOException e) {
//            throw e;
//        }

        // Check server reply
        String serverReply = fromServer.readLine();
        int replyCode = parseReply(serverReply);
        if (replyCode != 220) {
            throw new IOException();
        }

        /* SMTP handshake. We need the name of the local machine. 
           Send the appropriate SMTP handshake command. */ 
        
        String localhost = hostname; 
        sendCommand(("HELO" + localhost + CRLF), 250); 
        isConnected = true; 
    } 

    /* Send the message. Write the correct SMTP-commands in the 
       correct order. No checking for errors, just throw them to the 
       caller. */ 
    public void send(Envelope envelope) throws IOException { 
        /* Fill in */ 
        /* Send all the necessary commands to send a message. Call 
           sendCommand() to do the dirty work. Do _not_ catch the 
           exception thrown from sendCommand(). */ 
        /* Fill in */ 
        
        // Greet
        sendCommand("HELO",250);
        // Sender line
        sendCommand(("MAIL FROM: " + envelope.Sender), 250);
        // Recipient
        sendCommand(("RCPT TO: " + envelope.Recipient), 250);
        // Data segment
        sendCommand(("DATA" + CRLF), 354);

        // Message body
        String messageBody;
        // Add headers 
        messageBody = "SUBJECT: " + envelope.Message.Headers;
        // Add blank line
        messageBody += CRLF;
        // Add data from message
        messageBody += envelope.Message.Body;
        // Clear out the body
        messageBody += CRLF + "." + CRLF;
        // Finally, send the body...
        sendCommand(messageBody, 250);
    } 

    /* Close the connection. First, terminate on SMTP level, then 
       close the socket. */ 
    public void close() { 
        isConnected = false; 
        try { 
            sendCommand("QUIT", 221); 
            // connection.close(); 
        } catch (IOException e) { 
            System.out.println("Unable to close connection: " + e); 
            isConnected = true; 
        } 
    } 

    /* Send an SMTP command to the server. Check that the reply code is 
       what is is supposed to be according to RFC 821. */ 
    private void sendCommand(String command, int rc) throws IOException { 

        // Add the carridge return + line feed
        command += CRLF;
        
        // Send the command to the server
//        toServer.writeBytes(command);
        toServer.print(command);

        // Get the reply code
        String reply = fromServer.readLine();
        // Parse the responce code
        int replyCode = parseReply(reply);

        // Check the reply for an error
        if (replyCode != rc) {
            throw new IOException(command);
        }
        else {
            return;
        }
    } 

    /* Parse the reply line from the server. Returns the reply code. */ 
    private int parseReply(String reply) { 
        StringTokenizer replyTokens = new StringTokenizer(reply);
        String replyCodeString = replyTokens.nextToken(); // Get the first token 
        int replyCode = Integer.parseInt(replyCodeString); // Convert in to an Integer
        return replyCode;
    } 

    /* Destructor. Closes the connection if something bad happens. */ 
    protected void finalize() throws Throwable { 
        if(isConnected) { 
            close(); 
        } 
        super.finalize(); 
    } 
} 




