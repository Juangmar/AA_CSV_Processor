package business;

import java.io.File;
import java.io.FileInputStream;
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
			if(!ex.equals("") && !ex.equals("COMMENT_ID,AUTHOR,DATE,CONTENT,CLASS")) {
				String[] fields = ex.split(",");
				data[lastInd][0] = fields[1];
				data[lastInd][2] = fields[fields.length-1];
				String[] content = ex.split("\"");
				data[lastInd][1] = content[1];
				lastInd++;
			}
		}
		return false;
	}
	
}
