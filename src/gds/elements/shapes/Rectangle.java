package gds.elements.shapes;

import gds.elements.AbstractElement;
import gds.elements.positioning.Position;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;

public class Rectangle extends AbstractElement {
	
	AbstractLayerMap[] layerMap ;
	
	public double width_um, length_um, angleDegree, angleRad ;
	public Position P1, P2, P3, P4 ; // position of edges
	public Position V1, V2, V3, V4 ; // position of vertices
	
	public Rectangle(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Waveguide Layer") AbstractLayerMap[] layerMap,
			@ParamName(name="Position of the start edge") Position P1,
			@ParamName(name="Width (micron)") double width_um,
			@ParamName(name="Length (micron)") double length_um,
			@ParamName(name="Orientation angle (degree)") double angleDegree
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.P1 = P1 ;
		this.width_um = width_um ;
		this.length_um = length_um ;
		this.angleDegree = angleDegree ;
		this.angleRad = angleDegree * Math.PI/180 ;
		setVertices() ;
		setEdges() ;
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
	
	private void setVertices(){
		Position V1prime = P1.translateY(-width_um/2) ;
		V1 = V1prime.rotate(P1, angleDegree) ;
		Position V2prime = V1prime.translateX(length_um) ;
		V2 = V2prime.rotate(P1, angleDegree) ;
		Position V3prime = V2prime.translateY(width_um) ;
		V3 = V3prime.rotate(P1, angleDegree) ;
		Position V4prime = P1.translateY(width_um/2) ;
		V4 = V4prime.rotate(P1, angleDegree) ;
	}
	
	private void setEdges(){
		P2 = new Position((V1.getX()+V2.getX())/2, (V1.getY()+V2.getY())/2) ;
		P3 = new Position((V2.getX()+V3.getX())/2, (V2.getY()+V3.getY())/2) ;
		P4 = new Position((V3.getX()+V4.getX())/2, (V3.getY()+V4.getY())/2) ;
	}
	
	public Position[] getAllCorners(){
		return new Position[] {V1, V2, V3, V4} ;
	}
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st00 = "## ---------------------------------------- ##" ;
		String st01 = "##           Adding a RECTANGLE             ##" ;
		String st02 = "## ---------------------------------------- ##" ;
		String[] args = {st00, st01, st02} ;
		int n = layerMap.length ;
		for(int i=0; i<n; i++){
			int layerNumber = layerMap[i].getLayerNumber() ;
			int dataType = layerMap[i].getDataType() ;
			String title = "### adding a "+ layerMap[i].getLayerName() +" layer" ;
			// first creating an object of type Rectangle from gdspy library
			String point1 = V1.rotate(P1, -angleDegree).getString() ;
			String point2 = V3.rotate(P1, -angleDegree).getString() ; 
			String st2 = objectName + " = gdspy.Rectangle(" + point1 + "," + point2 + "," + "layer=" + layerNumber + "," + "datatype=" + dataType + ")"  ;
			// adding the rotation of the waveguide
			String st3 = objectName + ".rotate(" + angleRad + "," + P1.getString() + ")" ;
			// then we need to add this object to the cell
			String st4 = topCellName + ".add(" + objectName + ")" ;
			args = MoreMath.Arrays.concat(args, new String[] {title, st2, st3, st4}) ;
			}
//		String st5 = "" ;
//		args = MoreMath.Arrays.concat(args, new String[] {st5}) ;
		return args ;
	}
	
	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String st00 = "" ;
		String[] args = {st00} ;
		int n = layerMap.length ;
		for(int i=0; i<n; i++){
			int layerNumber = layerMap[i].getLayerNumber() ;
			int dataType = layerMap[i].getDataType() ;
			String title = "### adding a "+ layerMap[i].getLayerName() +" layer" ;
			// first creating an object of type Rectangle from gdspy library
			String point1 = V1.rotate(P1, -angleDegree).getString() ;
			String point2 = V3.rotate(P1, -angleDegree).getString() ; 
			String st2 = objectName + " = gdspy.Rectangle(" + point1 + "," + point2 + "," + "layer=" + layerNumber + "," + "datatype=" + dataType + ")"  ;
			// adding the rotation of the waveguide
			String st3 = objectName + ".rotate(" + angleRad + "," + P1.getString() + ")" ;
			// then we need to add this object to the cell
			String st4 = topCellName + ".add(" + objectName + ")" ;
			args = MoreMath.Arrays.concat(args, new String[] {title, st2, st3, st4}) ;
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
		Position P1_translated = P1.translateXY(dX, dY) ;
		AbstractElement Rectangle_new = new Rectangle(objectName, layerMap, P1_translated, width_um, length_um, angleDegree) ;
		return Rectangle_new ;
	}

	@Override
	public AbstractElement translateXY(String newName, double dX, double dY) {
		Position P1_translated = P1.translateXY(dX, dY) ;
		AbstractElement Rectangle_new = new Rectangle(newName, layerMap, P1_translated, width_um, length_um, angleDegree) ;
		return Rectangle_new ;
	}

}
