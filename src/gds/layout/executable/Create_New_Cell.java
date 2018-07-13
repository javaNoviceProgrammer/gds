package gds.layout.executable;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import ch.epfl.javancox.experiments.builder.ExperimentConfigurationCockpit;
import gds.io.PythonCompiler;
import gds.layout.cells.Cell;

public class Create_New_Cell implements Experiment {

	Cell cell ;

	public Create_New_Cell(
			@ParamName(name="Design your Cell") Cell cell
			){
		this.cell = cell ;
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		// creating the gds file
		PythonCompiler p = new PythonCompiler(cell) ;
		p.compile();
		AbstractResultsDisplayer.showGUI = false ;
	}
	
	public static void main(String[] args) {
		String pacakgeString = "gds";
		String classString = "gds.layout.executable.Create_New_Cell";
		ExperimentConfigurationCockpit.main(new String[] { "-p", pacakgeString, "-c", classString });
	}
	
}
