package gds.layout;

import gds.headers.Footer;
import gds.headers.Header;
import gds.layout.cells.Cell;
import gds.util.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;

public class Layout {
	
	// Step 1: define the name of the file
	// Step 2: import the necessary libraries through the header class
	// Step 3: add the body (including the photonic elements)
	// Step 4: add the footer for saving the .py file and closing the file
	
	String topCellName ;
	Header header ;
	Footer footer ;
	Cell[] cells ;
	int numCells ;

	public Layout(
			@ParamName(name="Top Cell Name") String topCellName ,
			@ParamName(name="Header") Header header,
			@ParamName(name="Footer") Footer footer,
			@ParamName(name="Cells") Cell[] cells
			){
		this.topCellName = topCellName  ;
		this.header = header ;
		this.footer = footer ;
		this.cells = cells ;
		this.numCells = cells.length ;
	}
	
	private void createTopCell(){
		String st0 = "" ;
		String st1 = "### Now creating the TOP CeLL" ;
		String st2 = topCellName + " = gdspy.Cell(" + "'" + topCellName + "'" + ")" ;
		String[] args0 = {st0, st1, st2} ;
		int n = cells.length ;
		String[] args1 = new String[n] ;	
		for(int i=0; i<n; i++){
			args1[i] = topCellName + ".add(" + cells[i].cellName + ")"  ;
		}
		String[] args = MoreMath.Arrays.concat(args0, args1) ;
		FileOutput fout = new FileOutput(topCellName + ".py","a") ; // note that we need to append to the existing file
		fout.println(args);
		fout.close();
	}
	
	public void execute(){
		// adding the header first
		header.writeToFile(topCellName);
		// then adding the main body of each Cell
		for(int i=0; i<numCells; i++){
			cells[i].appendToFile(topCellName);
		}
		// next creating the top_cell and adding all the cells
		createTopCell() ;
		// finally adding the footer
		footer.appendToFile(topCellName);
	}

	
}
