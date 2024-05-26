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
//		addParameter(ClusteringParameter.EPOCHS, 20);
	}

	@Override
	public Clusterer<UserData> getClusterer() {
//		int epochs = getParameters().getValue(ClusteringParameter.EPOCHS);
//		
//		checkParameter(ClusteringParameter.EPOCHS, epochs);
		
		return new NeuralMapAdapter();
	}
	
	private class NeuralMapAdapter extends SmileAdapter {

		private double[][] data;
		private NeuralMap neuralmap;
		
		public NeuralMapAdapter() {
			
		}
		
		@Override
		public VectorQuantizer execute(double[][] data) {
			
			this.data = data;
			
			neuralmap = new NeuralMap(10, 0.01, 0.002, 50, 0.995);
			
			for (int e = 0; e < 5; e++) {
		        for (int i : MathEx.permutate(data.length)) {
		        	neuralmap.update(data[i]);
		        }

		        // Removes staled neurons and the edges beyond lifetime.
		        neuralmap.clear(1E-7);
		    }
			
			return neuralmap;
		}
		
		@Override
		public Canvas getCanvas(boolean a) {
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
