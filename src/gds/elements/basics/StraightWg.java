package gds.elements.basics;

import gds.elements.AbstractElement;
import gds.elements.positioning.object_ports.TwoPortObject;
import gds.elements.positioning.Port;
import gds.elements.positioning.Position;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;

public class StraightWg extends AbstractElement {
	
	/** 
	 * we define the straight waveguides as Rectangle in the layout GDS file 
	 */

	double width_um ; // for AIM width is set to 400nm (0.4um)
	double length_um ; // length of the waveguide in micron
	double angleDegree, angleRad ; // this is the orientation angle with respect to the start edge
	AbstractLayerMap[] layerMap ;
	Position P1, P2 ; // coordinates of ports of the waveguide
	Position V1, V2, V3, V4 ; // defining the vertices of the rectangle (bottom and upper, left and right)
	public Port port1, port2 ;
	Port objectPort ;
	String portNumber ;
	Position hVec, vVec ;
	
	public StraightWg(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Layer Map") AbstractLayerMap[] layerMap,
			@ParamName(name="Port Number [portX]", default_="port1") String portNumber,
			@ParamName(name="Choose Port") Port objectPort,
			@ParamName(name="Length of the waveguide (um)") Entry length_um
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.portNumber = portNumber ;
		this.objectPort = objectPort ;
		this.length_um = length_um.getValue() ;
		this.width_um = objectPort.getWidthMicron() ;
		
		setPorts() ;
		setVertices() ;
		saveProperties() ;
		
		allElements.put(objectName, this) ;
	}
	
	@Override
	public void setPorts(){
		Position zero = new Position(0,0) ;
		TwoPortObject temp = new TwoPortObject(portNumber, objectPort, new Position[] {zero}) ;
		Position vec12 = temp.getPort1().connect().getNormalVec().resize(length_um) ;
		TwoPortObject wg = new TwoPortObject(portNumber, objectPort, new Position[] {vec12}) ;
		port1 = wg.getPort1() ;
		port2 = wg.getPort2() ;
		hVec = port1.connect().getNormalVec().resize(length_um) ;
		vVec = port1.connect().getEdgeVec().resize(width_um/2) ;
		angleDegree = port1.connect().getNormalDegree() ;
		angleRad = angleDegree * Math.PI/180 ;
		P1 = port1.getPosition() ;
		P2 = port2.getPosition() ;

		objectPorts.put(objectName+".port1", port1) ;
		objectPorts.put(objectName+".port2", port2) ;
		
	}

	private void setVertices(){
		V1 = P1.translateXY(vVec.scale(-1)) ;
		V2 = P2.translateXY(vVec.scale(-1)) ;
		V3 = P2.translateXY(vVec) ;
		V4 = P1.translateXY(vVec) ;
	}
	
	//****************************************************************************
	@Override
	public void saveProperties(){
		objectProperties.put(objectName+".length_um", length_um) ;
		objectProperties.put(objectName+".width_um", width_um) ;
		objectProperties.put(objectName+".angle_degree", angleDegree) ;
		objectProperties.put(objectName+".angle_rad", angleRad) ;
		
		allElements.put(objectName, this) ;
	}
	//****************************************************************************
	public Position center(){
		double xCenter = (P1.getX()+P2.getX())/2 ;
		double yCenter = (P1.getY()+P2.getY())/2 ;
		return new Position(xCenter, yCenter) ;
	}
	
	// generating the necessary python code***************
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName){
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##       Adding a STRAIGHT WAVEGUIDE        ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args = {st0, st1, st2} ;
		String point1 = V1.rotate(P1, -angleDegree).getString() ;
		String point2 = V3.rotate(P1, -angleDegree).getString() ; 
		int n = layerMap.length ;
		for(int i=0; i<n; i++){
			int layerNumber = layerMap[i].getLayerNumber() ;
			int dataType = layerMap[i].getDataType() ;
			String title = "### adding a "+ layerMap[i].getLayerName() +" layer" ;
			String st3 = objectName + " = gdspy.Rectangle(" + point1 + "," + point2 + "," + "layer=" + layerNumber + "," + "datatype=" + dataType + ")"  ;
			String st4 = objectName + ".rotate(" + angleRad + "," + P1.getString() + ")" ;
			String st5 = topCellName + ".add(" + objectName + ")" ;
			args = MoreMath.Arrays.concat(args, new String[] {title, st3, st4, st5}) ;
		}
		return args ;
	}
	
	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName){
		String[] args = new String[0] ;
		String point1 = V1.rotate(P1, -angleDegree).getString() ;
		String point2 = V3.rotate(P1, -angleDegree).getString() ; 
		int n = layerMap.length ;
		for(int i=0; i<n; i++){
			int layerNumber = layerMap[i].getLayerNumber() ;
			int dataType = layerMap[i].getDataType() ;
			String title = "### adding a "+ layerMap[i].getLayerName() +" layer" ;
			String st3 = objectName + " = gdspy.Rectangle(" + point1 + "," + point2 + "," + "layer=" + layerNumber + "," + "datatype=" + dataType + ")"  ;
			String st4 = objectName + ".rotate(" + angleRad + "," + P1.getString() + ")" ;
			String st5 = topCellName + ".add(" + objectName + ")" ;
			args = MoreMath.Arrays.concat(args, new String[] {title, st3, st4, st5}) ;
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
		Port port_translated = port1.translateXY(dX, dY) ;
		AbstractElement wg_translated = new StraightWg(objectName, layerMap, portNumber, port_translated, new Entry(length_um)) ;
		return wg_translated;
	}

	@Override
	public AbstractElement translateXY(String newName, double dX, double dY) {
		Port port_translated = port1.translateXY(dX, dY) ;
		AbstractElement wg_translated = new StraightWg(newName, layerMap, portNumber, port_translated, new Entry(length_um)) ;
		return wg_translated;
	}
	

}
