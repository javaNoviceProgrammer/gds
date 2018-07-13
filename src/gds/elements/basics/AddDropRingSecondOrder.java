package gds.elements.basics;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;
import gds.elements.AbstractElement;
import gds.elements.positioning.Port;
import gds.elements.positioning.Position;
import gds.elements.positioning.object_ports.FourPortObject;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;

public class AddDropRingSecondOrder extends AbstractElement {

	double inputGap_um, outputGap_um, ringRingGap_um, radius1_um, ringWidth1_um, ringWidth2_um, radius2_um, wg1Width_um, wg2Width_um, length_um ;
	double lx_um, ly_um ;
	StraightWg wg1, wg2 ;
	Ring ring1, ring2 ;
	AbstractLayerMap[] layerMap ;
	public Port port1, port2, port3, port4 ;
	Port objectPort ;
	Position ringCenter1, ringCenter2 ;
	String portNumber ;
	
	public AddDropRingSecondOrder(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Waveguide Layer") AbstractLayerMap[] layerMap,
			@ParamName(name="Port Number [portX]") String portNumber,
			@ParamName(name="Choose Port") Port objectPort,
			@ParamName(name="Width of the Ring 1 (um)") Entry ringWidth1_um,
			@ParamName(name="Radius of the ring 1 (um)") Entry radius1_um,
			@ParamName(name="Width of the Ring 2 (um)") Entry ringWidth2_um,
			@ParamName(name="Radius of the ring 2 (um)") Entry radius2_um,
			@ParamName(name="Input gap (nm)") Entry inputGap_nm,
			@ParamName(name="Output gap (nm)") Entry outputGap_nm,
			@ParamName(name="Ring-Ring coupling gap (nm)") Entry ringRingGap_nm
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.portNumber = portNumber ;
		this.objectPort = objectPort ;
		wg1Width_um = objectPort.getWidthMicron() ;
		wg2Width_um = objectPort.getWidthMicron() ; // assuming all the ports have the same width
		this.ringWidth1_um = ringWidth1_um.getValue() ;
		this.radius1_um = radius1_um.getValue() ;
		this.ringWidth2_um = ringWidth2_um.getValue() ;
		this.radius2_um = radius2_um.getValue() ;
		this.inputGap_um = inputGap_nm.getValue() * 1e-3 ;
		this.outputGap_um = outputGap_nm.getValue() * 1e-3 ;
		this.ringRingGap_um = ringRingGap_nm.getValue() * 1e-3 ;
		double lx1_um = 2*radius1_um.getValue() + ringWidth1_um.getValue() ;
		double lx2_um = 2*radius2_um.getValue() + ringWidth2_um.getValue() ;
		this.lx_um = Math.max(lx1_um, lx2_um) ;
		length_um = lx_um ;
		double ly1_um = 2*radius1_um.getValue()+ ringWidth1_um.getValue();
		double ly2_um = 2*radius2_um.getValue()+ ringWidth2_um.getValue() ;
		ly_um = ly1_um + ly2_um + ringRingGap_um + wg1Width_um/2 + wg2Width_um/2  + inputGap_um + outputGap_um ;

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
		Position vVec1 = port1.connect().getEdgeVec().resize(wg1Width_um/2 + inputGap_um + ringWidth1_um/2 + radius1_um) ;
		Position vVec2 = port1.connect().getEdgeVec().resize(ly_um-(wg2Width_um/2 + outputGap_um + ringWidth2_um/2 + radius2_um)) ;
		ringCenter1 = port1.getPosition().translateXY(hVec).translateXY(vVec1) ;
		ringCenter2 = port1.getPosition().translateXY(hVec).translateXY(vVec2) ;
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
		// creating the rings
		String ringName1 = objectName + "_" + "ring1" ;
		ring1 = new Ring(ringName1, layerMap, new Ring.Center(ringCenter1), new Entry(ringWidth1_um), new Entry(radius1_um)) ;
		String ringName2 = objectName + "_" + "ring2" ;
		ring2 = new Ring(ringName2, layerMap, new Ring.Center(ringCenter2), new Entry(ringWidth2_um), new Entry(radius2_um)) ;
		// creating output waveguide
		String wg2Name = objectName + "_" + "wg2" ;
		wg2 = new StraightWg(wg2Name, layerMap, "port1", port4, new Entry(length_um)) ;
	}
	
	@Override
	public void saveProperties(){
		objectProperties.put(objectName+".inputwg.width_um", wg1Width_um) ;
		objectProperties.put(objectName+".dropwg.width_um", wg2Width_um) ;
		objectProperties.put(objectName+".ring1.width_um", ringWidth1_um) ;
		objectProperties.put(objectName+".ring1.radius_um", radius1_um) ;
		objectProperties.put(objectName+".ring2.width_um", ringWidth2_um) ;
		objectProperties.put(objectName+".ring2.radius_um", radius2_um) ;
		objectProperties.put(objectName+".inputgap_nm", inputGap_um*1000) ;
		objectProperties.put(objectName+".inputgap_um", inputGap_um) ;
		objectProperties.put(objectName+".outputgap_nm", outputGap_um*1000) ;
		objectProperties.put(objectName+".outputgap_um", outputGap_um) ;
		objectProperties.put(objectName+".ringringgap_nm", ringRingGap_um*1000) ;
		objectProperties.put(objectName+".ringringgap_um", ringRingGap_um) ;
	}
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##       Adding an ADD-DROP RING            ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] st3 = wg1.getPythonCode_no_header(fileName, topCellName) ;
		String[] st4 = ring1.getPythonCode_no_header(fileName, topCellName) ;
		String[] st5 = ring2.getPythonCode_no_header(fileName, topCellName) ;
		String[] st6 = wg2.getPythonCode_no_header(fileName, topCellName) ;
		// now we need to append all the strings together
		String[] args = {st0, st1, st2} ;
		args = MoreMath.Arrays.concat(args, st3) ;
		args = MoreMath.Arrays.concat(args, st4) ;
		args = MoreMath.Arrays.concat(args, st5) ;
		args = MoreMath.Arrays.concat(args, st6) ;
		return args;
	}
	
	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] st3 = wg1.getPythonCode_no_header(fileName, topCellName) ;
		String[] st4 = ring1.getPythonCode_no_header(fileName, topCellName) ;
		String[] st5 = ring2.getPythonCode_no_header(fileName, topCellName) ;
		String[] st6 = wg2.getPythonCode_no_header(fileName, topCellName) ;
		// now we need to append all the strings together
		String[] args = {} ;
		args = MoreMath.Arrays.concat(args, st3) ;
		args = MoreMath.Arrays.concat(args, st4) ;
		args = MoreMath.Arrays.concat(args, st5) ;
		args = MoreMath.Arrays.concat(args, st6) ;
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
		AbstractElement AddDropRing_translated = new AddDropRingSecondOrder(objectName, layerMap, portNumber, port_translated, new Entry(ringWidth1_um), new Entry(radius1_um), 
				new Entry(ringWidth2_um), new Entry(radius2_um), new Entry(inputGap_um*1000), new Entry(outputGap_um*1000), new Entry(ringRingGap_um*1e3)) ;
		return AddDropRing_translated ;
	}
	
}
