package gds.elements.basics;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;
import gds.elements.AbstractElement;
import gds.elements.positioning.Port;
import gds.elements.positioning.Position;
import gds.elements.shapes.Path;
import gds.elements.shapes.path_elements.AbstractPathElement;
import gds.elements.shapes.path_elements.Segment;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;

public class GratingCoupler extends AbstractElement {

	AbstractLayerMap[] layerMap ;
	double width_in_um, length_in_um, sep_um, width_gc_um, length_gc_um, length_taper_um, period_gc_um  ;
	int num_gratings ;
	double lx_um, ly_um ;
	public Port port1 ;

	Path p1 ;
	StraightWg[] gratings ;

	public GratingCoupler(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Layer Map") AbstractLayerMap[] layerMap,
			@ParamName(name="Choose Port") Port objectPort,
			@ParamName(name="Width of Each Grating (um)", default_="20") Entry width_gc_um,
			@ParamName(name="Length of Each Grating (um)", default_="1") Entry length_gc_um,
			@ParamName(name="Separation of Grating Elements (um)", default_="0.5") Entry sep_um,
			@ParamName(name="Number of Grating Periods", default_="10") Entry num_gratings,
			@ParamName(name="Length of the tapered input (um)", default_="10") Entry length_taper_um
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.port1 = objectPort ;
		this.width_gc_um = width_gc_um.getValue() ;
		this.length_gc_um = length_gc_um.getValue() ;
		this.sep_um = sep_um.getValue() ;
		this.num_gratings = (int) num_gratings.getValue() ;
		this.length_taper_um = length_taper_um.getValue() ;
		period_gc_um = this.sep_um + this.length_gc_um ;
		lx_um = this.num_gratings * this.length_gc_um + this.length_taper_um ;
		ly_um = this.width_gc_um ;

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
//		objectProperties.put(objectName+".mmi.width_um", width_mmi_um) ;
//		objectProperties.put(objectName+".mmi.length_um", length_mmi_um) ;
//		objectProperties.put(objectName+".wg.width_um", width_wg_um) ;
//		objectProperties.put(objectName+".wg.length_um", length_wg_um) ;
//		objectProperties.put(objectName+".wgtaper.width_um", width_wg_taper_um) ;
//		objectProperties.put(objectName+".wgtaper.length_um", length_wg_taper_um) ;
//		objectProperties.put(objectName+".output.separation_um", sep_output_um) ;
	}

	private void createObject(){
		// creating input taper
		Segment taper = new Segment(length_taper_um, width_gc_um) ;
		AbstractPathElement[] elements = {taper} ;
		p1 = new Path(objectName+"_"+"taper", layerMap, port1, elements) ;
		// creating all the grating periods
		Position normVec = port1.getNormalVec().getUnitVector().scale(-1) ;
		Position edgeVec = port1.getEdgeVec().getUnitVector() ;
		Position P = port1.getPosition().translateXY(normVec.resize(length_taper_um+sep_um+length_gc_um/2)).translateXY(edgeVec.resize(width_gc_um/2)) ;
		gratings = new StraightWg[num_gratings] ;
		for(int i=0; i<num_gratings; i++) {
			Port wgPort = new Port(P, length_gc_um, edgeVec.getPhi_degree()) ;
			gratings[i] = new StraightWg(objectName+"_"+"gc"+"_"+(i+1), layerMap, "port1", wgPort, new Entry(width_gc_um)) ;
			P = P.translateXY(normVec.resize(period_gc_um)) ;
		}
	}

	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##             Adding a Grating             ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args = {st0, st1, st2} ;
		args = MoreMath.Arrays.concat(args, p1.getPythonCode_no_header(fileName, topCellName)) ;
		for(int i=0; i<num_gratings; i++) {
			args = MoreMath.Arrays.concat(args, gratings[i].getPythonCode_no_header(fileName, topCellName)) ;
		}
		return args ;
	}

	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args = p1.getPythonCode_no_header(fileName, topCellName) ;
		for(int i=0; i<num_gratings; i++) {
			args = MoreMath.Arrays.concat(args, gratings[i].getPythonCode_no_header(fileName, topCellName)) ;
		}
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
		Port port_translated = port1.translateXY(dX, dY) ;
		AbstractElement grating_translated = new GratingCoupler(objectName, layerMap, port_translated, new Entry(width_gc_um), new Entry(length_gc_um), new Entry(sep_um), new Entry(num_gratings), new Entry(length_taper_um)) ;
		return grating_translated;
	}

}
