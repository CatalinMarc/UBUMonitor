package es.ubu.lsi.ubumonitor.clustering.controller;

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
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.maps.BIRCHAlgorithm;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.maps.GrowingNeuralGasAlgorithm;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.maps.NeuralGasAlgorithm;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.maps.NeuralMapAlgorithm;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.maps.SOMAlgorithm;
import es.ubu.lsi.ubumonitor.clustering.analysis.AnalysisFactory;
import es.ubu.lsi.ubumonitor.clustering.analysis.ElbowFactory;
import es.ubu.lsi.ubumonitor.clustering.analysis.SilhouetteFactory;
import es.ubu.lsi.ubumonitor.clustering.analysis.methods.AnalysisMethod;
import es.ubu.lsi.ubumonitor.clustering.chart.Scatter2DChart;
import es.ubu.lsi.ubumonitor.clustering.chart.Scatter3DChart;
import es.ubu.lsi.ubumonitor.clustering.chart.MapScatter2D;
import es.ubu.lsi.ubumonitor.clustering.chart.MapScatter3D;
import es.ubu.lsi.ubumonitor.clustering.chart.SilhouetteChart;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.ActivityCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.GradesCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.LogCollector;
import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.SOMType;
import es.ubu.lsi.ubumonitor.clustering.exception.IllegalParamenterException;
import es.ubu.lsi.ubumonitor.clustering.util.JavaFXUtils;
import es.ubu.lsi.ubumonitor.clustering.util.SimplePropertySheetItem;
import es.ubu.lsi.ubumonitor.AppInfo;
import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetComponent;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetComponentEvent;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetSection;
import es.ubu.lsi.ubumonitor.model.datasets.DatasSetCourseModule;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;

/**
 * Controlador del clustering particional.
 * 
 * @author Ionut Catalin Marc
 *
 */
public class MapsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PartitionalClusteringController.class);

	private MainController mainController;
	
//	private  ClusteringController clusteringController;
	
	@FXML
	private ComboBox<Algorithm> comboBoxAlgorithm;

	@FXML
	private PropertySheet propertySheet;
	
	@FXML
	private CheckBox checkBoxLogs;

	@FXML
	private CheckBox checkBoxGrades;

	@FXML
	private CheckBox checkBoxActivity;

	@FXML
	private CheckComboBox<LogCollector<?>> checkComboBoxLogs;
	
	@FXML
	private DatePicker datePickerStart;
	
	@FXML
	private DatePicker datePickerEnd;
	
	@FXML
	private WebView webViewScatter;

	@FXML
	private WebView webView3DScatter;
	
	private GradesCollector gradesCollector;

	private ActivityCollector activityCollector;

	private ListView<EnrolledUser> listParticipants;
	
	private MapScatter2D mapScatter2D;
	
	private MapScatter3D mapScatter3D;
		
	/**
	 * Inicializa el controlador.
	 * 
	 * @param controller controlador general
	 */
	public void init(MainController controller) {
		mainController = controller;
//		clusteringTableController.init(controller);

		mapScatter2D = new MapScatter2D(this);
		mapScatter3D = new MapScatter3D(this);

		listParticipants = mainController.getSelectionUserController().getListParticipants();
		
		initAlgorithms();
		initCollectors();
	}

	private void initAlgorithms() {
		comboBoxAlgorithm.setCellFactory(callback -> new ListCell<Algorithm>() {
		    @Override
		    protected void updateItem(Algorithm algorithm, boolean empty) {
		        super.updateItem(algorithm, empty);
		        if (empty || algorithm == null) {
		            setText(null);
		            setGraphic(null);
		        } else {
		            setText(algorithm.getName() + " (" + algorithm.getLibrary() + ")");
//		            try {
//		                Image image = new Image(AppInfo.IMG_DIR + algorithm.getLibrary().toLowerCase() + ".png", 24, 24,
//		                        false, true);
//		                ImageView imageView = new ImageView(image);
//		                setGraphic(imageView);
//		            } catch (Exception e) {
//		                setGraphic(null);
//		            }
		        }
		    }
		});
		
		List<Algorithm> algorithms = Arrays.asList(new SOMAlgorithm(), new NeuralGasAlgorithm(),
				new GrowingNeuralGasAlgorithm(), new BIRCHAlgorithm(), new NeuralMapAlgorithm()
		/* ,new Spectral(), new DeterministicAnnealing() */);
		
		comboBoxAlgorithm.getItems().setAll(algorithms);			
		comboBoxAlgorithm.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> propertySheet
				.getItems().setAll(newValue.getParameters().getPropertyItems()));
		comboBoxAlgorithm.getSelectionModel().selectFirst();
	}

	private void initCollectors() {
		gradesCollector = new GradesCollector(mainController);
		activityCollector = new ActivityCollector(mainController);
		List<LogCollector<?>> list = new ArrayList<>();
		list.add(new LogCollector<>("component", mainController.getSelectionController().getListViewComponents(), DataSetComponent.getInstance(),
				t -> t.name().toLowerCase()));
		list.add(new LogCollector<>("event", mainController.getSelectionController().getListViewEvents(), DataSetComponentEvent.getInstance(),
				t -> t.getComponent().name().toLowerCase()));
		list.add(new LogCollector<>("section", mainController.getSelectionController().getListViewSection(), DataSetSection.getInstance(),
				t -> t.isVisible() ? "visible" : "not_visible"));
		list.add(new LogCollector<>("coursemodule", mainController.getSelectionController().getListViewCourseModule(),
				DatasSetCourseModule.getInstance(), t -> t.getModuleType().getModName()));
		checkComboBoxLogs.getItems().setAll(list);
		checkComboBoxLogs.getCheckModel().checkAll();
		
		JavaFXUtils.initDatePickers(datePickerStart, datePickerEnd, checkBoxLogs);
	}

	/**
	 * Ejecuta el clustering.
	 */
	public void executeClustering() {
		List<EnrolledUser> users = listParticipants.getSelectionModel().getSelectedItems();
		List<DataCollector> collectors = getSelectedCollectors();

		try {
			
			Algorithm algorithm = comboBoxAlgorithm.getSelectionModel().getSelectedItem();
			algorithm.setUsers(users);
			
			MapsExecuter mapsExecuter = new MapsExecuter(algorithm, users, collectors);
			
			boolean type = algorithm.getParameters().getValue(ClusteringParameter.SOM_TYPE) == SOMType.SOM_NEURONS;
			mapsExecuter.execute(type);

			mapScatter2D.updateChart(algorithm.getData2D());
			mapScatter3D.updateChart(algorithm.getData3D());
			
		} catch (IllegalStateException e) {
			UtilMethods.infoWindow(I18n.get(e.getMessage()));
		}

	}
	
	private List<DataCollector> getSelectedCollectors() {
		List<DataCollector> collectors = new ArrayList<>();
		if (checkBoxLogs.isSelected()) {
			List<LogCollector<?>> logCollectors = checkComboBoxLogs.getCheckModel().getCheckedItems();
			logCollectors.forEach(c -> c.setDate(datePickerStart.getValue(), datePickerEnd.getValue()));
			collectors.addAll(logCollectors);
		}
		if (checkBoxGrades.isSelected()) {
			collectors.add(gradesCollector);
		}
		if (checkBoxActivity.isSelected()) {
			collectors.add(activityCollector);
		}
		return collectors;
	}


//	/**
//	 * @return the clusteringTable
//	 */
//	public ClusteringTable getClusteringTable() {
//		return clusteringTableController;
//	}

	/**
	 * @return the mainController
	 */
	public MainController getMainController() {
		return mainController;
	}

	/**
	 * @return the propertySheet
	 */
	public PropertySheet getPropertySheet() {
		return propertySheet;
	}

	/**
	 * @return the checkComboBoxLogs
	 */
	public CheckComboBox<LogCollector<?>> getCheckComboBoxLogs() {
		return checkComboBoxLogs;
	}

	/**
	 * @return the checkBoxLogs
	 */
	public CheckBox getCheckBoxLogs() {
		return checkBoxLogs;
	}

	/**
	 * @return the checkBoxGrades
	 */
	public CheckBox getCheckBoxGrades() {
		return checkBoxGrades;
	}

	/**
	 * @return the checkBoxActivity
	 */
	public CheckBox getCheckBoxActivity() {
		return checkBoxActivity;
	}

	/**
	 * @return the webViewScatter
	 */
	public WebView getWebViewScatter() {
		return webViewScatter;
	}

	/**
	 * @return the webView3DScatter
	 */
	public WebView getWebView3DScatter() {
		return webView3DScatter;
	}

}
