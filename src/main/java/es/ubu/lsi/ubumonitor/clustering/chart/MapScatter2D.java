package es.ubu.lsi.ubumonitor.clustering.chart;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.ubu.lsi.ubumonitor.clustering.controller.Connector;
import es.ubu.lsi.ubumonitor.clustering.controller.MapConnector;
import es.ubu.lsi.ubumonitor.clustering.controller.MapsController;
import es.ubu.lsi.ubumonitor.clustering.controller.PartitionalClusteringController;
import es.ubu.lsi.ubumonitor.clustering.util.ExportUtil;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;

public class MapScatter2D extends AbstractChart {

	private MapConnector connector;
	
	private String data;
	
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
		this.data = data;
		
		WebEngine webEngine = getWebEngine();

		JSObject window = (JSObject) webEngine.executeScript("window");
		
		window.call("renderPlotlyChart", data);
        
	}
	
	@Override
	protected void exportData(File file) throws IOException {
		JSONObject jsonObject = new JSONObject(data);
		
		JSONArray xArray = jsonObject.getJSONArray("x");
        JSONArray yArray = jsonObject.getJSONArray("y");
        JSONArray labelsArray = jsonObject.getJSONArray("labels");
		
		String[] head = new String[] { "FullName", "X", "Y" };
		List<List<Object>> fileData = new ArrayList<>();
		
		for (int i = 0; i < labelsArray.length(); i++) {
			List<Object> row = new ArrayList<>();
			
			row.add(labelsArray.getString(i));
			row.add(xArray.getDouble(i));
			row.add(yArray.getDouble(i));
			
			
			fileData.add(row);
		}
		
		ExportUtil.exportCSV(file, head, fileData);
	}

}
