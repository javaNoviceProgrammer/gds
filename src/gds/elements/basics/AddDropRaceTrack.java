package gds.elements.basics;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;
import gds.elements.AbstractElement;
import gds.elements.positioning.Port;
import gds.elements.positioning.Position;
import gds.elements.positioning.object_ports.FourPortObject;
import gds.elements.shapes.Path;
import gds.elements.shapes.path_elements.AbstractPathElement;
import gds.elements.shapes.path_elements.Segment;
import gds.elements.shapes.path_elements.Turn;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;

public class AddDropRaceTrack extends AbstractElement {

	/**
	 * creating a rack-track ring resonator with input and output coupling waveguides
	 */
	
	AbstractLayerMap[] layerMap ;
	double radius_um, width_um, length_um, inputGap_nm, inputGap_um, outputGap_nm, outputGap_um ;
	double lx_um, ly_um;
	Port objectPort ;
	String portNumber ;
	public Port port1, port2, port3, port4 ;
	Position hVec, vVec ;
	Path raceTrack ;
	StraightWg wgIn, wgOut ;
	
	public AddDropRaceTrack(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Layer Map") AbstractLayerMap[] layerMap,
			@ParamName(name="Port Number [portX]") String portNumber,
			@ParamName(name="Choose Port") Port objectPort,
			@ParamName(name="Length of the coupling region (um)") Entry length_um,
			@ParamName(name="Radius (um)") Entry radius_um,
			@ParamName(name="Input gap size (nm)") Entry inputGap_nm,
			@ParamName(name="Output gap size (nm)") Entry outputGap_nm
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.portNumber = portNumber ;
		this.objectPort = objectPort ;
		this.width_um = objectPort.getWidthMicron() ;
		this.length_um = length_um.getValue() ;
		this.radius_um = radius_um.getValue() ;
		this.inputGap_nm = inputGap_nm.getValue() ;
		inputGap_um = inputGap_nm.getValue()/1000 ;
		this.outputGap_nm = outputGap_nm.getValue() ;
		outputGap_um = outputGap_nm.getValue()/1000 ;
		lx_um = 2*radius_um.getValue() + length_um.getValue() + 2*width_um/2 ;
		ly_um = 2*radius_um.getValue() + 4*width_um/2 + inputGap_um + outputGap_um ;
		
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
		FourPortObject adr = new FourPortObject(portNumber, objectPort, new Position[] {vec12, vec14, vec23}) ;
		port1 = adr.getPort1() ;
		port2 = adr.getPort2() ;
		port3 = adr.getPort3() ;
		port4 = adr.getPort4() ;
		
		objectPorts.put(objectName+".port1", port1) ;
		objectPorts.put(objectName+".port2", port2) ;
		objectPorts.put(objectName+".port3", port3) ;
		objectPorts.put(objectName+".port4", port4) ;
	}
	
	private void createObject(){
		Position hVec = port1.connect().getNormalVec().resize(radius_um + width_um/2) ;
		Position vVec = port1.connect().getEdgeVec().resize(width_um/2 + inputGap_um + width_um/2) ;
		Port initialPort = port1.translateXY(hVec).translateXY(vVec) ;
		Segment wg1 = new Segment(length_um) ;
		Turn turn1 = new Turn(radius_um, "ll") ;
		Segment wg2 = new Segment(length_um) ;
		Turn turn2 = new Turn(radius_um, "ll") ;
		AbstractPathElement[] elements = {wg1, turn1, wg2, turn2} ;
		raceTrack = new Path(objectName+"_ring", layerMap, initialPort, elements) ;
		
		wgIn = new StraightWg(objectName+"_wgIn", layerMap, "port1", port1, new Entry(lx_um)) ;
		wgOut = new StraightWg(objectName+"_wgOut", layerMap, "port1", port4, new Entry(lx_um)) ;
	}
	
	@Override
	public void saveProperties(){
		objectProperties.put(objectName+".width_um", width_um) ;
		objectProperties.put(objectName+".couplinglength_um", length_um) ;
		objectProperties.put(objectName+".radius_um", radius_um) ;
		objectProperties.put(objectName+".inputgap_um", inputGap_um) ;
		objectProperties.put(objectName+".inputgap_nm", inputGap_nm) ;
		objectProperties.put(objectName+".outputgap_um", outputGap_um) ;
		objectProperties.put(objectName+".outputgap_nm", outputGap_nm) ;
	}
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##    Adding an ADD-DROP RACE-TRACK RING    ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args = {st0, st1, st2} ;
		args = MoreMath.Arrays.concat(args, wgIn.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, raceTrack.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, wgOut.getPythonCode_no_header(fileName, topCellName)) ;
		return args;
	}

	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args =  wgIn.getPythonCode_no_header(fileName, topCellName) ;
		args = MoreMath.Arrays.concat(args, raceTrack.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, wgOut.getPythonCode_no_header(fileName, topCellName)) ;
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
		Port port_transted = objectPort.translateXY(dX, dY) ;
		AbstractElement raceTrack_translated = new AddDropRaceTrack(objectName, layerMap, portNumber, port_transted, new Entry(length_um), new Entry(radius_um), new Entry(inputGap_nm), new Entry(outputGap_nm)) ;
		return raceTrack_translated ;
	}

}
