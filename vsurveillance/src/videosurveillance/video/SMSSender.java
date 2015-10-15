package videosurveillance.video;

import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import javax.wireless.messaging.*;

import java.io.IOException;

/**
 * Prompts for text and sends it via an SMS MessageConnection
 */
public class SMSSender 
    implements Runnable {

  
    /** The port on which we send SMS messages */
    String smsPort; 
    /** The URL to send the message to */
    String destinationAddress;
    /** Area where the user enters a message to send */
    TextBox messageBox = new TextBox("Enter Message", null, 65535, TextField.ANY);
    /** Where to return if the user hits "Back" */
    Displayable backScreen;
    /** Displayed when a message is being sent */
    Displayable sendingScreen;
    
    /**
     * Initialize the MIDlet with the current display object and
     * graphical components. 
     */
    public SMSSender() {
	
	//	smsPort = getAppProperty("SMS-Port");
        
    }

    /**
     * Prompt for message and send it
     */
    public void promptAndSend(String destinationAddress, String smsPort)
    {
        this.destinationAddress = destinationAddress;
		this.smsPort = smsPort;
		new Thread(this).start();
       
    }
    
    
        
    /**
     * Send the message. Called on a separate thread so we don't have
     * contention for the display
     */
    public void run() {
        String address = destinationAddress + ":" + smsPort;
           
        MessageConnection smsconn = null;
        try {
            /** Open the message connection. */
            smsconn = (MessageConnection)Connector.open(address);

            TextMessage txtmessage = (TextMessage)smsconn.newMessage(
                MessageConnection.TEXT_MESSAGE);
            txtmessage.setAddress(address);
            txtmessage.setPayloadText(messageBox.getString());
            smsconn.send(txtmessage);
        } catch (Throwable t) {
System.out.println("Send caught: ");
t.printStackTrace();
        }
        if (smsconn != null) {
            try {
                smsconn.close();
            } catch (IOException ioe) {
System.out.println("Closing connection caught: ");
ioe.printStackTrace();
            }                
        }
    }
}
