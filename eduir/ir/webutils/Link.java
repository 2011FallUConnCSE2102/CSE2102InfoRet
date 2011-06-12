package eduir.ir.webutils;

import java.net.*;

/**
 * Link is a class that contains a URL.  Subclasses of link may keep
 * additional information (such as anchor text & other attributes)
 *
 * @author Ted Wild and Ray Mooney */

public class Link {

    private URL url = null;

    /** 
     * May be subclassed.  This constructor should not be invoked by
     * clients of <code>Link</code>.  */
    protected Link() {
	url = null;
    }

    /** Constructs a link with specified URL.
     *
     * @param url The URL for this link.  */
    public Link(URL url) {
	this.url = cleanURL(url);
    }

    /** Construct a link with specified URL string */
    public Link(String urlName) {
	try {
	    this.url = cleanURL(new URL(urlName));
	}
	catch (MalformedURLException e) {
	    System.err.println("Bad URL: " +  urlName);
	}
    }
    
    /**
     * Returns the URL of this link.  
     *
     * @return The URL of this link. */
    public final URL getURL() {
	return url;
    }

    public String toString() {
	return url.toString();
    }

    public boolean equals(Object o) {
	return (o instanceof Link) && ((Link) o).url.equals(this.url);
    }

    public int hashCode() {
	return url.hashCode();
    }

    /** Standardize URL */
    public static URL cleanURL(URL url) {
	return removeEndSlash(removeRef(url));
    }

    /** Removes slash at end of URL to normalize */
    public static URL removeEndSlash(URL url) {
	String name = url.toString();
	if (name.charAt(name.length() - 1) == '/')
	    try {
		return new URL(name.substring(0, name.length() - 1));
	    }
	    catch (MalformedURLException e) {
		System.err.println("Bad URL: " +  name);
	    }
	return url;
    }
    
    /** Remove the internal "ref" pointer in a URL if there is
     * one. This not part of the URL to a page itself */
    public static URL removeRef(URL url) {
	String ref = url.getRef();
	if (ref == null || ref.equals(""))
	    return url;
	String urlName = url.toString();
	int pos = urlName.lastIndexOf("#");
	if (pos >= 0) 
	    try {
		return (new URL(urlName.substring(0,pos)));
	    }
	    catch (MalformedURLException e) {
		System.err.println("Bad Ref in URL: " +  urlName);
	    }
	return url;
    }

    public static void main (String[] args) {
	System.out.println(new Link(args[0]));
    }
		
}// Link

