package es.ubu.lsi.ubumonitor.clustering.controller;

import java.util.List;

import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.controllers.tabs.MapsController;
import javafx.scene.control.TableView;

/**
 * Conector entre JavaScript y Java.
 * 
 * @author Ionut Catalin Marc
 *
 */
public class MapConnector {

	private PartitionalClusteringController controller;
	private List<ClusterWrapper> clusters;

	/**
	 * Constructor.
	 * 
	 * @param partitionalClusteringController controlador
	 */
	public MapConnector(PartitionalClusteringController partitionalClusteringController) {
		this.controller = partitionalClusteringController;
	}

//	/**
//	 * Marca un usuario en la tabla.
//	 * 
//	 * @param clusterIndex indice de la agrupación
//	 * @param index        indice del usuario dentro de la agrupación
//	 */
//	public void selectUser(int clusterIndex, int index) {
//		if (clusterIndex < clusters.size()) {
//			UserData userData = clusters.get(clusterIndex).get(index);
//			TableView<UserData> tableView = controller.getClusteringTable().getTableView();
//			tableView.getSelectionModel().select(userData);
//			tableView.scrollTo(userData);
//		}
//	}

	/**
	 * Establece los clusters.
	 * 
	 * @param clusters agrupaciones
	 */
	public void setClusters(List<ClusterWrapper> clusters) {
		this.clusters = clusters;
	}

}
