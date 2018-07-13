package gds.elements.shapes.path_elements;

import gds.elements.positioning.Position;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;

public class Segment extends AbstractPathElement {

	/**
	 * Segment from PATH: segment(length, direction=None, final_width=None, final_distance=None, axis_offset=0, layer=0, datatype=0)
	 */
	
	double length_um, angleDegree, angleRad ;
	String finalWidth_um ;
	Position hVec ;

	public Segment(
			@ParamName(name="Length (um)") double length_um,
			@ParamName(name="Final Width (um) [Taper]") double finalWidth_um
			){
		numElements++ ;
		this.length_um = length_um ;
		this.finalWidth_um = finalWidth_um + "" ;
		if(numElements != 1) {port1 = elementPorts.get("last.port2").connect() ;}
		hVec = port1.connect().getNormalVec().resize(length_um) ;
		port2 = port1.translateXY(hVec).connect() ;
		angleDegree = port2.getNormalDegree() ; 
		angleRad = angleDegree * Math.PI/180 ;
		updateLastElement() ;
	}
	
	public Segment(
			@ParamName(name="Length (um)") double length_um
			){
		numElements++ ;
		this.length_um = length_um ;
		this.finalWidth_um = "None" ;
		if(numElements != 1) {port1 = elementPorts.get("last.port2").connect() ;}
		hVec = port1.connect().getNormalVec().resize(length_um) ;
		port2 = port1.translateXY(hVec).connect() ;
		angleDegree = port2.getNormalDegree() ; 
		angleRad = angleDegree * Math.PI/180 ;
		updateLastElement() ;
	}
	
	private void updateLastElement(){
		elementPorts.put("last.port1", port1) ;
		elementPorts.put("last.port2", port2) ;
	}
	
	@Override
	public String[] getPythonCode(String pathName, AbstractLayerMap layerMap) {
		String st0 = "### adding a SEGMENT" ;
		String[] args = {st0} ;
		int layerNumber = layerMap.getLayerNumber() ;
		int dataType = layerMap.getDataType() ;
		String st1 = pathName + ".segment(" + length_um + "," + "direction=" + angleRad + "," + "final_width=" + 
					 						finalWidth_um + "," + "final_distance=None"+", " + "axis_offset=0" + ", " + "layer=" + layerNumber + "," + "datatype=" + dataType + ")" ;
		args = MoreMath.Arrays.concat(args, new String[] {st1}) ;
		return args;
	}
	
	@Override
	public String[] getPythonCode_no_header(String pathName, AbstractLayerMap layerMap) {
		String[] args = new String[0] ;
		int layerNumber = layerMap.getLayerNumber() ;
		int dataType = layerMap.getDataType() ;
		String st1 = pathName + ".segment(" + length_um + "," + "direction=" + angleRad + "," + "final_width=" + 
					 						finalWidth_um + "," + "final_distance=None"+", " + "axis_offset=0" + ", " + "layer=" + layerNumber + "," + "datatype=" + dataType + ")" ;
		args = MoreMath.Arrays.concat(args, new String[] {st1}) ;
		return args;
	}

	@Override
	public String getElementName() {
		return "Segment" ;
	}


}
