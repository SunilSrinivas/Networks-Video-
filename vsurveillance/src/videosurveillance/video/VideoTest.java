package videosurveillance.video;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import java.util.Vector;

public class VideoTest extends MIDlet implements CommandListener, Runnable {


    private static VideoPlayer videoPlayer = null;
    private static Vector  videoClips;
	
	
    private Command exitCommand = new Command("Exit", Command.EXIT, 1);
    private Command playCommand = new Command("Play", Command.ITEM, 1);
    //private Command helpCommand = new Command("Help", Command.HELP, 1);

    //private Alert helpScreen  = null;
    private Display display;
    private static List theList;
    private static VideoTest instance = null;

    private static Vector urls;

    static public VideoTest getInstance() {
        return instance;
    }

    static public List getList() {
        return theList;
    }

    public VideoTest() {
		
		
        instance = this;
        display  = Display.getDisplay(this);
        theList  = new List("Video Player", Choice.IMPLICIT);
        fillList();


	    System.out.println("Video MIDlet has been initiated.");


        theList.addCommand(playCommand);
        theList.addCommand(exitCommand);
        
		theList.setCommandListener(this);
		
        display.setCurrent(theList);
    }

    private void fillList() {
        videoClips = new Vector();
        
        videoClips.addElement("Video Capture -- Form");
        
        
        urls = new Vector();
        urls.addElement("capture://video");
        
        theList.append("Video Capture Form", null);
		
        
        /**
         * Going through the property list and testing if each is supported.
         *
        for (int n = 1; n < 100; n++) {
            String nthURL = "VideoTest-URL"+ n;
            String url = getAppProperty(nthURL);
            
            
            if (url == null || url.length() == 0) {
                break;
            }
	    if (!SimplePlayer.isSupported(url)) 		continue;
	    
            String nthTitle = "VideoTest-" + n;
            
            String title = getAppProperty(nthTitle);
            
            
            if (title == null || title.length() == 0) {
                title = url;
            }
            videoClips.addElement(title);
            urls.addElement(url);
            theList.append(title, null);
        }*/
        
    }//fillList


    /**
     * Called when this MIDlet is started for the first time,
     * or when it returns from paused mode.
     * If there is currently a Form or Canvas displaying
     * video, call its startApp() method.
     */
    public void startApp() {
        //try {
            if (videoPlayer != null)
                videoPlayer.startApp();
        //} catch (Exception e) {
        //    System.out.println("DEBUG: GOT EXCEPTION in VideoTest.startApp()!");
        //    e.printStackTrace();
        //}
    }


    /**
     * Called when this MIDlet is paused.
     * If there is currently a Form or Canvas displaying
     * video, call its startApp() method.
     */
    public void pauseApp() {
        //try {
            if (videoPlayer != null)
                videoPlayer.pauseApp();
        //} catch (Exception e) {
        //    System.out.println("DEBUG: GOT EXCEPTION in VideoTest.pauseApp()!");
        //    e.printStackTrace();
        //}
    }


    /**
     * Destroy must cleanup everything not handled
     * by the garbage collector.
     */
    public synchronized void destroyApp(boolean unconditional) {
        //try {
            if (videoPlayer != null)
                videoPlayer.close();

            nullPlayer();
        //} catch (Exception e) {
        //    System.out.println("DEBUG: GOT EXCEPTION in VideoTest.destroyApp("+unconditional+")!");
        //    e.printStackTrace();
        //}
    }

    public synchronized void nullPlayer() {
        videoPlayer = null;

    }



    public void run() {
    	P.rint("VideoTest:run()");
    	
        //try {

                videoPlayer = new VideoPlayer(display);
                videoPlayer.open("capture://video");

                
                //XXX
                //P.rint("VideoTest:run():Just finish calling videoPlayer.open()");
                
                
                if (videoPlayer != null) {
                    display.setCurrent(videoPlayer);
                    videoPlayer.startPlaying();   
                }

        //} catch (Exception e) {
        //    System.out.println("DEBUG: GOT EXCEPTION in VideoTest.run()!");
        //    e.printStackTrace();
        //}
    }

    /*
     * Respond to commands, including exit
     * On the exit command, cleanup and notify that the MIDlet has
     * been destroyed.
     */
    public void commandAction(Command c, Displayable s) {
        //try {
            if (c == exitCommand) {
                synchronized (this) {
                    if (videoPlayer != null) {
                         new ExitThread().start();
                    } else {
                        destroyApp(false);
                        notifyDestroyed();
                    }
                }
            } else if ((s == theList && c == List.SELECT_COMMAND) || c == playCommand) {
                synchronized (this) {
                    if (videoPlayer != null) {
                    	//return if something is active
                    	//this section is for starting
                        return;
                    }
                    //int i = theList.getSelectedIndex();

                    // need to start the players in a separate thread to
                    // not block the command listener thread during
                    // Player.realize: if it requires a security
                    // dialog (like "is it OK to use airtime?"),
                    // it would block the VM
                    (new Thread(this)).start();
                }
            }
        //} catch (Exception e) {
        //    System.out.println("DEBUG: GOT EXCEPTION in VideoTest.commandAction("+c.toString()+","+s.toString()+")!");
        //    e.printStackTrace();
        //}
    }
    
    class ExitThread extends Thread {
        public void run() {
            //try {
                // this is stop()+deallocate(), but not close(), 
                //which is done in destroyApp() ...
                if (videoPlayer != null) {
                    videoPlayer.stopVideoPlayer();
                    //videoPlayer = null;
                }
                destroyApp(false);
                notifyDestroyed();
            //} catch (Exception e) {
            //    System.out.println("DEBUG: GOT EXCEPTION in VideoTest.ExitThread.run()!");
            //    e.printStackTrace();
            //}
        }
    }
}

