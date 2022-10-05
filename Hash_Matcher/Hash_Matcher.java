import java.io.*;
import java.util.*;
import java.security.*;
public class Hash_Matcher {
    public static void main(String []args){
        
        Dictionary dic = new Dictionary();
        String first = "";
        String second = "";
        int nouns = 1;
        int nounUpper = 188;
        int verbs = 190;
        int verbUpper = 202; //indexes for the upper and lower bounds of each of the typed of words
        int adverbs = 204;
        int adverbUpper = 215;
        int names = 217;
        int namesUpper = 2129;

        String hashVal[] = new String [1000000];
        int count = 0;
        int maxScore = 0, posX = 0, posY = 0; //max score achieved so far, as well as the positions of the first and second sentences in the best comparison
 
        /*counter algorithm, used to
          iterate through all possible 
          sentences
        */
        for(int i = 0; i < hashVal.length ; i++){
            String name = dic.getWord(names);
            String sentence = name.charAt(name.length() - 1) == 's' ? name + "' " : name + "'s " + dic.getWord(nouns) + " " + dic.getWord(verbs) + " " + dic.getWord(adverbs) + ".";
            hashVal[i] = sha256(sentence);

            if(adverbs == adverbUpper){
                adverbs = 203;
                verbs++;
            }
            if(verbs == verbUpper){
                verbs = 190;
                nouns++;
            }
            if(nouns == nounUpper){
                nouns = 1;
                names++;
            }
            adverbs++;
        }
        //comparison algorithm
        for(int x = 0; x < hashVal.length - 1; x++){
            for(int y = x + 1; y < hashVal.length; y++){
                int score = calcScore(hashVal[x], hashVal[y]);
                if(score > maxScore && !(hashVal[x].equals(hashVal[y]))){
                    maxScore = score;
                    posX = x;
                    posY = y;
                    System.out.println(hashVal[x] + "\n" + hashVal[y] + " " + posX + " " + posY + "\n" + " " + maxScore);
                    System.out.println(getSentence(posX) + "\n" + getSentence(posY) + "\n");

                }else if(score == maxScore){
                    System.out.println("same score achieved. " + x + " " + y); //used to check for duplicate scores
                }
            }
            if(x % 1000 == 0) System.out.println("count = " + x); //used to keep track of where the algorithm is in the array
        }
        System.out.println("max positions were: " + posX + " " + posY + "\n" + "with a max score of: " + maxScore);
        System.out.println("\n" + "the sentence pair was: " + "\n" + getSentence(posX) + "\n" + getSentence(posY));
        System.out.println(hashVal[posX] + "\n" + hashVal[posY]);

    }
    //get sentence based on posX and posY
    public static String getSentence(int in){
        Dictionary dic = new Dictionary();
        int nouns = 1;
        int nounUpper = 188;
        int verbs = 190;
        int verbUpper = 202;
        int adverbs = 204;
        int adverbUpper = 215;
        int names = 217;
        int namesUpper = 2129;
        String sentence = "";

        for(int i = 0; i <= in; i++){
            sentence = dic.getWord(names) + "'s " + dic.getWord(nouns) + " " + dic.getWord(verbs) + " " + dic.getWord(adverbs) + ".";
            
            if(adverbs == adverbUpper){
                adverbs = 203;
                verbs++;
            }
            if(verbs == verbUpper){
                verbs = 190;
                nouns++;
            }
            if(nouns == nounUpper){
                nouns = 1;
                names++;
            }
            adverbs++;
        }
        return sentence;
    }
    public static String sha256(String input){
        try{
            MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
            byte[] salt = "CS210+".getBytes("UTF-8");
            mDigest.update(salt);
            byte[] data = mDigest.digest(input.getBytes("UTF-8"));
            StringBuffer sb = new StringBuffer();
            for (int i=0;i<data.length;i++){
                sb.append(Integer.toString((data[i]&0xff)+0x100,16).substring(1));
            }
            return sb.toString();
        }catch(Exception e){
            return(e.toString());
        }
    }
    //counts identical characters in two strings and returns an int
    public static int calcScore(String in1, String in2){
        int score = 0;
        for(int i = 0; i < in1.length(); i++){
            if(in1.charAt(i) == in2.charAt(i))
              score++;
        }
        return score;
    }
    //dictionary class and setup
    public static class Dictionary{
     
        private String input[]; 
    
        public Dictionary(){
            input = load("/Users/stephengallagher/Downloads/Word_Reference.txt");  
        }
        
        public int getSize(){
            return input.length;
        }
        
        public String getWord(int n){
            return input[n];
        }
        
        private String[] load(String file) {
            File aFile = new File(file);     
            StringBuffer contents = new StringBuffer();
            BufferedReader input = null;
            try {
                input = new BufferedReader( new FileReader(aFile) );
                String line = null; 
                int i = 0;
                while (( line = input.readLine()) != null){
                    contents.append(line);
                    i++;
                    contents.append(System.getProperty("line.separator"));
                }
            }catch (FileNotFoundException ex){
                System.out.println("Can't find the file - are you sure the file is in this location: "+file);
                ex.printStackTrace();
            }catch (IOException ex){
                System.out.println("Input output exception while processing file");
                ex.printStackTrace();
            }finally{
                try {
                    if (input!= null) {
                        input.close();
                    }
                }catch (IOException ex){
                    System.out.println("Input output exception while processing file");
                    ex.printStackTrace();
                }
            }
            String[] array = contents.toString().split("\n");
            for(String s: array){
                s.trim();
            }
            return array;
        }
    }
}
