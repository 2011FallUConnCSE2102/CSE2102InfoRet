   package eduir.ir.vsr;

   import java.io.*;
   import java.util.*;
   import eduir.ir.utilities.*;
   import java.lang.reflect.Method;



/**
 * Docment is an abstract class that provides for tokenization
 * of a document with stop-word removal and an iterator-like interface
 * similar to  StringTokenizer.
 * Also provides a method for converting a document into a 
 * vector-space bag-of-words in the form of a HashMap of 
 * tokens and their occurrence counts.
 *
 * @author Ray Mooney
 */


           
   public abstract class Document {
           
   
   
      protected static String language = "";
   
    /** The file where a list of stopwords, 1 per line, are stored */

      protected static String stopWordsFile = "";
   
    /** The number of stopwords in this file */
      protected static int numStopWords = 0;  
   
    /** The hashtable where stopwords are indexed */
      protected static HashSet stopWords = null;
       
    /** The next token in the document */
      protected String nextToken = null;
   
    /** The number of tokens currently read from document */
      protected int numTokens = 0;
   
    /** Whether to stem tokens with the Porter stemmer */
      protected boolean stem = false;
   
    /** Configuration Class */
      protected static Configuration configuration = new Configuration();
   
    /** Creates a new Document making sure that the stopwords
     * are loaded, indexed, and ready for use.  Subclasses
     * that create concrete instances MUST call prepareNextToken
     * before finishing to ensure that the first token is precomputed
     * and available.
     */
   
   /*Count the numbers of words that appear in the StopWords.txt file
   */   
   
              
      public static void countWords() {
              
      
         String line;
      
         try {
         // Open stopword file for reading
            BufferedReader in = new BufferedReader(new FileReader(stopWordsFile));
         // Read in stopwords, one per line, until file is empty
            while ((line = in.readLine()) != null) {
               numStopWords++;
            }
            in.close();
         }
         
         
                    
            catch (IOException e) {
                    
            
               System.out.println("\nCould not load stopwords file: " + stopWordsFile);
               System.exit(1);
            }
      
      }
   
   
   
   
              
      public Document(boolean stem, String language) {
              
      
         this.stem = stem;
         this.language = language;
      
         if (stopWords == null) {
         
            if (configuration.searchStopList(language)) {
               stopWordsFile = configuration.getDirStopLists()+"/"+language+".stop";
               countWords();
               loadStopWords();
            }
         }
      }
   
    /** Returns true if the document contains more tokens */
   
              
      public boolean hasMoreTokens() {
              
      
         if (nextToken == null)
            return false;
         else
            return true;
      }
   
    /** Returns the next token in the document or null if there are none */
   
              
      public String nextToken() {
              
      
         String token = nextToken;
         if (token == null) 
            return null;
         prepareNextToken();
         numTokens++;
         return token;
      }
   
   
   /**
    * Select a stemmer depend on the language.
    */
   
   
              
      public static String select_stemmer(String tokenInput, String language) {
              
      
      
      
         Class stemClass = null;
         SnowballProgram stemmer = null;
         String DirStemmers;
         try {
         
            DirStemmers = configuration.getDirStemmers();
         
            stemClass = Class.forName(DirStemmers + language + "Stemmer");
         
            stemmer = (SnowballProgram) stemClass.newInstance();
         
            Method stemMethod = stemClass.getMethod("stem", new Class[0]);
         
            Object [] emptyArgs = new Object[0];
         
            stemmer.setCurrent(tokenInput);
            stemMethod.invoke(stemmer, emptyArgs);
         
         }
         
         
                    
            catch ( Throwable e) {
                    
            
               System.out.println("Throwable Error");
               System.out.println(e);
               System.exit(1);
            }
      
         return stemmer.getCurrent();
      }
   
   
    /** The nextToken slot is always precomputed and stored by this method.
     * Performs stop-word removal of candidate tokens. */
   
              
      protected void prepareNextToken () {
              
      
      // Loop until a non-stopword token is found
         do {
            nextToken = getNextCandidateToken();
            if (nextToken == null) 
               return; // reached end of document
         // Normalize token string case to lower case.
            nextToken = nextToken.toLowerCase();
         // Do not include a token found in the stopword list as
         // indexed in the stopwords hashtable.
            if (configuration.searchStopList(language) && (stopWords.contains(nextToken)))
               nextToken = null;
            else if ((stem) && configuration.searchStemmer(language)){
               nextToken = select_stemmer(nextToken, language); 
               if (configuration.searchStopList(language) && stopWords.contains(nextToken))
                  nextToken = null;
            
            }
         }
         while (nextToken == null);
      }
   
    /** Return the next possible token in the document. Each subclass must implement
     * this method to produce candidate tokens for subsequent stop-word filtering.
     */
   
              
      protected abstract String getNextCandidateToken();
              
   
   
    /** Returns the total number of tokens in the document or -1 if
     *  there are still more tokens to be read and the total count is not yet available.
     */
   
              
      public int numberOfTokens() {
              
      
         if (nextToken == null)
            return numTokens;
         else
            return -1;
      }
   
    /** Load the stopwords from file to the hashtable where they are indexed. */
   
              
      protected static void loadStopWords() {
              
      
      // Initialize hashtable to proper size given known number of
      // stopwords in the file and a default 75% load factor with
      // 10 extra slots for spare room.
         int HashMapSize = (int)(numStopWords/0.75 + 10);
         stopWords = new HashSet(HashMapSize);
         String line;
         try {
         // Open stopword file for reading
            BufferedReader in = new BufferedReader(new FileReader(stopWordsFile));
         // Read in stopwords, one per line, until file is empty
            while ((line = in.readLine()) != null) {
            // Index word into the hashtable with 
            // the default empty string as a "dummy" value.
               stopWords.add(line.toLowerCase());
            }
            in.close();
         }
         
                    
            catch (IOException e) {
                    
            
               System.out.println("\nCould not load stopwords file: " + stopWordsFile);
               System.exit(1);
            }
      }
   
    /** Returns a hashmap version of the term-vector (bag of words) for this
     * document, where each token is a key whose value is the number of times 
     * it occurs in the document as stored in a Weight.
     * @see Weight
     */
   
              
      public HashMapVector hashMapVector () {
              
      
         if (numTokens != 0)
            return null;
         HashMapVector vector = new HashMapVector();
      // Process each token in the document and add it to the vector
         while (hasMoreTokens()) {
            String token = nextToken();
            vector.increment(token);
         }
         return vector;
      }
   
    /** Compute and print out (one line per term) the term-vector (bag of words) 
     * for this document 
     */
   
              
      public void printVector() {
              
      
         hashMapVector().print();
      }
   
   
   }
