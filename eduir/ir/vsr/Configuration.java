   package eduir.ir.vsr;

   import java.io.*;
   import java.util.*;

   class Configuration
   {
   
      private static String configurationFile = "C:/Documents and Settings/sat/Escritorio/ir/vsr/configuration.properties";
      private static String DirStopLists;
      private static String DirStemmers;
      private static Vector LanguageStopList = new Vector();
      private static Vector LanguageStemmer = new Vector();
   
   
      public Configuration () {
      
         readConfigurationFile();
      
      }
   
      static void readConfigurationFile () {
      
      
         try {
         
            BufferedReader f = new BufferedReader(new FileReader(configurationFile));
            String s;
            int pos;
            boolean mark = false;
         
            while (true) {
               s = f.readLine();
            
               if (s == null)
                  break;
            		
               if (s.startsWith("#")){
               /*do nothing*/
               }
               
               else if (s.compareTo("")==0){
               /*do nothing*/
               }
               
               else if (s.startsWith("@LanguagesStemmer")) {
                  mark = true;
               }
               
               else if (s.startsWith("@LanguagesStoplist")) {
                  mark = false;
               } 
               
               else if (s.startsWith("@DirStopLists")) {
                  //pos = s.indexOf(':');
                  DirStopLists = f.readLine();//s.substring(pos+1, s.length());
               
               } 
               else if (s.startsWith("@DirStemmers")) {
                  //pos = s.indexOf(':');
                  DirStemmers = f.readLine(); //s.substring(pos+1, s.length());
               
               } 
               else if (mark == true) {
                  LanguageStemmer.addElement(s);
               }
               else if (mark == false) {
                  LanguageStopList.addElement(s);
               }
            
            }
         
            f.close();
         
         }
            catch (IOException ioe)
            
            {
               System.out.println(ioe);
            }
      
      }
   
      public boolean searchStemmer (String Stemmer) {
      
         return (LanguageStemmer.contains(Stemmer));
      }
   
      public boolean searchStopList (String StopList) {
      
         return (LanguageStopList.contains(StopList));
      }
   
      public String getDirStemmers () {
      
         return (DirStemmers);
      }
   
      public String getDirStopLists () {
      
         return (DirStopLists);
      }
   
      public static void main(String[] args) {
         readConfigurationFile();
         System.out.println(DirStopLists);
         System.out.println(DirStemmers);
         System.out.println(LanguageStopList);
         System.out.println(LanguageStemmer);
      
      }
   
   }