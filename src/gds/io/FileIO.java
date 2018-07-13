package gds.io;

import flanagan.io.FileOutput;

public class FileIO {

	/**
	 * this class is intended to provide easy to access open, read, write operations on files
	 */
	
	public static class Write{
		public static void writeToFile(String fileName, String fileExtension, String[] textLines) {
			FileOutput fout = new FileOutput(fileName + fileExtension,"w") ;
			fout.println(textLines);
			fout.close();
		}
	}
	
	public static class Append{
		public static void writeToFile(String fileName, String fileExtension, String[] textLines) {
			FileOutput fout = new FileOutput(fileName + fileExtension,"a") ;
			fout.println(textLines);
			fout.close();
		}
	}
	
	
	public static class Open{
		
	}
	
	
	
	
	
	
	
	
}
