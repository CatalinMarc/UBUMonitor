package es.ubu.lsi.ubumonitor.view.chart.bridge;

import java.io.IOException;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.controllers.tabs.ClusteringController;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Tabs;
import es.ubu.lsi.ubumonitor.view.chart.clustering.ClusteringScatterClassic;
import es.ubu.lsi.ubumonitor.view.chart.clustering.ClusteringScatterHierarchical;
import javafx.scene.web.WebView;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

/**
 * Clustering connector.
 * 
 * @author Ionut Catalin Marc
 *
 */
public class ClusteringConnector extends JavaConnectorAbstract {
	
	private ClusteringController clusteringController;
	
	public ClusteringConnector(WebView webView, MainConfiguration mainConfiguration, MainController mainController,
			ClusteringController clusteringController, Course actualCourse) {
		super(webView, mainConfiguration, mainController, actualCourse);
		
		this.clusteringController = clusteringController;
		
		addChart(new ClusteringScatterClassic(mainController));
		addChart(new ClusteringScatterHierarchical(mainController));
		
		currentChart = charts.get(ChartType.getDefault(Tabs.CLUSTERING));
	}

	@Override
	public void manageOptions() {
		clusteringController.getAlgorithmList().setVisible(currentChart.isUseAlgorithms());
		clusteringController.getGridPaneReduce().setVisible(currentChart.isUseAlgorithms());
		clusteringController.getGridPaneIterations().setVisible(currentChart.isUseAlgorithms());
		clusteringController.getCheckBoxFilter().setVisible(currentChart.isUseAlgorithms());
		clusteringController.getPropertySheet().setVisible(currentChart.isUseAlgorithms());
		
		clusteringController.getLblDistance().setVisible(!currentChart.isUseAlgorithms());
		clusteringController.getLblDistanceCluster().setVisible(!currentChart.isUseAlgorithms());
		clusteringController.getChoiceBoxDistance().setVisible(!currentChart.isUseAlgorithms());
		clusteringController.getChoiceBoxLinkage().setVisible(!currentChart.isUseAlgorithms());
	}
}
