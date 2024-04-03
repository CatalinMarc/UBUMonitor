package es.ubu.lsi.ubumonitor.view.chart.clustering;

import java.io.IOException;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.RangeSlider;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.analysis.AnalysisFactory;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.LogCollector;
import es.ubu.lsi.ubumonitor.clustering.data.LinkageMeasure;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.SelectionController;
import es.ubu.lsi.ubumonitor.controllers.tabs.ClusteringController;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import smile.math.distance.Distance;

/**
 * Clase general para representar el clustering.
 * 
 * @author Ionut Catalin Marc
 *
 */
@SuppressWarnings("restriction")
public class ScatterClustering extends Plotly {

	/* ------------------- Controllers ------------------- */
	
	protected SelectionController sellectionController;
	protected ClusteringController clusteringController;
	
	/* ------------------- FXML Components ------------------- */
	
	protected ListView<Algorithm> algorithmList;
	
	protected PropertySheet propertySheet;
	
	protected CheckBox checkBoxLogs;
	protected CheckBox checkBoxGrades;
	protected CheckBox checkBoxActivity;
	protected CheckBox checkBoxFilter;
	protected CheckBox checkBoxReduce;
	
	protected CheckComboBox<LogCollector<?>> checkComboBoxLogs;
	
	protected DatePicker datePickerStart;
	protected DatePicker datePickerEnd;
	
	protected GridPane gridPane;
	protected GridPane gridPaneReduce;
	protected GridPane gridPaneIterations;
	
	protected Spinner<Integer> spinnerReduce;
	protected Spinner<Integer> spinnerIterations;
	
	protected Button buttonExecute;
	protected Button buttonAnalyze;
	
	protected RangeSlider rangeSlider;
	
	protected ChoiceBox<AnalysisFactory> choiceBoxAnalyze;
	
	protected ChoiceBox<Distance<double[]>> choiceBoxDistance;
	protected ChoiceBox<LinkageMeasure> choiceBoxLinkage;
	
	public ScatterClustering(MainController mainController, ChartType chartType) {
		super(mainController, chartType);
		
		initControllers();
		initComponents();
	}
	
	private void initControllers() {
		sellectionController = mainController.getSelectionController();  
		clusteringController = mainController.getWebViewTabsController().getClusteringController();
	}
	
	private void initComponents() {
		
		gridPane = clusteringController.getGridPane();
		
		algorithmList = clusteringController.getAlgorithmList();
		
		propertySheet = clusteringController.getPropertySheet();
		
		checkBoxLogs = clusteringController.getCheckBoxLogs();
		checkBoxGrades = clusteringController.getCheckBoxGrades();
		checkBoxActivity = clusteringController.getCheckBoxActivity();
		checkBoxFilter = clusteringController.getCheckBoxFilter();
		checkBoxReduce = clusteringController.getCheckBoxReduce();
		
		checkComboBoxLogs = clusteringController.getCheckComboBoxLogs();
		
		datePickerStart = clusteringController.getDatePickerStart();
		datePickerEnd = clusteringController.getDatePickerEnd();
		
		gridPaneReduce = clusteringController.getGridPaneReduce();
		gridPaneIterations = clusteringController.getGridPaneIterations();
		
		spinnerReduce = clusteringController.getSpinnerReduce();
		spinnerIterations = clusteringController.getSpinnerIterations();
		
		buttonExecute = clusteringController.getButtonExecute();
		buttonAnalyze = clusteringController.getButtonAnalyze();
		
		rangeSlider = clusteringController.getRangeSlider();
		
		choiceBoxAnalyze = clusteringController.getChoiceBoxAnalyze();
		
		
		choiceBoxDistance = clusteringController.getChoiceBoxDistance();
		choiceBoxLinkage = clusteringController.getChoiceBoxLinkage();
		
	}

	@Override
	public void exportCSV(String path) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createData(JSArray data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createLayout(JSObject layout) {
		// TODO Auto-generated method stub
		
	}

}
