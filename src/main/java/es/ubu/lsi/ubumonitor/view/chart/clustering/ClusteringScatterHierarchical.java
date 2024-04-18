package es.ubu.lsi.ubumonitor.view.chart.clustering;

import es.ubu.lsi.ubumonitor.clustering.algorithm.HierarchicalAlgorithm;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.ActivityCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.GradesCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.LogCollector;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.CheckComboBox;

import es.ubu.lsi.ubumonitor.clustering.data.LinkageMeasure;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.tabs.ClusteringController;
import es.ubu.lsi.ubumonitor.controllers.tabs.clustering.HierarchicalController;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetComponent;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetComponentEvent;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetSection;
import es.ubu.lsi.ubumonitor.model.datasets.DatasSetCourseModule;
import javafx.util.StringConverter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import smile.clustering.HierarchicalClustering;
import smile.math.distance.ChebyshevDistance;
import smile.math.distance.Distance;
import smile.math.distance.EuclideanDistance;
import smile.math.distance.ManhattanDistance;

/**
 * Clase que hereda de ScatterClustering para representar el clustering de forma jerarquica.
 * 
 * @author Ionut Catalin Marc
 *
 */
public class ClusteringScatterHierarchical extends ScatterClustering {

	/* Controller */
	protected HierarchicalController hierarchicalController;
	
	/* FXML Components */
	protected ChoiceBox<Distance<double[]>> choiceBoxDistance;
	protected ChoiceBox<LinkageMeasure> choiceBoxLinkage;
	
	protected CheckComboBox<LogCollector<?>> checkComboBoxLogs;

	protected CheckBox checkBoxLogs;
	protected CheckBox checkBoxGrades;
	protected CheckBox checkBoxActivity;

	protected DatePicker datePickerStart;
	protected DatePicker datePickerEnd;
	
	protected Button buttonExecute;
	
	protected Spinner<Integer> spinnerClusters;	

//	
//	private GradesCollector gradesCollector;
//
//	private ActivityCollector activityCollector;
//
//	private ListView<EnrolledUser> listParticipants;
//
	private HierarchicalAlgorithm hierarchicalAlgorithm;
//
//	private HierarchicalClustering hierarchicalClustering;
	
	public ClusteringScatterHierarchical(MainController mainController, ClusteringController clusteringController) {
		super(mainController, ChartType.CLUSTERING_HIERARCHICAL);
		
		hierarchicalAlgorithm = new HierarchicalAlgorithm();
		hierarchicalController = clusteringController.getHierarchicalController();
				
		clusteringClassic = false; // For the grid pane
		
		initComponents();
		initAlgorithms();
	}
	
	private void initAlgorithms() {
		choiceBoxDistance.setConverter(new StringConverter<Distance<double[]>>() {

			@Override
			public String toString(Distance<double[]> object) {
				return I18n.get("clustering.distance." + object.toString().replace(' ', '_').toLowerCase());
			}

			@Override
			public Distance<double[]> fromString(String string) {
				return null;
			}
		});
		List<Distance<double[]>> distances = Arrays.asList(new EuclideanDistance(), new ManhattanDistance(),
				new ChebyshevDistance());
		choiceBoxDistance.getItems().setAll(distances);
		choiceBoxDistance.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldValue, newValue) -> hierarchicalAlgorithm.setDistance(newValue));
		choiceBoxDistance.getSelectionModel().selectFirst();

		choiceBoxLinkage.getItems().setAll(LinkageMeasure.values());
		choiceBoxLinkage.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldValue, newValue) -> hierarchicalAlgorithm.setLinkageMeasure(newValue));
		choiceBoxLinkage.getSelectionModel().selectFirst();
		
	}
	
	private void initComponents() {
		choiceBoxDistance = hierarchicalController.getChoiceBoxDistance();
		choiceBoxLinkage = hierarchicalController.getChoiceBoxLinkage();
		
		checkComboBoxLogs = hierarchicalController.getCheckComboBoxLogs();

		checkBoxLogs = hierarchicalController.getCheckBoxLogs();
		checkBoxGrades = hierarchicalController.getCheckBoxGrades();
		checkBoxActivity = hierarchicalController.getCheckBoxActivity();

		datePickerStart = hierarchicalController.getDatePickerStart();
		datePickerEnd = hierarchicalController.getDatePickerEnd();
		
		buttonExecute = hierarchicalController.getButtonExecute();
		
		spinnerClusters = hierarchicalController.getSpinnerClusters();	
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
