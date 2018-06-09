package business;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class to normalize the given CSVs into just one plain CSV.
 * @author Juan Gomez-Martinho
 *
 */
public class Processor {

	/**
	 * Default constructor. Makes nothing.
	 */
	public Processor() {}
	
	/**
	 * Given a map of csv files, the method unifies them and save the important data
	 * @param e HashMap of CSV files.
	 * @return //The file containing the processed data.
	 * @throws Exception //If there's an I/O exception it is thrown
	 */
	public File compute(HashMap<Integer, File> e) throws Exception {
		final String comas = ";;;;;;;;;;;;;\r\n"; //For some reason, the lines in our examples had this many commas at the end of the line
		final String r = "\r"; //Line sep
		final String n = "\n"; //Line sep 2
		String all = ""; //String that will contain all the csv's content 
		for (File file : e.values()) { //Iterate very file into the HashMap
			if(file.canRead()) { //Only if the current file is readable
				try {
					FileInputStream in = new FileInputStream(file); //Create the input object
					StringBuilder builder = new StringBuilder(); //StringBuilter object to compose the content 
					int ch;
					while((ch = in.read()) != -1){ //Iterate on every character in the file
					    builder.append((char)ch); //Append the character into the builder.
					}
					in.close(); //All characters are red. Close the file.
					String end = builder.toString(); //Save the result string into a variable
						end.replaceAll(comas, n); //Delete all commas replacing them with "\n"
						end.replaceAll(r, n); //Replace all \r line separators with "\n"
					all += end + n; //Add the result string into the 
				}catch (Exception err) {
						
				}
			}else { //The file is not readable. Throw exception
				throw new Exception("At least one file is not readable.");  //Exception thrown with message
			}
		}
		String[] lines = all.split(n); //Split the text using the line separator "\n"
		
		List<String> vocab = new ArrayList<>(); //This list of string will compose the "vocab": the list of words of spam messages
		
		String[][] data = new String[lines.length][3]; //The matrix of final data. Each row has: author-message-spam/hmm
		int lastInd = 0; //Last line written on the data list
		for (String ex : lines) { //For each line of the csv compilation.
			if(!ex.equals("") && !ex.split(",")[0].equals("COMMENT_ID")) { //If the line after deleting keys is empty or it's the header
				if(ex.split("\"").length>3) {  //if after the Split using the " delimiter it's bigger than 3
					String[] fields = ex.split(","); //the delimiter of the csv's is ','
					data[lastInd][0] = fields[1]; // Author = fields(1) --> Ignore the message id
					data[lastInd][2] = fields[fields.length-1].replaceAll(";", "").replaceAll("\"", ""); //The spam/ham label is the last field, without ; or "
					String[] content = ex.split("\""); //The message is the first (1) field resulting from splitting using " 
					data[lastInd][1] = content[3].toLowerCase().replaceAll(";",""); //remove every ; from the message
					lastInd++; //Data written. Last index increased.
					if(data[lastInd-1][2].equals("1"))computeVocab(content[3].toLowerCase(), vocab); // If the message is spam, update the vocab list
				} else {
					String[] fields = ex.split(","); //the delimiter of the csv's is ','
					data[lastInd][0] = fields[1]; // Author = fields(1) --> Ignore the message id
					data[lastInd][2] = fields[fields.length-1].replaceAll(";", "").replace("\"",""); //The spam/ham label is the last field, without ; or "
					data[lastInd][1] = fields[3].toLowerCase().replaceAll(";", ""); //remove every ; from the message
					lastInd++; //Data written. Last index increased.
					if(data[lastInd-1][2].equals("1"))computeVocab(fields[3].toLowerCase(), vocab); // If the message is spam, update the vocab list
				}
			}
		}
		//After all files are processed
		writeVocab(vocab, e.get(0).getParent()); //Save the vocab list
		return save(lastInd, e.get(0).getParent(), data); //Save the resulting csv and return the created file
	}

	/**
	 * Given a spam message and the vocablist, the method inserts all new words (not symbols) from the message into the list
	 * @param message Spam message to be processed.
	 * @param vocab The initialized list containing the separated words.
	 */
	private void computeVocab(String message, List<String> vocab) {
		String[] words = message.split(" "); //Split the words using a simple space
		for(String word : words) { //for each resulting word:
			String processedWord = word.replaceAll("\"", "").replaceAll(":", "").replaceAll(",", "").replaceAll("\\d", "").replaceAll("\\W", "").replaceAll("_", ""); //delete all non alpha-numerical symbols.
			if(!vocab.contains(processedWord)&&!word.equals("")) vocab.add(processedWord); //If the resulting word is not already in the list, add it
		}
	}
	
	/**
	 * Given a list of words and a path, it's saved into a .txt file in the given path.
	 * @param vocab List<String> containing the words
	 * @param path Path to be written
	 * @throws IOException //If there's a problem creating the file, an exception is thrown
	 */
	private void writeVocab(List<String> vocab, String path) throws IOException {
		java.util.Collections.sort(vocab); //Sort the list alphabetically 
		File save = null; //Initialize the result file
		String totalPath = path + "vocab.txt"; //The file is the result of adding the filename to the path
		save = new File(totalPath); //Create the file object
		try { 
			BufferedWriter output = new BufferedWriter(new FileWriter(save)); //Create the output writer
				output.write(1 + "\t" + vocab.get(1)); //write the first line: "1 first_word"
			for (int i = 2; i < vocab.size(); i++) { //for each word in the list:
				output.write("\n" + i + "\t" + vocab.get(i)); //Write the line separator and the word
			}
			output.close(); //Close the file.
		} catch (IOException e) { //If there's a problem with the I/O
			e.printStackTrace(); //Write the error
			throw new IOException("Cannot write"); //Throw the exception
		}
	}

	/**
	 * Given the size of the data, the path of the file an the data itself, writes a CSV file
	 *  with the data in it.
	 * @param lastInd Size of the data
	 * @param path Absolute path for the file
	 * @param data Matrix with the data
	 * @return file with the data
	 */
	private File save(int lastInd, String path, String[][] data) {
		if(lastInd==0||path==""||data==null) return null; //If the data is empty, return null
		File save = null; //New file
		String totalPath = path + "_result.txt"; //The file is the result of adding the filename to the path
		save = new File(totalPath); //Create the file object
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(save)); //Create the output writer
			for (int i = 0; i < lastInd; i++) { //for each line in the data:
				output.write(data[i][0] + ";" + data[i][1] + ";" + data[i][2] + "\n"); //Write the line of the csv
			}
			output.close(); // Close the file
			return save; //return the file
		} catch (IOException e) {	//If there's a problem with the I/O	
			e.printStackTrace(); //Write the error
			return null; //Reutn null
		}
	}
}
