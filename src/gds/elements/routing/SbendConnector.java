package gds.elements.routing;

import gds.elements.AbstractElement;
import gds.elements.basics.Sbend;
import gds.elements.positioning.Port;
import gds.elements.positioning.Position;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;

public class SbendConnector extends AbstractElement {

	AbstractLayerMap[] layerMap ;
	Port port1 ; // this is the start port
	Port port2 ; // this is the end port
	double lx_um, ly_um, phi, hOffset_um, vOffset_um ;
	Sbend bend ;
	
	public SbendConnector(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Waveguide Layer") AbstractLayerMap[] layerMap,
			@ParamName(name="Start Port") Port port1, 
			@ParamName(name="End Port") Port port2
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		// these are the ports of the sbend connector, not the two objects that it connects them
		this.port1 = port1 ;
		this.port2 = port2 ;
		
		setPorts() ;
		createObject() ;
		saveProperties() ;
	}
	
	@Override
	public void setPorts() {
		lx_um = port2.getPosition().getX() - port1.getPosition().getX() ;
		ly_um = port2.getPosition().getY() - port1.getPosition().getY() ;
		Position vec12 = new Position (lx_um, ly_um) ;
		Position nVec = port1.connect().getNormalVec() ;
		phi = vec12.getPhi() - nVec.getPhi() ;
		hOffset_um = vec12.getMagnitude()*Math.cos(phi) ;
		hOffset_um = Math.abs(hOffset_um) ; // horizontal offset is always positive
		if(lx_um>0){
			vOffset_um = vec12.getMagnitude()*Math.sin(phi) ;
		}
		else{
			vOffset_um = -1*vec12.getMagnitude()*Math.sin(phi) ;
		}
	}
	
	public void createObject(){
		bend = new Sbend(objectName, layerMap, port1, new Entry(hOffset_um), new Entry(vOffset_um)) ; 
	}
	
	@Override
	public void saveProperties() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##      Adding a Sbend CONNECTION           ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args = new String[] {st0, st1, st2} ;
		args = MoreMath.Arrays.concat(args, bend.getPythonCode_no_header(fileName, topCellName)) ;
		return args;
	}

	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args = new String[0] ;
		args = MoreMath.Arrays.concat(args, bend.getPythonCode_no_header(fileName, topCellName)) ;
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
		AbstractElement bendConnector_translated = new SbendConnector(objectName, layerMap, port1_translated, port2_translated) ;
		return bendConnector_translated ;
	}
	
}
