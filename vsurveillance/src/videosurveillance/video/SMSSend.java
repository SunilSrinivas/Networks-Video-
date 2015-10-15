package videosurveillance.video;

import javax.microedition.midlet.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import javax.wireless.messaging.*;

import java.io.IOException;


public class SMSSend {

    /** The port on which we send SMS messages */
    String smsPort;
    /** Area where the user enters the phone number to send the message to */
    Alert errorMessageAlert;
    /** Alert that is displayed when a message is being sent */
    Alert sendingMessageAlert;
    /** Prompts for and sends the text message */
    SMSSender sender;
    /** The last visible screen when we paused */ 
    Displayable resumeScreen = null;
    
    /**
     * Initialize the MIDlet with the current display object and
     * graphical components. 
     */
    public SMSSend() {
       
	  //  smsPort = getAppProperty("SMS-Port");

        errorMessageAlert = new Alert("SMS", "hh", null, AlertType.ERROR);
        errorMessageAlert.setTimeout(5000);
    
        sendingMessageAlert = new Alert("SMS", null, null, AlertType.INFO);
        sendingMessageAlert.setTimeout(5000);
       
        sender = new SMSSender();
		//System.out.println("hhhh");
        
		
            
        
    }

    
    /**
     * Prompt for and send the message
     */
    public void promptAndSend(String address ) {
		
		// smsPort = getAppProperty("SMS-Port");

       // String address = txtDest.getString();
        if (!SMSSend.isValidPhoneNumber(address)) {
            errorMessageAlert.setString("Invalid phone number");
            //display.setCurrent(errorMessageAlert, address);
            return;
        }
        String statusMessage = "Sending message to " + address + "...";
        sendingMessageAlert.setString(statusMessage);
        sender.promptAndSend("sms://" + address , "50000");
    }
    
    /**
     * Check the phone number for validity
     * Valid phone numbers contain only the digits 0 thru 9, and may contain 
     * a leading '+'.
     */
    private static boolean isValidPhoneNumber(String number) {
        char[] chars = number.toCharArray();
        if (chars.length == 0) {
            return false;
        }
        int startPos = 0;
        // initial '+' is OK
        if (chars[0] == '+') {
            startPos = 1;
        }
        for (int i = startPos; i < chars.length; ++i) {
            if (!Character.isDigit(chars[i])) {
                return false;
            }
        }
        return true;
    }
}
