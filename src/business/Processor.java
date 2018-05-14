package business;

import java.io.File;
import java.util.HashMap;

public class Processor {

	public Processor() {}
	
	public boolean compute(HashMap<Integer, File> e) throws Exception {
		
		for (File file : e.values()) {
			if(file.canRead()) {
				
			}else {
				throw new Exception("At least one file is not readable.");
			}
		}
		
		return false;
	}
	
}
