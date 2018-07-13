package gds.elements.shapes.path_elements;

import gds.elements.positioning.Port;
import gds.elements.positioning.Position;
import gds.pdk.AbstractLayerMap;
import ch.epfl.general_libraries.utils.SimpleMap;

import java.util.Map;

public abstract class AbstractPathElement {
		
	public Port port1 = new Port(new Position(0,0), 0, 180) ;
	public Port port2 = port1.connect() ; // each path element has exactly two port
	
	public static int numElements = 0 ;
	public static Map<String, Port> elementPorts = new SimpleMap<String, Port>() ;
	
	public abstract String[] getPythonCode(String pathName, AbstractLayerMap layerMap) ;
	public abstract String[] getPythonCode_no_header(String pathName, AbstractLayerMap layerMap) ;
	public abstract String getElementName() ;
	
}
