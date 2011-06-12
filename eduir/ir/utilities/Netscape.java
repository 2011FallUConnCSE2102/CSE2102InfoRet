package eduir.ir.utilities;
import java.io.*;
import java.lang.*;

/** 
 * Utilities for displaying a URL or local file in the current Netscape
 * window using "netscape -remote".  User must have netscape running.
 *
 * @author Ray Mooney
*/

public class Netscape
{
    /** Make netscape display a given URL */
    public static void display(String url) {
	String[] cmd = new String[3];
	cmd[0] = "netscape";
	cmd[1] = "-remote";
	cmd[2] = "openURL(" + url + ")";
	try {
	    Runtime.getRuntime().exec(cmd);
	}
	catch (IOException e) {
	    System.out.println("Unable to run `netscape -remote' process.");
		}
    }

    /** Make netscape display a given file */
    public static void display(File file)  {
	display("file:" + file.getAbsolutePath());
    }

    /** Test interface */
    public static void main(String[] args) throws IOException {
	String name = args[0];
	File file = new File(name);
	System.out.println("\nDisplaying: " + file.getAbsolutePath());
	display(file);
    }

}
