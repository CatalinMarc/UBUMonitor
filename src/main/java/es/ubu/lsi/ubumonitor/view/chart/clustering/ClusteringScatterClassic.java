package es.ubu.lsi.ubumonitor.view.chart.clustering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
import es.ubu.lsi.ubumonitor.clustering.analysis.methods.AnalysisMethod;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.ActivityCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.GradesCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.LogCollector;
import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.util.JavaFXUtils;
import es.ubu.lsi.ubumonitor.clustering.util.SimplePropertySheetItem;
import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.tabs.ClusteringController;
import es.ubu.lsi.ubumonitor.controllers.tabs.clustering.AnalysisController;
import es.ubu.lsi.ubumonitor.controllers.tabs.clustering.ClassicController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import javafx.concurrent.Service;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

/**
 * Clase que hereda de ScatterClustering para representar el clustering de forma clasica.
 * 
 * @author Ionut Catalin Marc
 * 
 */
public class ClusteringScatterClassic extends ScatterClustering {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ClusteringScatterClassic.class);
	
	/*---------------------- CONTROLLER ----------------------*/
	protected ClassicController classicController;
	//protected ClusteringTable clusteringTableController;

	/*---------------------- FXML COMPONENTS ----------------------*/
	protected ComboBox<Algorithm> comboBoxAlgorithm;
	
	protected PropertySheet propertySheet;
	
	protected CheckBox checkBoxLogs;
	protected CheckBox checkBoxGrades;
	protected CheckBox checkBoxActivity;
	protected CheckBox checkBoxFilter;
	protected CheckBox checkBoxReduce;
	
	protected CheckComboBox<LogCollector<?>> checkComboBoxLogs;
	
	protected DatePicker datePickerStart;
	protected DatePicker datePickerEnd;
	
	protected Spinner<Integer> spinnerReduce;
	protected Spinner<Integer> spinnerIterations;
	
	protected Button buttonExecute;
	protected Button buttonAnalyze;
	
	protected RangeSlider rangeSlider;
	
	protected ChoiceBox<AnalysisFactory> choiceBoxAnalyze;
	
	protected ProgressIndicator progressExecute;
	
	/*---------------------- COLLECTORS ----------------------*/
	private GradesCollector gradesCollector;
	private ActivityCollector activityCollector;
	
	private List<ClusterWrapper> clusters;

	private Scatter2DChart graph;

	private SilhouetteChart silhouette;

	private Scatter3DChart graph3D;

	private Service<Void> service;
	
	public ClusteringScatterClassic(MainController mainController, ClusteringController clusteringController) {
		super(mainController, ChartType.CLUSTERING_CLASSIC);

		classicController = clusteringController.getClassicController();
				
		clusteringClassic = true;
		
		initComponents();
		initAlgorithms();

	}

	private void initAlgorithms() {
		
		rangeSlider.setHighValue(10.0);
		
		choiceBoxAnalyze.getItems().setAll(new ElbowFactory(), new SilhouetteFactory());
		choiceBoxAnalyze.getSelectionModel().selectFirst();
		
		spinnerReduce.disableProperty().bind(checkBoxReduce.selectedProperty().not());
		spinnerReduce.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9999));
		spinnerReduce.getEditor().textProperty().addListener(JavaFXUtils.getSpinnerListener(spinnerReduce));

		spinnerIterations.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9999, 20));
		spinnerIterations.getEditor().textProperty().addListener(JavaFXUtils.getSpinnerListener(spinnerIterations));

		checkComboBoxLogs.disableProperty().bind(checkBoxLogs.selectedProperty().not());
	
		comboBoxAlgorithm.setCellFactory(callback -> new ListCell<Algorithm>() {
		    @Override
		    protected void updateItem(Algorithm algorithm, boolean empty) {
		        super.updateItem(algorithm, empty);
		        if (empty || algorithm == null) {
		            setText(null);
		            setGraphic(null);
		        } else {
		            setText(algorithm.getName() + " (" + algorithm.getLibrary() + ")");
		            try {
		                Image image = new Image(AppInfo.IMG_DIR + algorithm.getLibrary().toLowerCase() + ".png", 24, 24,
		                        false, true);
		                ImageView imageView = new ImageView(image);
		                setGraphic(imageView);
		            } catch (Exception e) {
		                setGraphic(null);
		            }
		        }
		    }
		});
		
		List<Algorithm> algorithms = Arrays.asList(new KMeansPlusPlus(), new FuzzyKMeans(), new DBSCAN(),
				new MultiKMeansPlusPlus(), new KMeans(), new XMeans(), new GMeans(), new DBSCANSmile(), new DENCLUE()
		/* ,new Spectral(), new DeterministicAnnealing() */);
		
		comboBoxAlgorithm.getItems().setAll(algorithms);			
		comboBoxAlgorithm.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> propertySheet
				.getItems().setAll(newValue.getParameters().getPropertyItems()));
		comboBoxAlgorithm.getSelectionModel().selectFirst();
		
	}
	
//	/**
//	 * Ejecuta el analisis.
//	 */
//	public void executeAnalysis() {
//		Algorithm algorithm = comboBoxAlgorithm.getSelectionModel().getSelectedItem();
//		if (algorithm.getParameters().getValue(ClusteringParameter.NUM_CLUSTER) == null) {
//			UtilMethods.errorWindow(I18n.get("clustering.invalid"));
//			return;
//		}
//		int start = (int) rangeSlider.getLowValue();
//		int end = (int) rangeSlider.getHighValue();
//
//		List<EnrolledUser> users = mainController.getSelectionUserController().getListParticipants().getSelectionModel().getSelectedItems();
//		List<DataCollector> collectors = getSelectedCollectors();
//		AnalysisMethod analysisMethod = choiceBoxAnalyze.getValue().createAnalysis(algorithm);
//
//		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/OptimalClusters.fxml"));
//		UtilMethods.createDialog(loader, Controller.getInstance().getStage());
//		AnalysisController controller = loader.getController();
//		controller.setUp(analysisMethod, users, collectors, start, end);
//	}
//	
//	private List<DataCollector> getSelectedCollectors() {
//		List<DataCollector> collectors = new ArrayList<>();
//		if (checkBoxLogs.isSelected()) {
//			List<LogCollector<?>> logCollectors = checkComboBoxLogs.getCheckModel().getCheckedItems();
//			logCollectors.forEach(c -> c.setDate(datePickerStart.getValue(), datePickerEnd.getValue()));
//			collectors.addAll(logCollectors);
//		}
//		if (checkBoxGrades.isSelected()) {
//			collectors.add(gradesCollector);
//		}
//		if (checkBoxActivity.isSelected()) {
//			collectors.add(activityCollector);
//		}
//		return collectors;
//	}

	private void initComponents() {
		
		comboBoxAlgorithm = classicController.getComboBoxAlgorithm();
		
		propertySheet = classicController.getPropertySheet();
		
		checkBoxLogs = classicController.getCheckBoxLogs();
		checkBoxGrades = classicController.getCheckBoxGrades();
		checkBoxActivity = classicController.getCheckBoxActivity();
		checkBoxFilter = classicController.getCheckBoxFilter();
		checkBoxReduce = classicController.getCheckBoxReduce();
		
		checkComboBoxLogs = classicController.getCheckComboBoxLogs();
		
		datePickerStart = classicController.getDatePickerStart();
		datePickerEnd = classicController.getDatePickerEnd();
		
		spinnerReduce = classicController.getSpinnerReduce();
		spinnerIterations = classicController.getSpinnerIterations();
		
		buttonExecute = classicController.getButtonExecute();
		buttonAnalyze = classicController.getButtonAnalyze();
		
		rangeSlider = classicController.getRangeSlider();
		
		choiceBoxAnalyze = classicController.getChoiceBoxAnalyze();
		
		progressExecute = classicController.getProgressExecute();
		
	}

}
