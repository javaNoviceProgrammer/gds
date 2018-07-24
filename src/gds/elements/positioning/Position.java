package gds.elements.positioning;

import gds.elements.positioning.coordinates.AbstractCoordinate;
import gds.util.Complex;
import gds.util.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;

public class Position {

	/**
	 * This class is position coordinates of a point
	 */

	double x, y ;
	double rho, phi ;

	public Position(
			@ParamName(name="X") double x,
			@ParamName(name="Y") double y
			){
		this.x = x ;
		this.y = y ;
		Complex c = new Complex(x,y) ;
		this.rho = c.abs() ;
		this.phi = c.phaseMinusPiToPi() ;
	}

	public Position(
			AbstractCoordinate coordinates
			){
		this.x = coordinates.getX() ;
		this.y = coordinates.getY() ;
		this.rho = coordinates.getRho() ;
		this.phi = coordinates.getPhi() ;
	}

	public void Polar(double rho, double phi){
		this.x = rho * Math.cos(phi) ;
		this.y = rho * Math.sin(phi) ;
	}

	public Position resize(double s){
		double x = s*Math.cos(phi) ;
		double y = s*Math.sin(phi) ;
		return new Position(x,y) ;
	}

	public Position scale(double s){
		return new Position(x*s, y*s) ;
	}

	// adding setters --> although not recommended

	public void setX(double x){
		this.x = x;
	}

	public void setY(double y){
		this.y = y ;
	}

	public void setXY(double x, double y){
		this.x = x ;
		this.y = y ;
	}


	// now getters for reading coordinates

	public double getX(){
		return x ;
	}

	public double getY(){
		return y ;
	}

	public double[] getXY(){
		return new double[] {x, y} ;
	}

	public double getRho(){
		return rho ;
	}

	public double getMagnitude(){
		return rho ;
	}

	public double getPhi(){
		return phi ;
	}

	public double getPhi_degree(){
		return phi * 180/Math.PI ;
	}

	public double[] getRhoPhi(){
		return new double[] {rho, phi} ;
	}

	// Translating a point by an offset

	public Position translateX(double DX){
		return new Position(x+DX, y) ;
	}

	public Position translateY(double DY){
		return new Position(x, y+DY) ;
	}

	public Position translateXY(double DX, double DY){
		return new Position(x+DX, y+DY) ;
	}

	public Position translateX(Position P){
		return new Position(x+P.getX(), y) ;
	}

	public Position translateY(Position P){
		return new Position(x, y+P.getY()) ;
	}

	public Position translateXY(Position P){
		return new Position(x+P.getX(), y+P.getY()) ;
	}

	public Position getUnitVector(){
		return this.resize(1) ;
	}

	// Rotation around a specific origin
	public Position rotate(Position P, double angleDegree){
		double angleRad = MoreMath.Conversions.Angles.toRadian(angleDegree) ;
		Position relVec = new Position(x-P.getX(), y-P.getY()) ;
		double xRotate = Math.cos(angleRad)*relVec.getX() - Math.sin(angleRad)*relVec.getY() ;
		double yRotate = Math.sin(angleRad)*relVec.getX() + Math.cos(angleRad)*relVec.getY() ;
		Position rotateVec = new Position(xRotate, yRotate) ;
		return P.translateXY(rotateVec) ;
	}

	// Evolving the point by Rotation around a specific origin
	public void selfRotate(Position P, double angleDegree){
		double angleRad = MoreMath.Conversions.Angles.toRadian(angleDegree) ;
		Position relVec = new Position(x-P.getX(), y-P.getY()) ;
		double xRotate = Math.cos(angleRad)*relVec.getX() - Math.sin(angleRad)*relVec.getY() ;
		double yRotate = Math.sin(angleRad)*relVec.getX() + Math.cos(angleRad)*relVec.getY() ;
		Position rotateVec = new Position(xRotate, yRotate) ;
		this.x =  P.translateXY(rotateVec).getX() ;
		this.y = P.translateXY(rotateVec).getY() ;
	}

	// Returning the string of the coordinate: (x,y)
	public String getString(){
		String st = "(" + x + "," + y + ")" ;
		return st ;
	}
	
	public String toString() {
		return getString() ;
	}

	//******* main method for test purposes ***********
	public static void main(String[] args) {
		Position P = new Position(-10,0) ;
		System.out.println(P.getString()) ;
		System.out.println(P.getUnitVector().getString());
	}


}
