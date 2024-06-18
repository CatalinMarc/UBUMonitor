package es.ubu.lsi.ubumonitor.clustering.algorithm.smile.maps;

import java.awt.Color;
import java.util.Arrays;

import org.apache.commons.math3.ml.clustering.Clusterer;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.SmileAdapter;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.maps.BIRCHAlgorithm.BIRCHAdapter;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import smile.math.MathEx;
import smile.plot.swing.Canvas;
import smile.plot.swing.Line;
import smile.plot.swing.ScatterPlot;
import smile.plot.swing.Line.Style;
import smile.vq.NeuralMap;
import smile.vq.VectorQuantizer;
import smile.vq.hebb.Edge;
import smile.vq.hebb.Neuron;

public class NeuralMapAlgorithm extends Algorithm {

	private static final String NAME = "NeuralMap";
	private static final String LIBRARY = "Smile";
	
	public NeuralMapAlgorithm() {
		super(NAME, LIBRARY);
		addParameter(ClusteringParameter.DISTANCE, 10);
		addParameter(ClusteringParameter.EPS_BEST, 0.01);
		addParameter(ClusteringParameter.EPS_NEIGHBOR, 0.002);
		addParameter(ClusteringParameter.EDGE_LIFETIME, 50);
		addParameter(ClusteringParameter.BETA, 0.995);
	}

	@Override
	public Clusterer<UserData> getClusterer() {
		int dist = getParameters().getValue(ClusteringParameter.DISTANCE);
		double eps_best = getParameters().getValue(ClusteringParameter.EPS_BEST);
		double eps_neighbor = getParameters().getValue(ClusteringParameter.EPS_NEIGHBOR);
		int edge_lifetime = getParameters().getValue(ClusteringParameter.EDGE_LIFETIME);
		double beta = getParameters().getValue(ClusteringParameter.BETA);
		
		checkParameter(ClusteringParameter.DISTANCE, dist);
		checkParameter(ClusteringParameter.EPS_BEST, eps_best);
		checkParameter(ClusteringParameter.EPS_NEIGHBOR, eps_neighbor);
		checkParameter(ClusteringParameter.EDGE_LIFETIME, edge_lifetime);
		checkParameter(ClusteringParameter.BETA, beta);
		
		return new NeuralMapAdapter(dist, eps_best, eps_neighbor, edge_lifetime, beta);
	}
	
	private class NeuralMapAdapter extends SmileAdapter {

		private int dist;
		private double eps_best;
		private double eps_neighbor;
		private int edge_lifetime;
		private double beta;
		
		private double[][] data;
		private NeuralMap neuralmap;
		
		public NeuralMapAdapter(int dist, double eps_best, double eps_neighbor,
				int edge_lifetime, double beta) {
			this.dist = dist;
			this.eps_best = eps_best;
			this.eps_neighbor = eps_neighbor; 
			this.edge_lifetime = edge_lifetime; 
			this.beta = beta;
		}
		
		@Override
		public VectorQuantizer execute(double[][] data) {
			
			this.data = data;
			
			neuralmap = new NeuralMap(dist, eps_best, eps_neighbor, edge_lifetime, beta);
			
			for (int e = 0; e < 5; e++) {
		        for (int i : MathEx.permutate(data.length)) {
		        	neuralmap.update(data[i]);
		        }

		        neuralmap.clear(1E-7);
		    }
			
			setData();
			return neuralmap;
		}
		
		private void setData() {
			Neuron[] neurons = neuralmap.neurons();
			double[][] neuronsArray = Arrays.stream(neurons).map(n -> n.w).toArray(double[][]::new); 
			
			
			setData2D(data, neuronsArray, neurons);
			clearData3D();
			if(componentSize != 2)
				setData3D(neuronsArray);
		}
		
		@Override
		public Canvas getCanvas(boolean SOMType) {
			Neuron[] neurons = neuralmap.neurons();
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
