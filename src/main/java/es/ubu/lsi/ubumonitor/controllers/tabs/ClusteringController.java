package es.ubu.lsi.ubumonitor.controllers.tabs;

import es.ubu.lsi.ubumonitor.clustering.controller.ClusteringTable;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.WebViewAction;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.controllers.tabs.clustering.ClassicController;
//import es.ubu.lsi.ubumonitor.controllers.tabs.clustering.ClusteringTableController;
import es.ubu.lsi.ubumonitor.controllers.tabs.clustering.GraphClassicController;
import es.ubu.lsi.ubumonitor.controllers.tabs.clustering.HierarchicalController;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.view.chart.bridge.ClusteringConnector;
import es.ubu.lsi.ubumonitor.view.chart.bridge.JavaConnector;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.fxml.FXML;


/**
 * Controlador general de la parte de clustering.
 * 
 * @author Ionut Catalin Marc
 *
 */
public class ClusteringController extends WebViewAction {
	
	private ClusteringConnector javaConnector;
	
	@FXML 
	private GridPane classic;
	@FXML
	private ClassicController classicController;
	
	@FXML 
	private GridPane hierarchical;
	@FXML
	private HierarchicalController hierarchicalController;
	
//	@FXML
//	private TabPane clusteringTable;
	@FXML
	private ClusteringTable clusteringTableController;


	public void init(MainController mainController, Tab tab, Course actualCourse, MainConfiguration mainConfiguration,
			Stage stage) {
		
		classicController.init(mainController, this);
		hierarchicalController.init(mainController);
		
		javaConnector = new ClusteringConnector(webViewController.getWebViewCharts(), mainConfiguration, mainController, this, actualCourse);
		init(tab, actualCourse, mainConfiguration, stage, javaConnector);

		
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

	/**
	 * @return the javaConnector
	 */
	public ClusteringConnector getJavaConnector() {
		return javaConnector;
	}

	/**
	 * @return the classic
	 */
	public GridPane getClassic() {
		return classic;
	}

	/**
	 * @return the classicController
	 */
	public ClassicController getClassicController() {
		return classicController;
	}

	/**
	 * @return the hierarchical
	 */
	public GridPane getHierarchical() {
		return hierarchical;
	}

	/**
	 * @return the hierarchicalController
	 */
	public HierarchicalController getHierarchicalController() {
		return hierarchicalController;
	}
	
	/**
	 * @return the clusteringTableController
	 */
	public ClusteringTable getClusteringTableController() {
		return clusteringTableController;
	}

	/**
	 * @return the webView
	 */
	public WebView getWebView() {
		return webViewController.getWebViewCharts();
	}

}