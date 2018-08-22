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
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;

public class MMI2x2 extends AbstractElement {

	AbstractLayerMap[] layerMap ;
	double width_mmi_um, length_mmi_um, sep_input_um, sep_output_um, width_wg_um, length_wg_um, width_wg_taper_um, length_wg_taper_um  ;
	double lx_um, ly_in_um, ly_out_um ;
	public Port port1, port2, port3, port4 ;
	Port objectPort ;
	Path p1, p2, p3, p4 ;
	StraightWg wg_mmi ;
	String portNumber ;

	public MMI2x2(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Layer Map") AbstractLayerMap[] layerMap,
			@ParamName(name="Port Number [portX]") String portNumber,
			@ParamName(name="Choose Port") Port objectPort,
			@ParamName(name="Width of MMI region (um)", default_="6") Entry width_mmi_um,
			@ParamName(name="Length of MMI region (um)", default_="32") Entry length_mmi_um,
			@ParamName(name="Separation of input ports (um)", default_="3.14") Entry sep_input_um,
			@ParamName(name="Separation of output ports (um)", default_="3.14") Entry sep_output_um,
			@ParamName(name="Width of the tapered input/output (um)", default_="2") Entry width_wg_taper_um,
			@ParamName(name="Length of the tapered input/output (um)", default_="10") Entry length_wg_taper_um
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.objectPort = objectPort ;
		this.portNumber = portNumber ;
		this.width_mmi_um = width_mmi_um.getValue() ;
		this.length_mmi_um = length_mmi_um.getValue() ;
		this.sep_input_um = sep_input_um.getValue() ;
		this.sep_output_um = sep_output_um.getValue() ;
		this.width_wg_um = objectPort.getWidthMicron() ;
		this.length_wg_um = 5 ;
		this.width_wg_taper_um = width_wg_taper_um.getValue() ;
		this.length_wg_taper_um = length_wg_taper_um.getValue() ;
		lx_um = 2*length_wg_um + 2*length_wg_taper_um.getValue() + length_mmi_um.getValue() ;
		ly_in_um = sep_input_um.getValue() ;
		ly_out_um = sep_output_um.getValue() ;

		setPorts() ;
		createObject() ;
		saveProperties() ;
	}

	public MMI2x2(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Layer Map") AbstractLayerMap[] layerMap,
			@ParamName(name="Port Number [portX]") String portNumber,
			@ParamName(name="Choose Port") Port objectPort
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.objectPort = objectPort ;
		this.portNumber = portNumber ;
		this.width_mmi_um = 6 ;
		this.length_mmi_um = 32 ;
		this.sep_input_um = 3.14 ;
		this.sep_output_um = 3.14 ;
		this.width_wg_um = objectPort.getWidthMicron() ;
		this.length_wg_um = 5 ;
		this.width_wg_taper_um = 2 ;
		this.length_wg_taper_um = 10 ;
		lx_um = 2*length_wg_um + 2*length_wg_taper_um + length_mmi_um ;
		ly_in_um = sep_input_um ;
		ly_out_um = sep_output_um ;

		setPorts() ;
		createObject() ;
		saveProperties() ;
	}

	@Override
	public void setPorts(){
		Position zero = new Position(0,0) ;
		FourPortObject temp = new FourPortObject(portNumber, objectPort, new Position[] {zero, zero, zero}) ;
		Position vec12 = temp.getPort1().connect().getNormalVec().resize(lx_um) ;
		Position vec14 = temp.getPort1().connect().getEdgeVec().resize(ly_in_um) ;
		Position vec23 = temp.getPort1().connect().getEdgeVec().resize(ly_out_um) ;
		FourPortObject MMI = new FourPortObject(portNumber, objectPort, new Position[] {vec12, vec14, vec23}) ;
		port1 = MMI.getPort1() ;
		port2 = MMI.getPort2() ;
		port3 = MMI.getPort3() ;
		port4 = MMI.getPort4() ;
		objectPorts.put(objectName+".port1", port1) ;
		objectPorts.put(objectName+".port2", port2) ;
		objectPorts.put(objectName+".port3", port3) ;
		objectPorts.put(objectName+".port4", port4) ;
	}

	private void createObject(){
		// creating waveguides for port 1
		Segment wg1 = new Segment(length_wg_um) ;
		Segment tap1 = new Segment(length_wg_taper_um, width_wg_taper_um) ;
		AbstractPathElement[] p1_elements = {wg1, tap1} ;
		p1 = new Path(objectName+"_wg1", layerMap, port1, p1_elements) ;

		// creating waveguides for port 2
		Segment wg2 = new Segment(length_wg_um) ;
		Segment tap2 = new Segment(length_wg_taper_um, width_wg_taper_um) ;
		AbstractPathElement[] p2_elements = {wg2, tap2} ;
		p2 = new Path(objectName+"_wg2", layerMap, port2, p2_elements) ;

		// creating waveguides for port 3
		Segment wg3 = new Segment(length_wg_um) ;
		Segment tap3 = new Segment(length_wg_taper_um, width_wg_taper_um) ;
		AbstractPathElement[] p3_elements = {wg3, tap3} ;
		p3 = new Path(objectName+"_wg3", layerMap, port3, p3_elements) ;

		// creating waveguides for port 4
		Segment wg4 = new Segment(length_wg_um) ;
		Segment tap4 = new Segment(length_wg_taper_um, width_wg_taper_um) ;
		AbstractPathElement[] p4_elements = {wg4, tap4} ;
		p4 = new Path(objectName+"_wg4", layerMap, port4, p4_elements) ;

		// creating the MMI waveguide
		Position vec = p1.port2.getEdgeVec().resize(ly_in_um/2) ;
		Port port_mmi = p1.port2.translateXY(vec).resize(width_mmi_um).connect() ;
		wg_mmi = new StraightWg(objectName+"_wg_mmi", layerMap, "port1", port_mmi, new Entry(length_mmi_um)) ;

	}

	@Override
	public void saveProperties(){
		allElements.put(objectName, this) ;
	}

	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##          Adding a MMI 2x2                ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args = {st0, st1, st2} ;
		args = MoreMath.Arrays.concat(args, p1.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, wg_mmi.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, p2.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, p3.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, p4.getPythonCode_no_header(fileName, topCellName)) ;
		return args ;
	}

	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args = p1.getPythonCode_no_header(fileName, topCellName) ;
		args = MoreMath.Arrays.concat(args, wg_mmi.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, p2.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, p3.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, p4.getPythonCode_no_header(fileName, topCellName)) ;
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
		AbstractElement mmi_translated = new MMI2x2(objectName, layerMap, portNumber, port_translated, new Entry(width_mmi_um), new Entry(length_mmi_um), new Entry(sep_input_um), new Entry(sep_output_um), new Entry(width_wg_taper_um), new Entry(length_wg_taper_um)) ;
		return mmi_translated;
	}

	@Override
	public AbstractElement translateXY(String newName, double dX, double dY) {
		Port port_translated = objectPort.translateXY(dX, dY) ;
		AbstractElement mmi_translated = new MMI2x2(newName, layerMap, portNumber, port_translated, new Entry(width_mmi_um), new Entry(length_mmi_um), new Entry(sep_input_um), new Entry(sep_output_um), new Entry(width_wg_taper_um), new Entry(length_wg_taper_um)) ;
		return mmi_translated;
	}


}
