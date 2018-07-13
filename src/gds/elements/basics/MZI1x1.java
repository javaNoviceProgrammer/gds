package gds.elements.basics;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;
import gds.elements.AbstractElement;
import gds.elements.positioning.Port;
import gds.elements.positioning.Position;
import gds.elements.positioning.object_ports.TwoPortObject;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;

public class MZI1x1 extends AbstractElement {

	AbstractLayerMap[] layerMap ;
	double width_um, hOffset_um, vOffset_um, length_arm_um, lx_um ;
	public Port port1, port2 ;
	Port objectPort ;
	String portNumber ;
	StraightWg wg1, wg2 ;
	Yjunction Yin, Yout ;
	
	public MZI1x1(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Layer Map") AbstractLayerMap[] layerMap,
			@ParamName(name="Port Number [portX]", default_="port1") String portNumber,
			@ParamName(name="Choose Port") Port objectPort,
			@ParamName(name="Horizontal offset (um)", default_="10") Entry hOffset_um,
			@ParamName(name="Vertical offset (um)", default_="10") Entry vOffset_um,
			@ParamName(name="Length of the Arm") Entry length_arm_um
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.portNumber = portNumber ;
		this.objectPort = objectPort ;
		this.width_um = objectPort.getWidthMicron() ;
		this.hOffset_um = hOffset_um.getValue() ;
		this.vOffset_um = vOffset_um.getValue() ;
		this.length_arm_um = length_arm_um.getValue() ;
		lx_um = 2*hOffset_um.getValue() + length_arm_um.getValue() + 2*5 ;
		
		setPorts() ;
		createObject() ;
		saveProperties() ;
	}
	
	@Override
	public void setPorts(){
		Position zero = new Position(0,0) ;
		TwoPortObject temp = new TwoPortObject(portNumber, objectPort, new Position[] {zero}) ;
		Position vec12 = temp.getPort1().connect().getNormalVec().resize(lx_um) ;
		TwoPortObject mzi = new TwoPortObject(portNumber, objectPort, new Position[] {vec12}) ;
		port1 = mzi.getPort1() ;
		port2 = mzi.getPort2() ;
		objectPorts.put(objectName+".port1", port1) ;
		objectPorts.put(objectName+".port2", port2) ;
	}
	
	private void createObject(){
		// creating input Y-junction
		Yin = new Yjunction(objectName+"_Yin", layerMap, "port1", port1, new Entry(hOffset_um), new Entry(vOffset_um)) ;
		// creating lower arm
		wg2 = new StraightWg(objectName+"_wg2", layerMap, "port1", Yin.port2.connect(), new Entry(length_arm_um)) ; 
		// creating upper arm
		wg1 = new StraightWg(objectName+"_wg1", layerMap, "port1", Yin.port3.connect(), new Entry(length_arm_um)) ;
		// creating output Y-junction
		Yout = new Yjunction(objectName+"_Yout", layerMap, "port1", port2, new Entry(hOffset_um), new Entry(vOffset_um)) ;

	}
	
	@Override
	public void saveProperties(){
		objectProperties.put(objectName+".arm.length_um", length_arm_um) ;
		objectProperties.put(objectName+".arm.width_um", width_um) ;
		objectProperties.put(objectName+".offset.horizontal_um", hOffset_um) ;
		objectProperties.put(objectName+".offset.vertical_um", vOffset_um) ;
	}
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##       Adding a MACH-ZEHNDER 1x1          ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args = {st0, st1, st2} ;
		args = MoreMath.Arrays.concat(args, Yin.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, wg1.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, wg2.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, Yout.getPythonCode_no_header(fileName, topCellName)) ;
		return args ;
	}

	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args = Yin.getPythonCode_no_header(fileName, topCellName) ;
		args = MoreMath.Arrays.concat(args, wg1.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, wg2.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, Yout.getPythonCode_no_header(fileName, topCellName)) ;
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
		Port port_translated = port1.translateXY(dX, dY) ;
		AbstractElement mzi_translated = new MZI1x1(objectName, layerMap, portNumber, port_translated, new Entry(hOffset_um), new Entry(vOffset_um), new Entry(length_arm_um)) ;
		return mzi_translated ;
	}
	

}
