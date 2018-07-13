package gds.elements.shapes.path_elements;

import gds.elements.positioning.Position;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;

public class Turn extends AbstractPathElement {

	/**
	 * Similar to ARC, but does not specify the initial and final angle. 
	 * turn(radius, angle, number_of_points=0.01, max_points=199, final_width=None, final_distance=None, layer=0, datatype=0)
	 * 
	 * DEFAULT direction: if (angle>0) then the turn is to the LEFT. if (angle<0), the turn is to the right.
	 * 
	 * I just use this class for 90 & 180 degree turns to make life easier ;)
	 */
	
	double radius_um, angleDegree, angleRad ;
	String finalWidth_um ;
	Position vec, center ;
	
	// this is for 90degree and 180degree turns --> WITH taper
	public Turn(
			@ParamName(name="Radius (um)") double radius_um,
			@ParamName(name="Direction [r, rr, l, ll]") String direction,
			@ParamName(name="Final width (um) [Taper]") double finalWidth_um
			){
		numElements++ ;
		this.radius_um = radius_um ;
		if(direction.equals("r")){angleDegree = -90 ;}
		else if(direction.equals("rr")){angleDegree = -180 ;}
		else if(direction.equals("l")){angleDegree = +90 ;} 
		else{angleDegree = +180 ;} 
		angleRad = angleDegree * Math.PI/180 ;
		if(numElements != 1) {port1 = elementPorts.get("last.port2").connect() ; }
		vec = port1.getEdgeVec().resize(radius_um) ;
		center = port1.getPosition().translateXY(vec) ;
		port2 = port1.rotate(center, angleDegree).connect() ; // don't forget to apply connect function
		this.finalWidth_um = finalWidth_um + "" ;
		updateLastElement() ;
	}
	
	// this is for 90degree and 180degree turns --> WITHOUT taper
	public Turn(
			@ParamName(name="Radius (um)") double radius_um,
			@ParamName(name="Direction [r, rr, l, ll]") String direction
			){
		numElements++ ;
		this.radius_um = radius_um ;
		if(direction.equals("r")){angleDegree = -90 ;}
		else if(direction.equals("rr")){angleDegree = -180 ;}
		else if(direction.equals("l")){angleDegree = +90 ;} 
		else{angleDegree = +180 ;} 
		angleRad = angleDegree * Math.PI/180 ;
		if(numElements != 1) {port1 = elementPorts.get("last.port2").connect() ; }
		vec = port1.getEdgeVec().resize(radius_um) ;
		center = port1.getPosition().translateXY(vec) ;
		port2 = port1.rotate(center, angleDegree).connect() ; // don't forget to apply connect function
		this.finalWidth_um = "None" ;
		updateLastElement() ;
	}
	
	private void updateLastElement(){
		elementPorts.put("last.port1", port1) ;
		elementPorts.put("last.port2", port2) ;
	}
	
	@Override
	public String[] getPythonCode(String pathName, AbstractLayerMap layerMap) {
		String st0 = "### adding a TURN" ;
		String[] args = {st0} ;
		int layerNumber = layerMap.getLayerNumber() ;
		int dataType = layerMap.getDataType() ;
		String st1 = pathName + ".turn(" + radius_um + "," + angleRad + "," + "number_of_points=2000" + "," +  "max_points=5000" + "," +
											"final_width=" + finalWidth_um + "," + "final_distance=None" + "," +  "layer=" + layerNumber + "," + "datatype=" + dataType + ")" ;
		args = MoreMath.Arrays.concat(args, new String[] {st1}) ;
		return args;
	}
	
	@Override
	public String[] getPythonCode_no_header(String pathName, AbstractLayerMap layerMap) {
		String[] args = {} ;
		int layerNumber = layerMap.getLayerNumber() ;
		int dataType = layerMap.getDataType() ;
		String st1 = pathName + ".turn(" + radius_um + "," + angleRad + "," + "number_of_points=2000" + "," +  "max_points=5000" + "," +
											"final_width=" + finalWidth_um + "," + "final_distance=None" + "," +  "layer=" + layerNumber + "," + "datatype=" + dataType + ")" ;
		args = MoreMath.Arrays.concat(args, new String[] {st1}) ;
		return args;
	}

	@Override
	public String getElementName() {
		return "Turn" ;
	}

}
