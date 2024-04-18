package es.ubu.lsi.ubumonitor.controllers.tabs.clustering;

import org.controlsfx.control.CheckComboBox;

import es.ubu.lsi.ubumonitor.clustering.controller.collector.LogCollector;
import es.ubu.lsi.ubumonitor.clustering.data.LinkageMeasure;
import es.ubu.lsi.ubumonitor.controllers.MainController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import smile.math.distance.Distance;

/**
 * Controlador del clustering particional.
 * 
 * @author Xing Long Ji
 *
 */
public class HierarchicalController {

	private MainController mainController;

	@FXML
	private ChoiceBox<Distance<double[]>> choiceBoxDistance;

	@FXML
	private ChoiceBox<LinkageMeasure> choiceBoxLinkage;
	
	@FXML
	private CheckComboBox<LogCollector<?>> checkComboBoxLogs;

	@FXML
	private CheckBox checkBoxLogs;

	@FXML
	private CheckBox checkBoxGrades;

	@FXML
	private CheckBox checkBoxActivity;

	@FXML
	private DatePicker datePickerStart;
	
	@FXML
	private DatePicker datePickerEnd;
	
	@FXML
	private Button buttonExecute;
	
	@FXML
	private Spinner<Integer> spinnerClusters;	

	/**
	 * Inicializa el controlador.
	 * 
	 * @param controller controlador general
	 */
	public void init(MainController controller) {
		mainController = controller;

	}

	/**
	 * @return the mainController
	 */
	public MainController getMainController() {
		return mainController;
	}

	/**
	 * @return the choiceBoxDistance
	 */
	public ChoiceBox<Distance<double[]>> getChoiceBoxDistance() {
		return choiceBoxDistance;
	}

	/**
	 * @return the choiceBoxLinkage
	 */
	public ChoiceBox<LinkageMeasure> getChoiceBoxLinkage() {
		return choiceBoxLinkage;
	}

	/**
	 * @return the checkComboBoxLogs
	 */
	public CheckComboBox<LogCollector<?>> getCheckComboBoxLogs() {
		return checkComboBoxLogs;
	}

	/**
	 * @return the checkBoxLogs
	 */
	public CheckBox getCheckBoxLogs() {
		return checkBoxLogs;
	}

	/**
	 * @return the checkBoxGrades
	 */
	public CheckBox getCheckBoxGrades() {
		return checkBoxGrades;
	}

	/**
	 * @return the checkBoxActivity
	 */
	public CheckBox getCheckBoxActivity() {
		return checkBoxActivity;
	}

	/**
	 * @return the datePickerStart
	 */
	public DatePicker getDatePickerStart() {
		return datePickerStart;
	}

	/**
	 * @return the datePickerEnd
	 */
	public DatePicker getDatePickerEnd() {
		return datePickerEnd;
	}

	/**
	 * @return the buttonExecute
	 */
	public Button getButtonExecute() {
		return buttonExecute;
	}

	/**
	 * @return the spinnerClusters
	 */
	public Spinner<Integer> getSpinnerClusters() {
		return spinnerClusters;
	}


}
