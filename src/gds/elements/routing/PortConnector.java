package gds.elements.routing;

import gds.elements.AbstractElement;
import gds.elements.positioning.Port;
import gds.elements.positioning.Position;
import gds.elements.shapes.Path;
import gds.elements.shapes.path_elements.AbstractPathElement;
import gds.elements.shapes.path_elements.Arc;
import gds.elements.shapes.path_elements.Segment;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;

/**
 * This class gets two ports as inputs and creates a PATH connection between them using the PATH elements
 * need to know the start and end positions and the start and end angles.
 *  Different approaches can be taken:
 *  	1. Shortest PATH
 *  	2. No crossing
 *  	3. No overlap with other objects
 *  	4. Fully AUTOMATIC
 *  	5. Semi AUTOMATIC
 *  	6. MANUAL
 */

public class PortConnector extends AbstractElement {

	AbstractLayerMap[] layerMap ;
	Port port1 ; // this is the start port
	Port port2 ; // this is the end port
	double radius_um = 4.8 ; // radius of each turn
	double length_um = 5 ; // length of each segment
	public String tempMap ;
	public String[] map ;
	int wgCounter = 0 ;
	int turnCounter = 0 ;
	Path pathConnector ;
	public AbstractPathElement[] pathElements ;
	
	public PortConnector(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Waveguide Layer") AbstractLayerMap[] layerMap,
			@ParamName(name="Start Port") Port port1,
			@ParamName(name="End Port") Port port2,
			@ParamName(name="Connection MAP") String map
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.port1 = port1 ;
		this.port2 = port2 ;
		this.tempMap = map.toUpperCase() ; // turning to upper case

		setConnectionMapArray() ;
		setPorts() ;
		createObject() ;
		saveProperties() ;
		
	}
	
	public void setConnectionMapArray(){
		char[] temp = tempMap.toCharArray() ;
		int k=0, M=0 ;
		for(int i=0; i<temp.length; i++){
			if(temp[i]=='W' || temp[i]=='R' || temp[i]=='L'){M++;}
		}
		map = new String[M] ;
		for(int i=0; i<temp.length; i++){
//			if(temp[i]=='W') {map[k] = "W" ; k++ ; wgCounter++ ;}
			if(temp[i]=='R'){ map[k] = "R" ; k++ ; turnCounter++ ;}
			else if(temp[i]=='L'){ map[k] = "L" ; k++ ; turnCounter++ ;}
			else{map[k] = "W" ; k++ ; wgCounter++ ;}
		}
	}
	
	@Override
	public void setPorts() {

	}
	
	public double setAngleDegree_TR(double startAngleDegree){
		// set the start angle between 0 and 360
		Position z = new Position(Math.cos(startAngleDegree*Math.PI/180), Math.sin(startAngleDegree*Math.PI/180)) ;
		double angleDegree = 0 ;
		if(z.getPhi_degree() == 0){angleDegree = 90; }
		else if(z.getPhi_degree()>0 && z.getPhi_degree()<90){angleDegree = 90-z.getPhi_degree() ; }
		else if(z.getPhi_degree() == 90){angleDegree = 90 ; }
		else if(z.getPhi_degree()>90 && z.getPhi_degree()<180){angleDegree = z.getPhi_degree()-90; }
		else if(z.getPhi_degree() == -90){angleDegree = 90 ; }
		else if(z.getPhi_degree()>-90 && z.getPhi_degree()<0){angleDegree = z.getPhi_degree()+90; }
		else if(z.getPhi_degree()== -180 || z.getPhi_degree() == 180){angleDegree = 90; }
		else if(z.getPhi_degree()>-180 && z.getPhi_degree()<-90){angleDegree = z.getPhi_degree()+180 ; }
		return angleDegree ;
	}
	
	public double setAngleDegree_TL(double startAngleDegree){
		// set the start angle between 0 and 360
		Position z = new Position(Math.cos(startAngleDegree*Math.PI/180), Math.sin(startAngleDegree*Math.PI/180)) ;
		double angleDegree = 0 ;
		if(z.getPhi_degree() == 0){angleDegree = 90; }
		else if(z.getPhi_degree()>0 && z.getPhi_degree()<90){angleDegree = 90-z.getPhi_degree() ; }
		else if(z.getPhi_degree() == 90){angleDegree = 90 ; }
		else if(z.getPhi_degree()>90 && z.getPhi_degree()<180){angleDegree = 180-z.getPhi_degree(); }
		else if(z.getPhi_degree() == -90){angleDegree = 90 ; }
		else if(z.getPhi_degree()>-90 && z.getPhi_degree()<0){angleDegree = -z.getPhi_degree(); }
		else if(z.getPhi_degree()== -180 || z.getPhi_degree() == 180){angleDegree = 90; }
		else if(z.getPhi_degree()>-180 && z.getPhi_degree()<-90){angleDegree = -z.getPhi_degree()-90 ; }
		return angleDegree ;
	}
	
	// needs to be corrected.....
	public void createObject(){
		// creating the connector elements
		int N = turnCounter + wgCounter ;
		double startAngleDegree = port1.getNormalDegree() ;
		pathElements = new AbstractPathElement[N] ;
		for(int i=0; i<N; i++){
			if(map[i].equals("W")){
				pathElements[i] = new Segment(length_um) ;
				}
			else if(map[i].equals("R")){
				pathElements[i] = new Arc(radius_um, true, setAngleDegree_TR(startAngleDegree)) ;
				startAngleDegree = startAngleDegree - setAngleDegree_TR(startAngleDegree) ;
				} 
			else if(map[i].equals("L")){
				pathElements[i] = new Arc(radius_um, false, setAngleDegree_TL(startAngleDegree)) ;
				startAngleDegree = startAngleDegree + setAngleDegree_TL(startAngleDegree) ;
				}
		}

		// finally creating the path connector
		pathConnector = new Path(objectName, layerMap, port1, pathElements) ;
	}
	
	@Override
	public void saveProperties() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##     Adding a PORT-To-PORT CONNECTION     ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args = new String[] {st0, st1, st2} ;
		args = MoreMath.Arrays.concat(args, pathConnector.getPythonCode_no_header(fileName, topCellName)) ;
		return args;
	}

	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args = new String[0] ;
		args = MoreMath.Arrays.concat(args, pathConnector.getPythonCode_no_header(fileName, topCellName)) ;
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
		Port port2_translated = port2.translateXY(dX, dY) ;
		AbstractElement portConnector_translated = new PortConnector(objectName, layerMap, port1_translated, port2_translated, tempMap) ;
		return portConnector_translated ;
	}

	@Override
	public AbstractElement translateXY(String newName, double dX, double dY) {
		Port port1_translated = port1.translateXY(dX, dY) ;
		Port port2_translated = port2.translateXY(dX, dY) ;
		AbstractElement portConnector_translated = new PortConnector(newName, layerMap, port1_translated, port2_translated, tempMap) ;
		return portConnector_translated ;
	}

}
