package es.ubu.lsi.ubumonitor.view.chart.bridge;

import java.io.IOException;
import java.util.Locale;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.controllers.tabs.ClusteringController;
import es.ubu.lsi.ubumonitor.controllers.tabs.clustering.ClassicController;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.JSArray;
import es.ubu.lsi.ubumonitor.util.JSObject;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.view.chart.Chart;
import es.ubu.lsi.ubumonitor.view.chart.ChartType;
import es.ubu.lsi.ubumonitor.view.chart.Tabs;
import es.ubu.lsi.ubumonitor.view.chart.clustering.ClusteringChart;
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
	
	private ClassicController classicController;
	
	public ClusteringConnector(WebView webView, MainConfiguration mainConfiguration, MainController mainController,
			ClusteringController clusteringController, Course actualCourse) {
		super(webView, mainConfiguration, mainController, actualCourse);
		
		this.clusteringController = clusteringController;
		this.classicController = clusteringController.getClassicController();
		
		addChart(new ClusteringScatterClassic(mainController, clusteringController));
		addChart(new ClusteringScatterHierarchical(mainController, clusteringController));
		
		currentChart = charts.get(ChartType.getDefault(Tabs.CLUSTERING));
	}

	@Override
	public void manageOptions() {
		// Change the grid pane depending on the tab selected
		clusteringController.getClassic().setVisible(currentChart.isClusteringClassic());
		clusteringController.getHierarchical().setVisible(!currentChart.isClusteringClassic());
	}
	
    
    @Override
	public void inititDefaultValues() {
    	//super.inititDefaultValues();
    	JSArray jsArray = new JSArray();
		for (ChartType chartType : charts.keySet()) {
			JSObject jsObject = new JSObject();
			jsObject.putWithQuote("id", chartType.toString());
			jsObject.putWithQuote("text", I18n.get(chartType));
			jsObject.putWithQuote("type", chartType.getTab());
			jsArray.add(jsObject);
			System.out.println(I18n.get(chartType));
		}
		
		JSArray innerButtons = new JSArray();
	   	 
		innerButtons.add(getObject(classicController.getWebViewScatter(), "Gráfico 2D"));
		innerButtons.add(getObject(classicController.getWebView3DScatter(), "Gráfico 3D"));
		innerButtons.add(getObject(classicController.getWebViewSilhouette(), "Análisis de silueta"));

		webEngine.executeScript("generateClusteringButtons(" + jsArray + "," + innerButtons + ")");
		webEngine.executeScript("createChartDivs()");
		webEngine.executeScript(String.format("translate(%s,'%s')", "'btnLegend'",
				UtilMethods.escapeJavaScriptText(I18n.get("btnLegend"))));
		webEngine.executeScript(String.format("translate(%s,'%s')", "'btnMean'",
				UtilMethods.escapeJavaScriptText(I18n.get("btnMean"))));
		webEngine.executeScript(String.format("translate(%s,'%s')", "'btnGroupMean'",
				UtilMethods.escapeJavaScriptText(I18n.get("btnGroupMean"))));
		webEngine.executeScript("setLocale('" + Locale.getDefault()
				.toLanguageTag() + "')");

//		String htmlCode = (String) webEngine.executeScript("document.documentElement.outerHTML");
//        System.out.println(htmlCode);
    	
        
		updateOptionsImages();

    }
    
    private JSObject getObject(ClusteringChart chart, String text) {
    	JSObject jsObject = new JSObject();
    	
    	jsObject.putWithQuote("id", chart.toString());
		jsObject.putWithQuote("text", text);
		//jsObject.putWithQuote("type", chartType.getTab());
		
		return jsObject;
    }
    
    @Override
	public void updateChartFromJS(String typeChart) {
    	super.updateChartFromJS(typeChart);
    	webEngine.executeScript(String.format("clusteringInnerButtons('%s')", 
    			UtilMethods.escapeJavaScriptText(typeChart)));
   	
	}
    
}
