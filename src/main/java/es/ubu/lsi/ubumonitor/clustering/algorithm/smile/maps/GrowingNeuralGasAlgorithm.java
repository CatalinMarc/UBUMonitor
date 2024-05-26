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
	}
	
	@Override
	public Clusterer<UserData> getClusterer() {
		int epochs = getParameters().getValue(ClusteringParameter.EPOCHS);
		
		checkParameter(ClusteringParameter.EPOCHS, epochs);
		
		return new GrowingNeuralGasAdapter(epochs);
	}
	
	private class GrowingNeuralGasAdapter extends SmileAdapter {

		private int epochs;
		
		private double[][] data;
		private GrowingNeuralGas gng;
		
		public GrowingNeuralGasAdapter(int epochs) {			
			this.epochs = epochs;
		}
		
		@Override
		public VectorQuantizer execute(double[][] data) {
			
			this.data = data;
			
			gng = new GrowingNeuralGas(data[0].length);
			
		    for (int e = 0; e < epochs; e++) {
		    	int[] permutation = MathEx.permutate(data.length);
		        for (int i : permutation) {
		        	gng.update(data[i]);
		        }
		    }
		    
		    return gng;		
		}
		
		public Canvas getCanvas(boolean a) {
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
