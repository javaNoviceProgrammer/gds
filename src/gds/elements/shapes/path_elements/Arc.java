package gds.elements.shapes.path_elements;

import gds.elements.positioning.Position;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;

public class Arc extends AbstractPathElement {

	/**
	 * gdspy: arc(radius, initial_angle, final_angle, number_of_points=0.01, max_points=199, final_width=None, final_distance=None, layer=0, datatype=0)
	 */
	
	public double radius_um ;
	String finalWidth_um ;
	public double initialAngleDegree, initialAngleRad, finalAngleDegree, finalAngleRad  ;
	Position vec, center ;
	
	// This one is WITH the linear taper
	public Arc(
			@ParamName(name="Radius of curvature (um)") double radius_um,
			@ParamName(name="ARC to right? ") boolean toRight,
			@ParamName(name="Angle (degree)") double angleDegree,
			@ParamName(name="Final width (um) [Taper]") double finalWidth_um
			){
		numElements++ ;
		this.radius_um = radius_um ;
		if(numElements != 1) {port1 = elementPorts.get("last.port2").connect() ; }
		if(toRight){
			initialAngleDegree = port1.connect().getAngleDegree() ;
			initialAngleRad = initialAngleDegree * Math.PI/180 ;
			finalAngleDegree = initialAngleDegree - angleDegree ;
			finalAngleRad = finalAngleDegree * Math.PI/180 ;
			vec = port1.getEdgeVec().resize(radius_um) ;
			center = port1.getPosition().translateXY(vec) ;
			port2 = port1.rotate(center, -angleDegree).connect() ; // don't forget to apply connect function
		}
		else{
			initialAngleDegree = port1.getAngleDegree() ;
			initialAngleRad = initialAngleDegree * Math.PI/180 ;
			finalAngleDegree = initialAngleDegree + angleDegree ;
			finalAngleRad = finalAngleDegree * Math.PI/180 ;
			vec = port1.connect().getEdgeVec().resize(radius_um) ;
			center = port1.getPosition().translateXY(vec) ;
			port2 = port1.rotate(center, angleDegree).connect() ; // don't forget to apply connect function
		}
		this.finalWidth_um = finalWidth_um + "" ;
		updateLastElement() ;
	}
	
	// This one is WITHOUT the linear taper
	public Arc(
			@ParamName(name="Radius (um)") double radius_um,
			@ParamName(name="ARC to right? ") boolean toRight,
			@ParamName(name="Angle (degree)") double angleDegree
			){
		numElements++ ;
		this.radius_um = radius_um ;
		if(numElements != 1) {port1 = elementPorts.get("last.port2").connect() ; }
		if(toRight){
			initialAngleDegree = port1.connect().getAngleDegree() ;
			initialAngleRad = initialAngleDegree * Math.PI/180 ;
			finalAngleDegree = initialAngleDegree - angleDegree ;
			finalAngleRad = finalAngleDegree * Math.PI/180 ;
			vec = port1.getEdgeVec().resize(radius_um) ;
			center = port1.getPosition().translateXY(vec) ;
			port2 = port1.rotate(center, -angleDegree).connect() ; // don't forget to apply connect function
		}
		else{
			initialAngleDegree = port1.getAngleDegree() ;
			initialAngleRad = initialAngleDegree * Math.PI/180 ;
			finalAngleDegree = initialAngleDegree + angleDegree ;
			finalAngleRad = finalAngleDegree * Math.PI/180 ;
			vec = port1.connect().getEdgeVec().resize(radius_um) ;
			center = port1.getPosition().translateXY(vec) ;
			port2 = port1.rotate(center, angleDegree).connect() ; // don't forget to apply connect function
		}
		this.finalWidth_um = "None" ;
		updateLastElement() ;
	}
	
	private void updateLastElement(){
		elementPorts.put("last.port1", port1) ;
		elementPorts.put("last.port2", port2) ;
	}

	//******* generating the python code *************
	@Override
	public String[] getPythonCode(String pathName, AbstractLayerMap layerMap) {
		String st0 = "### adding an ARC" ;
		String[] args = {st0} ;
		int layerNumber = layerMap.getLayerNumber() ;
		int dataType = layerMap.getDataType() ;
		String st1 = pathName + ".arc(" + radius_um + "," + initialAngleRad + "," + finalAngleRad + "," +  "number_of_points=3000" + "," +  "max_points=5000" + "," +
											"final_width=" + finalWidth_um + "," + "final_distance=None" + "," +  "layer=" + layerNumber + "," + "datatype=" + dataType + ")" ;
		args = MoreMath.Arrays.concat(args, new String[] {st1}) ;
		return args;
	}
	
	@Override
	public String[] getPythonCode_no_header(String pathName, AbstractLayerMap layerMap) {
		String[] args = new String[0] ;
		int layerNumber = layerMap.getLayerNumber() ;
		int dataType = layerMap.getDataType() ;
		String st1 = pathName + ".arc(" + radius_um + "," + initialAngleRad + "," + finalAngleRad + "," +  "number_of_points=3000" + "," +  "max_points=5000" + "," +
											"final_width=" + finalWidth_um + "," + "final_distance=None" + "," +  "layer=" + layerNumber + "," + "datatype=" + dataType + ")" ;
		args = MoreMath.Arrays.concat(args, new String[] {st1}) ;
		return args;
	}

	@Override
	public String getElementName() {
		return "Arc" ;
	}
	//***********************************************

}
