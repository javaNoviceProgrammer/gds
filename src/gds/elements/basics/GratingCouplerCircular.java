package gds.elements.basics;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;
import gds.elements.AbstractElement;
import gds.elements.positioning.Port;
import gds.elements.positioning.Position;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;

public class GratingCouplerCircular extends AbstractElement {

	AbstractLayerMap[] layerMap ;
	double width_in_um, sep_um, length_gc_um, radius_taper_um, period_gc_um, angle_gc_degree, angle_gc_rad  ;
	int num_gratings ;
	public Port port1 ;

	Disk taper ;
	StraightWg wg ;
	CurvedWg[] gratings ;

	public GratingCouplerCircular(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Layer Map") AbstractLayerMap[] layerMap,
			@ParamName(name="Choose Port") Port objectPort,
			@ParamName(name="Angle of Grating (um)") Entry angle_gc_degree,
			@ParamName(name="Length of Each Grating (um)") Entry length_gc_um,
			@ParamName(name="Separation of Grating Elements (um)") Entry sep_um,
			@ParamName(name="Number of Grating Periods") Entry num_gratings,
			@ParamName(name="Radius of the tapered input (um)") Entry radius_taper_um
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.port1 = objectPort ;
		this.width_in_um = port1.getWidthMicron() ;
		this.angle_gc_degree = angle_gc_degree.getValue() ;
		this.angle_gc_rad = this.angle_gc_degree * Math.PI/180 ;
		this.length_gc_um = length_gc_um.getValue() ;
		this.sep_um = sep_um.getValue() ;
		this.num_gratings = (int) num_gratings.getValue() ;
		this.radius_taper_um = radius_taper_um.getValue() ;
		period_gc_um = this.sep_um + this.length_gc_um ;

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
		// creating input wg
		double x_um = width_in_um/2 /Math.tan(angle_gc_rad/2) ;
		wg = new StraightWg(objectName+"_"+"wg1", layerMap, "port1", port1, new Entry(x_um)) ;

		Position normVec = port1.getNormalVec().getUnitVector().rotate(port1.getPosition(), -angle_gc_degree/2).scale(-1) ;
		Position edgeVec = port1.getEdgeVec().getUnitVector().rotate(port1.getPosition(), -angle_gc_degree/2) ;
		Position P = port1.getPosition().translateXY(normVec.resize(radius_taper_um+sep_um+length_gc_um/2)) ;
		// creating circular taper
		taper = new Disk(objectName+"_"+"taper", layerMap, new Disk.Center(port1.getPosition()), new Entry(radius_taper_um), new Entry(angle_gc_degree), new Entry(normVec.getPhi_degree())) ;
		// creating all the grating periods
		gratings = new CurvedWg[num_gratings] ;
		double radius_gc_um = radius_taper_um + sep_um + length_gc_um/2 ;
		for(int i=0; i<num_gratings; i++) {
			Port wgPort = new Port(P, length_gc_um, edgeVec.getPhi_degree()) ;
			gratings[i] = new CurvedWg(objectName+"_"+"gc"+"_"+(i+1), layerMap, "port1", wgPort, false, new Entry(radius_gc_um), new Entry(angle_gc_degree)) ;
			P = P.translateXY(normVec.resize(period_gc_um)) ;
			radius_gc_um += period_gc_um ;
		}
	}

	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##             Adding a Grating             ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args = {st0, st1, st2} ;
		args = MoreMath.Arrays.concat(args, wg.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, taper.getPythonCode_no_header(fileName, topCellName)) ;
		for(int i=0; i<num_gratings; i++) {
			args = MoreMath.Arrays.concat(args, gratings[i].getPythonCode_no_header(fileName, topCellName)) ;
		}
		return args ;
	}

	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args = wg.getPythonCode_no_header(fileName, topCellName) ;
		args = MoreMath.Arrays.concat(args, taper.getPythonCode_no_header(fileName, topCellName)) ;
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
		AbstractElement grating_translated = new GratingCouplerCircular(objectName, layerMap, port_translated, new Entry(angle_gc_degree), new Entry(length_gc_um), new Entry(sep_um), new Entry(num_gratings), new Entry(radius_taper_um)) ;
		return grating_translated;
	}

}
