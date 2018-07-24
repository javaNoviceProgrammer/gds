package gds.functions.basic;

import ch.epfl.general_libraries.clazzes.ParamName;
import gds.elements.AbstractElement;
import gds.functions.Function;

public class Copy extends Function {
	
	/**
	 * gdspy.copy(obj, dx, dy)
	 */
	
	AbstractElement element ;
	double dx_um, dy_um ;
	
	public Copy(
			@ParamName(name="Name of the selected object") String objectName,
			@ParamName(name="Name of the copy") String copyName,
			@ParamName(name = "dx (um)") double dx_um,
			@ParamName(name="dy (um)") double dy_um
			) {

		element = allElements.get(objectName).translateXY(copyName, dx_um, dy_um) ;
		setPorts() ;
		saveProperties() ;
		allElements.put(copyName, element) ;
	}
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		return element.getPythonCode(fileName, topCellName);
	}

	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		return element.getPythonCode_no_header(fileName, topCellName);
	}

	@Override
	public void writeToFile(String fileName, String topCellName) {
		element.writeToFile(fileName, topCellName);
	}

	@Override
	public void appendToFile(String fileName, String topCellName) {
		element.appendToFile(fileName, topCellName);
	}

	@Override
	public void setPorts() {
		element.setPorts();
	}

	@Override
	public void saveProperties() {
		element.saveProperties();
	}

	@Override
	public AbstractElement translateXY(double dX, double dY) {
		return element.translateXY(dX, dY) ;
	}

	@Override
	public AbstractElement translateXY(String newName, double dX, double dY) {
		return element.translateXY(newName, dX, dY) ;
	}

}
