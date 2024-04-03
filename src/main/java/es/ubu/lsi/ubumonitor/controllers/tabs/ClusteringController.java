package es.ubu.lsi.ubumonitor.controllers.tabs;

import java.util.Arrays;
import java.util.List;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.RangeSlider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.AppInfo;
import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.algorithm.apache.DBSCAN;
import es.ubu.lsi.ubumonitor.clustering.algorithm.apache.FuzzyKMeans;
import es.ubu.lsi.ubumonitor.clustering.algorithm.apache.KMeansPlusPlus;
import es.ubu.lsi.ubumonitor.clustering.algorithm.apache.MultiKMeansPlusPlus;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.DBSCANSmile;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.DENCLUE;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.GMeans;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.KMeans;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.XMeans;
import es.ubu.lsi.ubumonitor.clustering.analysis.AnalysisFactory;
import es.ubu.lsi.ubumonitor.clustering.analysis.ElbowFactory;
import es.ubu.lsi.ubumonitor.clustering.analysis.SilhouetteFactory;
import es.ubu.lsi.ubumonitor.clustering.chart.Scatter2DChart;
import es.ubu.lsi.ubumonitor.clustering.chart.Scatter3DChart;
import es.ubu.lsi.ubumonitor.clustering.chart.SilhouetteChart;
import es.ubu.lsi.ubumonitor.clustering.controller.ClusteringTable;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.ActivityCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.GradesCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.LogCollector;
import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.LinkageMeasure;
import es.ubu.lsi.ubumonitor.clustering.util.JavaFXUtils;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.WebViewAction;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.view.chart.bridge.ClusteringConnector;
import es.ubu.lsi.ubumonitor.view.chart.bridge.JavaConnector;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import smile.math.distance.Distance;
import javafx.concurrent.Service;

/**
 * Controlador general de la parte de clustering.
 * 
 * @author Ionut Catalin Marc
 *
 */
public class ClusteringController extends WebViewAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClusteringController.class);
	
	private ClusteringConnector javaConnector;
	
	@FXML
	private ClusteringTable clusteringTableController;

	@FXML
	private ListView<Algorithm> algorithmList;

	@FXML
	private PropertySheet propertySheet;

	@FXML
	private CheckComboBox<LogCollector<?>> checkComboBoxLogs;

	@FXML
	private CheckBox checkBoxLogs;

	@FXML
	private CheckBox checkBoxGrades;

	@FXML
	private CheckBox checkBoxActivity;

	@FXML
	private CheckBox checkBoxReduce;

	@FXML
	private Spinner<Integer> spinnerReduce;

	@FXML
	private RangeSlider rangeSlider;

	@FXML
	private ChoiceBox<AnalysisFactory> choiceBoxAnalyze;

	@FXML
	private Spinner<Integer> spinnerIterations;

	@FXML
	private CheckBox checkBoxFilter;

	@FXML
	private Button buttonExecute;
	
	@FXML
	private Button buttonAnalyze;

	@FXML
	private ProgressIndicator progressExecute;
	
	@FXML
	private DatePicker datePickerStart;
	
	@FXML
	private DatePicker datePickerEnd;
	
	@FXML
	private GridPane gridPane;
	
	@FXML
	private GridPane gridPaneReduce;
	
	@FXML
	private GridPane gridPaneIterations;
	
	@FXML
	private Label lblDistance;
	
	@FXML
	private Label lblDistanceCluster;
	
	@FXML
	private ChoiceBox<Distance<double[]>> choiceBoxDistance;
	
	@FXML
	private ChoiceBox<LinkageMeasure> choiceBoxLinkage;
	
	
	/* Graficas */

	@FXML
	private WebView webViewScatter;

	@FXML
	private WebView webView3DScatter;

	@FXML
	private WebView webViewSilhouette;

	@Override
	public void init(MainController mainController, Tab tab, Course actualCourse, MainConfiguration mainConfiguration,
			Stage stage) {
		javaConnector = new ClusteringConnector(webViewController.getWebViewCharts(), mainConfiguration, mainController, this, actualCourse);
		init(tab, actualCourse, mainConfiguration, stage, javaConnector);
		
		rangeSlider.setHighValue(10.0);

		choiceBoxAnalyze.getItems().setAll(new ElbowFactory(), new SilhouetteFactory());
		choiceBoxAnalyze.getSelectionModel().selectFirst();
		
	}
	
	@Override
	public void onWebViewTabChange() {
		javaConnector.updateOptionsImages();
		
	}

	@Override
	public void updateListViewEnrolledUser() {
		updateChart();
		
	}

	@Override
	public void updatePredicadeEnrolledList() {
		updateChart();
		
	}

	@Override
	public void applyConfiguration() {
		updateChart();
		
	}
	
	@Override
	public JavaConnector getJavaConnector() {
		return javaConnector;
	}

	public ListView<Algorithm> getAlgorithmList() {
		return algorithmList;
	}
	
	public PropertySheet getPropertySheet() {
		return propertySheet;
	}

	public CheckBox getCheckBoxLogs() {
		return checkBoxLogs;
	}
	
	public CheckBox getCheckBoxGrades() {
		return checkBoxGrades;
	}
	
	public CheckBox getCheckBoxActivity() {
		return checkBoxActivity;
	}
	
	public CheckBox getCheckBoxFilter() {
		return checkBoxFilter;
	}
	
	public CheckBox getCheckBoxReduce() {
		return checkBoxReduce;
	}
	
	public CheckComboBox<LogCollector<?>> getCheckComboBoxLogs() {
		return checkComboBoxLogs;
	}
	
	public DatePicker getDatePickerStart() {
		return datePickerStart;
	}
	
	public DatePicker getDatePickerEnd() {
		return datePickerEnd;
	}
	
	public Spinner<Integer> getSpinnerReduce() {
		return spinnerReduce;
	}
	
	public Spinner<Integer> getSpinnerIterations() {
		return spinnerIterations;
	}
	
	public Button getButtonExecute() {
		return buttonExecute;
	}
	
	public Button getButtonAnalyze() {
		return buttonAnalyze;
	}
	
	public RangeSlider getRangeSlider() {
		return rangeSlider;
	}
	
	public ChoiceBox<AnalysisFactory> getChoiceBoxAnalyze() {
		return choiceBoxAnalyze;
	}
	
	public GridPane getGridPane() {
		return gridPane;
	}
	
	public GridPane getGridPaneReduce() {
		return gridPaneReduce;
	}
	
	public GridPane getGridPaneIterations() {
		return gridPaneIterations;
	}
	
	public Label getLblDistance() {
		return lblDistance;
	}
	
	public Label getLblDistanceCluster() {
		return lblDistanceCluster;
	}
	
	public ChoiceBox<Distance<double[]>> getChoiceBoxDistance() {
		return choiceBoxDistance;
	}
	
	public ChoiceBox<LinkageMeasure> getChoiceBoxLinkage() {
		return choiceBoxLinkage;
	}

}
