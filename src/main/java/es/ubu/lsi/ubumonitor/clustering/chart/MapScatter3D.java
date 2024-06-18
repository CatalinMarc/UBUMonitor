package es.ubu.lsi.ubumonitor.clustering.chart;

import java.io.File;
import java.io.IOException;
import java.util.List;

import es.ubu.lsi.ubumonitor.clustering.controller.Connector;
import es.ubu.lsi.ubumonitor.clustering.controller.MapConnector;
import es.ubu.lsi.ubumonitor.clustering.controller.MapsController;
import es.ubu.lsi.ubumonitor.clustering.controller.PartitionalClusteringController;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class MapScatter3D extends AbstractChart {

	private MapConnector connector;
	
	public MapScatter3D(MapsController mapsController) {
		super(mapsController.getWebView3DScatter());
		
		WebEngine webEngine = getWebEngine();

		connector = new MapConnector(mapsController);
		webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
			if (Worker.State.SUCCEEDED != newState)
				return;
			netscape.javascript.JSObject window = (netscape.javascript.JSObject) webEngine.executeScript("window");
			window.setMember("javaConnector", connector);
		});
		webEngine.load(getClass().getResource("/graphics/Maps3DChart.html").toExternalForm());
			
	}
	
	public void updateChart(String data) {
		
		WebEngine webEngine = getWebEngine();

		webEngine.executeScript("clearChart()");
		
		JSObject window = (JSObject) webEngine.executeScript("window");
		
		window.call("renderChart3D", data);
	}
	
	@Override
	protected void exportData(File file) throws IOException {
		
	}

}
