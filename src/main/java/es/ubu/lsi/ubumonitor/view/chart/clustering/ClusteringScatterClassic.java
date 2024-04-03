package es.ubu.lsi.ubumonitor.view.chart.clustering;

import java.util.Arrays;
import java.util.List;

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
import es.ubu.lsi.ubumonitor.clustering.util.JavaFXUtils;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import javafx.scene.control.ListCell;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Clase que hereda de ScatterClustering para representar el clustering de forma clasica.
 * 
 * @author Ionut Catalin Marc
 * 
 */
public class ClusteringScatterClassic extends ScatterClustering {

	public ClusteringScatterClassic(MainController mainController) {
		super(mainController, ChartType.CLUSTERING_CLASSIC);

		useAlgorithms = true;
		
		initAlgorithms();
	}

	private void initAlgorithms() {
		spinnerReduce.disableProperty().bind(checkBoxReduce.selectedProperty().not());
		spinnerReduce.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9999));
		spinnerReduce.getEditor().textProperty().addListener(JavaFXUtils.getSpinnerListener(spinnerReduce));

		spinnerIterations.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9999, 20));
		spinnerIterations.getEditor().textProperty().addListener(JavaFXUtils.getSpinnerListener(spinnerIterations));

		checkComboBoxLogs.disableProperty().bind(checkBoxLogs.selectedProperty().not());

		algorithmList.setCellFactory(callback -> new ListCell<Algorithm>() {
			@Override
			public void updateItem(Algorithm algorithm, boolean empty) {
				super.updateItem(algorithm, empty);
				if (empty) {
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
		algorithmList.getItems().setAll(algorithms);
		algorithmList.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> propertySheet
				.getItems().setAll(newValue.getParameters().getPropertyItems()));
		algorithmList.getSelectionModel().selectFirst();
	}
	
}
