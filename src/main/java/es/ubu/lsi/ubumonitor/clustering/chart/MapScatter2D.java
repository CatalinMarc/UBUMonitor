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

public class MapScatter2D extends AbstractChart {

	private MapConnector connector;
	
	public MapScatter2D(MapsController mapsController) {
		super(mapsController.getWebViewScatter());
		
		WebEngine webEngine = getWebEngine();

		connector = new MapConnector(mapsController);
		webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
			if (Worker.State.SUCCEEDED != newState)
				return;
			netscape.javascript.JSObject window = (netscape.javascript.JSObject) webEngine.executeScript("window");
			window.setMember("javaConnector", connector);
		});
		webEngine.load(getClass().getResource("/graphics/MapsChart.html").toExternalForm());
		
	}
	
	public void updateChart(String data) {
	
		WebEngine webEngine = getWebEngine();

		JSObject window = (JSObject) webEngine.executeScript("window");
		
		window.call("renderPlotlyChart", data);
		//window.call("connectNeurons", adjacencyMatrix);
        
	}
	
	@Override
	protected void exportData(File file) throws IOException {
		
	}

}
