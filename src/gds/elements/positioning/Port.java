package gds.elements.positioning;

import gds.elements.DataBase;
import ch.epfl.general_libraries.clazzes.ParamName;

public class Port extends DataBase {

	/**
	 * A port is uniquely described by its position, width, and normal direction.
	 * Note that the layer map info comes from the object, not the port.
	 * The edge vector is always 90 degree Clockwise rotation of the normal vector. (This is just my convention)
	 */
	
	Position P ;
	double width_um ;
	double angle_degree ;
	double angle_rad ; 
	double normal_degree ;
	double normal_rad ;
	Position normalVec ;
	Position edgeVec ;
	
	public Port(
			@ParamName(name="Position") Position P,
			@ParamName(name="Width (um)") double width_um,
			@ParamName(name="Normal Angle (degree)") double normal_degree
			){
		this.P = P ;
		this.width_um = width_um ;
		this.normal_degree = normal_degree ;
		normal_rad = normal_degree * Math.PI/180 ;
		angle_degree = normal_degree + 90 ;
		angle_rad = angle_degree * Math.PI/180 ;
		normalVec = new Position(Math.cos(normal_rad), Math.sin(normal_rad)) ;
		edgeVec = normalVec.rotate(new Position(0,0), 90) ;
	}
	
	public Port(
			@ParamName(name="Reference to Other Objects") String objectPort,
			@ParamName(name="Offset X (um)", default_="0") double offsetX_um,
			@ParamName(name="Offset Y (um)", default_="0") double offsetY_um
			){
		P = objectPorts.get(objectPort).connect().getPosition().translateXY(offsetX_um, offsetY_um) ;
		width_um = objectPorts.get(objectPort).connect().getWidthMicron() ;
		normal_degree = objectPorts.get(objectPort).connect().getNormalDegree() ;
		normal_rad = objectPorts.get(objectPort).connect().getNormalRad() ;
		angle_degree = objectPorts.get(objectPort).connect().getAngleDegree() ;
		angle_rad = objectPorts.get(objectPort).connect().getAngleRad() ;
		normalVec = objectPorts.get(objectPort).connect().getNormalVec() ;
		edgeVec = objectPorts.get(objectPort).connect().getEdgeVec() ;
	}
	
	
	// getters to retrieve the information about the port
	
	public Position getPosition(){
		return P ;
	}
	
	public double getWidthMicron(){
		return width_um ;
	}
	
	public double getAngleDegree(){
		return angle_degree ;
	}
	
	public double getNormalDegree(){
		return normal_degree ;
	}
	
	public double getAngleRad(){
		return angle_rad ;
	}
	
	public double getNormalRad(){
		return normal_rad ;
	}
	
	public Position getNormalVec(){
		return normalVec ;
	}
	
	public Position getEdgeVec(){
		return edgeVec ;
	}
	
	// Self Operations that mutate the existing port
	
	public void selfTranslateXY(double dX, double dY){
		this.P = P.translateXY(dX, dY) ;
	}
	
	public void selfRotate(Position center, double angle_degree){
		this.P = this.rotate(center, angle_degree).P ;
		this.width_um = this.rotate(center, angle_degree).width_um ;
		this.angle_degree = this.rotate(center, angle_degree).angle_degree ;
		this.angle_rad = this.rotate(center, angle_degree).angle_rad ;
		this.normal_degree = this.rotate(center, angle_degree).normal_degree ;
		this.normal_rad = this.rotate(center, angle_degree).normal_rad ;
		this.normalVec = this.rotate(center, angle_degree).normalVec ;
		this.edgeVec = this.rotate(center, angle_degree).edgeVec ;
	}
	
	
	// now methods to manipulating the ports
	
	public Port translateXY(double dX, double dY){
		Position P_translated = P.translateXY(dX, dY) ;
		return new Port(P_translated, width_um, normal_degree) ;
	}
	
	public Port resize(double width_um_new){
		return new Port(this.getPosition(), width_um_new, this.getNormalDegree()) ;
	}
	
	public Port scale(double s){
		return new Port(this.getPosition(), s*this.getWidthMicron(), this.getNormalDegree()) ;
	}
	
	public Port translateXY(Position vec){
		Position P_translated = P.translateXY(vec) ;
		return new Port(P_translated, width_um, normal_degree) ;
	}
	
	public Port rotate(Position center, double angle_degree){
		Position P_rotated = P.rotate(center, angle_degree) ; // first need to rotate the position of the 
		Position N = P.translateXY(normalVec) ;
		N.selfRotate(center, angle_degree);
		Position normalVec_rotated = N.translateXY(-P_rotated.getX(), -P_rotated.getY()) ;
		double normal_rad_rotated = normalVec_rotated.getPhi() ;
		double normal_degree_rotated = normal_rad_rotated * 180/Math.PI ;
		return new Port(P_rotated, width_um, normal_degree_rotated) ;
	}
	
	public Port connect(){
		return new Port(P, width_um, normal_degree+180) ; // when connecting all the properties copy. Normal angle changes 180 degree
	}
	
	
	public String getString(){
		String st0 = "[Position= " + P.getString() + ", " + "Width= " + width_um + ", Normal= " + normal_degree + ", Edge= " + angle_degree + "]" ;
		return st0 ;
	}
	
	
	//*****************For test**************
	
/*	public static void main(String[] args){
		Port port = new Port(new Position(0,0), 0.4, 0) ;
		System.out.println(port.getEdgeVec().getString());
		System.out.println(port.getNormalVec().getString());
		Port port1 = port.translateXY(2, 2) ;
		System.out.println(port1.getEdgeVec().getString());
		System.out.println(port1.getNormalVec().getString());
//		port.selfTranslateXY(2, 0);
//		System.out.println(port.getString());
//		port.selfRotate(new Position(1,0), -45);
//		System.out.println(port.getString());
//		Port port1 = port.connect(); 
//		System.out.println(port1.getString());
	}*/
	
	
}
