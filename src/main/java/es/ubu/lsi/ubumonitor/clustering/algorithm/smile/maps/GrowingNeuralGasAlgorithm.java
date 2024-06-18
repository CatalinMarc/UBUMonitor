package es.ubu.lsi.ubumonitor.clustering.algorithm.smile.maps;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.ml.clustering.Clusterer;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.SmileAdapter;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import smile.math.MathEx;
import smile.math.TimeFunction;
import smile.plot.swing.Canvas;
import smile.plot.swing.Plot;
import smile.plot.swing.Line;
import smile.plot.swing.ScatterPlot;
import smile.plot.swing.Line.Style;
import smile.vq.GrowingNeuralGas;
import smile.vq.VectorQuantizer;
import smile.vq.hebb.Edge;
import smile.vq.hebb.Neuron;

public class GrowingNeuralGasAlgorithm extends Algorithm {

	private static final String NAME = "GrowingNeuralGas";
	private static final String LIBRARY = "Smile";

	/**
	 * Constructor del algoritmo UMatrix.
	 */
	public GrowingNeuralGasAlgorithm() {
		super(NAME, LIBRARY);
		addParameter(ClusteringParameter.EPOCHS, 20);
		addParameter(ClusteringParameter.EPS_BEST, 0.01);
		addParameter(ClusteringParameter.EPS_NEIGHBOR, 0.002);
		addParameter(ClusteringParameter.EDGE_LIFETIME, 50);
		addParameter(ClusteringParameter.LAMBDA, 100);
		addParameter(ClusteringParameter.ALPHA, 0.5);
		addParameter(ClusteringParameter.BETA, 0.995);
	}
	
	@Override
	public Clusterer<UserData> getClusterer() {
		int epochs = getParameters().getValue(ClusteringParameter.EPOCHS);
		double eps_best = getParameters().getValue(ClusteringParameter.EPS_BEST);
		double eps_neighbor = getParameters().getValue(ClusteringParameter.EPS_NEIGHBOR);
		int edge_lifetime = getParameters().getValue(ClusteringParameter.EDGE_LIFETIME);
		int lambda = getParameters().getValue(ClusteringParameter.LAMBDA);
		double alpha = getParameters().getValue(ClusteringParameter.ALPHA);
		double beta = getParameters().getValue(ClusteringParameter.BETA);
		
		checkParameter(ClusteringParameter.EPOCHS, epochs);
		checkParameter(ClusteringParameter.EPS_BEST, eps_best);
		checkParameter(ClusteringParameter.EPS_NEIGHBOR, eps_neighbor);
		checkParameter(ClusteringParameter.EDGE_LIFETIME, edge_lifetime);
		checkParameter(ClusteringParameter.LAMBDA, lambda);
		checkParameter(ClusteringParameter.ALPHA, alpha);
		checkParameter(ClusteringParameter.BETA, beta);
		
		return new GrowingNeuralGasAdapter(epochs, eps_best, eps_neighbor, edge_lifetime, lambda, alpha, beta);
	}
	
	private class GrowingNeuralGasAdapter extends SmileAdapter {

		private int epochs;
		private double eps_best;
		private double eps_neighbor;
		private int edge_lifetime;
		private int lambda;
		private double alpha;;
		private double beta;
		
		private double[][] data;
		private GrowingNeuralGas gng;
		
		public GrowingNeuralGasAdapter(int epochs, double eps_best, double eps_neighbor,
				int edge_lifetime, int lambda, double alpha, double beta) {	
			this.epochs = epochs;
			this.eps_best = eps_best;
			this.eps_neighbor = eps_neighbor; 
			this.edge_lifetime = edge_lifetime; 
			this.lambda = lambda;
			this.alpha = alpha;
			this.beta = beta;
			
		}
		
		@Override
		public VectorQuantizer execute(double[][] data) {
			
			this.data = data;
			
			gng = new GrowingNeuralGas(data[0].length, eps_best, eps_neighbor,
					edge_lifetime, lambda, alpha, beta);
			
		    for (int e = 0; e < epochs; e++) {
		    	int[] permutation = MathEx.permutate(data.length);
		        for (int i : permutation) {
		        	gng.update(data[i]);
		        }
		    }
		    
		    setData();
		    return gng;		
		}
		
		private void setData() {
			Neuron[] neurons = gng.neurons();
			double[][] neuronsArray = Arrays.stream(neurons).map(n -> n.w).toArray(double[][]::new);
			
			setData2D(data, neuronsArray, neurons);
			clearData3D();
			if(componentSize != 2)
				setData3D(neuronsArray);
		}
				
		public Canvas getCanvas(boolean SOMType) {
			Neuron[] neurons = gng.neurons();
			double[][] neuronsArray = Arrays.stream(neurons).map(n -> n.w).toArray(double[][]::new);
			
	        ScatterPlot scatter = ScatterPlot.of(neuronsArray, '#', Color.BLUE);
	        Canvas canvas = scatter.canvas();

	        for (Neuron neuron : neurons) {
	            for (Edge edge : neuron.edges) {
	                double[][] edgeCoords = {neuron.w, edge.neighbor.w};
	                canvas.add(new Line(edgeCoords, Style.SOLID ,'#' , Color.BLUE));
	            }
	        }
	        
	        ScatterPlot scatterData = ScatterPlot.of(data, '#', Color.BLACK);
	        
	        canvas.add(scatterData);
	        
	        return canvas;
		}
	}
}
