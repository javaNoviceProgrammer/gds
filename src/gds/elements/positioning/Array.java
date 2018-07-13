package gds.elements.positioning;

import gds.elements.AbstractElement;
import gds.util.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;

public class Array extends AbstractElement {

	/**
	 * The goal of this class is to create 1D or 2D array (MOSAIC) of objects.
	 * The input should be an object and the output should be an array of those objects
	 */
	
//	String objectName ;
	AbstractElement element ; // this is bottom left corner element of the array (hence: dx>=0 and dy>=0 ALWAYS!!)
	public AbstractElement[][] array ;
	int numRows, numColumns ;
	double dx, dy ;
	
	public Array(
			@ParamName(name="Array Name") String objectName,
			@ParamName(name="Choose Element") AbstractElement element,
			@ParamName(name="Number of rows") int numRows,
			@ParamName(name="Number of columns") int numColumns,
			@ParamName(name="Column period >= 0 (micron)") double dx,
			@ParamName(name="Row Period >= 0 (micron)") double dy
			){
		this.objectName = objectName ;
		this.element = element ;
		this.numRows = numRows ;
		this.numColumns = numColumns ;
		this.dx = dx ;
		this.dy = dy ;
		createArray() ;
		saveProperties() ;
	}
	
	@Override 
	public void setPorts(){
		// nothing to do here
	}
	
	@Override
	public void saveProperties(){
		
	}
	
	private void createArray(){
		// first need to define a 2D array of the selected object
		array = new AbstractElement[numRows][numColumns] ;

		for(int i=0; i<numRows; i++){
			for(int j=0; j<numColumns; j++){
				AbstractElement tempElement = element ;
				tempElement.objectName = objectName+"_"+i+"_"+j ;
				array[i][j] =  tempElement.translateXY(j*dx, i*dy) ;
			}
		}
	}
	

	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##           Adding an ARRAY                ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args = {st0, st1, st2} ;
		for(int i=0; i<numRows; i++){
			for(int j=0; j<numColumns; j++){
				args = MoreMath.Arrays.concat(args, array[i][j].getPythonCode_no_header(fileName, topCellName)) ;
			}
		}
		return args ;
	}
	
	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args = new String[0] ;
		for(int i=0; i<numRows; i++){
			for(int j=0; j<numColumns; j++){
				args = MoreMath.Arrays.concat(args, array[i][j].getPythonCode_no_header(fileName, topCellName)) ;
			}
		}
		return args ;
	}

	@Override
	public void writeToFile(String fileName, String topCellName) {
		FileOutput fout = new FileOutput(fileName + ".py","w") ;
		fout.println(getPythonCode(fileName, topCellName));
		fout.close();
	}

	@Override
	public void appendToFile(String fileName, String topCellName) {
		FileOutput fout = new FileOutput(fileName + ".py","a") ;
		fout.println(getPythonCode(fileName, topCellName));
		fout.close();
	}


	@Override
	public AbstractElement translateXY(double dX, double dY) {
		AbstractElement element_translated = element.translateXY(dX, dY) ;
		AbstractElement array_translated = new Array(objectName, element_translated, numRows, numColumns, dx, dy) ;
		return array_translated;
	}

}
