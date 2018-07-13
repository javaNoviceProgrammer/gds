package gds.elements.positioning.coordinates;

import ch.epfl.general_libraries.clazzes.ParamName;

public class Polar extends AbstractCoordinate {

	public Polar(
			@ParamName(name="Rho") double rho,
			@ParamName(name="Phi (degree)") double phiDegree
			) {
		this.rho = rho ;
		this.phiRad = phiDegree * Math.PI/180d ;
		this.x = rho * Math.cos(this.phiRad) ;
		this.y = rho * Math.sin(this.phiRad) ;
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
