package gds.elements.shapes;

import gds.elements.AbstractElement;
import gds.elements.positioning.Position;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;

public class Polygon extends AbstractElement {

	/**
	 * class gdspy.Polygon(points, layer=0, datatype=0, verbose=True)
	 */
	
	AbstractLayerMap[] layerMap ;
	
	Position[] pointArray ; // an array of all vertices
	
	public Polygon(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Waveguide Layer") AbstractLayerMap[] layerMap,
			@ParamName(name="Vector of Vertices") Position[] pointArray
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.pointArray = pointArray ;
		setPorts();
		saveProperties() ;
	}
	
	@Override
	public void setPorts(){
		// nothing to be done
	}
	
	@Override
	public void saveProperties(){
		// we can add the vertices to the database
	}
	
	public void selfRotate(Position P, double angleDegree){
		int n = pointArray.length ;
		Position[] pointArrayRotated = new Position[n] ;
		for(int i=0; i<n; i++){
			pointArrayRotated[i] = pointArray[i].rotate(P, angleDegree) ;
		}
		this.pointArray = pointArrayRotated ;
	}
	
	public String getVertices(){
		int n = pointArray.length ;
		String st = "[" ;
		for(int i=0; i<n-1; i++){
			st += pointArray[i].getString() + "," ;
		}
		st += pointArray[n-1].getString() ;
		st += "]" ;
		return st ;
	}
	
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st00 = "## ---------------------------------------- ##" ;
		String st01 = "##           Adding a POLYGON               ##" ;
		String st02 = "## ---------------------------------------- ##" ;
		String[] args = {st00, st01, st02} ;
		int m = layerMap.length ;
		for(int j=0; j<m; j++){
			// defining the layer of the object
			int layerNumber = layerMap[j].getLayerNumber() ;
			int dataType = layerMap[j].getDataType() ;
			// creating the polygon object
			String title = "### adding a "+ layerMap[j].getLayerName() +" layer" ;
			String st2 = objectName + " = gdspy.Polygon([" ;
			int n = pointArray.length ;
			for(int i=0; i<n-1; i++){
				st2 += pointArray[i].getString() + "," ;
			}
			st2 += pointArray[n-1].getString() + "], " ;
			st2 += "layer=" + layerNumber + "," + "datatype=" + dataType + "," + "verbose = False)" ;
			String st3 = topCellName + ".add(" + objectName + ")" ;
			args = MoreMath.Arrays.concat(args, new String[] {title, st2, st3}) ;
		}
		return args;
	}
	
	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args = new String[0] ;
		int m = layerMap.length ;
		for(int j=0; j<m; j++){
			// defining the layer of the object
			int layerNumber = layerMap[j].getLayerNumber() ;
			int dataType = layerMap[j].getDataType() ;
			// creating the polygon object
			String title = "### adding a "+ layerMap[j].getLayerName() +" layer" ;
			String st2 = objectName + " = gdspy.Polygon([" ;
			int n = pointArray.length ;
			for(int i=0; i<n-1; i++){
				st2 += pointArray[i].getString() + "," ;
			}
			st2 += pointArray[n-1].getString() + "], " ;
			st2 += "layer=" + layerNumber + "," + "datatype=" + dataType + "," + "verbose = False)" ;
			String st3 = topCellName + ".add(" + objectName + ")" ;
			args = MoreMath.Arrays.concat(args, new String[] {title, st2, st3}) ;
		}
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
		int n = pointArray.length ;
		Position[] pointArray_translated = new Position[n] ;
		for(int i=0; i<n; i++){
			pointArray_translated[i] = pointArray[i].translateXY(dX, dY) ;
		}
		Polygon polygon_translated = new Polygon(objectName, layerMap, pointArray_translated) ;
		return polygon_translated;
	}

}
