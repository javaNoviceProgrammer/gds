package gds.elements.shapes;

import gds.elements.AbstractElement;
import gds.elements.positioning.Port;
import gds.elements.positioning.Position;
import gds.elements.shapes.path_elements.AbstractPathElement;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;

public class Path extends AbstractElement {

	/**
	 * PATH gdspy: ## class gdspy.Path(width, initial_point=(0, 0), number_of_paths=1, distance=0) ## 
	 * PATH has several types: 
	 * 1) arc
	 * 2) parametric
	 * 3) segment
	 * 4) turn
	 * ----> Goal is to create a PATH and be able to put elements one after another. 
	 * PATH automatically places the elements one after another.
	 */
	
	double width_um ;
	Position P1, P2 ; // Position of the initial path (I'm assuming this is the initial edge)
	int numOfPaths ;
	double distance_um ; // distance between the centers of adjacent paths
	AbstractPathElement[] pathElements ;
	AbstractLayerMap[] layerMap ;
	public Port port1, port2 ;
	
	public Path(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Layer Map") AbstractLayerMap[] layerMap,
			@ParamName(name="Choose Start Port") Port port1,
			@ParamName(name="Path Elements") AbstractPathElement[] pathElements
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.port1 = port1 ;
		this.width_um = port1.getWidthMicron() ;
		this.numOfPaths = 1 ;
		this.distance_um = 0 ;
		this.pathElements = pathElements ;
		// all the elemenets in the path are created with respect to (0,0)--> then translated and resize the port
		this.port2 = pathElements[pathElements.length-1].port2.translateXY(port1.getPosition()).rotate(port1.getPosition(), port1.connect().getNormalDegree()) ;
		port2 = port2.resize(port1.getWidthMicron()) ;
		setPorts() ;
		saveProperties() ;
		// just make sure to clear the static variables for the next run
		AbstractPathElement.numElements = 0 ;
		AbstractPathElement.elementPorts.clear();
	}
	
	@Override
	public void setPorts(){
		P1 = port1.getPosition() ;
		P2 = port2.getPosition() ; 
		objectPorts.put(objectName+".port1", port1) ;
		objectPorts.put(objectName+".port2", port2) ;
	}

	@Override
	public void saveProperties(){
		objectProperties.put(objectName+".width_um", width_um) ;
		
		objectProperties.put(objectName+".port1.angle_degree", port1.getAngleDegree()) ;
		objectProperties.put(objectName+".port1.angle_rad", port1.getAngleRad()) ;
		objectProperties.put(objectName+".port1.normal_degree", port1.getNormalDegree()) ;
		objectProperties.put(objectName+".port1.normal_rad", port1.getNormalRad()) ;
		
		objectProperties.put(objectName+".port2.angle_degree", port2.getAngleDegree()) ;
		objectProperties.put(objectName+".port2.angle_rad", port2.getAngleRad()) ;
		objectProperties.put(objectName+".port2.normal_degree", port2.getNormalDegree()) ;
		objectProperties.put(objectName+".port2.normal_rad", port2.getNormalRad()) ;
	}
	
	//******************************Generating the Python Code*******************
	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st00 = "## ---------------------------------------- ##" ;
		String st01 = "##             Adding a PATH                ##" ;
		String st02 = "## ---------------------------------------- ##" ;
		String[] args = new String[] {st00, st01, st02} ;
		int m = layerMap.length ;
		for(int k=0; k<m; k++){
			int n = pathElements.length ;
			String st1 = "### adding a new PATH on layer " + layerMap[k].getLayerName() ;
			String st2 = objectName + " = gdspy.Path(" + width_um + "," + "initial_point=" + P1.getString() + "," + "number_of_paths=" + numOfPaths + "," + " distance=" + distance_um+")" ;
			args = MoreMath.Arrays.concat(args, new String[] {st1, st2}) ;
			for(int i=0; i<n; i++){
				String[] st3 = pathElements[i].getPythonCode(objectName, layerMap[k]) ;
				args = MoreMath.Arrays.concat(args, st3) ;
			}
			String st40 = objectName + ".rotate(" + port1.connect().getNormalRad() + "," + P1.getString() + ")" ;
			String st4 = topCellName + ".add(" + objectName + ")" ;
			args = MoreMath.Arrays.concat(args, new String[] {st40, st4}) ;
		}
		return args ;
	}
	
	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args = new String[0] ;
		int m = layerMap.length ;
		for(int k=0; k<m; k++){
			int n = pathElements.length ;
			String st1 = "### adding a new PATH on layer " + layerMap[k].getLayerName() ;
			String st2 = objectName + " = gdspy.Path(" + width_um + "," + "initial_point=" + P1.getString() + "," + "number_of_paths=" + numOfPaths + "," + " distance=" + distance_um+")" ;
			args = MoreMath.Arrays.concat(args, new String[] {st1, st2}) ;
			for(int i=0; i<n; i++){
				String[] st3 = pathElements[i].getPythonCode(objectName, layerMap[k]) ;
				args = MoreMath.Arrays.concat(args, st3) ;
			}
			String st40 = objectName + ".rotate(" + port1.connect().getNormalRad() + "," + P1.getString() + ")" ;
			String st4 = topCellName + ".add(" + objectName + ")" ;
			args = MoreMath.Arrays.concat(args, new String[] {st40, st4}) ;
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
		Port port1_translated = port1.translateXY(dX, dY) ;
		AbstractElement path_translated = new Path(objectName, layerMap, port1_translated, pathElements) ;
		return path_translated ;
	}
	
	
	
}
