package gds.elements.positioning.coordinates;

import ch.epfl.general_libraries.clazzes.ParamName;

public class Cartesian extends AbstractCoordinate {

	public Cartesian(
			@ParamName(name="X") double x,
			@ParamName(name="Y") double y
			){
		this.x = x ;
		this.y = y ;
		this.rho = Math.sqrt(x*x+y*y) ;
		this.phiRad = Math.atan2(x,y) ;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public double getRho() {
		return rho;
	}

	@Override
	public double getPhi() {
		return phiRad;
	}

}
