package gds.elements;

import gds.elements.positioning.Port;
import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.utils.SimpleMap;

import java.util.Map;

public abstract class DataBase {

	
	public static Map<String, Port> objectPorts = new SimpleMap<String, Port>() ; // this map contains all ports of all objects (use: "objectName.portNumber")
	public static Map<String, Double> objectProperties = new SimpleMap<String, Double>() ; // this map for storing properties of each object
	public static Map<String, AbstractElement> allElements = new SimpleMap<String, AbstractElement>() ; // all created elements
	
	public static class Entry{
		double e ;
		public Entry(
				@ParamName(name="Value") double entry
				){
			this.e = entry ;
		}
		public Entry(
				@ParamName(name="Reference to other Objects") String entry, // Reference: [ObjectName.Parameter_Unit]
				@ParamName(name="Multiplier [+/-]", default_="1") double multiplier,
				@ParamName(name="Offset [+/-]", default_="0") double offset
				){
			this.e = objectProperties.get(entry) * multiplier + offset ;
		}
		public double getValue(){
			return e ;
		}
	}
	
	
	public static void clear(){
		objectPorts.clear(); 
		objectProperties.clear();
		allElements.clear();
	}
	
}
