package gds.elements.basics;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;
import gds.elements.AbstractElement;
import gds.elements.positioning.Port;
import gds.elements.positioning.Position;
import gds.elements.positioning.object_ports.ThreePortObject;
import gds.elements.shapes.Path;
import gds.elements.shapes.path_elements.AbstractPathElement;
import gds.elements.shapes.path_elements.Segment;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;

public class GratingCoupler extends AbstractElement {

	AbstractLayerMap[] layerMap ;
	double width_mmi_um, length_mmi_um, sep_output_um, width_wg_um, length_wg_um, width_wg_taper_um, length_wg_taper_um  ;
	double lx_um, ly_um ;
	public Port port1 ;
	Port objectPort ;

	Path p1, p2, p3 ;
	StraightWg wg_mmi ;

	public GratingCoupler(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Layer Map") AbstractLayerMap[] layerMap,
			@ParamName(name="Choose Port") Port objectPort,
			@ParamName(name="Width of MMI region (um)", default_="6") Entry width_mmi_um,
			@ParamName(name="Length of MMI region (um)", default_="32") Entry length_mmi_um,
			@ParamName(name="Separation of output ports (um)", default_="3.14") Entry sep_output_um,
			@ParamName(name="Length of the input/output waveguides (um)", default_="5") Entry length_wg_um,
			@ParamName(name="Width of the tapered input/output (um)", default_="2") Entry width_wg_taper_um,
			@ParamName(name="Length of the tapered input/output (um)", default_="10") Entry length_wg_taper_um
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.objectPort = objectPort ;
		this.width_mmi_um = width_mmi_um.getValue() ;
		this.length_mmi_um = length_mmi_um.getValue() ;
		this.sep_output_um = sep_output_um.getValue() ;
		this.width_wg_um = objectPort.getWidthMicron() ;
		this.length_wg_um = length_wg_um.getValue() ;
		this.width_wg_taper_um = width_wg_taper_um.getValue() ;
		this.length_wg_taper_um = length_wg_taper_um.getValue() ;
		lx_um = 2*length_wg_um.getValue() + 2*length_wg_taper_um.getValue() + length_mmi_um.getValue() ;
		ly_um = sep_output_um.getValue()/2 ;

		setPorts() ;
		createObject() ;
		saveProperties() ;
	}

	@Override
	public void setPorts(){
		objectPorts.put(objectName+".port1", port1) ;
	}

	@Override
	public void saveProperties(){
		objectProperties.put(objectName+".mmi.width_um", width_mmi_um) ;
		objectProperties.put(objectName+".mmi.length_um", length_mmi_um) ;
		objectProperties.put(objectName+".wg.width_um", width_wg_um) ;
		objectProperties.put(objectName+".wg.length_um", length_wg_um) ;
		objectProperties.put(objectName+".wgtaper.width_um", width_wg_taper_um) ;
		objectProperties.put(objectName+".wgtaper.length_um", length_wg_taper_um) ;
		objectProperties.put(objectName+".output.separation_um", sep_output_um) ;
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
		// creating waveguides for port 2
		Segment wg3 = new Segment(length_wg_um) ;
		Segment tap3 = new Segment(length_wg_taper_um, width_wg_taper_um) ;
		AbstractPathElement[] p3_elements = {wg3, tap3} ;
		p3 = new Path(objectName+"_wg3", layerMap, port3, p3_elements) ;
		// creating the MMI waveguide
		Port port_mmi = p1.port2.connect().resize(width_mmi_um) ;
		wg_mmi = new StraightWg(objectName+"_wg_mmi", layerMap, "port1", port_mmi, new Entry(length_mmi_um)) ;

	}

	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##             Adding a MMI 1x2             ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args = {st0, st1, st2} ;
		args = MoreMath.Arrays.concat(args, p1.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, wg_mmi.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, p2.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, p3.getPythonCode_no_header(fileName, topCellName)) ;
		return args ;
	}

	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args = p1.getPythonCode_no_header(fileName, topCellName) ;
		args = MoreMath.Arrays.concat(args, wg_mmi.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, p2.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, p3.getPythonCode_no_header(fileName, topCellName)) ;
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
		AbstractElement mmi_translated = new GratingCoupler(objectName, layerMap, portNumber, port_translated, new Entry(width_mmi_um), new Entry(length_mmi_um), new Entry(sep_output_um), new Entry(length_wg_um), new Entry(width_wg_taper_um), new Entry(length_wg_taper_um)) ;
		return mmi_translated;
	}

}
