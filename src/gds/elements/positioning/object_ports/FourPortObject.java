package gds.elements.positioning.object_ports;

import gds.elements.positioning.Port;
import gds.elements.positioning.Position;
import ch.epfl.general_libraries.clazzes.ParamName;

public class FourPortObject {

	/**
	 * One horizontal vectors: hVec12
	 * Two vertical vectors: vVec14, vVec23 ;
	 */
	
	Position hVec12, vVec14, vVec23 ;
	String portNumber ;
	Port selectedPort ; // this is the port that is selected
	Port port1, port2, port3, port4 ;
	
	public FourPortObject(
			@ParamName(name="Port Number") String portNumber,
			@ParamName(name="Selected Port") Port selectedPort,
			@ParamName(name="[hVec12, vVec14, vVec23]") Position[] positionVecs
			){
		this.portNumber = portNumber ;
		this.selectedPort = selectedPort ;
		hVec12 = positionVecs[0] ;
		vVec14 = positionVecs[1] ;
		vVec23 = positionVecs[2] ;
		setPorts() ;
	}
	
	// Checking the port numbers
	private boolean isPort1(){
		if(portNumber.equals("port1") || portNumber.equals("Port1") || portNumber.equals("port 1") || portNumber.equals("Port 1")){
			return true ;
		}
		else{
			return false ;
		}
	}
	
	private boolean isPort2(){
		if(portNumber.equals("port2") || portNumber.equals("Port2") || portNumber.equals("port 2") || portNumber.equals("Port 2")){
			return true ;
		}
		else{
			return false ;
		}
	}
	
	private boolean isPort3(){
		if(portNumber.equals("port3") || portNumber.equals("Port3") || portNumber.equals("port 3") || portNumber.equals("Port 3")){
			return true ;
		}
		else{
			return false ;
		}
	}
	
	private boolean isPort4(){
		if(portNumber.equals("port4") || portNumber.equals("Port4") || portNumber.equals("port 4") || portNumber.equals("Port 4")){
			return true ;
		}
		else{
			return false ;
		}
	}
	
	// now calculating the ports 
	private void setPorts(){
		if(isPort1()){
			port1 = selectedPort ;
			port2 = selectedPort.translateXY(hVec12).connect() ;
			port3 = selectedPort.translateXY(hVec12).translateXY(vVec23).connect() ;
			port4 = selectedPort.translateXY(vVec14) ;
		}
		else if(isPort2()){
			port1 = selectedPort.translateXY(hVec12.scale(-1)).connect() ;
			port2 = selectedPort ;
			port3 = selectedPort.translateXY(vVec23) ;
			port4 = selectedPort.translateXY(hVec12.scale(-1)).translateXY(vVec14).connect() ;
		}
		else if(isPort3()){
			port1 = selectedPort.translateXY(vVec23.scale(-1)).translateXY(hVec12.scale(-1)).connect() ;
			port2 = selectedPort.translateXY(vVec23.scale(-1)) ;
			port3 = selectedPort ;
			port4 = selectedPort.translateXY(vVec23.scale(-1)).translateXY(hVec12.scale(-1)).translateXY(vVec14).connect() ;
		}
		else if(isPort4()){
			port1 = selectedPort.translateXY(vVec14.scale(-1)) ;
			port2 = selectedPort.translateXY(vVec14.scale(-1)).translateXY(hVec12).connect() ;
			port3 = selectedPort.translateXY(vVec14.scale(-1)).translateXY(hVec12).translateXY(vVec23).connect() ;
			port4 = selectedPort ;
		}
		
	}
	
	// finally returning the calculated ports --> Becareful about the direction of each port!
	public Port getPort1(){
		return port1 ;
	}
	
	public Port getPort2(){
		return port2 ;
	}
	
	public Port getPort3(){
		return port3 ;
	}
	
	public Port getPort4(){
		return port4 ;
	}
	
	
}
