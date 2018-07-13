package gds.elements;

public abstract class AbstractElement extends DataBase {

	/** 
	 * Elements include: StraightWg, CurvedWg, ...
	 */
	
	public String objectName ;
	public static int numElements = 0 ; // this keeps track of how many elements have been created in a cell
	
	public abstract void setPorts() ;
	public abstract void saveProperties() ;
	
	public abstract String[] getPythonCode(String fileName, String topCellName) ;
	public abstract String[] getPythonCode_no_header(String fileName, String topCellName) ;
	public abstract void writeToFile(String fileName, String topCellName) ;
	public abstract void appendToFile(String fileName, String topCellName) ;
	public abstract AbstractElement translateXY(double dX, double dY) ;
	

	
}
