   package eduir.ir.vsr;

   import java.io.*;
   import java.util.*;

/** A simple document represented by a String
 *
 * @author Ray Mooney
 */
    public class TextStringDocument extends Document {
   
    /** StringTokenizer delim for tokenizing only alphabetic strings. */
      public static final String tokenizerDelim = " \t\n\r\f\'\"\\1234567890!@#$%^&*()_+-={}|[]:;<,>.?/`~";
   
    /** The tokenizer for this document. */
      protected StringTokenizer tokenizer = null;
   
    /** Create a simple Document for this string */
       public TextStringDocument(String string, boolean stem, String language) {
         super(stem, language);
         this.tokenizer = new StringTokenizer(string, tokenizerDelim);
         prepareNextToken();
      
      
      
      }
   
    /** Get the next token from this string */
       protected String getNextCandidateToken() {
         if (tokenizer == null || !tokenizer.hasMoreTokens()) 
            return null;
         return tokenizer.nextToken();
      }
   
    /** For testing, print the bag-of-words vector for the given string */
       public static void main(String[] args) throws IOException {
         String input = args[0];
         String language = args[1];
      
         Document doc = new TextStringDocument(input, true, language);
         doc.printVector();
         System.out.println("\nNumber of Tokens: " + doc.numberOfTokens());
      }
   
   }
