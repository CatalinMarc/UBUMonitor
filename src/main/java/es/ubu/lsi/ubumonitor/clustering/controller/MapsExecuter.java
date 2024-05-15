package es.ubu.lsi.ubumonitor.clustering.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.ml.clustering.Clusterer;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.SmileAdapter;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;

public class MapsExecuter {

	private List<UserData> usersData;
	private double[][] map;
	
	public MapsExecuter(Algorithm algorithm, List<EnrolledUser> enrolledUsers,
			List<DataCollector> dataCollectors) {
		this.map = algorithm.getMaps(enrolledUsers, dataCollectors);
		usersData = enrolledUsers.stream().map(UserData::new).collect(Collectors.toList());
		if (usersData.size() < 2)
			throw new IllegalStateException("clustering.error.notUsers");
		dataCollectors.forEach(collector -> collector.collect(usersData));
	}
	
	/**
	 * Devuelve el mapa.
	 * 
	 * @return map
	 */
	public double[][] getClusterer() {
		return map;
	}
	
	/**
	 * Devuelve la lista de usuarios.
	 * 
	 * @return lista de usuarios
	 */
	public List<UserData> getUserData() {
		return usersData;
	}
	
}
