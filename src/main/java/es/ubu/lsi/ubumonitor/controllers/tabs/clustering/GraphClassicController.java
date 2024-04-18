package es.ubu.lsi.ubumonitor.controllers.tabs.clustering;

import es.ubu.lsi.ubumonitor.controllers.MainController;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;

public class GraphClassicController {
	
	private MainController mainController;
	
	@FXML
	private WebView webViewScatter;

	@FXML
	private WebView webView3DScatter;

	@FXML
	private WebView webViewSilhouette;

	public void init(MainController mainController) {
		this.mainController = mainController;
	}
	
	/**
	 * @return the webViewScatter
	 */
	public WebView getWebViewScatter() {
		return webViewScatter;
	}

	/**
	 * @return the webView3DScatter
	 */
	public WebView getWebView3DScatter() {
		return webView3DScatter;
	}

	/**
	 * @return the webViewSilhouette
	 */
	public WebView getWebViewSilhouette() {
		return webViewSilhouette;
	}
}
