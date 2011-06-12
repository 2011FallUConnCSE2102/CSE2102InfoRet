package eduir.ir.webutils;

import java.util.*;
import com.stevesoft.pat.*;
import java.io.*;

/**
 * RobotExclusionSet provides support for the Robots Exclusion
 * Protocol.  This class provides the ability to parse a robots.txt
 * file and to check files to make sure that access to them has not
 * been disallowed by the robots.txt file.  This class can also be
 * used to exclude files linked to on a page that specifies NOFOLLOW
 * in its Robots META tag.
 *
 * @author Ted Wild & Ray Mooney */

public class RobotExclusionSet extends AbstractSet implements Set {

    private LinkedList set;

    /**
     * Constructs an empty set.
     */
    public RobotExclusionSet() {
	super();
	set = new LinkedList();
    }

    /**
     * Constructs a set containing the paths in the robots.txt file
     * for this site. The  robots.txt
     * file should conform to the Robots Exclusion Protocol
     * specification, available at
     * http://www.robotstxt.org/wc/norobots.html.
     *
     * @param site The name of the site
     */
    public RobotExclusionSet(String site){
	super();
	set = new LinkedList();
	String robotText =  WebPage.getWebPage("http://" +  site +  "/robots.txt");
	if (robotText != null) 
	    this.parseRobotsFileString(robotText);
    }
    
    public int size() {
	return set.size();
    }

    public boolean add(Object o) {
	if (set.contains(o))
	    return false;
	
	set.add(o);
	return true;
    }
    
    public Iterator iterator() {
	return set.iterator();
    }

    /**
     * Checks to see if a path is prohibited by this set.  A path is
     * prohibited if it starts with an entry in this set.
     * 
     * @param o A <code>String</code> object representing the path.
     *
     * @return <code>true</code> iff. <code>o</code> is a
     * <code>String</code> object, <code>o</code> is not
     * <code>null</code>, and for each element e in this set
     * <code>!o.startsWith(e)</code>.  */
    public boolean contains(Object o) {

	if (set == null || !(o instanceof String))
	    return false;

	String path = (String) o;

	if (path.equals(""))
	    path = "/";

	Iterator i = set.iterator();
	boolean disallowed = false;

	while (!disallowed && i.hasNext()) {
	    String disallowedPath = (String) i.next();

	    if (path.startsWith(disallowedPath)) 
		disallowed = true;
	}

	return disallowed;
    }

    /**
     * This method based on code in the WWW::RobotRules module in the
     * libwww-perl5 library, available from www.cpan.org.
     *
     * @param robotsFile The robots.txt file represented as a string.  
     */
    private void parseRobotsFileString(String robotsFile) {
	Regex userAgentLine = Regex.perlCode("m/User-Agent:\\s*(.*)/i");
	String remainingText = robotsFile;
    
	while (userAgentLine.search(remainingText)) {
	    remainingText = userAgentLine.right();
	    
	    if (userAgentLine.stringMatched(1).indexOf('*') != -1) {
		    
		// this User-Agent line applies to this robot
		Regex disallowLine = Regex.perlCode("m/Disallow:\\s*(.*)/i");
		Regex blankLine = Regex.perlCode("m/\n\\s*\n/");
		String linesLeft = new String(remainingText);

		if (disallowLine.search(linesLeft)) {

		    if (!blankLine.search(linesLeft) ||
			blankLine.matchedFrom() > disallowLine.matchedFrom()) {
			
			// there is at least one disallow line that applies to this robot
			while (disallowLine.search(linesLeft)) {
			    String disallowed = disallowLine.stringMatched(1);
			    
			    // trim whitespace
			    disallowed = Regex.perlCode("s/\\s+$//").replaceFirst(disallowed);
			    
			    if (disallowed.length() > 0) {
 
				if (disallowed.endsWith("/"))
				    disallowed = disallowed.substring(0, disallowed.lastIndexOf('/'));

				this.add(disallowed);
			    }
			    linesLeft = disallowLine.right();
			}
		    }
		}
	    }
	}
    }

    /** The following methods are for test/diagnostic purposes */

    /**
     * Outputs list of disallowed rules.  Intended only for testing.
     *
     * @param w The writer the list should be output to. */
    void printDisallowed(Writer w) {
	Iterator i = this.iterator();
	
	try {
	    
	    while (i.hasNext()) 
		w.write((String) i.next() + '\n');
	    
	    w.flush();
	}
	catch (IOException e) {
	    System.err.println("RobotExclusionSet.printDisallowed(): error writing to writer.  " + e);
	    System.exit(1);
	}
    }
}
	
