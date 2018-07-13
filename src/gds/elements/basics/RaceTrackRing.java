package gds.elements.basics;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;
import gds.elements.AbstractElement;
import gds.elements.positioning.Port;
import gds.elements.positioning.Position;
import gds.elements.shapes.Path;
import gds.elements.shapes.path_elements.AbstractPathElement;
import gds.elements.shapes.path_elements.Segment;
import gds.elements.shapes.path_elements.Turn;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;

public class RaceTrackRing extends AbstractElement {

	/**
	 * For the race-track ring, we use PATH to create the object
	 */
	
	AbstractLayerMap[] layerMap ;
	double radius_um, width_um, length_um, angle_degree, angle_rad ;
	Center center ;
	Path raceTrack ;
	
	
	public RaceTrackRing(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Layer Map") AbstractLayerMap[] layerMap,
			@ParamName(name="Coordinates of the center") Center center,
			@ParamName(name="Width of the ring (um)") Entry width_um,
			@ParamName(name="Length of the ring (um)") Entry length_um,
			@ParamName(name="Radius of curvature (um)") Entry radius_um,
			@ParamName(name="Orientation Angle (degree)") Entry angle_degree
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.center = center ;
		this.width_um = width_um.getValue() ;
		this.length_um = length_um.getValue() ;
		this.radius_um = radius_um.getValue() ;
		this.angle_degree = angle_degree.getValue() ;
		this.angle_rad = angle_degree.getValue() * Math.PI/180 ;
		setPorts() ;
		createObject() ;
		saveProperties() ;
	}
	
	@Override
	public void setPorts(){
		objectPorts.put(objectName+".center", new Port(center.getCenter(), 0, 0)) ;
	}
	
	private void createObject(){
		Position initialP = center.getCenter().translateXY(-length_um/2, -radius_um) ;
		Segment wg1 = new Segment(length_um, 0) ;
		Segment wg2 = new Segment(length_um, 180) ;
		Turn turn1 = new Turn(radius_um, "ll") ;
		Turn turn2 = new Turn(radius_um, "ll") ;
		AbstractPathElement[] elements = {wg1, turn1, wg2, turn2} ;
		raceTrack = new Path(objectName, layerMap, new Port(initialP, width_um, angle_degree).connect(), elements) ;
	}
	
	@Override
	public void saveProperties(){
		objectProperties.put(objectName+".width_um", width_um) ;
		objectProperties.put(objectName+".length_um", length_um) ;
		objectProperties.put(objectName+".radius_um", radius_um) ;
	}
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##        Adding a RACE-TRACK RING          ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args = {st0, st1, st2} ;
		args = MoreMath.Arrays.concat(args, raceTrack.getPythonCode_no_header(fileName, topCellName)) ;
		return args;
	}
	
	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args = raceTrack.getPythonCode_no_header(fileName, topCellName) ;
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
		Center center_translated = center.translateXY(dX, dY) ;
		AbstractElement raceTrackRing_translated = new RaceTrackRing(objectName, layerMap, center_translated, new Entry(width_um), new Entry(length_um), new Entry(radius_um), new Entry(angle_degree)) ;
		return raceTrackRing_translated;
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
