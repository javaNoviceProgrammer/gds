package gds.layout.cells;

import java.io.File;

import gds.elements.AbstractElement;
import gds.elements.DataBase;
import gds.elements.positioning.Position;
import gds.headers.Footer;
import gds.headers.Header;
import gds.util.CustomJFileChooser;
import gds.util.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;

public class Cell {

	/**
	 * The GDS layout consists of various Cells
	 */

	public String cellName, cellPath ;
	public AbstractElement[] elements ;
	public Position P ;
	boolean createPy ;
	boolean runLayoutViewer ;

	public Cell(
			@ParamName(name="Cell Name") String cellName,
			@ParamName(name="Run Layout Viewer?") boolean runLayoutViewer,
			@ParamName(name="Elements") AbstractElement[] elements
			){
		this.cellName = cellName ;
		this.elements = elements ;
		this.runLayoutViewer = runLayoutViewer ;
		// create python file and compile it
		cellPath = CustomJFileChooser.path + File.separator + cellName ;
		savePyFile() ;
		// now clear the data base
		DataBase.clear();
	}

	public Cell(
			@ParamName(name="Cell Name") String cellName,
			@ParamName(name="Run Layout Viewer?") boolean runLayoutViewer,
			@ParamName(name="Global translation of the Cell") Position P,
			@ParamName(name="Elements") AbstractElement[] elements
			){
		this.cellName = cellName ;
		this.elements = elements ;
		this.P = P ;
		this.runLayoutViewer = runLayoutViewer ;
		// create python file and compile it
		cellPath = CustomJFileChooser.path + File.separator + cellName ;
		savePyFile() ;
		translateCell();
		// now clear the data base
		DataBase.clear();
	}

	public String[] getPythonCode(String fileName) {
		String st0 = "" ;
		String st1 = "## ---------------------------------------- ##" ;
		String st2 = "##       Creating a new CELL                ##" ;
		String st3 = "## ---------------------------------------- ##" ;
		String st4 = cellName + " = gdspy.Cell(" + "'" + cellName + "'" + ")" ;
		String[] args = {st0, st1, st2, st3, st4} ;
		int n = elements.length ;
		for(int i=0; i<n; i++){
			args = MoreMath.Arrays.concat(args, elements[i].getPythonCode(fileName, cellName)) ;
		}
		return args ;
	}

	public String[] getPythonCode_no_header(String fileName) {
		String st0 = "" ;
		String st4 = cellName + " = gdspy.Cell(" + "'" + cellName + "'" + ")" ;
		String[] args = {st0, st4} ;
		int n = elements.length ;
		for(int i=0; i<n; i++){
			args = MoreMath.Arrays.concat(args, elements[i].getPythonCode(fileName, cellName)) ;
		}
		return args ;
	}


	public void writeToFile(String fileName) {
		FileOutput fout = new FileOutput(fileName + ".py","w") ;
		fout.println(getPythonCode(fileName));
		fout.close();
	}


	public void appendToFile(String fileName) {
		FileOutput fout = new FileOutput(fileName + ".py","a") ;
		fout.println(getPythonCode(fileName));
		fout.close();
	}


/*	public Cell translateXY(double dX, double dY) {
		int n = elements.length ;
		AbstractElement[] elements_translated = new AbstractElement[n] ;
		for(int i=0; i<n; i++){
			elements_translated[i] = elements[i].translateXY(dX, dY) ;
		}
		Cell cell_translated = new Cell(cellName, elements_translated, P, false) ;
		return cell_translated ;
	}*/


	private void translateCell() {
		int n = elements.length ;
		for(int i=0; i<n; i++){
			elements[i] = elements[i].translateXY(P.getX(), P.getY()) ;
		}
	}

	// creating a python code specifically for the cell with complete header and footer
	public void savePythonFile(){
		Header header = new Header(true, true, true, true, true) ;
		header.writeToFile(cellName);
		appendToFile(cellName);
		Footer footer = new Footer() ;
		footer.appendToFile(cellName);
	}

	public void savePyFile(){
		String fullPath = cellPath ;
		Header header = new Header(true, true, true, true, true) ;
		header.writeToFile(fullPath);
		appendToFile(fullPath);
		Footer footer = new Footer() ;
//		footer.appendToFile(cellName, fullPath);
		footer.appendToFile(cellName, fullPath, runLayoutViewer);
		System.gc();
	}


}
