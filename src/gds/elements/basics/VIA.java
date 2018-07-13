package gds.elements.basics;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;
import gds.elements.AbstractElement;
import gds.elements.positioning.Port;
import gds.elements.positioning.Position;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;

public class VIA extends AbstractElement {

	/**
	 * This is VIA from M1 to M2 in for AIM PDK.
	 * The width are fixed = 0.4 um
	 */
	
	AbstractLayerMap layerMap1, layerMap2, layerMapVIA ;
	public Position center ;
	StraightWg rect1, rect2, rectVIA ;
	double width_um ;
	double width_VIA_um ;
	public Port port, port_VIA ;

	public VIA(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="First Layer") AbstractLayerMap layerMap1,
			@ParamName(name="Second Layer") AbstractLayerMap layerMap2,
			@ParamName(name="VIA Layer") AbstractLayerMap layerMapVIA,
			@ParamName(name="Position & Width") Port port,
			@ParamName(name="Width of the VIA", default_="0.4") Entry width_VIA_um
			){
		this.objectName = objectName ;
		this.layerMap1 = layerMap1 ;
		this.layerMap2 = layerMap2 ;
		this.layerMapVIA = layerMapVIA ; 
		this.port = port ;
		this.width_um = port.getWidthMicron() ;
		this.width_VIA_um = width_VIA_um.getValue() ;
		setPorts() ;
		createVIA1() ;
		saveProperties() ;
	}
	
	@Override
	public void setPorts(){
		double dx = (width_um-width_VIA_um)/2 ;
		Position hVec = port.connect().getNormalVec().resize(dx) ;
		port_VIA = port.translateXY(hVec).resize(width_VIA_um) ;
		center = port.getPosition().translateXY(hVec.resize(width_um/2)) ;
	
	}
	
	private void createVIA1(){
		// creating a metal 1 rectangle
		rect1 = new StraightWg(objectName+"_rect1", new AbstractLayerMap[] {layerMap1}, "port1", port, new Entry(width_um)) ;
		// creating a metal 2 rectangle
		rect2 = new StraightWg(objectName+"_rect2", new AbstractLayerMap[] {layerMap2}, "port1", port, new Entry(width_um)) ;
		// creating a VIA rectangle
		rectVIA = new StraightWg(objectName+"_rectVIA", new AbstractLayerMap[] {layerMapVIA}, "port1", port_VIA, new Entry(width_VIA_um)) ;
	}
	
	@Override
	public void saveProperties(){
		objectProperties.put(objectName+".width_um", width_um) ;
		objectProperties.put(objectName+".via.width_um", width_VIA_um) ;
	}
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##             Adding a VIA                 ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args = {st0, st1, st2} ;
		String[] st3 = rect1.getPythonCode_no_header(fileName, topCellName) ;
		String[] st4 = rect2.getPythonCode_no_header(fileName, topCellName) ;
		String[] st5 = rectVIA.getPythonCode_no_header(fileName, topCellName) ;
		args = MoreMath.Arrays.concat(args, st3) ;
		args = MoreMath.Arrays.concat(args, st4) ;
		args = MoreMath.Arrays.concat(args, st5) ;
		return args;
	}
	
	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args = new String[0] ;
		String[] st3 = rect1.getPythonCode_no_header(fileName, topCellName) ;
		String[] st4 = rect2.getPythonCode_no_header(fileName, topCellName) ;
		String[] st5 = rectVIA.getPythonCode_no_header(fileName, topCellName) ;
		args = MoreMath.Arrays.concat(args, st3) ;
		args = MoreMath.Arrays.concat(args, st4) ;
		args = MoreMath.Arrays.concat(args, st5) ;
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
		Port P1_translated = port.translateXY(dX, dY) ;
		AbstractElement VIA_new = new VIA(objectName, layerMap1, layerMap2, layerMapVIA, P1_translated, new Entry(width_VIA_um)) ;
		return VIA_new ;
	}
	
	
}
