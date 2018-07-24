package gds.elements.basics;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;
import gds.elements.AbstractElement;
import gds.elements.positioning.Position;
import gds.pdk.AbstractLayerMap;

public class Disk extends AbstractElement {

	/** 
	 * Disk is essentially a ring with inner radius set to zero.
	 * The outer radius is the radius of the disk.
	 */
	
	AbstractLayerMap[] layerMap ;
	Position center ;
	double radius_um, angle_degree, angle_rad, startAngle_degree, startAngle_rad ;
	AbstractElement ringAsDisk ;
	Center centerPosition ;
	
	public Disk(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Waveguide Layer") AbstractLayerMap[] layerMap,
			@ParamName(name="Choose Center") Center centerPosition,
			@ParamName(name="Radius (um)") Entry radius_um // from center to the edge of the disk
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.centerPosition = centerPosition ;
		this.radius_um = radius_um.getValue() ;
		this.angle_degree = 360 ;
		this.angle_rad = angle_degree*Math.PI/180D ;
		this.startAngle_degree = 0 ;
		this.startAngle_rad = this.startAngle_degree*Math.PI/180D ;
		setPorts() ;
		createDisk() ;
		saveProperties() ;
	}
	
	public Disk(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Waveguide Layer") AbstractLayerMap[] layerMap,
			@ParamName(name="Choose Center") Center centerPosition,
			@ParamName(name="Radius (um)") Entry radius_um, // from center to the edge of the disk
			@ParamName(name="Angle (degree)") Entry angle_degree,
			@ParamName(name="Start angle (degree)") Entry startAngle_degree
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.centerPosition = centerPosition ;
		this.radius_um = radius_um.getValue() ;
		this.angle_degree = angle_degree.getValue() ;
		this.angle_rad = this.angle_degree * Math.PI/180D ;
		this.startAngle_degree = startAngle_degree.getValue() ;
		this.startAngle_rad = this.startAngle_degree*Math.PI/180D ;
		setPorts() ;
		createDisk() ;
		saveProperties() ;
	}
	
	@Override
	public void setPorts(){
		center = centerPosition.getCenter() ;
	}
	
	public void createDisk(){
		// creating a new ring with zero inner radius
	//	ringAsDisk = new Ring(objectName, layerMap, new Ring.Center(center), new Entry(radius_um), new Entry(radius_um/2)) ;
		ringAsDisk = new Ring(objectName, layerMap, new Ring.Center(center), new Entry(startAngle_degree), new Entry(radius_um), new Entry(radius_um/2), new Entry(angle_degree)) ;
	}
	
	@Override
	public void saveProperties(){
		objectProperties.put(objectName+".radius_um", radius_um) ;
		objectProperties.put(objectName+".center.x", center.getX()) ;
		objectProperties.put(objectName+".center.y", center.getY()) ;
		
		allElements.put(objectName, this) ;
	}
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##             Adding a DISK                ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args = ringAsDisk.getPythonCode(fileName, topCellName) ;
		args[0] = st0 ;
		args[1] = st1 ;
		args[2] = st2 ;
		return args ;
	}

	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args = ringAsDisk.getPythonCode_no_header(fileName, topCellName) ;
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
		Center center_translated = centerPosition.translateXY(dX, dY) ;
		AbstractElement disk_new = new Disk(objectName, layerMap, center_translated, new Entry(radius_um)) ;
		return disk_new ;
	}
	
	@Override
	public AbstractElement translateXY(String newName, double dX, double dY) {
		Center center_translated = centerPosition.translateXY(dX, dY) ;
		AbstractElement disk_new = new Disk(newName, layerMap, center_translated, new Entry(radius_um)) ;
		return disk_new ;
	}
	
	//************************************ defining and creating the ports ************
	public static class Center{

		Position P ;
		
		public Center(Position P){
			this.P = P ;
		}
		
		public Center(@ParamName(name="Reference to Other Objects") String objectPort,
				  @ParamName(name="Offset X (um)", default_="0") double offsetX_um,
				  @ParamName(name="Offset Y (um)", default_="0") double offsetY_um
				  ){
			this.P = objectPorts.get(objectPort).getPosition().translateXY(offsetX_um, offsetY_um) ;
		}
		
		public Position getCenter() {
			return P;
		}
		
		public Center translateXY(double dx, double dy){
			return new Center(P.translateXY(dx, dy)) ;
		}
		
	}
	
	//********************************************************

}
