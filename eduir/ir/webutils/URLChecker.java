package eduir.ir.webutils;

import java.net.*;

/**
 * URLChecker.java
 * trys to clean up some URLs that do not conform to the standard and cause confusion.  Valid URL's are returned 
 * unchanged.  The idea behind this class is to fix some common problems (like leaving spaces in URLs) with 
 * simple heuristics.
 *
 * June 1, 2001
 * Ted Wild
 */

public class URLChecker {
  
    public static URL getURL(String urlString) throws MalformedURLException {

	String checkedUrl = urlString.replace(' ', '+');

	return new URL(checkedUrl);
    }
	    
    private URLChecker() {
    }
}

















