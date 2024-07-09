package es.ubu.lsi.ubumonitor.clustering.chart;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import es.ubu.lsi.ubumonitor.clustering.controller.Connector;
import es.ubu.lsi.ubumonitor.clustering.controller.MapsController;
import es.ubu.lsi.ubumonitor.clustering.controller.PartitionalClusteringController;
import es.ubu.lsi.ubumonitor.clustering.util.ExportUtil;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class MapScatter3D extends AbstractChart {
	
	private String data;
	
	public MapScatter3D(MapsController mapsController) {
		super(mapsController.getWebView3DScatter());
		
		WebEngine webEngine = getWebEngine();

		webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
			if (Worker.State.SUCCEEDED != newState)
				return;
		});
		webEngine.load(getClass().getResource("/graphics/Maps3DChart.html").toExternalForm());
			
	}
	
	public void updateChart(String data) {
		this.data = data;
		
		WebEngine webEngine = getWebEngine();

		webEngine.executeScript("clearChart()");
		
		JSObject window = (JSObject) webEngine.executeScript("window");
		
		window.call("renderChart3D", data);
	}
	
	@Override
	protected void exportData(File file) throws IOException {
		JSONObject jsonObject = new JSONObject(data);
		
		JSONArray xArray = jsonObject.getJSONArray("x");
        JSONArray yArray = jsonObject.getJSONArray("y");
        JSONArray zArray = jsonObject.getJSONArray("z");
        JSONArray labelsArray = jsonObject.getJSONArray("labels");
		
		String[] head = new String[] { "FullName", "X", "Y", "Z" };
		List<List<Object>> fileData = new ArrayList<>();
		
		for (int i = 0; i < labelsArray.length(); i++) {
			List<Object> row = new ArrayList<>();
			
			row.add(labelsArray.getString(i));
			row.add(xArray.getDouble(i));
			row.add(yArray.getDouble(i));
			row.add(zArray.getDouble(i));
			
			
			fileData.add(row);
		}
		
		ExportUtil.exportCSV(file, head, fileData);
	}

}
