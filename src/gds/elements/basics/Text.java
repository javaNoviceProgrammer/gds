package gds.elements.basics;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;
import gds.elements.AbstractElement;
import gds.elements.positioning.Position;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;

public class Text extends AbstractElement {

	/**
	 * TEXT gdspy: class gdspy.Text(text, size, position=(0, 0), horizontal=True, angle=0, layer=0, datatype=0)
	 *  AIM TEXT layers: (MLA, FNA, SEA, M2A) --> Size : 12um
	 */
	
	AbstractLayerMap[] layerMap ;
	String text ;
	double size, orientationAngleDegree, orientationAngleRad ;
	Position P ;
	String isHorizontal ;
	boolean isHorizontalBool ;
	
	public Text(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Waveguide Layer") AbstractLayerMap[] layerMap,
			@ParamName(name="TEXT") String text,
			@ParamName(name="Start Position") Position P,
			@ParamName(name="Size") Entry size,
			@ParamName(name="is Horizontal") boolean isHorizontal,
			@ParamName(name="Rotation angle (degree)") Entry orientationAngleDegree
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.text = text ;
		this.P = P ;
		this.size = size.getValue() ;
		this.isHorizontalBool = isHorizontal ;
		if(isHorizontal){this.isHorizontal = "True" ;} else{this.isHorizontal = "False" ;}
		this.orientationAngleDegree = orientationAngleDegree.getValue() ;
		orientationAngleRad = orientationAngleDegree.getValue() * Math.PI/180 ;
		saveProperties() ;
	}
	
	@Override
	public void setPorts(){
		// nothing for this class
	}
	
	@Override
	public void saveProperties(){
		allElements.put(objectName, this) ;
	}
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##             Adding a TEXT                ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args = {st0, st1, st2} ;
		int n = layerMap.length ;
		for(int i=0; i<n; i++){
			int layerNumber = layerMap[i].getLayerNumber() ;
			int dataType = layerMap[i].getDataType() ;
			String title = "### adding a "+ layerMap[i].getLayerName() +" layer" ;
			String st3 = objectName + " = gdspy.Text(" + "'" + text + "'" + "," + size + "," + "position=" + P.getString() + "," + "horizontal=" + isHorizontal + "," + "angle=" + 
							orientationAngleRad + "," + "layer=" + layerNumber + "," + "datatype=" + dataType + ")" ;
			String st4 = topCellName + ".add(" + objectName + ")" ;
			args = MoreMath.Arrays.concat(args, new String[] {title, st3, st4}) ;
		}
		return args;
	}
	
	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args = new String[0] ;
		int n = layerMap.length ;
		for(int i=0; i<n; i++){
			int layerNumber = layerMap[i].getLayerNumber() ;
			int dataType = layerMap[i].getDataType() ;
			String title = "### adding a "+ layerMap[i].getLayerName() +" layer" ;
			String st3 = objectName + " = gdspy.Text(" + "'" + text + "'" + "," + size + "," + "position=" + P.getString() + "," + "horizontal=" + isHorizontal + "," + "angle=" + 
							orientationAngleRad + "," + "layer=" + layerNumber + "," + "datatype=" + dataType + ")" ;
			String st4 = topCellName + ".add(" + objectName + ")" ;
			args = MoreMath.Arrays.concat(args, new String[] {title, st3, st4}) ;
		}
		return args;
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
		Position P_translated = P.translateXY(dX, dY) ;
		AbstractElement text_translated = new Text(objectName, layerMap, text, P_translated, new Entry(size), isHorizontalBool, new Entry(orientationAngleDegree)) ;
		return text_translated;
	}

	@Override
	public AbstractElement translateXY(String newName, double dX, double dY) {
		Position P_translated = P.translateXY(dX, dY) ;
		AbstractElement text_translated = new Text(newName, layerMap, text, P_translated, new Entry(size), isHorizontalBool, new Entry(orientationAngleDegree)) ;
		return text_translated;
	}

}
