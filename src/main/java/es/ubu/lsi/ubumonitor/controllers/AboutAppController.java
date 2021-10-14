package es.ubu.lsi.ubumonitor.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import es.ubu.lsi.ubumonitor.AppInfo;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebEngine;

public class AboutAppController implements Initializable {

	private StringBuilder appInfo;

	@FXML
	private Label labelJre;

	@FXML
	private TextArea textArea;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		appInfo = new StringBuilder();
		append("Application version: ", AppInfo.APPLICATION_VERSION);
		append("Model version: ", AppInfo.MODEL_VERSION); 
		append("Installation directory for Java Runtime Environment (JRE): ", AppInfo.JAVA_HOME);
		append("JRE vendor name: ", AppInfo.JAVA_VENDOR);
		append("JRE version number: ", AppInfo.JAVA_VERSION);
		append("JFX version number: ", AppInfo.JAVA_FX_VERSION);
		append("Webview User Agent: ", new WebEngine().getUserAgent());
		append("Operating system architecture: ", AppInfo.OS_ARCH);
		append("Operating system name: ", AppInfo.OS_NAME);
		append("Operating system version: ", AppInfo.OS_VERSION);
		append("User working directory: ", AppInfo.USER_DIR);
		append("User home directory: ", AppInfo.USER_HOME);
		append("User account name: ", AppInfo.USER_NAME);
		textArea.setText(appInfo.toString());
		Platform.runLater(() -> textArea.requestFocus());

	}

	public void openLicense() {
		UtilMethods.openURL("https://github.com/yjx0003/UBUMonitor/blob/master/LICENSE");
	}

	public void openGithub() {
		UtilMethods.openURL(AppInfo.GITHUB);
	}
	
	public void openUBU() {
		UtilMethods.openURL("https://www.ubu.es/");
	}

	public void openAuthor1() {
		UtilMethods.mailTo("ypji@ubu.es");
	}

	public void openAuthor2() {
		throw new IllegalAccessError("Hola");
		//UtilMethods.openURL("https://investigacion.ubu.es/investigadores/35319/detalle");
	}

	public void openAuthor3() {
		UtilMethods.openURL("https://investigacion.ubu.es/investigadores/35408/detalle");
	}

	public void openAuthor4() {
		UtilMethods.mailTo("xjx1001@alu.ubu.es");
	}
	private void append(String key, String value) {
		appInfo.append(key + value + "\n");

	}
}
