package gds.layout;

import gds.headers.Footer;
import gds.headers.Header;
import gds.layout.cells.Cell;
import gds.util.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;

public class Layout {

	String topCellName ;
	Header header ;
	Footer footer ;
	Cell[] cells ;
	int numCells ;

/*	public Layout(
			@ParamName(name="Layout Name") String topCellName ,
			@ParamName(name="Header") Header header,
			@ParamName(name="Footer") Footer footer,
			@ParamName(name="Cells") Cell[] cells
			){
		this.topCellName = topCellName  ;
		this.header = header ;
		this.footer = footer ;
		this.cells = cells ;
		this.numCells = cells.length ;
	}*/

	public Layout(
			@ParamName(name="Layout Name") String topCellName ,
			@ParamName(name="Cells") Cell[] cells
			){
		this.topCellName = topCellName  ;
		this.header = new Header() ;
		this.footer = new Footer() ;
		this.cells = cells ;
		this.numCells = cells.length ;
	}

	private void createLayout(){
		String st0 = "" ;
		String st1 = "### Now creating the LAYOUT" ;
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
		header.writeToFile(topCellName);
		for(int i=0; i<numCells; i++){
			cells[i].appendToFile(topCellName);
		}
		createLayout() ;
		footer.appendToFile(topCellName);
	}


}
