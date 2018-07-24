package gds.elements.basics;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;
import gds.elements.AbstractElement;
import gds.elements.positioning.Port;
import gds.elements.positioning.Position;
import gds.elements.positioning.object_ports.FourPortObject;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;

public class MZI2x2 extends AbstractElement {

	AbstractLayerMap[] layerMap ;
	double width_um, dc_length_um, dc_gap_nm, dc_gap_um, hOffset_um, vOffset_um, arm_length_um ;
	double lx_um, ly_um ;
	Port objectPort ;
	String portNumber ;
	public Port port1, port2, port3, port4 ;
	DirectionalCoupler DC1, DC2 ;
	StraightWg wg1, wg2 ;
	
	public MZI2x2(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Layer Map") AbstractLayerMap[] layerMap,
			@ParamName(name="Port Number [portX]") String portNumber,
			@ParamName(name="Choose Port") Port objectPort,
			@ParamName(name="Length of directional coupler (um)") Entry dc_length_um,
			@ParamName(name="Gap of the directional coupler (nm)") Entry dc_gap_nm,
			@ParamName(name="Horizontal offset (um)") Entry hOffset_um,
			@ParamName(name="Vertical offset (um)") Entry vOffset_um,
			@ParamName(name="Length of the arm of MZI (um)") Entry arm_length_um
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.portNumber = portNumber ;
		this.objectPort = objectPort ;
		this.width_um = objectPort.getWidthMicron() ;
		this.dc_length_um = dc_length_um.getValue() ;
		this.dc_gap_nm = dc_gap_nm.getValue() ;
		dc_gap_um = dc_gap_nm.getValue()/1000 ;
		this.hOffset_um = hOffset_um.getValue() ;
		this.vOffset_um = vOffset_um.getValue() ;
		this.arm_length_um = arm_length_um.getValue() ;
		lx_um = 2*hOffset_um.getValue() + dc_length_um.getValue() + arm_length_um.getValue() + 2*hOffset_um.getValue() + dc_length_um.getValue() ;
		ly_um = 2*vOffset_um.getValue() + width_um + dc_gap_um ;
		
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
		FourPortObject mzi = new FourPortObject(portNumber, objectPort, new Position[] {vec12, vec14, vec23}) ;
		port1 = mzi.getPort1() ;
		port2 = mzi.getPort2() ;
		port3 = mzi.getPort3() ;
		port4 = mzi.getPort4() ;
		
		objectPorts.put(objectName+".port1", port1) ;
		objectPorts.put(objectName+".port2", port2) ;
		objectPorts.put(objectName+".port3", port3) ;
		objectPorts.put(objectName+".port4", port4) ;
	}
	
	private void createObject(){
		// creating directional couplers
		DC1 = new DirectionalCoupler(objectName+"_dc1", layerMap, "port1", port1, new Entry(dc_length_um), new Entry(dc_gap_nm), new Entry(hOffset_um), new Entry(vOffset_um)) ;
		DC2 = new DirectionalCoupler(objectName+"_dc2", layerMap, "port2", port2, new Entry(dc_length_um), new Entry(dc_gap_nm), new Entry(hOffset_um), new Entry(vOffset_um)) ;
		// creating two straight waveguies for the arms
		wg1 = new StraightWg(objectName+"_wg1", layerMap, "port1", DC1.port2.connect(), new Entry(arm_length_um)) ; // lower arm
		wg2 = new StraightWg(objectName+"_wg2", layerMap, "port1", DC1.port3.connect(), new Entry(arm_length_um)) ; // upper arm
	}
	
	@Override
	public void saveProperties(){
		objectProperties.put(objectName+".width_um", width_um) ;
		objectProperties.put(objectName+".dc.length_um", dc_length_um) ;
		objectProperties.put(objectName+".dc.gap_um", dc_gap_um) ;
		objectProperties.put(objectName+".dc.gap_nm", dc_gap_nm) ;
		objectProperties.put(objectName+".offset.horizontal_um", hOffset_um) ;
		objectProperties.put(objectName+".offset.vertical_um", vOffset_um) ;
		objectProperties.put(objectName+".arm.length_um", arm_length_um) ;
		
		allElements.put(objectName, this) ;
	}
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##        Adding a MACH-ZEHNDER 2x2         ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args = {st0, st1, st2} ;
		args = MoreMath.Arrays.concat(args, DC1.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, wg1.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, wg2.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, DC2.getPythonCode_no_header(fileName, topCellName)) ;
		return args ;
	}

	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args = DC1.getPythonCode_no_header(fileName, topCellName) ;
		args = MoreMath.Arrays.concat(args, wg1.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, wg2.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, DC2.getPythonCode_no_header(fileName, topCellName)) ;
		return args ;
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
		AbstractElement mzi_translated = new MZI2x2(objectName, layerMap, portNumber, port_translated, new Entry(dc_length_um), new Entry(dc_gap_nm), new Entry(hOffset_um), new Entry(vOffset_um), new Entry(arm_length_um)) ;
		return mzi_translated;
	}

	@Override
	public AbstractElement translateXY(String newName, double dX, double dY) {
		Port port_translated = objectPort.translateXY(dX, dY) ;
		AbstractElement mzi_translated = new MZI2x2(newName, layerMap, portNumber, port_translated, new Entry(dc_length_um), new Entry(dc_gap_nm), new Entry(hOffset_um), new Entry(vOffset_um), new Entry(arm_length_um)) ;
		return mzi_translated;
	}
	
}
