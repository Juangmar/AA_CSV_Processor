package business;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Processor {

	public Processor() {}
	
	public boolean compute(HashMap<Integer, File> e) throws Exception {
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
		
		String[][] data = new String[lines.length][3];
		int lastInd = 0;
		for (String ex : lines) {
			if(!ex.equals("") && !ex.split(",")[0].equals("COMMENT_ID")) {
				if(ex.split("\"").length>3) {
					String[] fields = ex.split(",");
					data[lastInd][0] = fields[1];
					data[lastInd][2] = fields[fields.length-1];
					String[] content = ex.split("\"");
					data[lastInd][1] = content[3];
					lastInd++;
				} else {
					String[] fields = ex.split(",");
					data[lastInd][0] = fields[1];
					data[lastInd][2] = fields[fields.length-1];
					data[lastInd][1] = fields[3];
					lastInd++;
				}
				
			}
		}
		return save(lastInd, e.get(0).getParent(), data);
	}

	private boolean save(int lastInd, String path, String[][] data) {
		boolean result = false;
		String totalPath = path + "result.csv";
		File save = new File(totalPath);
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(save));
			for (int i = 0; i < lastInd; i++) {
				output.write(data[i][0] + "," + data[i][1] + "," + data[i][2] + "\n");
			}
			output.close();
			result = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
}
