package es.ubu.lsi.ubumonitor.clustering.chart;

import java.io.File;
import java.io.IOException;
import java.util.List;

import es.ubu.lsi.ubumonitor.clustering.controller.Connector;
import es.ubu.lsi.ubumonitor.clustering.controller.MapConnector;
import es.ubu.lsi.ubumonitor.clustering.controller.PartitionalClusteringController;
import es.ubu.lsi.ubumonitor.controllers.tabs.MapsController;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;

import netscape.javascript.JSObject;

public class ScatterMap extends AbstractChart {

	private MapConnector connector;
	
	public ScatterMap(PartitionalClusteringController partitionalClusteringController) {
		super(partitionalClusteringController.getWebViewScatter());
		
		System.setProperty("sun.java2d.opengl", "true");
		
		WebEngine webEngine = getWebEngine();

		connector = new MapConnector(partitionalClusteringController);
		webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
			if (Worker.State.SUCCEEDED != newState)
				return;
			netscape.javascript.JSObject window = (netscape.javascript.JSObject) webEngine.executeScript("window");
			window.setMember("javaConnector", connector);
		});
		webEngine.load(getClass().getResource("/graphics/MapsChart.html").toExternalForm());
		
		
		//webEngine.load(getClass().getResource("/graphics/MapsChart.html").toExternalForm());	
		// Pass data to the WebView once it is loaded
//		webEngine.documentProperty().addListener((obs, oldDoc, newDoc) -> {
//            if (newDoc != null) {
//                JSObject window = (JSObject) webEngine.executeScript("window");
//                window.call("renderPlotlyChart", createDataObject());
//            }
//        });
	}
	
	public void updateChart(String data) {
		char dim = data.charAt(data.length() - 1);
		String newData = data.substring(0, data.length() - 1);
	
		WebEngine webEngine = getWebEngine();

		webEngine.executeScript("clearChart()");
		
		JSObject window = (JSObject) webEngine.executeScript("window");
		
		if(dim == '2') {
			window.call("renderChart2D", newData);
			return;
		}
		window.call("renderChart3D", newData);
        
	}
	
	@Override
	protected void exportData(File file) throws IOException {
		
	}

}
