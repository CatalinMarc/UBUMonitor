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
import smile.plot.swing.Canvas;
import smile.vq.VectorQuantizer;

public class MapsExecuter {

	private List<UserData> usersData;
	private SmileAdapter map;
	
	public MapsExecuter(Algorithm algorithm, List<EnrolledUser> enrolledUsers,
			List<DataCollector> dataCollectors) {
		this.map = (SmileAdapter) algorithm.getClusterer();
		usersData = enrolledUsers.stream().map(UserData::new).collect(Collectors.toList());
		if (usersData.size() < 2)
			throw new IllegalStateException("clustering.error.notUsers");
		dataCollectors.forEach(collector -> collector.collect(usersData));
	}
	
	public Canvas execute(boolean a) {
		checkData();

		return map.execute(usersData, a);
	}
	
//	public Canvas executeSOMGrid() {
//		checkData();
//
//		return map.execute(usersData);
//	}
	
//	public double[][][] execute3D() {
//		
//		checkData();
//		
//		return map.maps3D(usersData);
//	}
	
	private void checkData() {
		if (usersData.size() < 2)
			throw new IllegalStateException("clustering.error.notUsers");

		if (usersData.get(0).getData().isEmpty())
			throw new IllegalStateException("clustering.error.notData");
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