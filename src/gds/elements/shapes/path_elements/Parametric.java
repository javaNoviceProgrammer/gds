package gds.elements.shapes.path_elements;

import gds.elements.positioning.Port;
import gds.elements.positioning.Position;
import gds.pdk.AbstractLayerMap;
import gds.util.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class Parametric extends AbstractPathElement {

	/**
	 * gdspy PATH: parametric(curve_function, curve_derivative=None, number_of_evaluations=99, max_points=199, final_width=None, final_distance=None, layer=0, datatype=0)
	 * this class creates a path based on a given parametric curve: (x,y) = (X(t), Y(t)) where t is the parameter.
	 */
	
	String Xt, Yt ;
	String Xt_matlab, Yt_matlab ;
	Expression xt, yt ; // these are the for evaluating matlab strings
	int numEvals = 1000 ;
	String functionName = "curvature_function" ;
	String finalWidth_um ;
	
	// WITH taper
	public Parametric(
			@ParamName(name="Curve X(t) function [0<t<1] --> Matlab Math") String Xt_matlab,
			@ParamName(name="Curve Y(t) function [0<t<1] --> Matlab Math") String Yt_matlab,
			@ParamName(name="Curve X(t) function [0<t<1] --> Python Math") String Xt,
			@ParamName(name="Curve Y(t) function [0<t<1] --> Python Math") String Yt,
			@ParamName(name="Final Width (micron) [Taper]") double finalWidth_um
			){
		numElements++ ;
		if(numElements != 1) {port1 = elementPorts.get("last.port2").connect() ;}
		this.Xt = Xt ;
		this.Yt = Yt ;
		this.Xt_matlab = Xt_matlab ;
		this.Yt_matlab = Yt_matlab ;
		xt = new ExpressionBuilder(Xt_matlab).variable("t").build() ;
		yt = new ExpressionBuilder(Yt_matlab).variable("t").build() ;
		this.finalWidth_um = finalWidth_um + "" ;
		// calculating port 2
		double vec_x = xt.setVariable("t", 1).evaluate()-xt.setVariable("t", 0).evaluate() ;
		double vec_y = yt.setVariable("t", 1).evaluate()-yt.setVariable("t", 0).evaluate() ;
		Position vec = new Position(vec_x, vec_y) ; // this is the translation vector
		// now I need the normal direction
		double dx = 1e-3, dy = 1e-3 ;
		double norm_vec_x = xt.setVariable("t", 1).evaluate()-xt.setVariable("t", 1-dx).evaluate() ;
		double norm_vec_y = yt.setVariable("t", 1).evaluate()-yt.setVariable("t", 1-dy).evaluate() ;
		Position norm_vec = new Position(norm_vec_x, norm_vec_y) ;
		port2 = new Port(port1.getPosition().translateXY(vec), port1.getWidthMicron(), norm_vec.getPhi() * 180/Math.PI) ;
		updateLastElement() ;
	}
	
	// WITHOUT taper
	public Parametric(
			@ParamName(name="Curve X(t) function [0<t<1] --> Matlab Math") String Xt_matlab,
			@ParamName(name="Curve Y(t) function [0<t<1] --> Matlab Math") String Yt_matlab,
			@ParamName(name="Curve X(t) function [0<t<1] --> Python Math") String Xt,
			@ParamName(name="Curve Y(t) function [0<t<1] --> Python Math") String Yt
			){
		numElements++ ;
		if(numElements != 1) {port1 = elementPorts.get("last.port2").connect() ;}
		this.Xt = Xt ;
		this.Yt = Yt ;
		this.Xt_matlab = Xt_matlab ;
		this.Yt_matlab = Yt_matlab ;
		xt = new ExpressionBuilder(Xt_matlab).variable("t").build() ;
		yt = new ExpressionBuilder(Yt_matlab).variable("t").build() ;
		this.finalWidth_um = "None" ;
		// calculating port 2
		double vec_x = xt.setVariable("t", 1).evaluate()-xt.setVariable("t", 0).evaluate() ;
		double vec_y = yt.setVariable("t", 1).evaluate()-yt.setVariable("t", 0).evaluate() ;
		Position vec = new Position(vec_x, vec_y) ; // this is the translation vector
		// now I need the normal direction
		double dx = 1e-3, dy = 1e-3 ;
		double norm_vec_x = xt.setVariable("t", 1).evaluate()-xt.setVariable("t", 1-dx).evaluate() ;
		double norm_vec_y = yt.setVariable("t", 1).evaluate()-yt.setVariable("t", 1-dy).evaluate() ;
		Position norm_vec = new Position(norm_vec_x, norm_vec_y) ;
		port2 = new Port(port1.getPosition().translateXY(vec), port1.getWidthMicron(), norm_vec.getPhi()) ;
		updateLastElement() ;
	}
	
	private void updateLastElement(){
		elementPorts.put("last.port1", port1) ;
		elementPorts.put("last.port2", port2) ;
	}
	
	private String[] getFunction(){
//		String st0 = "### creating curvature function " ;
		String st1 = "def " + functionName + "(t)" + ":" ;
		String st2 = "	return " + "(" + Xt + "," + Yt + ")" ;
		String[] args = {st1, st2} ;
		return args ;
	}

	@Override
	public String[] getPythonCode(String pathName, AbstractLayerMap layerMap) {
		String st0 = "### adding a PARAMETRIC" ;
		String[] args = {st0} ;
		int layerNumber = layerMap.getLayerNumber() ;
		int dataType = layerMap.getDataType() ;
		// adding the function of curvature
		args = MoreMath.Arrays.concat(args, getFunction()) ;
		// now adding the parametric curve
		String st1 = pathName + ".parametric(" + functionName + "," + "curve_derivative=None" + "," + "number_of_evaluations=" + numEvals + "," + "max_points=1000" + "," + "final_width=" + 
					 						finalWidth_um + "," + "final_distance=None"+ ", " + "layer=" + layerNumber + "," + "datatype=" + dataType + ")" ;
		args = MoreMath.Arrays.concat(args, new String[] {st1}) ;
		return args;
	}
	
	@Override
	public String[] getPythonCode_no_header(String pathName, AbstractLayerMap layerMap) {
		String[] args = new String[0] ;
		int layerNumber = layerMap.getLayerNumber() ;
		int dataType = layerMap.getDataType() ;
		// adding the function of curvature
		args = MoreMath.Arrays.concat(args, getFunction()) ;
		// now adding the parametric curve
		String st1 = pathName + ".parametric(" + functionName + "," + "curve_derivative=None" + "," + "number_of_evaluations=" + numEvals + "," + "max_points=1000" + "," + "final_width=" + 
					 						finalWidth_um + "," + "final_distance=None"+ ", " + "layer=" + layerNumber + "," + "datatype=" + dataType + ")" ;
		args = MoreMath.Arrays.concat(args, new String[] {st1}) ;
		return args;
	}

	@Override
	public String getElementName() {
		return "Parametric" ;
	}

}
