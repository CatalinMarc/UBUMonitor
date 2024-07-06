package es.ubu.lsi.ubumonitor.clustering.controller;

import java.util.List;

import es.ubu.lsi.ubumonitor.clustering.data.ClusterWrapper;

/**
 * Conector entre JavaScript y Java.
 * 
 * @author Ionut Catalin Marc
 *
 */
public class MapConnector {

	private MapsController controller;
	private List<ClusterWrapper> clusters;

	/**
	 * Constructor.
	 * 
	 * @param mapsController controlador
	 */
	public MapConnector(MapsController mapsController) {
		this.controller = mapsController;
	}

	/**
	 * Establece los clusters.
	 * 
	 * @param clusters agrupaciones
	 */
	public void setClusters(List<ClusterWrapper> clusters) {
		this.clusters = clusters;
	}

}
