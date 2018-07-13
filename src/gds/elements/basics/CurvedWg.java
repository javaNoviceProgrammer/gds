package gds.elements.basics;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;
import gds.elements.AbstractElement;
import gds.elements.positioning.Port;
import gds.elements.positioning.Position;
import gds.elements.positioning.object_ports.TwoPortObject;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;

public class CurvedWg extends AbstractElement {

	/** we define the straight waveguides as Rectangle in the layout GDS file
	 *  This means that we use 'gdspy.Path' class in python code
	 */

	double width_um, radius_um ; 
	double Rin, Rout ;
	double angleDegree, angleRad, startAngleDegree, startAngleRad, finalAngleRad, finalAngleDegree ;
	AbstractLayerMap[] layerMap ;
	public Port port1, port2 ;
	Port objectPort ;
	String portNumber ;
	Position P1, P2 ; // coordinates of ports
	Position V1, V2, V3, V4 ; // defining the vertices of the rectangle (bottom and upper, left and right)
	public Position center ;
	double lx_um, ly_um ;
	boolean toRight ;
	
	public CurvedWg(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Layer Map") AbstractLayerMap[] layerMap,
			@ParamName(name="Port Number [portX]", default_="port1") String portNumber,
			@ParamName(name="Choose Port") Port objectPort,
			@ParamName(name="Curve to RIGHT?") boolean toRight,
			@ParamName(name="Radius (um)") Entry radius_um,
			@ParamName(name="Angle (degree)") Entry angleDegree // could be positive or negative
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.portNumber = portNumber ;
		this.objectPort = objectPort ;
		this.width_um = objectPort.getWidthMicron() ;
		this.radius_um = radius_um.getValue() ; // from center to the middle of the ring
		Rin = radius_um.getValue() - width_um/2 ;
		Rout = radius_um.getValue() + width_um/2 ;
		this.angleDegree = angleDegree.getValue() ; // could be positive or negative
		angleRad = angleDegree.getValue() * Math.PI/180 ;
		this.toRight = toRight ;
		setParams() ;
		setPorts() ;
		setVertices() ;
		saveProperties();
	}
	
	
	public void setParams(){
		Position zero = new Position(0,0) ;
		TwoPortObject temp = new TwoPortObject(portNumber, objectPort, new Position[] {zero}) ;
		if(toRight){
			angleDegree = -1 * angleDegree ;
			angleRad = angleDegree * Math.PI/180 ;
			startAngleDegree = temp.getPort1().getAngleDegree()-180 ;
			startAngleRad = startAngleDegree * Math.PI/180 ;
			finalAngleDegree = startAngleDegree + angleDegree ;
			finalAngleRad = finalAngleDegree * Math.PI/180 ;
		}
		else{
			startAngleDegree = temp.getPort1().getAngleDegree() ;
			startAngleRad = startAngleDegree * Math.PI/180 ;
			finalAngleDegree = startAngleDegree + angleDegree ;
			finalAngleRad = finalAngleDegree * Math.PI/180 ;
		}

	}
	
	
	@Override
	public void setPorts(){
		lx_um = radius_um * (Math.cos(finalAngleRad)-Math.cos(startAngleRad)) ;
		ly_um = radius_um * (Math.sin(finalAngleRad)-Math.sin(startAngleRad)) ;
		Position vec12 = new Position(lx_um, ly_um) ;
		TwoPortObject wg = new TwoPortObject(portNumber, objectPort, new Position[] {vec12}) ;
		port1 = wg.getPort1() ;
		port2 = wg.getPort2() ;
		center = port1.translateXY(-radius_um*Math.cos(startAngleRad), -radius_um*Math.sin(startAngleRad)).getPosition() ;
		
		P1 = port1.getPosition() ;
		P2 = port2.getPosition() ;
		
		objectPorts.put(objectName+".port1", port1) ;
		objectPorts.put(objectName+".port2", port2) ;
	}
	
	
	//****************************************************************************
	@Override
	public void saveProperties(){
		objectProperties.put(objectName+".width_um", width_um) ;
		objectProperties.put(objectName+".radius_um", radius_um) ;
		objectProperties.put(objectName+".angle_degree", Math.abs(angleDegree)) ;
		objectProperties.put(objectName+".angle_rad", Math.abs(angleRad)) ;
		objectProperties.put(objectName+".startAngle_degree", startAngleDegree) ;
		objectProperties.put(objectName+".startAngle_rad", startAngleRad) ;
		objectProperties.put(objectName+".finalAngle_degree", finalAngleDegree) ;
		objectProperties.put(objectName+".finalAngle_rad", finalAngleRad) ;
		
		objectProperties.put(objectName+".port1.angle_degree", port1.getAngleDegree()) ;
		objectProperties.put(objectName+".port1.angle_rad", port1.getAngleRad()) ;
		objectProperties.put(objectName+".port1.normal_degree", port1.getNormalDegree()) ;
		objectProperties.put(objectName+".port1.normal_rad", port1.getNormalRad()) ;
		
		objectProperties.put(objectName+".port2.angle_degree", port2.getAngleDegree()) ;
		objectProperties.put(objectName+".port2.angle_rad", port2.getAngleRad()) ;
		objectProperties.put(objectName+".port2.normal_degree", port2.getNormalDegree()) ;
		objectProperties.put(objectName+".port2.normal_rad", port2.getNormalRad()) ;
		
	}
	//****************************************************************************
	private void setVertices(){
		Position cp1 = new Position(P1.getX()-center.getX(), P1.getY()-center.getY()) ;
		Position P1V1 = cp1.resize(width_um/2) ;
		V1 = P1.translateXY(P1V1) ;
		Position P1V4 = cp1.resize(-width_um/2) ;
		V4 = P1.translateXY(P1V4) ;
		V2 = V1.rotate(center, angleDegree) ;
		V3 = V4.rotate(center, angleDegree) ;
	}
	
	// generating the necessary python code***************
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName){
		String st01 = "## ---------------------------------------- ##" ;
		String st02 = "##       Adding a CURVED WAVEGUIDE          ##" ;
		String st03 = "## ---------------------------------------- ##" ;
		String[] args = {st01, st02, st03} ;
		int n = layerMap.length ;
		for(int i=0; i<n; i++){
			int layerNumber = layerMap[i].getLayerNumber() ;
			int dataType = layerMap[i].getDataType() ;
			String point1 = "(" + center.getX() + "," + center.getY() + ")" ;
			String title = "### adding a "+ layerMap[i].getLayerName() +" layer" ;
			String st2 = objectName + " = gdspy.Round(" + point1 + "," + Rout + "," + "inner_radius=" + Rin + "," + "initial_angle=" + startAngleRad + "," +
						"final_angle=" + finalAngleRad + "," + "number_of_points=3000" + "," + "max_points=5000" + "," + "layer=" + layerNumber + "," + "datatype=" + dataType + ")"  ;

			String st3 = topCellName + ".add(" + objectName + ")" ;
			args = MoreMath.Arrays.concat(args, new String[] {title, st2, st3}) ;
		}
		return args ;
	}
	
	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName){
		String[] args = new String[0] ;
		int n = layerMap.length ;
		for(int i=0; i<n; i++){
			int layerNumber = layerMap[i].getLayerNumber() ;
			int dataType = layerMap[i].getDataType() ;
			String point1 = "(" + center.getX() + "," + center.getY() + ")" ;
			String title = "### adding a "+ layerMap[i].getLayerName() +" layer" ;
			String st2 = objectName + " = gdspy.Round(" + point1 + "," + Rout + "," + "inner_radius=" + Rin + "," + "initial_angle=" + startAngleRad + "," +
						"final_angle=" + finalAngleRad + "," + "number_of_points=3000" + "," + "max_points=5000" + "," + "layer=" + layerNumber + "," + "datatype=" + dataType + ")"  ;

			String st3 = topCellName + ".add(" + objectName + ")" ;
			args = MoreMath.Arrays.concat(args, new String[] {title, st2, st3}) ;
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
		Port port1_translated = port1.translateXY(dX, dY) ;
		AbstractElement Cwg_translated = new CurvedWg(objectName, layerMap, portNumber, port1_translated, toRight, new Entry(radius_um), new Entry(angleDegree)) ;
		return Cwg_translated;
	}
	
	
}
