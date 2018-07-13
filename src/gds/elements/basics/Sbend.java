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

public class Sbend extends AbstractElement {

	/**
	 * For this class, we use the parametric PATH element to create an S-bend.
	 * X(t) = W*t , Y(t) = V/2 * (1-cos(pi*t)), where 0<t<1 is the curve parameter.
	 * W is the horizontal offset. V is the vertical offset. W and V can be positive or negative.
	 */
	
	AbstractLayerMap[] layerMap ;
	Path SbendPath ;
	double horizontalOffset_um, verticalOffset_um, width_um ;
	public Port port1, port2 ;
		
	public Sbend(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Waveguide Layer") AbstractLayerMap[] layerMap,
			@ParamName(name="Choose Start Port") Port port1,
			@ParamName(name="Horizontal offset [+] (um)") Entry horizontalOffset_um,
			@ParamName(name="Vertical offset [+/-] (um)") Entry verticalOffset_um
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.port1 = port1 ;
		this.width_um = port1.getWidthMicron() ;
		double angleDegree = port1.getNormalVec().getPhi_degree();
		if(angleDegree <= 90 && angleDegree >= -90){
			this.verticalOffset_um = -1*verticalOffset_um.getValue() ;
			this.horizontalOffset_um = horizontalOffset_um.getValue() ;
		}
		else{
			this.verticalOffset_um = verticalOffset_um.getValue() ;
			this.horizontalOffset_um = horizontalOffset_um.getValue() ;
		}

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
		// need to create a path
		String Xt = "t*" + horizontalOffset_um ;
		String Xt_matlab = "t*" + horizontalOffset_um ;
		String Yt = verticalOffset_um + " * 1/2*(1-numpy.cos(numpy.pi * t))" ; // creating the cosine Sbend
		String Yt_matlab = verticalOffset_um + " * 1/2*(1-cos(pi * t))" ;
		Parametric Sbend = new Parametric(Xt_matlab, Yt_matlab, Xt, Yt) ;
		SbendPath = new Path(objectName+"_path", layerMap, port1, new AbstractPathElement[] {Sbend}) ;
		port2 = SbendPath.port2 ;
	}
	
	@Override
	public void saveProperties(){
		objectProperties.put(objectName+".width_um", width_um) ;
		objectProperties.put(objectName+".offset.horizontal_um", horizontalOffset_um) ;
		objectProperties.put(objectName+".offset.vertical_um", verticalOffset_um) ;

	}
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##          Adding a S-BEND                 ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args = {st0, st1, st2} ;
		args = MoreMath.Arrays.concat(args, SbendPath.getPythonCode_no_header(fileName, topCellName)) ;
		return args;
	}
	
	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args = new String[0] ;
		args = MoreMath.Arrays.concat(args, SbendPath.getPythonCode_no_header(fileName, topCellName)) ;
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
		AbstractElement Sbend_translated = new Sbend(objectName, layerMap, port1_translated, new Entry(horizontalOffset_um), new Entry(verticalOffset_um)) ;
		return Sbend_translated;
	}
	


}
