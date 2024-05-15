package es.ubu.lsi.ubumonitor.clustering.algorithm.smile.maps;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.ml.clustering.Clusterer;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import smile.math.MathEx;
import smile.math.TimeFunction;
import smile.vq.NeuralGas;

public class NeuralGasA extends Algorithm {

	private static final String NAME = "NeuralGas";
	private static final String LIBRARY = "Smile";
	
	NeuralGasAdapter adapter;

	/**
	 * Constructor del algoritmo UMatrix.
	 */
	public NeuralGasA() {
		super(NAME, LIBRARY);
//		addParameter(ClusteringParameter.EPS, 0.4);
//		addParameter(ClusteringParameter.MIN_POINTS, 1);
//		
		adapter = new NeuralGasAdapter();
	}
	
	@Override
	public Clusterer<UserData> getClusterer() {
		return null;
	}
	
	@Override
	public double[][] getMaps(List<EnrolledUser> users, List<DataCollector> collectors) {
		return new NeuralGasAdapter().execute(users, collectors);
	}
	
	public double[][] execute(List<EnrolledUser> users, List<DataCollector> collectors) {
		
		return adapter.execute(users, collectors);
	}
	
	private class NeuralGasAdapter {

		private List<UserData> usersData;
		
		public NeuralGasAdapter() {
			
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
			int T = data.length * epochs;
			
	        NeuralGas gas = new NeuralGas(NeuralGas.seed(400, data),
	                TimeFunction.exp(0.3, T / 2.0),
	                TimeFunction.exp(30, T / 8.0),
	                TimeFunction.constant(data.length * 2));

		    for (int e = 0; e < epochs; e++) {
		        for (int i : MathEx.permutate(data.length)) {
		            gas.update(data[i]);
		        }
		    }
	    
		    return gas.neurons();
		
		}
		
		
	}
}
