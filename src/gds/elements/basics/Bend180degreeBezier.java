package gds.elements.basics;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;
import gds.elements.AbstractElement;
import gds.elements.positioning.Port;
import gds.elements.shapes.Path;
import gds.elements.shapes.path_elements.AbstractPathElement;
import gds.elements.shapes.path_elements.Parametric;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;

public class Bend180degreeBezier extends AbstractElement {

	/**
	 * For this class, we use the parametric PATH element to create a Bezier curve.
	 */

	AbstractLayerMap[] layerMap ;
	Path bendPath ;
	double R_um, d_um, width_um ;
	public Port port1, port2 ;

	public Bend180degreeBezier(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Waveguide Layer") AbstractLayerMap[] layerMap,
			@ParamName(name="Choose Start Port") Port port1,
			@ParamName(name="R parameter (um)") Entry R_um,
			@ParamName(name="d parameter (um)") Entry d_um
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.port1 = port1 ;
		this.width_um = port1.getWidthMicron() ;
		this.R_um = R_um.getValue() ;
		this.d_um = d_um.getValue() ;

		createObject() ;
		setPorts() ;
		saveProperties() ;
	}

	@Override
	public void setPorts(){
		objectPorts.put(objectName+".port1", port1) ;
		objectPorts.put(objectName+".port2", port2) ;
	}

	private void createObject(){
		// x parameter
		double x0 = 0 ;
		double x1 = d_um ;
		double x2 = d_um ;
		double x3 = 0 ;
		String Xt = "(1-t)*(1-t)*(1-t)*" + x0 + "+ 3*(1-t)*(1-t)*t*" + x1 + "+ 3*(1-t)*t*t*" + x2 + "+ t*t*t*" + x3 ;
		// y parameter
		double y0 = 0 ;
		double y1 = 0 ;
		double y2 = 2*R_um ;
		double y3 = 2*R_um ;
		String Yt = "(1-t)*(1-t)*(1-t)*" + y0 + "+ 3*(1-t)*(1-t)*t*" + y1 + "+ 3*(1-t)*t*t*" + y2 + "+ t*t*t*" + y3 ;
		// need to create a path
		String Xt_matlab = Xt ;
		String Yt_matlab = Yt ;
		Parametric Sbend = new Parametric(Xt_matlab, Yt_matlab, Xt, Yt) ;
		bendPath = new Path(objectName+"_path", layerMap, port1, new AbstractPathElement[] {Sbend}) ;
		port2 = bendPath.port2 ;
	}

	@Override
	public void saveProperties(){
		objectProperties.put(objectName+".width_um", width_um) ;
		objectProperties.put(objectName+".R_um", R_um) ;
		objectProperties.put(objectName+".d_um", d_um) ;

	}

	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##        Adding a 180 DEGREE BEZIER        ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args = {st0, st1, st2} ;
		args = MoreMath.Arrays.concat(args, bendPath.getPythonCode_no_header(fileName, topCellName)) ;
		return args;
	}

	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args = new String[0] ;
		args = MoreMath.Arrays.concat(args, bendPath.getPythonCode_no_header(fileName, topCellName)) ;
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
		Port port1_translated = port1.translateXY(dX, dY) ;
		AbstractElement Sbend_translated = new Bend180degreeBezier(objectName, layerMap, port1_translated, new Entry(R_um), new Entry(d_um)) ;
		return Sbend_translated;
	}



}
