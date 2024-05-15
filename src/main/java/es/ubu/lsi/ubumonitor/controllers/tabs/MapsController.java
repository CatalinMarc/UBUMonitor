package es.ubu.lsi.ubumonitor.controllers.tabs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.controlsfx.control.CheckComboBox;
import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.algorithm.HierarchicalAlgorithm;
import es.ubu.lsi.ubumonitor.clustering.controller.AlgorithmExecuter;
import es.ubu.lsi.ubumonitor.clustering.controller.MapsExecuter;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.maps.UMatrix;
import es.ubu.lsi.ubumonitor.clustering.controller.ClusteringTable;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.ActivityCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.GradesCollector;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.LogCollector;
import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.LinkageMeasure;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.clustering.util.JavaFXUtils;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetComponent;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetComponentEvent;
import es.ubu.lsi.ubumonitor.model.datasets.DataSetSection;
import es.ubu.lsi.ubumonitor.model.datasets.DatasSetCourseModule;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import smile.clustering.HierarchicalClustering;
import smile.math.distance.ChebyshevDistance;
import smile.math.distance.Distance;
import smile.math.distance.EuclideanDistance;
import smile.math.distance.ManhattanDistance;
import smile.plot.swing.Canvas;
import smile.plot.swing.Hexmap;
import smile.plot.swing.ScatterPlot;
import smile.plot.swing.Palette;
import smile.plot.swing.Point;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.maps.NeuralGasA;
import java.awt.Color;

public class MapsController {

	@FXML
	private ComboBox<Algorithm> comboBoxAlgorithm;

	@FXML
	private ImageView imageView;

	@FXML
	private Pane pane;
	
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
	
	private GradesCollector gradesCollector;

	private ActivityCollector activityCollector;

	private ListView<EnrolledUser> listParticipants;
	
	double[][] mapsClustering;
	
	/**
	 * Inicializa el controlador.
	 * 
	 * @param controller controlador general
	 */
	public void init(MainController mainController, ClusteringController clusteringController) {

		listParticipants = mainController.getSelectionUserController().getListParticipants();


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
		checkComboBoxLogs.disableProperty().bind(checkBoxLogs.selectedProperty().not());

//		ChangeListener<? super Number> listener = (obs, newValue, oldValue) -> setImage();
//		pane.widthProperty().addListener(listener);
//		pane.heightProperty().addListener(listener);

		initContextMenu(imageView);
		JavaFXUtils.initDatePickers(datePickerStart, datePickerEnd, checkBoxLogs);
		
		initAlgorithms();
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
		
		List<Algorithm> algorithms = Arrays.asList(new UMatrix(), new NeuralGasA()
		/* ,new Spectral(), new DeterministicAnnealing() */);
		
		comboBoxAlgorithm.getItems().setAll(algorithms);			
//		comboBoxAlgorithm.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> propertySheet
//				.getItems().setAll(newValue.getParameters().getPropertyItems()));
		comboBoxAlgorithm.getSelectionModel().selectFirst();
	}

	private void initContextMenu(ImageView imageView) {
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.setAutoHide(true);

		MenuItem exportPNG = new MenuItem(I18n.get("text.exportpng"));
		//exportPNG.setOnAction(e -> exportPNG());

		contextMenu.getItems().setAll(exportPNG);
		imageView.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.SECONDARY) {
				contextMenu.show(imageView, e.getScreenX(), e.getScreenY());
			} else {
				contextMenu.hide();
			}
		});
	}
	
	/**
	 * Ejecuta el clustering.
	 */
	public void executeClustering() {
		List<EnrolledUser> users = listParticipants.getSelectionModel().getSelectedItems();

		List<DataCollector> collectors = new ArrayList<>();
		if (checkBoxLogs.isSelected()) {
			List<LogCollector<?>> logCollectors = checkComboBoxLogs.getCheckModel().getCheckedItems();
			logCollectors.forEach(c -> c.setDate(datePickerStart.getValue(), datePickerEnd.getValue()));
			collectors.addAll(checkComboBoxLogs.getCheckModel().getCheckedItems());
		}
		if (checkBoxGrades.isSelected()) {
			collectors.add(gradesCollector);
		}
		if (checkBoxActivity.isSelected()) {
			collectors.add(activityCollector);
		}

		try {
			//prueba varios algoritmos a la vez
			Algorithm algorithm = comboBoxAlgorithm.getSelectionModel().getSelectedItem();
			
			MapsExecuter mapsExecuter = new MapsExecuter(algorithm, users, collectors);
			
			double[][] matrix = mapsExecuter.getClusterer();
		
			setImage(matrix);
		} catch (IllegalStateException e) {
			UtilMethods.infoWindow(I18n.get(e.getMessage()));
		}
				
//		//prueba neural gas
//		try {
//			NeuralGasA a = new NeuralGasA();
//			double[][] matrix  = a.execute(users, collectors);
//			setImage(matrix);
//		} catch (IllegalStateException e) {
//			UtilMethods.infoWindow(I18n.get(e.getMessage()));
//		}
		
		
//		//prueba umatrix
//		try {
//			UMatrix umatrix = new UMatrix();
//			double[][] matrix  = umatrix.execute(users, collectors);
//			setImage(matrix);
//		} catch (IllegalStateException e) {
//			UtilMethods.infoWindow(I18n.get(e.getMessage()));
//		}
	}

	private void setImage(double[][] matrix) {
		if (matrix == null)
			return;
		
//		Point points = new Point(matrix, '#', Color.BLUE);
//		
//		Point[] points = new Point[matrix.length];
//		
//		for (int i = 0; i < matrix.length; i++) {
//			points[i] = new Point(new double[][]{matrix[i]}, '#', Color.BLUE);
//		}
//		
//		ScatterPlot scatter = new ScatterPlot(points);
//		Canvas canvas = scatter.canvas();
//		canvas.setMargin(0.05);
//		Image image = SwingFXUtils
//				.toFXImage(canvas.toBufferedImage((int) pane.getWidth() + 1, (int) pane.getHeight() + 1), null);
//		imageView.setImage(image);
		
		Hexmap hexmap = new Hexmap(matrix, Palette.jet(256), null);
		Canvas canvas = hexmap.canvas();
		canvas.setMargin(0.05);
		Image image = SwingFXUtils
				.toFXImage(canvas.toBufferedImage((int) pane.getWidth() + 1, (int) pane.getHeight() + 1), null);
		imageView.setImage(image);
	}
	
}