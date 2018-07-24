package gds.elements.basics;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;
import gds.elements.AbstractElement;
import gds.elements.positioning.Array;
import gds.elements.positioning.Position;
import gds.elements.shapes.Rectangle;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;

public class Pad extends AbstractElement {

	AbstractLayerMap layerMap1, layerMap2, layerMapVIA ;
	Position P1, P2, P3, P4 ;
	public Position center ; 
	Position P1_via ;
	double width_um, width_VIA_um, dx_um, dy_um ;
	int numRows, numColumns ;
	double Xmargin_um, Ymargin_um ;
	Rectangle rect1, rect2, rectVIA ;
	Array viaArray ;
	
	public Pad(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="First Layer") AbstractLayerMap layerMap1,
			@ParamName(name="Second Layer") AbstractLayerMap layerMap2,
			@ParamName(name="VIA Layer") AbstractLayerMap layerMapVIA,
			@ParamName(name="Width of the two layer", default_="60") Entry width_um,
			@ParamName(name="Width of the VIA", default_="3") Entry width_VIA_um,
			@ParamName(name="Number of VIA rows") int numRows,
			@ParamName(name="Number of VIA columns") int numColumns,
			@ParamName(name="Horizontal distance between VIAs (micron)") Entry dx_um,
			@ParamName(name="Vertical distance between VIAs (micron)") Entry dy_um,
			@ParamName(name="Position of the start corner") Position P1
			){
		this.objectName = objectName ;
		this.layerMap1 = layerMap1 ;
		this.layerMap2 = layerMap2 ;
		this.layerMapVIA = layerMapVIA ;
		this.width_um = width_um.getValue() ;
		this.width_VIA_um = width_VIA_um.getValue() ;
		this.dx_um = dx_um.getValue() ;
		this.dy_um = dy_um.getValue() ;
		this.numRows = numRows ;
		this.numColumns = numColumns ;
		this.P1 = P1 ;
		
		setPorts() ;
		createObject() ;
		saveProperties() ;
	}
	
	@Override
	public void setPorts(){
		P2 = P1.translateX(width_um) ;
		P3 = P2.translateY(width_um) ;
		P4 = P1.translateY(width_um) ;
		center = P1.translateXY(width_um/2, width_um/2) ;
		// now calculating the margin
		Xmargin_um = (width_um-numColumns*width_VIA_um-(numColumns-1)*dx_um)/2 ;
		Ymargin_um = (width_um-numRows*width_VIA_um-(numRows-1)*dy_um)/2 ;
		P1_via = P1.translateXY(Xmargin_um, Ymargin_um) ;
	}
	
	private void createObject(){
		// creating a metal 1 rectangle
		rect1 = new Rectangle(objectName+"_rect1", new AbstractLayerMap[] {layerMap1}, P1.translateY(width_um/2), width_um, width_um, 0) ;
		// creating a metal 2 rectangle
		rect2 = new Rectangle(objectName+"_rect2", new AbstractLayerMap[] {layerMap2}, P1.translateY(width_um/2), width_um, width_um, 0) ;
		// creating a VIA rectangle
		rectVIA = new Rectangle(objectName+"_rectVIA", new AbstractLayerMap[] {layerMapVIA}, P1_via.translateY(width_VIA_um/2), width_VIA_um, width_VIA_um, 0) ;
		// now creating an array of VIAs
		viaArray = new Array(objectName+"_VIA_array", rectVIA, numRows, numColumns, dx_um + width_VIA_um, dy_um + width_VIA_um) ;
	}
	
	@Override
	public void saveProperties(){
		allElements.put(objectName, this) ;
	}
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##              Adding a PAD                ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args = {st0, st1, st2} ;
		args = MoreMath.Arrays.concat(args, rect1.getPythonCode(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, rect2.getPythonCode(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, viaArray.getPythonCode(fileName, topCellName)) ;
		return args;
	}
	
	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args = {} ;
		args = MoreMath.Arrays.concat(args, rect1.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, rect2.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, viaArray.getPythonCode_no_header(fileName, topCellName)) ;
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
		Position P1_translated = P1.translateXY(dX, dY) ;
		AbstractElement pad_translated = new Pad(objectName, layerMap1, layerMap2, layerMapVIA, new Entry(width_um), new Entry(width_VIA_um), numRows, numColumns, new Entry(dx_um), new Entry(dy_um), P1_translated) ;
		return pad_translated;
	}

	@Override
	public AbstractElement translateXY(String newName, double dX, double dY) {
		Position P1_translated = P1.translateXY(dX, dY) ;
		AbstractElement pad_translated = new Pad(newName, layerMap1, layerMap2, layerMapVIA, new Entry(width_um), new Entry(width_VIA_um), numRows, numColumns, new Entry(dx_um), new Entry(dy_um), P1_translated) ;
		return pad_translated;
	}

}
