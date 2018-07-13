package gds.elements.basics;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;
import gds.elements.AbstractElement;
import gds.elements.positioning.Port;
import gds.elements.positioning.Position;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;

public class RingWg extends AbstractElement {

	double inputGap_nm, inputGap_um, radius_um, wg1Width_um, ringWidth_um, length_um ;
	AbstractLayerMap[] layerMap ;
	StraightWg wg1 ;
	Ring ring ;
	double lx_um, ly_um ;
	public Port port1, port2 ;
	Port objectPort ;
	String portNumber ;
	Position P1, P2;
	Position V1, V2, V3, V4 ;
	Position center ;
	
	public RingWg(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Waveguide Layer") AbstractLayerMap[] layerMap,
			@ParamName(name="Port Number [portX]", default_="port1") String portNumber,
			@ParamName(name="Choose Port") Port objectPort,
			@ParamName(name="Width of the Ring (um)") Entry ringWidth_um,
			@ParamName(name="Radius of the ring (um)") Entry radius_um,
			@ParamName(name="Input gap (nm) [+/-]") Entry inputGap_nm
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.portNumber = portNumber ;
		this.objectPort = objectPort ;
		this.wg1Width_um = objectPort.getWidthMicron() ;
		this.ringWidth_um = ringWidth_um.getValue() ;
		this.radius_um = radius_um.getValue() ;
		this.inputGap_nm = inputGap_nm.getValue() ;
		this.inputGap_um = inputGap_nm.getValue()/1000 ; // sign of the input gap determines the position of the ring with respect to the waveguide
		length_um = 2*radius_um.getValue() + ringWidth_um.getValue() ;

		createObject() ;
		setPorts() ;
		saveProperties() ;
		setCorners() ;
	}
	
	@Override
	public void setPorts(){
		objectPorts.put(objectName+".port1", port1) ;
		objectPorts.put(objectName+".port2", port2) ;
		objectPorts.put(objectName+".ringcenter", new Port(center, 0, 0)) ;
	}
	
	private void createObject(){
		// creating input waveguide
		String wg1Name = objectName + "_" + "wg1" ;
		wg1 = new StraightWg(wg1Name, layerMap, portNumber, objectPort, new Entry(length_um)) ;
		// calculating the ports and center of the ring
		this.port1 = wg1.port1 ;
		this.port2 = wg1.port2 ;
		P1 = port1.getPosition() ;
		P2 = port2.getPosition() ;
		Position Pmiddle = new Position((P1.getX()+P2.getX())/2, (P1.getY()+P2.getY())/2) ;
		Position normalVec = port2.getEdgeVec().scale(inputGap_um + MoreMath.Functions.sign(inputGap_um)*(wg1Width_um/2 + ringWidth_um/2 + radius_um)) ;
		center = Pmiddle.translateXY(normalVec) ;
		// creating the ring
		String ringName = objectName + "_" + "ring" ;
		ring = new Ring(ringName, layerMap, new Ring.Center(center), new Entry(ringWidth_um), new Entry(radius_um)) ;
	}
	
	@Override
	public void saveProperties(){
		objectProperties.put(objectName+".wg.width_um", wg1Width_um) ;
		objectProperties.put(objectName+".ring.width_um", ringWidth_um) ;
		objectProperties.put(objectName+".ring.radius_um", radius_um) ;
		objectProperties.put(objectName+".gap_nm", inputGap_um*1000) ;
		objectProperties.put(objectName+".gap_um", inputGap_um) ;
		// now adding port 1
		objectProperties.put(objectName+".port1.angle_degree", port1.getAngleDegree()) ;
		objectProperties.put(objectName+".port1.angle_rad", port1.getAngleRad()) ;
		objectProperties.put(objectName+".port1.normal_degree", port1.getNormalDegree()) ;
		objectProperties.put(objectName+".port1.normal_rad", port1.getNormalRad()) ;
		// now adding port 2
		objectProperties.put(objectName+".port2.angle_degree", port2.getAngleDegree()) ;
		objectProperties.put(objectName+".port2.angle_rad", port2.getNormalRad()) ;
		objectProperties.put(objectName+".port2.normal_degree", port2.getNormalDegree()) ;
		objectProperties.put(objectName+".port2.normal_rad", port2.getNormalRad()) ;
	}
	
	private void setCorners(){
		V1 = wg1.V1 ;
		V2 = wg1.V2 ;
		V3 = wg1.V3 ;
		V4 = wg1.V4 ;
	}
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##           Adding a RING-WG               ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] st3 = wg1.getPythonCode_no_header(fileName, topCellName) ;
		String[] st4 = ring.getPythonCode_no_header(fileName, topCellName) ;
		// now we need to append all the strings together
		String[] args = {st0, st1, st2} ;
		args = MoreMath.Arrays.concat(args, st3) ;
		args = MoreMath.Arrays.concat(args, st4) ;
		return args;
	}
	
	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] st3 = wg1.getPythonCode_no_header(fileName, topCellName) ;
		String[] st4 = ring.getPythonCode_no_header(fileName, topCellName) ;
		// now we need to append all the strings together
		String[] args = new String[0] ;
		args = MoreMath.Arrays.concat(args, st3) ;
		args = MoreMath.Arrays.concat(args, st4) ;
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
		Port port_translated = objectPort.translateXY(dX, dY) ;
		AbstractElement RingWg_translated = new RingWg(objectName, layerMap, portNumber, port_translated, new Entry(ringWidth_um), new Entry(radius_um), new Entry(inputGap_nm)) ;
		return RingWg_translated ;
	}
	


}
