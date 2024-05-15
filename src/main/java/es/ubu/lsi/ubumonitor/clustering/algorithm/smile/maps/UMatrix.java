package es.ubu.lsi.ubumonitor.clustering.algorithm.smile.maps;

import smile.vq.Neighborhood;
import smile.vq.SOM;
import smile.clustering.HierarchicalClustering;
import smile.clustering.PartitionClustering;
import smile.clustering.linkage.Linkage;
import smile.math.MathEx;
import smile.math.TimeFunction;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.ml.clustering.Clusterer;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.SmileAdapter;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;

public class UMatrix extends Algorithm {
	
	private static final String NAME = "UMatrix";
	private static final String LIBRARY = "Smile";
	
	UMatrixAdapter adapter;

	/**
	 * Constructor del algoritmo UMatrix.
	 */
	public UMatrix() {
		super(NAME, LIBRARY);
		addParameter(ClusteringParameter.EPS, 0.4);
		addParameter(ClusteringParameter.MIN_POINTS, 1);
		
		adapter = new UMatrixAdapter();
	}
	
	@Override
	public UMatrixAdapter getClusterer() {
		return null;//new UMatrixAdapter();
	}
	
	@Override
	public double[][] getMaps(List<EnrolledUser> users, List<DataCollector> collectors) {
		return new UMatrixAdapter().execute(users, collectors);
	}
	
	public double[][] execute(List<EnrolledUser> users, List<DataCollector> dataCollectors) {
		
		return adapter.execute(users, dataCollectors);
	}
	
	private class UMatrixAdapter extends SmileAdapter {

		private List<UserData> usersData;
		
		public UMatrixAdapter() {
			
		}
		
		public double[][] execute(List<EnrolledUser> users, List<DataCollector> collectors) {
			
			usersData = users.stream().map(UserData::new).collect(Collectors.toList());
			collectors.forEach(collector -> collector.collect(usersData));

			if (usersData.size() < 2)
				throw new IllegalStateException("clustering.error.notUsers");

			if (usersData.get(0).getData().isEmpty())
				throw new IllegalStateException("clustering.error.notData");

			double[][] data = usersData.stream().map(UserData::getPoint).toArray(double[][]::new);
			
			int epochs = 20;
			double[][][] lattice = smile.vq.SOM.lattice(20, 20, data);
			
			SOM som = new SOM(lattice,
	                TimeFunction.constant(0.1),
	                Neighborhood.Gaussian(1, data.length * epochs / 4));

		    for (int e = 0; e < epochs; e++) {
		        for (int i : MathEx.permutate(data.length)) {
		            som.update(data[i]);
		        }
		    }
		    
	    	return som.umatrix();
	    	
		}

		@Override
		protected PartitionClustering execute(double[][] data) {
			return null;
		}
		
	}
}
