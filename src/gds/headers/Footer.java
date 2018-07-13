package gds.headers;

import gds.io.LayoutViewer;
import gds.util.MoreMath;
import ch.epfl.general_libraries.clazzes.ParamName;
import flanagan.io.FileOutput;

public class Footer {

	/** this class implements the footers of the gdspy which
	 *  include saving the file in python
	 */

	double units, precision ;

	public Footer(
			@ParamName(name="Units (m)", default_="1e-6") double units,
			@ParamName(name="Precision (m)", default_="1e-9") double precision
			){
		this.units = units ;
		this.precision = precision ;
	}

	public Footer(){
		this.units = 1e-6 ;
		this.precision = 1e-9 ;
	}

	public String[] getPythonCode(String fileName){ // file name should be without .py extension
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##       Creating the GDS file              ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args0 = {st0, st1, st2} ;
		String[] args = new String[5] ;
		args[0] = "##### Export gds file" ;
		args[1] = "name= os.path.abspath(os.path.dirname(os.sys.argv[0])) + os.sep +" + "'" + fileName + "'" ;
		args[2] = "file_out = open(name + " + "'" + ".gds" + "'" + "," + "'" + "wb" + "'" + ")" ;
		args[3] = "gdspy.write_gds(file_out," + " unit=" + units + ", precision=" + precision + ")" ;
		args[4] = "file_out.close()" ;
		args = MoreMath.Arrays.concat(args0, args) ;
		String[] args1 = LayoutViewer.execute() ;
		args = MoreMath.Arrays.concat(args, args1) ;
		return args ;
	}

	public String[] getPythonCode(String fileName, boolean runLayoutViewer){ // file name should be without .py extension
		String st0 = "## ---------------------------------------- ##" ;
		String st1 = "##       Creating the GDS file              ##" ;
		String st2 = "## ---------------------------------------- ##" ;
		String[] args0 = {st0, st1, st2} ;
		String[] args = new String[5] ;
		args[0] = "##### Export gds file" ;
		args[1] = "name= os.path.abspath(os.path.dirname(os.sys.argv[0])) + os.sep +" + "'" + fileName + "'" ;
		args[2] = "file_out = open(name + " + "'" + ".gds" + "'" + "," + "'" + "wb" + "'" + ")" ;
		args[3] = "gdspy.write_gds(file_out," + " unit=" + units + ", precision=" + precision + ")" ;
		args[4] = "file_out.close()" ;
		args = MoreMath.Arrays.concat(args0, args) ;
		if(runLayoutViewer){
			String[] args1 = LayoutViewer.execute() ;
			args = MoreMath.Arrays.concat(args, args1) ;
		}
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

	public void writeToFile(String fileName, String filePath){
		FileOutput fout = new FileOutput(filePath + ".py","w") ;
		fout.println(getPythonCode(fileName));
		fout.close();
	}

	public void appendToFile(String fileName, String filePath){
		FileOutput fout = new FileOutput(filePath + ".py","a") ;
		fout.println(getPythonCode(fileName));
		fout.close();
	}

	public void appendToFile(String fileName, String filePath, boolean runLayoutViewer){
		FileOutput fout = new FileOutput(filePath + ".py","a") ;
		fout.println(getPythonCode(fileName, runLayoutViewer));
		fout.close();
	}

}
