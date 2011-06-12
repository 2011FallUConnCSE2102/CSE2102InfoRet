   package eduir.ir.vsr;

   import java.io.*;
   import java.util.*;

/** An HTML file document where HTML commands are removed
 * from the token stream.  To include HTML tokens, just
 * create a TextFileDocument from the HTML file.
 *
 * @author Ray Mooney
 */

           
   public class HTMLFileDocument extends FileDocument {
           
   
    /** StringTokenizer delim for tokenizing only alphabetic strings. */
      public static final String tokenizerDelim = " \t\n\r\f\'\"\\1234567890!@#$%^&*()_+-={}|[]:;<,>.?/`~";
   
    /** The tokenizer for lines read from this document. */
      protected StringTokenizer tokenizer = null;
   
    /** Create a new HTML document for the given file. */
              
      public HTMLFileDocument(File file, boolean stem, String language) {
              
         super(file, stem, language); // Create a FileDocument
      
         try {
         // Create a StringTokenizer for the first line in the file.
         // This tokenizer returns delimiters as separate tokens in order to
         // detect HTML commands
            String line = reader.readLine();
            if (line != null) {
               this.tokenizer = new StringTokenizer(line, tokenizerDelim, true);
            }
            prepareNextToken(); // Prepare the first token in the file
         }
                    
            catch (IOException e) {
                    
               System.out.println("\nCould not read TextFileDocument: " + file);
               System.exit(1);
            }
      }
   
    /** Create a new text document for the given file name. */
              
      public HTMLFileDocument(String fileName, boolean stem, String language) {
              
         this(new File(fileName), stem, language);
         this.language = language;
      }
   
    /** Return the next non-HTML-command token in the document, or null if none left. */
              
      protected String getNextCandidateToken() {
              
         if (tokenizer == null) 
            return null;
         String candidateToken = null;
      // The following flag is set to true when inside an HTML command, i.e.
      // between a "<" and a ">"
         boolean inTag = false;  
         try {
         // Loop until a non-HTML-command token is found
            while (candidateToken == null) {
            // Loop until you find a line in the file with a token
               while (!tokenizer.hasMoreTokens()) {
               // Read a line from the file
                  String line = reader.readLine();
                  if (line == null) {
                  // End of file, no more tokens, return null
                     reader.close();
                     return null;
                  }
                  else 
                  // Create a tokenizer for this line in the file.
                  // This tokenizer returns delimiters as separate tokens in order to
                  // detect HTML commands
                     tokenizer = new StringTokenizer(line, tokenizerDelim, true);
               }
            // Get the next token in the current line
               candidateToken = tokenizer.nextToken();
               if (inTag) {
                  if (candidateToken.equals(">"))
                  // Exiting the HTML tag
                     inTag = false;
               // Don't include tokens within an HTML tag
                  candidateToken = null;
               }
               else if (candidateToken.equals("<")) {
               // Entering an HTML tag, discard such tokens
                  inTag = true;
                  candidateToken = null;
               }
               else if (tokenizerDelim.indexOf(candidateToken) >= 0) {
               // If token is another delimiter character (as found with a string search), 
               // discard it
                  candidateToken = null;
               }
            }
         }
                    
            catch (IOException e) {
                    
               System.out.println("\nCould not read from HTMLFileDocument: " + file);
               System.exit(1);
            }
         return candidateToken;
      }
   
    /** For testing, print the bag-of-words vector for a given HTML file */
              
      public static void main(String[] args) throws IOException {
              
      
         String fileName = args[0]; 
         Document doc = new HTMLFileDocument(fileName, true, args[1]);
         doc.printVector();
         System.out.println("\nNumber of Tokens: " + doc.numberOfTokens());
      }
   
   }

