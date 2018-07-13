package gds.headers;

import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;

public class Header {
	
	/** This class imports the 
	 *  required python libraries
	 */
	
	int m = 4 ; // number of constructor's items
	String[] args = new String[m] ;
	
	public Header(
			@ParamName(name="import os library", default_="true") boolean import_os,
			@ParamName(name="import numpy library", default_="true") boolean import_numpy,
			@ParamName(name="import gdspy library", default_="true") boolean import_gdspy,
			@ParamName(name="import math library", default_="true") boolean import_math,
			@ParamName(name="Print gdspy version?", default_="true") boolean print_gdspy_version
			){
		if(import_os){args[0] = "import os" ;} else{args[0] = "" ; } 
		if(import_numpy){args[1] = "import numpy" ;} else{args[1] = "" ; } 
		if(import_gdspy){args[2] = "import gdspy" ;} else{args[2] = "" ; } 
		if(import_math){args[3] = "import math" ;} else{args[3] = "" ; } 
//		if(print_gdspy_version){args[4] = "print ('Using gdspy module version ' + gdspy.__version__)" ;} else{args[4] = "" ; }
	}
	
	public Header(){
		args[0] = "import os" ;
		args[1] = "import numpy" ;
		args[2] = "import gdspy" ;
		args[3] = "import math" ;
//		args[4] = "print ('Using gdspy module version ' + gdspy.__version__)" ;
	}

	
	public String[] getPythonCode(String fileName){
		return args ;
	}
	
	public void writeToFile(String fileName){
		FileOutput fout = new FileOutput(fileName + ".py","w") ;
		fout.println(getPythonCode(fileName));
		fout.close();
	}
	
	public void appendToFile(String fileName){
		FileOutput fout = new FileOutput(fileName + ".py","a") ;
		fout.println(getPythonCode(fileName));
		fout.close();
	}
	

}
