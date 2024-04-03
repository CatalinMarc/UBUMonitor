package es.ubu.lsi.ubumonitor.view.chart.clustering;

import es.ubu.lsi.ubumonitor.clustering.algorithm.HierarchicalAlgorithm;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import es.ubu.lsi.ubumonitor.clustering.data.LinkageMeasure;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Plotly;
import javafx.util.StringConverter;
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

	private HierarchicalAlgorithm hierarchicalAlgorithm = new HierarchicalAlgorithm();
	
	public ClusteringScatterHierarchical(MainController mainController) {
		super(mainController, ChartType.CLUSTERING_HIERARCHICAL);
		
		useAlgorithms = false;
		
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
