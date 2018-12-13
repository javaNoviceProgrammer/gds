package gds.io;

import gds.layout.cells.Cell;
import gds.util.OSDetector;

import java.io.IOException;

public class PythonCompiler {

	static String fileName ;
	static String pythonVersion = "3.5" ;

	public PythonCompiler(String fileName){
		PythonCompiler.fileName = fileName ;
	}

	public PythonCompiler(Cell cell){
		PythonCompiler.fileName = cell.cellPath ;
	}
	
	public void setPythonVersion(String version) {
		pythonVersion = version ;
	}

	public void compile(){
		String command = "" ;
		if(OSDetector.isMac()){
			command = "/Library/Frameworks/Python.framework/Versions/3.5/bin/python3 " + fileName + ".py" ;
		}
		else if(OSDetector.isWindows()){
			command = "python " + "\"" + fileName + ".py" + "\"" ;
		}
		try {
			Runtime.getRuntime().exec(command) ;
			System.gc();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void compile(Cell cell){
		String command = "" ;
		if(OSDetector.isMac()){
			command = "/Library/Frameworks/Python.framework/Versions/" + pythonVersion + "/bin/python3 " + cell.cellPath + ".py" ;
		}
		else if(OSDetector.isWindows()){
			command = "python " + "\"" + cell.cellPath + ".py" + "\"" ;
		}
		try {
			Runtime.getRuntime().exec(command) ;
			System.gc();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
