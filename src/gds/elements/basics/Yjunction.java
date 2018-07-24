package gds.elements.basics;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;
import gds.elements.AbstractElement;
import gds.elements.positioning.Port;
import gds.elements.positioning.Position;
import gds.elements.positioning.object_ports.ThreePortObject;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;

public class Yjunction extends AbstractElement {

	AbstractLayerMap[] layerMap ;
	double width_um, length_um, hOffset_um, vOffset_um ;
	Position P1, P2, P3 ;
	Port objectPort ;
	public Port port1, port2, port3; 
	String portNumber ;
	StraightWg wg ;
	Sbend bend1, bend2 ;
	
	public Yjunction(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Layer Map") AbstractLayerMap[] layerMap,
			@ParamName(name="Port Number [portX]") String portNumber,
			@ParamName(name="Choose Port") Port objectPort,
			@ParamName(name="Horizontal offset (um)") Entry hOffset_um,
			@ParamName(name="Vertical offset (um)") Entry vOffset_um
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.portNumber = portNumber ;
		this.objectPort = objectPort ;
		width_um = objectPort.getWidthMicron() ;
		this.length_um = 5  ;
		this.hOffset_um = hOffset_um.getValue() ;
		this.vOffset_um = vOffset_um.getValue() ;
		
		setPorts() ;
		createObject() ;
		saveProperties() ;
	}
	
	@Override
	public void setPorts(){
		Position zero = new Position(0,0) ;
		ThreePortObject temp = new ThreePortObject(portNumber, objectPort, new Position[] {zero, zero}) ;
		Position hVec = temp.getPort1().connect().getNormalVec().resize(hOffset_um+length_um) ;
		Position vVec = temp.getPort1().connect().getEdgeVec().scale(-vOffset_um/2) ;
		Position vec12 = hVec.translateXY(vVec) ;
		Position vec23 = vVec.scale(-1).getUnitVector().resize(vOffset_um) ;
		ThreePortObject Y = new ThreePortObject(portNumber, objectPort, new Position[] {vec12, vec23}) ;
		port1 = Y.getPort1() ;
		port2 = Y.getPort2() ;
		port3 = Y.getPort3() ;
		objectPorts.put(objectName+".port1", port1) ;
		objectPorts.put(objectName+".port2", port2) ;
		objectPorts.put(objectName+".port3", port3) ;
	}
	
	private void createObject(){
		wg = new StraightWg(objectName+"_wg", layerMap, "port1", port1, new Entry(length_um)) ;
		bend1 = new Sbend(objectName+"_bend1", layerMap, wg.port2.connect(), new Entry(hOffset_um), new Entry(-vOffset_um/2)) ; // Sbend going downward
		bend2 = new Sbend(objectName+"_bend2", layerMap, wg.port2.connect(), new Entry(hOffset_um), new Entry(vOffset_um/2)) ; // Sbend going upward
	}
	
	@Override
	public void saveProperties(){
		objectProperties.put(objectName+".width_um", width_um) ;
		objectProperties.put(objectName+".length_um", length_um) ;
		objectProperties.put(objectName+".offset.horizontal_um", hOffset_um) ;
		objectProperties.put(objectName+".offset.vertical_um", vOffset_um) ;

		allElements.put(objectName, this) ;
	}
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##           Adding a Y-JUNCTION            ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args = {st0, st1, st2} ;
		args = MoreMath.Arrays.concat(args, wg.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, bend1.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, bend2.getPythonCode_no_header(fileName, topCellName)) ;
		return args ;
	}

	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args = wg.getPythonCode_no_header(fileName, topCellName) ;
		args = MoreMath.Arrays.concat(args, bend1.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, bend2.getPythonCode_no_header(fileName, topCellName)) ;
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
		AbstractElement y_translated = new Yjunction(objectName, layerMap, portNumber, port_translated, new Entry(hOffset_um), new Entry(vOffset_um) ) ;
		return y_translated ;
	}

	@Override
	public AbstractElement translateXY(String newName, double dX, double dY) {
		Port port_translated = objectPort.translateXY(dX, dY) ;
		AbstractElement y_translated = new Yjunction(newName, layerMap, portNumber, port_translated, new Entry(hOffset_um), new Entry(vOffset_um) ) ;
		return y_translated ;
	}
	
}
