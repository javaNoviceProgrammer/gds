package gds.elements.basics;

import gds.elements.AbstractElement;
import gds.elements.positioning.Port;
import gds.elements.positioning.Position;
import gds.elements.positioning.object_ports.FourPortObject;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;

public class AddDropRing extends AbstractElement {

	double inputGap_um, outputGap_um, radius_um, ringWidth_um, wg1Width_um, wg2Width_um, length_um ;
	double lx_um, ly_um ;
	StraightWg wg1, wg2 ;
	Ring ring ;
	AbstractLayerMap[] layerMap ;
	public Port port1, port2, port3, port4 ;
	Port objectPort ;
	Position ringCenter ;
	String portNumber ;
	
	public AddDropRing(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Waveguide Layer") AbstractLayerMap[] layerMap,
			@ParamName(name="Port Number [portX]") String portNumber,
			@ParamName(name="Choose Port") Port objectPort,
			@ParamName(name="Width of the Ring (um)") Entry ringWidth_um,
			@ParamName(name="Radius of the ring (um)") Entry radius_um,
			@ParamName(name="Input gap (nm)") Entry inputGap_nm,
			@ParamName(name="Output gap (nm)") Entry outputGap_nm
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.portNumber = portNumber ;
		this.objectPort = objectPort ;
		wg1Width_um = objectPort.getWidthMicron() ;
		wg2Width_um = objectPort.getWidthMicron() ; // assuming all the ports have the same width
		this.ringWidth_um = ringWidth_um.getValue() ;
		this.radius_um = radius_um.getValue() ;
		this.inputGap_um = inputGap_nm.getValue()/1000 ;
		this.outputGap_um = outputGap_nm.getValue()/1000 ;
		lx_um = 2*radius_um.getValue() + ringWidth_um.getValue() ;
		length_um = lx_um ;
		ly_um = 2*radius_um.getValue() + wg1Width_um/2 + wg2Width_um/2 + 2*ringWidth_um.getValue()/2 + inputGap_um + outputGap_um ;

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
		Position hVec = port1.connect().getNormalVec().resize(lx_um/2) ;
		Position vVec = port1.connect().getEdgeVec().resize(wg1Width_um/2 + inputGap_um + ringWidth_um/2 + radius_um) ;
		ringCenter = port1.getPosition().translateXY(hVec).translateXY(vVec) ;
		// put the ports to the data base
		objectPorts.put(objectName+".port1", port1) ;
		objectPorts.put(objectName+".port2", port2) ;
		objectPorts.put(objectName+".port3", port3) ;
		objectPorts.put(objectName+".port4", port4) ;
	}
	
	
	private void createObject(){
		// creating input waveguide
		String wg1Name = objectName + "_" + "wg1" ;
		wg1 = new StraightWg(wg1Name, layerMap, "port1", port1, new Entry(length_um)) ;
		// creating the ring
		String ringName = objectName + "_" + "ring" ;
		ring = new Ring(ringName, layerMap, new Ring.Center(ringCenter), new Entry(ringWidth_um), new Entry(radius_um)) ;
		// creating output waveguide
		String wg2Name = objectName + "_" + "wg2" ;
		wg2 = new StraightWg(wg2Name, layerMap, "port1", port4, new Entry(length_um)) ;
	}
	
	@Override
	public void saveProperties(){
		objectProperties.put(objectName+".inputwg.width_um", wg1Width_um) ;
		objectProperties.put(objectName+".dropwg.width_um", wg2Width_um) ;
		objectProperties.put(objectName+".ringwidth_um", ringWidth_um) ;
		objectProperties.put(objectName+".radius_um", radius_um) ;
		objectProperties.put(objectName+".inputgap_nm", inputGap_um*1000) ;
		objectProperties.put(objectName+".inputgap_um", inputGap_um) ;
		objectProperties.put(objectName+".outputgap_nm", outputGap_um*1000) ;
		objectProperties.put(objectName+".outputgap_um", outputGap_um) ;
		
		allElements.put(objectName, this) ;
	}
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##       Adding an ADD-DROP RING            ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] st3 = wg1.getPythonCode_no_header(fileName, topCellName) ;
		String[] st4 = ring.getPythonCode_no_header(fileName, topCellName) ;
		String[] st5 = wg2.getPythonCode_no_header(fileName, topCellName) ;
		// now we need to append all the strings together
		String[] args = {st0, st1, st2} ;
		args = MoreMath.Arrays.concat(args, st3) ;
		args = MoreMath.Arrays.concat(args, st4) ;
		args = MoreMath.Arrays.concat(args, st5) ;
		return args;
	}
	
	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] st3 = wg1.getPythonCode_no_header(fileName, topCellName) ;
		String[] st4 = ring.getPythonCode_no_header(fileName, topCellName) ;
		String[] st5 = wg2.getPythonCode_no_header(fileName, topCellName) ;
		// now we need to append all the strings together
		String[] args = {} ;
		args = MoreMath.Arrays.concat(args, st3) ;
		args = MoreMath.Arrays.concat(args, st4) ;
		args = MoreMath.Arrays.concat(args, st5) ;
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
		AbstractElement AddDropRing_translated = new AddDropRing(objectName, layerMap, portNumber, port_translated, new Entry(ringWidth_um), new Entry(radius_um), new Entry(inputGap_um*1000), new Entry(outputGap_um*1000)) ;
		return AddDropRing_translated ;
	}

	@Override
	public AbstractElement translateXY(String newName, double dX, double dY) {
		Port port_translated = objectPort.translateXY(dX, dY) ;
		AbstractElement AddDropRing_translated = new AddDropRing(newName, layerMap, portNumber, port_translated, new Entry(ringWidth_um), new Entry(radius_um), new Entry(inputGap_um*1000), new Entry(outputGap_um*1000)) ;
		return AddDropRing_translated ;
	}
	
}
