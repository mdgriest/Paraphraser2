import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
/*
Mitchell Griest
 Paraphraser 2 (Non GUI)
 Summer 2016
 
 mdgriest@crimson.ua.edu
 */

ArrayList<Character> specialCharacters = new ArrayList<Character>();

PrintWriter output;

void setup() {
  selectInput("Select a file to paraphrase:", "fileSelected");

  specialCharacters.add('.');
  specialCharacters.add('?');
  specialCharacters.add('!');
  specialCharacters.add(',');
  specialCharacters.add(';');
  specialCharacters.add(',');
}

//Called once the user chooses a file to paraphrase
void fileSelected(File file) {
  if (file == null) {
    println("Window was closed or the user hit cancel.");
  } else if (!file.getName().endsWith(".txt")) {
    println("Whoops! Please select a .txt file");
  } else {
    try {
      //Create the output file
      String fileName = file.getName();
      String newFileName = fileName.substring(0, fileName.length()-4) + 
        " Paraphrased.txt";
      output = createWriter(newFileName);

      //Add the contents of the original file
      output.println("BEFORE:\n");
      String[] originalContents = loadStrings(fileName);
      for (String line : originalContents) {
        output.println(line);
      }

      output.println("\n---------------------------------------------------------\n");
      output.println("AFTER:\n");

      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
      String line = null;
      //For each line in the original file
      while ( (line = br.readLine())!= null ) {
        //Split on whitespace (any amount)
        String[] contents = line.split("\\s+");
        //For each word in that line
        for (String word : contents) {
          //If the word should be paraphrased
          if (word.startsWith("/")) {
            //If the word ends in a special character
            if (specialCharacters.contains(word.charAt(word.length() - 1))) {
              //Strip the special character before paraphrasing
              String newWord = word.substring(0, word.length() - 1);
              //Add the paraphrased word
              output.append(paraphrase(newWord.substring(1)));
              //And the special character that was on the end of it
              output.append(word.charAt(word.length() - 1));
            }
            //Otherwise, just paraphrase the word and add it to the output
            else {
              output.print(paraphrase(word));
            }
          } else {
            output.print(word);
          }
          output.print(" ");
        }
        output.println();
      }
      br.close();
      output.flush();
      output.close();
      exit();
    }
    catch(IOException e) {
      println(e);
    }
  }
}

String paraphrase(String word) {
  try {
    //Create the appropriate URL for the word to be paraphrased
    String url = "http://www.thesaurus.com/browse/" + word;
    //Grab the HTML document from thesaurs.com
    Document doc = Jsoup.connect(url).get();
    //Go find the relevancy list
    Elements rList = doc.select(".relevancy-list");
    //Get an ArrayList of all the synonyms
    Elements synonyms = rList.select(".text");
    //Get the most relevant synonym from that list
    if (synonyms.size() != 0) {
      return synonyms.get(0).text();
    }
    //If there are no synonyms, return the original word
    return word;
  }
  catch(HttpStatusException e) {
    //And return it with the '/' removed
    return word.substring(1);
  }
  catch(IOException e) {
    System.err.println(e);
    return("Whoops!");
  }
}