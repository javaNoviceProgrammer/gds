package gds.elements.basics;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;
import gds.elements.AbstractElement;
import gds.elements.positioning.Port;
import gds.elements.positioning.Position;
import gds.elements.positioning.object_ports.FourPortObject;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;

public class DirectionalCoupler extends AbstractElement {

	/**
	 * need four S-bends and two straight waveguides
	 */
	
	AbstractLayerMap[] layerMap ;
	Position P1, P2, P3, P4 ; // position of the ports
	Position Q1, Q2, Q3, Q4 ; // start and end position of the straight waveguides
	double length_um, width_um, gap_nm, gap_um, horizontalOffset_um, verticalOffset_um;
	double lx_um, ly_um ;
	Port objectPort ;
	String portNumber ;
	public Port port1, port2, port3, port4 ;
	Sbend bend1, bend2, bend3, bend4 ;
	StraightWg wg1, wg2 ;
	Position hVec, vVec ;
	
	public DirectionalCoupler(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Layer Map") AbstractLayerMap[] layerMap,
			@ParamName(name="Port Number [portX]", default_="port1") String portNumber,
			@ParamName(name="Choose Port") Port objectPort, 
			@ParamName(name="Length of the coupler (um)") Entry length_um,
			@ParamName(name="Gap size (nm)") Entry gap_nm,
			@ParamName(name="Horizontal offset of each bend (um)") Entry horizontalOffset_um,
			@ParamName(name="Vertical offset of each bend (um)") Entry verticalOffset_um
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.portNumber = portNumber ;
		this.objectPort = objectPort ;
		this.width_um = objectPort.getWidthMicron() ;
		this.length_um = length_um.getValue() ;
		this.gap_nm = gap_nm.getValue() ;
		this.gap_um = gap_nm.getValue()/1000 ;
		this.horizontalOffset_um = horizontalOffset_um.getValue() ;
		this.verticalOffset_um = verticalOffset_um.getValue() ;
		lx_um = 2*horizontalOffset_um.getValue() + length_um.getValue() ;
		ly_um = 2*verticalOffset_um.getValue() + width_um + gap_um ;
		
		setPorts() ;
		createObject() ;
		saveProperties() ;
	}
	
	@Override
	public void setPorts(){
		Position zero = new Position(0,0) ;
		FourPortObject temp = new FourPortObject(portNumber, objectPort, new Position[] {zero, zero, zero}) ;
		Position vec12 = temp.getPort1().connect().getNormalVec().resize(lx_um) ;
		Position vec14 = temp.getPort1().connect().getEdgeVec().resize(ly_um) ;
		Position vec23 = temp.getPort1().connect().getEdgeVec().resize(ly_um) ;
		FourPortObject dc = new FourPortObject(portNumber, objectPort, new Position[] {vec12, vec14, vec23}) ;
		port1 = dc.getPort1() ;
		port2 = dc.getPort2() ;
		port3 = dc.getPort3() ;
		port4 = dc.getPort4() ;
		
		objectPorts.put(objectName+".port1", port1) ;
		objectPorts.put(objectName+".port2", port2) ;
		objectPorts.put(objectName+".port3", port3) ;
		objectPorts.put(objectName+".port4", port4) ;
	}
	
	private void createObject(){
		// creating four Sbends
		bend1 = new Sbend(objectName+"_bend1", layerMap, port1, new Entry(horizontalOffset_um), new Entry(verticalOffset_um)) ;
//		bend2 = new Sbend(objectName+"_bend2", layerMap, port2, new Entry(horizontalOffset_um),  new Entry(verticalOffset_um)) ;
//		bend3 = new Sbend(objectName+"_bend3", layerMap, port3, new Entry(horizontalOffset_um), new Entry(-verticalOffset_um)) ;
		bend4 = new Sbend(objectName+"_bend4", layerMap, port4, new Entry(horizontalOffset_um), new Entry(-verticalOffset_um)) ;
		// creating two straight waveguides
		wg1 = new StraightWg(objectName+"_wg1", layerMap, "port1", bend1.port2.connect(), new Entry(length_um)) ; // lower waveguide
		wg2 = new StraightWg(objectName+"_wg2", layerMap, "port1", bend4.port2.connect(), new Entry(length_um)) ; // upper waveguide
		
		bend2 = new Sbend(objectName+"_bend2", layerMap, wg1.port2.connect(), new Entry(horizontalOffset_um),  new Entry(-verticalOffset_um)) ;
		bend3 = new Sbend(objectName+"_bend3", layerMap, wg2.port2.connect(), new Entry(horizontalOffset_um), new Entry(verticalOffset_um)) ;
	}
	
	@Override
	public void saveProperties(){
		objectProperties.put(objectName+".width_um", width_um) ;
		objectProperties.put(objectName+".length_um", length_um) ;
		objectProperties.put(objectName+".gap_um", gap_um) ;
		objectProperties.put(objectName+".gap_nm", gap_nm) ;
		objectProperties.put(objectName+".offset.horizontal_um", horizontalOffset_um) ;
		objectProperties.put(objectName+".offset.vertical_um", verticalOffset_um) ;
		
	}
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##     Adding a DIRECTIONAL COUPLER         ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args = {st0, st1, st2} ;
		args = MoreMath.Arrays.concat(args, bend1.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, bend2.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, bend3.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, bend4.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, wg1.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, wg2.getPythonCode_no_header(fileName, topCellName)) ;
		return args;
	}
	
	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args = new String[0] ;
		args = MoreMath.Arrays.concat(args, bend1.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, bend2.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, bend3.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, bend4.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, wg1.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, wg2.getPythonCode_no_header(fileName, topCellName)) ;
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
		AbstractElement DC_translated = new DirectionalCoupler(objectName, layerMap, portNumber, port_translated, new Entry(length_um), new Entry(gap_nm), new Entry(horizontalOffset_um), new Entry(verticalOffset_um)) ;
		return DC_translated ;
	}

}
