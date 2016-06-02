import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import org.jsoup.Jsoup; 
import org.jsoup.helper.Validate; 
import org.jsoup.nodes.Document; 
import org.jsoup.nodes.Element; 
import org.jsoup.select.Elements; 
import java.util.Scanner; 
import java.io.BufferedReader; 
import java.io.InputStreamReader; 
import java.io.FileInputStream; 

import org.jsoup.*; 
import org.jsoup.examples.*; 
import org.jsoup.helper.*; 
import org.jsoup.nodes.*; 
import org.jsoup.parser.*; 
import org.jsoup.safety.*; 
import org.jsoup.select.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class paraphraser2 extends PApplet {










/*
Mitchell Griest
 Paraphraser 2 (Non GUI)
 Summer 2016
 
 mdgriest@crimson.ua.edu
 */

ArrayList<Character> specialCharacters = new ArrayList<Character>();

PrintWriter output;

boolean txtMode = false;

public void setup() {
  specialCharacters.add('.');
  specialCharacters.add('?');
  specialCharacters.add('!');
  specialCharacters.add(',');
  specialCharacters.add(';');
  specialCharacters.add(',');

  

  //Create the font
  textAlign(CENTER, CENTER);
  int fontSize = 100;
  PFont font = createFont("Courier", fontSize);
  textFont(font);

  //TXT button (left)
  fill(blue);
  rect(0, 0, width/2, height);
  fill(white);
  text(".txt", width/4, height/2);

  //PDF button (right)
  font = createFont("Helvetica-light", fontSize);
  textFont(font);
  fill(white);
  rect(width/2, 0, width, height);
  fill(blue);
  text(".md", 3*width/4, height/2);

  //Central instructions
  fill(darkGray);
  noStroke();
  rectMode(CENTER);
  rect(width/2, 0.1f*height, 0.75f*width, 0.1f*height, 5);
  fill(white);
  font = createFont("Helvetica-light", fontSize * 0.25f);
  textFont(font);
  textAlign(CENTER, CENTER);
  text("What type of output file would you like?", width/2, height * 0.1f);
}

public void draw() {
}

//Called once the user chooses a file to paraphrase
public void fileSelected(File file) {
  if (file == null) {
    println("Window was closed or the user hit cancel.");
  } else if (!file.getName().endsWith(".txt")) {
    println("Whoops! Please select a .txt file");
  } else {
    try {
      //Create the output file
      String fileName = file.getName();

      String newFileName = fileName.substring(0, fileName.length()-4) + "_paraphrased";
      if (txtMode) newFileName += ".txt";
      else newFileName += ".md";
      output = createWriter(newFileName);

      //Add the contents of the original file
      String header = txtMode? "BEFORE:\n" : "#Before\n";
      output.println(header);
      String[] originalContents = loadStrings(fileName);
      for (String line : originalContents) {
        output.println(line);
      }

      String spacer = txtMode? "\n---------------------------------------------------------\n" : "\n---\n";
      output.println(spacer);

      String footer = txtMode? "AFTER:\n" : "#After\n";
      output.println(footer);

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
              addBold();
              output.append(paraphrase(newWord.substring(1)));
              addBold();
              //And the special character that was on the end of it
              output.append(word.charAt(word.length() - 1));
            }
            //Otherwise, just paraphrase the word and add it to the output
            else {
              addBold();
              output.print(paraphrase(word));
              addBold();
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

      //If the user wants a PDF, create it from the .md file we created
      //TODO PDF generation not working, only .md for now
      //if (!txtMode) {
      //  String PDFname = fileName.substring(0, fileName.length()-4) + "_paraphrased.pdf";
      //  String[] command = {"pandoc", "-s", "-o", PDFname, "newFileName"};
      //  Runtime rt = Runtime.getRuntime();
      //  Process p = rt.exec(command);
      //}
      exit();
    }
    catch(IOException e) {
      println(e);
    }
  }
}

public String paraphrase(String word) {
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

public void mouseClicked() {
  println(key);
  if (mouseX < width/2) txtMode = true; 
  else txtMode = false;
  selectInput("Select a file to paraphrase:", "fileSelected");
}

public void addBold(){
  if(!txtMode){
    output.append("**");
  }
}

int red = 0xffF27074;
int darkRed = 0xff8E2800;
int clay = 0xffB64926;
int yellow = 0xffFFA500;
int paleYellow = 0xffFFFF9D;
int orange = 0xffFF6138;
int green = 0xff468966;
int limeGreen = 0xffBDF271;
int lightGreen = 0xffBEEB9F;
int middleGreen = 0xff79BD8F;
int blueGreen = 0xff00A388;
int blue = 0xff1E90FF;
int dullBlue = 0xff348899;
int aqua = 0xff29D9C2;
int lightBlue = 0xff99CCFF;
int darkGrayBlue = 0xff6D7889;
int lightGray = 0xffD4D4D4;
int middleGray = 0xffAAAAAA;
int darkGray = 0xff4B4747;
int white = 0xffF3F4F2;
  public void settings() {  size(640, 320); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "paraphraser2" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
