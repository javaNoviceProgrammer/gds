package gds.functions;

import gds.elements.AbstractElement;

public abstract class Function extends AbstractElement {
	
	public abstract String[] getPythonCode(String fileName, String topCellName) ;
	public abstract String[] getPythonCode_no_header(String fileName, String topCellName) ;
	public abstract void writeToFile(String fileName, String topCellName) ;
	public abstract void appendToFile(String fileName, String topCellName) ;

}
