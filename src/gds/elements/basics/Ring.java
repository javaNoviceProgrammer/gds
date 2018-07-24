package gds.elements.basics;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;
import gds.elements.AbstractElement;
import gds.elements.positioning.Position;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;

public class Ring extends AbstractElement {

	/**
	 * class gdspy.Round(center, radius, inner_radius=0, initial_angle=0, fnal_angle=0, number_of_points=0.01, max_points=199, layer=0, datatype=0)
	 */

	double width_um ;
	double radius_um ;
	double Rin, Rout ;
	double angleDegree, angleRad ; // this is the orientation angle with respect to the start edge
	double startAngleDegree, startAngleRad ; // this is between 0 and pi. The sign of this angle determines where the center of the bend lies.
	AbstractLayerMap[] layerMap ;
	
	public Position center ;
	Center centerPosition ;
	
	public Ring(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Waveguide Layer") AbstractLayerMap[] layerMap,
			@ParamName(name="Choose Center") Center centerPosition,
			@ParamName(name="Width of the ring (um)") Entry width_um,
			@ParamName(name="Radius (um)") Entry radius_um // from center to the middle of the ring
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.startAngleDegree = 0 ;
		startAngleRad = startAngleDegree * Math.PI/180 ;
		this.width_um = width_um.getValue() ;
		this.radius_um = radius_um.getValue() ; // from center to the middle of the ring
		Rin = radius_um.getValue() - width_um.getValue()/2 ;
		Rout = radius_um.getValue() + width_um.getValue()/2 ;
		this.angleDegree = 360 ; // could be positive or negative
		angleRad = angleDegree * Math.PI/180 ;
		this.centerPosition = centerPosition ;
		setPorts() ;
		saveProperties() ;
	}
	
	public Ring(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Waveguide Layer") AbstractLayerMap[] layerMap,
			@ParamName(name="Choose Center") Center centerPosition,
			@ParamName(name="Start angle (degree)") Entry startAgnle_degree,
			@ParamName(name="Width of the ring (um)") Entry width_um,
			@ParamName(name="Radius (um)") Entry radius_um, // from center to the middle of the ring
			@ParamName(name="Span Angle (degree)") Entry span_angle_degree // from center to the middle of the ring
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.startAngleDegree = 0 ;
		startAngleRad = startAgnle_degree.getValue() * Math.PI/180 ;
		this.width_um = width_um.getValue() ;
		this.radius_um = radius_um.getValue() ; // from center to the middle of the ring
		Rin = radius_um.getValue() - width_um.getValue()/2 ;
		Rout = radius_um.getValue() + width_um.getValue()/2 ;
		this.angleDegree = span_angle_degree.getValue() ; // could be positive or negative
		angleRad = angleDegree * Math.PI/180D ;
		this.centerPosition = centerPosition ;
		setPorts() ;
		saveProperties() ;
	}
	
	@Override
	public void setPorts(){
		center = centerPosition.getCenter() ;
	}
	
	@Override
	public void saveProperties(){
		objectProperties.put(objectName+".center.x", center.getX()) ;
		objectProperties.put(objectName+".center.y", center.getY()) ;
		objectProperties.put(objectName+".radius_um", radius_um) ;
		objectProperties.put(objectName+".width_um", width_um) ;
		objectProperties.put(objectName+".innerradius_um", Rin) ;
		objectProperties.put(objectName+".outerradius_um", Rout) ;
		
		allElements.put(objectName, this) ;
	}
	
	// generating the necessary python code***************
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName){
		double endAngleRad = startAngleRad + angleRad ;
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##              Adding a RING               ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args = {st0, st1, st2} ;
		int n = layerMap.length ;
		for(int i=0; i<n; i++){
			int layerNumber = layerMap[i].getLayerNumber() ;
			int dataType = layerMap[i].getDataType() ;
			// first creating an object of type Round from gdspy library
			String point1 = "(" + center.getX() + "," + center.getY() + ")" ;
			String title = "### adding a "+ layerMap[i].getLayerName() +" layer" ;
			String st3 = objectName + " = gdspy.Round(" + point1 + "," + Rout + "," + "inner_radius=" + Rin + "," + "initial_angle=" + startAngleRad + "," +
						"final_angle=" + endAngleRad + "," + "number_of_points=2000" + "," + "max_points=5000" + "," + "layer=" + layerNumber + "," + "datatype=" + dataType + ")"  ;
			// then we need to add this object to the cell
			String st4 = topCellName + ".add(" + objectName + ")" ;
			args = MoreMath.Arrays.concat(args, new String[] {title, st3, st4}) ;
		}
		return args ;
	}
	
	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName){
		double endAngleRad = startAngleRad + angleRad ;
		String[] args = new String[0] ;
		int n = layerMap.length ;
		for(int i=0; i<n; i++){
			int layerNumber = layerMap[i].getLayerNumber() ;
			int dataType = layerMap[i].getDataType() ;
			// first creating an object of type Round from gdspy library
			String point1 = "(" + center.getX() + "," + center.getY() + ")" ;
			String title = "### adding a "+ layerMap[i].getLayerName() +" layer" ;
			String st3 = objectName + " = gdspy.Round(" + point1 + "," + Rout + "," + "inner_radius=" + Rin + "," + "initial_angle=" + startAngleRad + "," +
						"final_angle=" + endAngleRad + "," + "number_of_points=2000" + "," + "max_points=5000" + "," + "layer=" + layerNumber + "," + "datatype=" + dataType + ")"  ;
			// then we need to add this object to the cell
			String st4 = topCellName + ".add(" + objectName + ")" ;
			args = MoreMath.Arrays.concat(args, new String[] {title, st3, st4}) ;
		}
		return args ;
	}
	
	@Override
	public void writeToFile(String fileName, String topCellName){
		FileOutput fout = new FileOutput(fileName + ".py","w") ;
		fout.println(getPythonCode(fileName, topCellName));
		fout.close();
	}
	
	@Override
	public void appendToFile(String fileName, String topCellName){
		FileOutput fout = new FileOutput(fileName + ".py","a") ;
		fout.println(getPythonCode(fileName, topCellName));
		fout.close();
	}

	@Override
	public AbstractElement translateXY(double dX, double dY) {
		Center center_translated = centerPosition.translateXY(dX, dY) ;
		AbstractElement ring_translated = new Ring(objectName, layerMap, center_translated, new Entry(width_um), new Entry(radius_um)) ;
		return ring_translated;
	}
	
	@Override
	public AbstractElement translateXY(String newName, double dX, double dY) {
		Center center_translated = centerPosition.translateXY(dX, dY) ;
		AbstractElement ring_translated = new Ring(newName, layerMap, center_translated, new Entry(width_um), new Entry(radius_um)) ;
		return ring_translated;
	}
	
	//************************************ defining and creating the ports ************
	public static class Center{

		Position P ;
		
		public Center(Position P){
			this.P = P ;
		}
		
		public Center(@ParamName(name="Reference to Other Objects") String objectPort,
				  @ParamName(name="Offset X (um)", default_="0") double offsetX_um,
				  @ParamName(name="Offset Y (um)", default_="0") double offsetY_um
				  ){
			this.P = objectPorts.get(objectPort).getPosition().translateXY(offsetX_um, offsetY_um) ;
		}
		
		public Position getCenter() {
			return P;
		}
		
		public Center translateXY(double dx, double dy){
			return new Center(P.translateXY(dx, dy)) ;
		}
		
	}

	//********************************************************
	
}
