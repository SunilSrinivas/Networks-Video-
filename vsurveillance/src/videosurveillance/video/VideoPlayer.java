package videosurveillance.video;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.*;

import javax.microedition.io.Connector;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.Spacer;
import javax.microedition.lcdui.StringItem;
import javax.microedition.media.Control;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.GUIControl;
import javax.microedition.media.control.RateControl;
import javax.microedition.media.control.RecordControl;
import javax.microedition.media.control.VideoControl;
import javax.microedition.media.control.VolumeControl;
import javax.wireless.messaging.MessagePart;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;
import javax.wireless.messaging.MessagePart;
import javax.wireless.messaging.MultipartMessage;
import javax.wireless.messaging.SizeExceededException;


/**
 * Play Video/Capture in a Form using MMAPI
 *
 */
public class VideoPlayer extends Form
    implements Runnable, CommandListener, PlayerListener {

    private static final String TITLE_TEXT = "Video Player (Form) ";

    private static Player player = null;
    private static boolean isCapturePlayer;


    private static Image logo = null;

    private Display parentDisplay;
    private long duration;
    private final Command backCommand = new Command("Back", Command.BACK, 1);
    private final Command playCommand = new Command("Play", Command.ITEM, 1);
    private final Command snapCommand = new Command("Snapshot", Command.ITEM, 1);
    private final Command pauseCommand = new Command("Pause", Command.ITEM, 10);
	private TextField txtDest = new TextField("Destination","", 15, TextField.PHONENUMBER);
    private Item videoItem;
    private StringItem status;
    private StringItem audioStatus;
    private StringItem time;
	private Alert errorAlert;
	private MessagePart imagePart;
	private MessageConnection messageConnection;
	private Message nextMessage = null;
	private byte [] back_ground = null;
	private byte [] frame_ground = null;
	private int [] int_array_back_ground;
	private	int [] int_array_frame_ground;
	private	int [] int_array_fore_ground;

	private P p;
	private SMSSend smssend;

//    private RateControl rc;

    private int currentVolume;
    private boolean muted;
    private int currentRate = 100000;
    private VideoControl vidc;
	

    // pause/resume support
    private boolean suspended = false;
    private boolean restartOnResume = false;
    private long restartMediaTime;

    public VideoPlayer(Display parentDisplay) {
        super(TITLE_TEXT);

        this.parentDisplay = parentDisplay;
        initialize();
    }

    void initialize() {
        addCommand(backCommand);
        addCommand(snapCommand);
		append(txtDest);
        setCommandListener(this);

        try {
	    if (logo == null)
		logo = Image.createImage("/icons/logo.png");
        } catch (Exception ex) {
            logo = null;
        }
        if ( logo == null)
            System.out.println("can not load logo.png");

    }

    /*
     * Respond to commands, including back
     */
    public void commandAction(Command c, Displayable s) {
        //try {
            if (s == this) {
                if (c == backCommand) {
                    close();
                    parentDisplay.setCurrent(VideoTest.getList());
                } 
                
                else if (videoItem != null && c == snapCommand) {
					//System.out.print(""+txtDest.size());
					if (txtDest.size() != 0)
					{
						doSnapshot();
						//System.out.println("hhhh");//
						//p.rint("fjai");

					}
					else{
					//	p.rint("enter the number");
						//Alert.errorAlert.setString("Enter the Destiantion Number");

					}
                 //   
                } 
                
                else if (videoItem == null && c == pauseCommand) {
                    removeCommand(pauseCommand);
                    addCommand(playCommand);
                    pause();
                }
                
                else if (videoItem == null && c == playCommand) {
                    startPlaying();
                    removeCommand(playCommand);
                    addCommand(pauseCommand);
                }
            }
        //} catch (Exception e) {
        //    System.out.println("DEBUG: GOT EXCEPTION in VideoPlayer.commandAction("+c.toString()+","+s.toString()+")!");
        //    e.printStackTrace();
        //}
    }

    
    /**
     * Loops to see if volume and rate have been changed.
     */
    public void run() {
    	P.rint("VideoPlayer:run()");
    	
        //try {
            while (player != null) {
            	P.rint("VideoPlayer:run():while loop");
                // sleep 200 millis. If suspended, 
                // sleep until MIDlet is restarted
                do {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ie) {
                    }
                } while (player != null && suspended);

                synchronized (this) {
                    if (player == null)
                        return;


                    long k = player.getMediaTime();

                    
                }
                
            }
       
    }//run   

    

    
    /**
     * Called by MIDlet after instantiating this object. 
     * Creates the player.  XXX
     */
    public void open(String url) {   //capture://video        theCamera
    	P.rint("VideoPlayer:open()");
        try {
            synchronized (this) {
                if ( player == null ) {

                    player = Manager.createPlayer(url);          //creating player for the url

                    
                    
                    player.addPlayerListener(this);
        		    isCapturePlayer = url.startsWith("capture:");
                }
            }
            player.realize();      //realise
            

            
            
         
            
            //set to use_gui_primitive
            if (  (vidc = (VideoControl) player.getControl("VideoControl")) != null  ) {
                videoItem = (Item)vidc.initDisplayMode(VideoControl.USE_GUI_PRIMITIVE, null);
                //vidc.setDisplaySize(240, 140);
                
            } else if (logo != null) {
                append(new ImageItem("", logo, ImageItem.LAYOUT_CENTER,""));
            }
            
           
            
            Control [] controls = player.getControls();

            for (int i = 0; i < controls.length; i++) {
                if (controls[i] instanceof GUIControl && controls[i] != vidc) {
                    append((Item) controls[i]);
                }


            }
            status = new StringItem("Status: ","");
    	    status.setLayout(Item.LAYOUT_NEWLINE_AFTER);
            append(status);

            
            
            
            time = new StringItem("","");
            time.setLayout(Item.LAYOUT_NEWLINE_AFTER);
            append(time);


            
            
            

            
            player.prefetch();
            
            
            
            if (videoItem == null)
                addCommand(pauseCommand);
            else {
                Spacer spacer = new Spacer(3, 10);
                spacer.setLayout(Item.LAYOUT_NEWLINE_BEFORE);
                append(spacer);
                append(videoItem);         ///append the videoItem, if don't append snapShot will get blank screen
    	    }
            
                 
                        
        } catch (Exception ex) {
            error(ex);
            ex.printStackTrace();
            close();
        }
        
        
    }//open   /**/
    


    /**
     * Start
     */
    //XXX
    public void startPlaying() {
    	P.rint("VideoPlayer:startPlaying()");
        if (player == null)
            return;
        try {
        	
            duration = player.getDuration();
            
        	//P.rint("VideoPlayer:start(): duration is "+duration);            
            
            

            
            player.start();  //without this will get black square
            

           
           
            
        } catch (Exception ex) {
            System.err.println(ex);
            error(ex);
            close();
        }
        
        
    }//startPlaying

    
    
//  general purpose error method, displays on screen as well to output
    public void error(Exception e) {
    	Alert alert = new Alert("Error");
    alert.setString(e.getMessage());

    parentDisplay.setCurrent(alert);
    e.printStackTrace();
    } 
    
    public void error(String s) {
    	Alert alert = new Alert("Error");
    alert.setString(s);
    alert.setTimeout(2000);

    parentDisplay.setCurrent(alert, this);

    } 
    
    
    public void close() {
        synchronized (this) {
            pause();  //stop()
            if (player != null) {
                player.close();
                player = null;
            }
        }
        VideoTest.getInstance().nullPlayer();
    }

    
    
    public void pause() {
        if ( player != null)  {
            try {
                player.stop();
            } catch (MediaException me) {
            	System.err.println(me);
            }
        }
    }

    
    /**
     * Sets the text to show playing-state and rate.
     */
    private synchronized void updateStatus() {
        if (player == null)
            return;
        status.setText(
            ((player.getState() == Player.STARTED) ? "Playing, ": "Paused, ") +
           "Rate: " + (currentRate/1000) + "%\n");
    }

    public void playerUpdate(Player plyr, String evt, Object evtData) {
        //try {
            if ( evt == END_OF_MEDIA ) {   //loop around playing forever
                try {
                    player.setMediaTime(0);
                    //player.start();
                } catch (MediaException me) {
                    System.err.println(me);
                }
            } else if (evt == STARTED || evt == STOPPED) {
                updateStatus();
            }
        
    }

    
    /**
     * Initiates the snapshot.
     */
    private void doSnapshot() {
    	
    	P.rint("VideoPlayer:doSnapshot()");
 
    	
        new SnapshotThread().start();
    }
    
    
    
    /**
     * Inner class Snapshot which is a thread.
     * Performs image processing and then appends to this form.
     */
    //XXX
    class SnapshotThread extends Thread {
		
		
		Image im_back_ground;
		Image im_frame_ground;
		int percentage;
		int int_total_value_back_ground = 0;
		int int_total_value_frame = 0;
		int int_total_value_fore_ground = 0;
		public void run() {
            try {
				if (back_ground == null){

					back_ground =  vidc.getSnapshot("encoding=jpeg");
				
					im_back_ground = Image.createImage(back_ground, 0, back_ground.length);
                    					
                    int imgCols_back_ground = im_back_ground.getWidth();
                    int imgRows_back_ground = im_back_ground.getHeight();
                    
                    int_array_back_ground = new int[imgCols_back_ground * imgRows_back_ground];    //Convert into One Dim Pixel values

                    im_back_ground.getRGB(int_array_back_ground, 0, imgCols_back_ground, 0, 0, imgCols_back_ground, imgRows_back_ground);	

					System.out.println("background template is taken");
								
				}

			
				System.out.println("taken");
                byte [] frame = vidc.getSnapshot("encoding=jpeg");   //use this for sun WTK emulator

				im_frame_ground = Image.createImage(frame, 0, frame.length);
                    
				int imgCols_frame_ground = im_frame_ground.getWidth();
                int imgRows_frame_ground = im_frame_ground.getHeight();

				
                    
                int_array_frame_ground = new int[imgCols_frame_ground * imgRows_frame_ground];  // Convert into One Dim Pixel values

				im_frame_ground.getRGB(int_array_frame_ground, 0, imgCols_frame_ground, 0, 0, imgCols_frame_ground, imgRows_frame_ground);

				int length = im_frame_ground.getHeight() * im_frame_ground.getWidth();

								


				try{

				
				for (int x=0; x < length ;x++ )
				{
				//	System.out.println("aken");	
				//	System.out.print("value1: "+int_array_frame_ground[x]);
				//	System.out.println("ken");	
				//	System.out.print("value2: "+int_array_back_ground[x]);
			//		System.out.println("Appplying  Background Subtraction Algorthim");

					//Math.abs(img1.getIntensity(i, j) - img2.getIntensity(i, j)
			//		int y;
			//		y= 25 - 12;
			//		System.out.println("y:"+ y);
					int_total_value_back_ground +=int_array_back_ground[x];
					int_total_value_frame += int_array_frame_ground[x];
//					int_array_fore_ground[x] = Math.abs(int_array_frame_ground[x]-int_array_back_ground[x]);
//					System.out.println("en");	
//					System.out.print("value: "+int_array_fore_ground[x]);
				//	percentage = int_array_fore_ground[x]/int_array_back_ground[x] * 100;
				}
				System.out.println("int_total_value_back_ground :" +int_total_value_back_ground);
				System.out.println("int_total_value_frame :" +int_total_value_frame);
				int_total_value_fore_ground = int_total_value_frame - int_total_value_back_ground ;
				System.out.println("int_total_value_fore_ground :" +int_total_value_fore_ground);
				percentage = (int_total_value_fore_ground / int_total_value_back_ground) * 100;
				
				}
				catch(Exception e){
					System.out.print("hai"+e);
				}
////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
				

			if (percentage > 3 ){
				
          			System.out.print("Above the Percentage");								
					try{
							smssend = new SMSSend();
						//	tosend(frame);
							String address = txtDest.getString();
							smssend.promptAndSend(address);
					//		System.out.println();
					}
                   catch(Exception e){
					   System.out.println("eee"+e);
				   }
				   back_ground  = frame;
					doSnapshot();
                    
			} else {
					back_ground  = frame;
					doSnapshot();
                    
				}
			} catch (MediaException me) {
                System.err.println(me);
                
                
            } catch (Exception e) {
                System.out.println("DEBUG: GOT EXCEPTION in VideoPlayer.SnapshotThread.run()!");
                e.printStackTrace();
                error(e);
            }
        }

    
    }//end class Snapshot which is a thread

    
    
    public synchronized void stopVideoPlayer() {
		// stop & deallocate
		player.deallocate();
	}

public void tosend(byte[] frame) {
	
try{
					System.out.println("hai:::::::::");
						String recipient = "mms://"+txtDest.getString()+":"+"500001";

						MessagePart imagePart = new MessagePart(frame,
												         	   0,
															   frame.length,
															   "",
															   "",
															   "",
															   null);

						
					
						sendMessage(recipient, imagePart);
						

					}catch (IOException ioe){
						//midlet.showError("Problem in sending MMS");
					}

}




public void sendMessage(String recipientAddress, MessagePart textPart ){
		try {
			String mmsConnection = "mms://:500001";
			try
			{
				messageConnection = (MessageConnection)Connector.open(mmsConnection);
				System.out.println("Bye");
			}
			catch (Exception e)
			{
				System.out.println("hai");
			}
			
				
			MultipartMessage mmsMessage = (MultipartMessage) messageConnection.newMessage(MessageConnection.MULTIPART_MESSAGE);
	
			mmsMessage.setAddress(recipientAddress);
			mmsMessage.addMessagePart(textPart);
			
			nextMessage= mmsMessage;
			
			
			new Thread (){
				public void run(){
					try {
						messageConnection.send(nextMessage);
					} catch (IOException ioe){
					//	showError("Not able to open connection..");
					}
				}
			}.start();
			
		} catch (SizeExceededException see){
		//	showError("Message size is too large..");
		} catch (IllegalArgumentException iae){
		//	showError("Destination number not valid..");
		} catch (ArrayIndexOutOfBoundsException aiobe){
		//	showError("Array index out of bound..");
		}
	}
    
	/**
	 * Deallocate the player and the display thread.
	 * Some VM's may stop players and threads
	 * on their own, but for consistent user
	 * experience, it's a good idea to explicitly
	 * stop and start resources such as player
	 * and threads.
	 */
	public synchronized void pauseApp() {
		suspended = true;
		if (player != null && player.getState() >= Player.STARTED) {
			// player was playing, so stop it and release resources.
			if (!isCapturePlayer) {
				restartMediaTime = player.getMediaTime();
			}
			player.deallocate();
			// make sure to restart upon resume
			restartOnResume = true;
		} else {
			restartOnResume = false;
		}
	}

	/**
	 * If the player was playing when the MIDlet was paused,
	 * then the player will be restarted here.
	 */
	public synchronized void startApp() {
		suspended = false;
		if (player != null && restartOnResume) {
			try {
				player.prefetch();
				if (!isCapturePlayer) {
					try {
						player.setMediaTime(restartMediaTime);
					} catch (MediaException me) {
						System.err.println(me);
					}
				}
				player.start();
			} catch (MediaException me) {
				System.err.println(me);
			}
		}
		restartOnResume = false;
	}

}//class




