package gds.layout.executable;

import ch.epfl.general_libraries.clazzes.ParamName;
import ch.epfl.general_libraries.experiment_aut.Experiment;
import ch.epfl.general_libraries.experiment_aut.WrongExperimentException;
import ch.epfl.general_libraries.results.AbstractResultsDisplayer;
import ch.epfl.general_libraries.results.AbstractResultsManager;
import gds.layout.Layout;

public class Create_New_Layout implements Experiment {

	Layout layout ;
	
	public Create_New_Layout(
			@ParamName(name="Design your Layout") Layout layout
			){
		this.layout = layout ;
		
	}

	@Override
	public void run(AbstractResultsManager man, AbstractResultsDisplayer dis) throws WrongExperimentException {
		layout.execute(); 
		
	}

}
