package es.ubu.lsi.ubumonitor.controllers.tabs.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.RangeSlider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.clustering.controller.ClusteringTable;
import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.analysis.AnalysisFactory;
import es.ubu.lsi.ubumonitor.clustering.analysis.methods.AnalysisMethod;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.ActivityCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.GradesCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.LogCollector;
import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.exception.IllegalParamenterException;
import es.ubu.lsi.ubumonitor.clustering.util.JavaFXUtils;
import es.ubu.lsi.ubumonitor.clustering.util.SimplePropertySheetItem;
import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetComponent;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetComponentEvent;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetSection;
import es.ubu.lsi.ubumonitor.model.datasets.DatasSetCourseModule;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.Chart;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.clustering.ClusteringChart;
import es.ubu.lsi.ubumonitor.view.chart.clustering.Scatter2DChart;
import es.ubu.lsi.ubumonitor.view.chart.clustering.Scatter3DChart;
import es.ubu.lsi.ubumonitor.view.chart.clustering.SilhouetteChart;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.web.WebView;
import es.ubu.lsi.ubumonitor.controllers.tabs.ClusteringController;
import es.ubu.lsi.ubumonitor.controllers.tabs.clustering.AnalysisController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import es.ubu.lsi.ubumonitor.clustering.controller.AlgorithmExecuter;
/**
 * Controlador del clustering particional.
 * 
 * @author Xing Long Ji
 *
 */
public class ClassicController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClassicController.class);
	
	private MainController mainController; 

	private ClusteringController clusteringController;
		
	@FXML
	private ComboBox<Algorithm> comboBoxAlgorithm;

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
	private CheckBox checkBoxFilter;
	
	@FXML
	private DatePicker datePickerStart;
	
	@FXML
	private DatePicker datePickerEnd;
	
	@FXML
	private Spinner<Integer> spinnerReduce;

	@FXML
	private Spinner<Integer> spinnerIterations;
	
	@FXML
	private Button buttonExecute;
	
	@FXML
	private Button buttonAnalyze;
	
	@FXML
	private RangeSlider rangeSlider;

	@FXML
	private ChoiceBox<AnalysisFactory> choiceBoxAnalyze;
	
	@FXML
	private ProgressIndicator progressExecute;
	
	
	
	private GradesCollector gradesCollector;

	private ActivityCollector activityCollector;
	
	private List<ClusterWrapper> clusters;
	
	//private Map<String, ClusteringChart> clusteringCharts;
	
	private Scatter2DChart graph;

	private SilhouetteChart silhouette;

	private Scatter3DChart graph3D;
	
	private Service<Void> service;

	/**
	 * Inicializa el controlador.
	 * 
	 * @param controller controlador general
	 */
	public void init(MainController controller, ClusteringController clusteringController) {
		this.mainController = controller;
		this.clusteringController = clusteringController;
		
		graph = new Scatter2DChart(this);
		silhouette = new SilhouetteChart(this);
		graph3D = new Scatter3DChart(this);
		
//		clusteringCharts.put(1, new Scatter2DChart(this));
//		clusteringCharts.put(2, new SilhouetteChart(this));
//		clusteringCharts.put(3, new Scatter3DChart(this));
		
		initCollectors();
		initService();
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
	
	private void initService() {
		service = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						List<EnrolledUser> users = mainController.getSelectionUserController().getListParticipants().getSelectionModel()
								.getSelectedItems();
						Algorithm algorithm = comboBoxAlgorithm.getSelectionModel().getSelectedItem();

						List<DataCollector> collectors = getSelectedCollectors();

						AlgorithmExecuter algorithmExecuter = new AlgorithmExecuter(algorithm, users, collectors);

						int dim = checkBoxReduce.isSelected() ? spinnerReduce.getValue() : 0;
						int iter = spinnerIterations.getValue();
						clusters = algorithmExecuter.execute(iter, dim, checkBoxFilter.isSelected());

						silhouette.setDistanceType(algorithm.getParameters().getValue(ClusteringParameter.DISTANCE_TYPE));
						LOGGER.debug("Parametros: {}", algorithm.getParameters());
						LOGGER.debug("Clusters: {}", clusters);
						return null;
					}
				};
			}
		};
		buttonExecute.disableProperty().bind(service.runningProperty());
		progressExecute.visibleProperty().bind(service.runningProperty());
		service.setOnSucceeded(e -> {
//			silhouette.updateChart(clusters);
			getClusteringTable().updateTable(clusters);
			updateRename();
//			graph.updateChart(clusters);
//			graph3D.updateChart(clusters);
			service.reset();
		});
		service.setOnFailed(e -> {
			Throwable exception = service.getException();
			UtilMethods.infoWindow(exception instanceof IllegalParamenterException ? exception.getMessage()
					: I18n.get(exception.getMessage()));
			service.reset();
		});
	}
	
	/**
	 * Ejecuta el algoritmo de clustering.
	 */
	public void executeClustering() {
		service.start();
	}
	
	/**
	 * Ejecuta el analisis.
	 */
	public void executeAnalysis() {
		Algorithm algorithm = comboBoxAlgorithm.getSelectionModel().getSelectedItem();
		if (algorithm.getParameters().getValue(ClusteringParameter.NUM_CLUSTER) == null) {
			UtilMethods.errorWindow(I18n.get("clustering.invalid"));
			return;
		}
		int start = (int) rangeSlider.getLowValue();
		int end = (int) rangeSlider.getHighValue();

		List<EnrolledUser> users = mainController.getSelectionUserController().getListParticipants().getSelectionModel().getSelectedItems();
		List<DataCollector> collectors = getSelectedCollectors();
		AnalysisMethod analysisMethod = choiceBoxAnalyze.getValue().createAnalysis(algorithm);

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/OptimalClusters.fxml"));
		UtilMethods.createDialog(loader, Controller.getInstance().getStage());
		AnalysisController controller = loader.getController();
		controller.setUp(analysisMethod, users, collectors, start, end);
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
	
	private void updateRename() {
		List<PropertySheet.Item> items = IntStream.range(0, clusters.size())
				.mapToObj(i -> new SimplePropertySheetItem(String.valueOf(i), String.valueOf(i)))
				.collect(Collectors.toList());
		getClusteringTable().getPropertySheetLabel().getItems().setAll(items);
		getClusteringTable().getButtonLabel().setOnAction(e -> {
			for (int i = 0; i < items.size(); i++) {
				String name = items.get(i).getValue().toString();
				clusters.get(i).setName(name);
				getClusteringTable().getPropertyEditorLabel().add(name);
			}
			getClusteringTable().updateTable(clusters);
			graph.rename(clusters);
			graph3D.rename(clusters);
			silhouette.rename(clusters);
		});
	}
	
	/**
	 * @return the clusteringTable
	 */
	public ClusteringTable getClusteringTable() {
		return clusteringController.getClusteringTableController();
	}
	
	
	/**
	 * @return the mainController
	 */
	public MainController getMainController() {
		return mainController;
	}
	
	/**
	 * @return the clusteringController
	 */
	public ClusteringController getClusteringController() {
		return clusteringController;
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
	 * @return the checkBoxReduce
	 */
	public CheckBox getCheckBoxReduce() {
		return checkBoxReduce;
	}

	/**
	 * @return the spinnerReduce
	 */
	public Spinner<Integer> getSpinnerReduce() {
		return spinnerReduce;
	}

	/**
	 * @return the rangeSlider
	 */
	public RangeSlider getRangeSlider() {
		return rangeSlider;
	}

	/**
	 * @return the choiceBoxAnalyze
	 */
	public ChoiceBox<AnalysisFactory> getChoiceBoxAnalyze() {
		return choiceBoxAnalyze;
	}

	/**
	 * @return the spinnerIterations
	 */
	public Spinner<Integer> getSpinnerIterations() {
		return spinnerIterations;
	}

	/**
	 * @return the buttonExecute
	 */
	public Button getButtonExecute() {
		return buttonExecute;
	}
	
	/**
	 * @return the comboBoxAlgorithm
	 */
	public ComboBox<Algorithm> getComboBoxAlgorithm() {
		return comboBoxAlgorithm;
	}

	/**
	 * @return the checkBoxFilter
	 */
	public CheckBox getCheckBoxFilter() {
		return checkBoxFilter;
	}

	/**
	 * @return the datePickerStart
	 */
	public DatePicker getDatePickerStart() {
		return datePickerStart;
	}

	/**
	 * @return the datePickerEnd
	 */
	public DatePicker getDatePickerEnd() {
		return datePickerEnd;
	}

	/**
	 * @return the buttonAnalyze
	 */
	public Button getButtonAnalyze() {
		return buttonAnalyze;
	}

	/**
	 * @return the progressExecute
	 */
	public ProgressIndicator getProgressExecute() {
		return progressExecute;
	}
	
	/**
	 * @return the webViewScatter
	 */
	public Scatter2DChart getWebViewScatter() {
		return graph;
	}

	/**
	 * @return the webView3DScatter
	 */
	public Scatter3DChart getWebView3DScatter() {
		return graph3D;
	}

	/**
	 * @return the webViewSilhouette
	 */
	public SilhouetteChart getWebViewSilhouette() {
		return silhouette;
	}
	
}