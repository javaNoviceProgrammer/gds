package gds.elements.basics;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;
import gds.elements.AbstractElement;
import gds.elements.positioning.Port;
import gds.elements.positioning.Position;
import gds.elements.shapes.Rectangle;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;

public class Frame extends AbstractElement {
	
	/**
	 * AIM SUNY Frame: width = 30um, Lx (inside) = 6000um, Ly (inside) = 8500um
	 * Layer Map: XXXFILL (7 layers), XXXCHE (3 layers), DIA, MR1A, MR2A --> total = 13 layers
	 */

	AbstractLayerMap[] layerMap ;
	double width_um, lx_um, ly_um ;
	public Position P1, P2, P3, P4 ;
	public Position Q1, Q2, Q3, Q4 ;
	public Position center ;
	Rectangle rect1, rect2, rect3, rect4, rect5, rect6, rect7, rect8 ;
	
	public Frame(
			@ParamName(name="Object Name") String objectName,
			@ParamName(name="Waveguide Layer") AbstractLayerMap[] layerMap,
			@ParamName(name="Coordinates of the start Corner") Position P1,
			@ParamName(name="Thickness of the frame (um)") Entry width_um,
			@ParamName(name="Horizontal length (um)") Entry lx_um,
			@ParamName(name="Vertical length (um)") Entry ly_um
			){
		this.objectName = objectName ;
		this.layerMap = layerMap ;
		this.P1 = P1 ;
		this.width_um = width_um.getValue() ;
		this.lx_um = lx_um.getValue() ;
		this.ly_um = ly_um.getValue() ;
		setPorts() ;
		createFrame() ;
		saveProperties() ;
	}
	
	@Override
	public void setPorts(){
		P2 = P1.translateX(lx_um) ;
		P3 = P2.translateY(ly_um) ;
		P4 = P3.translateX(-lx_um) ;
		Q1 = P1.translateXY(-width_um, -width_um) ;
		Q2 = P2.translateXY(width_um, -width_um) ;
		Q3 = P3.translateXY(width_um, width_um) ;
		Q4 = P4.translateXY(-width_um, width_um) ;
		center = P1.translateXY(lx_um/2, ly_um/2) ;
		
		// adding the positions of corners to the data base
		objectPorts.put(objectName+".port1", new Port(P1, width_um, 0)) ;
		objectPorts.put(objectName+".port2", new Port(P2, width_um, 0)) ;
		objectPorts.put(objectName+".port3", new Port(P3, width_um, 0)) ;
		objectPorts.put(objectName+".port4", new Port(P4, width_um, 0)) ;
	}
	
	@Override
	public void saveProperties(){
		objectProperties.put(objectName+".thickness_um", width_um) ;
		objectProperties.put(objectName+".length_um", lx_um) ;
		objectProperties.put(objectName+".width_um", ly_um) ;
	}
	
	public void createFrame(){
		// need to create 8 rectangles
		 rect1 = new Rectangle(objectName+"_rect1", layerMap, Q1.translateY(width_um/2), width_um, width_um, 0) ;
		 rect2 = new Rectangle(objectName+"_rect2", layerMap, P1.translateY(-width_um/2), width_um, lx_um, 0) ;
		 rect3 = new Rectangle(objectName+"_rect3", layerMap, P2.translateY(-width_um/2), width_um, width_um, 0) ;
		 rect4 = new Rectangle(objectName+"_rect4", layerMap, P2.translateX(width_um/2), width_um, ly_um, 90) ;
		 rect5 = new Rectangle(objectName+"_rect5", layerMap, P3.translateY(width_um/2), width_um, width_um, 0) ;
		 rect6 = new Rectangle(objectName+"_rect6", layerMap, P4.translateY(width_um/2), width_um, lx_um, 0) ;
		 rect7 = new Rectangle(objectName+"_rect7", layerMap, Q4.translateY(-width_um/2), width_um, width_um, 0) ;
		 rect8 = new Rectangle(objectName+"_rect8", layerMap, P1.translateX(-width_um/2), width_um, ly_um, 90) ;
	}
	
	
	@Override
	public String[] getPythonCode(String fileName, String topCellName) {
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##              Adding a FRAME              ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args = {st0, st1, st2} ;
		args = MoreMath.Arrays.concat(args, rect1.getPythonCode(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, rect2.getPythonCode(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, rect3.getPythonCode(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, rect4.getPythonCode(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, rect5.getPythonCode(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, rect6.getPythonCode(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, rect7.getPythonCode(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, rect8.getPythonCode(fileName, topCellName)) ;
		return args ;
	}
	
	@Override
	public String[] getPythonCode_no_header(String fileName, String topCellName) {
		String[] args = {} ;
		args = MoreMath.Arrays.concat(args, rect1.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, rect2.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, rect3.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, rect4.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, rect5.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, rect6.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, rect7.getPythonCode_no_header(fileName, topCellName)) ;
		args = MoreMath.Arrays.concat(args, rect8.getPythonCode_no_header(fileName, topCellName)) ;
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
		AbstractElement frame_translated = new Frame(objectName, layerMap, P1_translated, new Entry(width_um), new Entry(lx_um), new Entry(ly_um)) ;
		return frame_translated ;
	}

}
