package business;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Processor {

	public Processor() {}
	
	public File compute(HashMap<Integer, File> e) throws Exception {
		final String comas = ";;;;;;;;;;;;;\r\n";
		final String r = "\r";
		final String n = "\n";
		String all = "";
		for (File file : e.values()) {
			if(file.canRead()) {
				try {
					FileInputStream in = new FileInputStream(file);
					StringBuilder builder = new StringBuilder();
					int ch;
					while((ch = in.read()) != -1){
					    builder.append((char)ch);
					}
					in.close();
					String end = builder.toString();
						end.replaceAll(comas, n);
						end.replaceAll(r, n);
					all += end + n;
				}catch (Exception err) {
						
				}
			}else {
				throw new Exception("At least one file is not readable.");
			}
		}
		String[] lines = all.split(n);
		
		List<String> vocab = new ArrayList<>();
		
		String[][] data = new String[lines.length][3];
		int lastInd = 0;
		for (String ex : lines) {
			if(!ex.equals("") && !ex.split(",")[0].equals("COMMENT_ID")) {
				if(ex.split("\"").length>3) {
					String[] fields = ex.split(",");
					data[lastInd][0] = fields[1];
					data[lastInd][2] = fields[fields.length-1].replaceAll(";", "").replaceAll("\"", "");
					String[] content = ex.split("\"");
					data[lastInd][1] = content[3].toLowerCase().replaceAll(";","");
					lastInd++;
					if(data[lastInd-1][2].equals("1"))computeVocab(content[3].toLowerCase(), vocab);
				} else {
					String[] fields = ex.split(",");
					data[lastInd][0] = fields[1];
					data[lastInd][2] = fields[fields.length-1].replaceAll(";", "").replace("\"","");
					data[lastInd][1] = fields[3].toLowerCase().replaceAll(";", "");
					lastInd++;
					if(data[lastInd-1][2].equals("1"))computeVocab(fields[3].toLowerCase(), vocab);
				}
				
			}
		}
		writeVocab(vocab, e.get(0).getParent());
		return save(lastInd, e.get(0).getParent(), data);
	}

	private void computeVocab(String message, List<String> vocab) {
		String[] words = message.split(" ");
		for(String word : words) {
			String processedWord = word.replaceAll("\"", "").replaceAll(":", "").replaceAll(",", "").replaceAll("\\d", "").replaceAll("\\W", "").replaceAll("_", "");
			if(!vocab.contains(processedWord)&&!word.equals("")) vocab.add(processedWord);
		}
	}
	
	private void writeVocab(List<String> vocab, String path) throws IOException {
		java.util.Collections.sort(vocab);
		
		File save = null;
		String totalPath = path + "vocab.txt";
		save = new File(totalPath);
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(save));
				output.write(1 + "\t" + vocab.get(1));
			for (int i = 2; i < vocab.size(); i++) {
				output.write("\n" + i + "\t" + vocab.get(i));
			}
			output.close();
		} catch (IOException e) {
			
			e.printStackTrace();
			throw new IOException("Cannot write");
		}
		

	}

	private File save(int lastInd, String path, String[][] data) {
		if(lastInd==0||path==""||data==null) return null;
		
		File save = null;
		String totalPath = path + "_result.txt";
		save = new File(totalPath);
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(save));
			for (int i = 0; i < lastInd; i++) {
				output.write(data[i][0] + ";" + data[i][1] + ";" + data[i][2] + "\n");
			}
			output.close();
			return save;
		} catch (IOException e) {
			
			e.printStackTrace();
			return null;
		}
	}
	
}
