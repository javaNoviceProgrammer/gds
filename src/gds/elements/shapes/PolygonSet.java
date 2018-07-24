package gds.elements.shapes;

import gds.elements.AbstractElement;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;

public class PolygonSet extends AbstractElement {

	/**
	 * 	----> class gdspy.PolygonSet(polygons, layer=0, datatype=0, verbose=True)
	 */
	
	AbstractLayerMap[] layerMap ;
	
	STring[] polygonArray ; // an array of all vertices
	
	public PolygonSet(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Layer Map") AbstractLayerMap[] layerMap,
			@ParamName(name="Vector of Vertices [(x1,y1), ...]") STring[] polygonArray
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.polygonArray = polygonArray ;
		setPorts() ;
		saveProperties() ;
	}
	
	@Override
	public void setPorts(){
		// nothing to be done
	}
	
	@Override
	public void saveProperties(){
		
	}
	
	private String getAllVertices(){
		int n = polygonArray.length ;
		String st = "[" ;
		for(int i=0; i<n-1; i++){
			st += polygonArray[i].getCoordinates() + "," ;
		}
		st += polygonArray[n-1].getCoordinates() + "]" ;
		return st ;
	}
	
	
	public static class STring{
		String coordinates ;
		public STring(
				@ParamName(name="Enter coordinates") String coordinates
				){
			this.coordinates = coordinates ;
		}
		
		public String getCoordinates(){
			String st = "[" + coordinates + "]";
			return st ;
		}
	}

	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st00 = "## ---------------------------------------- ##" ;
		String st01 = "##       Adding a new POLYGON-SET           ##" ;
		String st02 = "## ---------------------------------------- ##" ;
		String[] args = {st00, st01, st02} ;
		int m = layerMap.length ;
		for(int j=0; j<m; j++){
			// defining the layer of the object
			int layerNumber = layerMap[j].getLayerNumber() ;
			int dataType = layerMap[j].getDataType() ;
			// creating the PolygonSet object
			String title = "### adding a "+ layerMap[j].getLayerName() +" layer" ;
			String st2 = objectName + " = gdspy.PolygonSet(" ;
			st2 += getAllVertices() + "," ;
			st2 += "layer=" + layerNumber + "," + "datatype=" + dataType + "," + "verbose = False)" ;
			String st3 = topCellName + ".add(" + objectName + ")" ;
			args = MoreMath.Arrays.concat(args, new String[] {title, st2, st3}) ;
		}
		return args;
	}
	
	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String st00 = "" ;
		String[] args = {st00} ;
		int m = layerMap.length ;
		for(int j=0; j<m; j++){
			// defining the layer of the object
			int layerNumber = layerMap[j].getLayerNumber() ;
			int dataType = layerMap[j].getDataType() ;
			// creating the PolygonSet object
			String title = "### adding a "+ layerMap[j].getLayerName() +" layer" ;
			String st2 = objectName + " = gdspy.PolygonSet(" ;
			st2 += getAllVertices() + "," ;
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
	public AbstractElement translateXY(double dX, double dY) {// need to think about this one!!
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractElement translateXY(String newName, double dX, double dY) {
		// TODO Auto-generated method stub
		return null;
	}


	
	
}
