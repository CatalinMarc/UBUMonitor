package es.ubu.lsi.ubumonitor.clustering.algorithm.smile.maps;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
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
import smile.plot.swing.ScatterPlot;
import smile.vq.NeuralGas;
import smile.vq.VectorQuantizer;
import smile.vq.hebb.Neuron;

public class NeuralGasAlgorithm extends Algorithm {

	private static final String NAME = "NeuralGas";
	private static final String LIBRARY = "Smile";

	/**
	 * Constructor del algoritmo UMatrix.
	 */
	public NeuralGasAlgorithm() {
		super(NAME, LIBRARY);
		addParameter(ClusteringParameter.EPOCHS, 20);
		addParameter(ClusteringParameter.NEURONS, 50);
	}
	
	@Override
	public Clusterer<UserData> getClusterer() {
		int epochs = getParameters().getValue(ClusteringParameter.EPOCHS);
		int neurons = getParameters().getValue(ClusteringParameter.NEURONS);
		
		checkParameter(ClusteringParameter.EPOCHS, epochs);
		checkParameter(ClusteringParameter.NEURONS, neurons);
		
		return new NeuralGasAdapter(epochs, neurons);
	}
	
	private class NeuralGasAdapter extends SmileAdapter {

		private int epochs;
		private int neurons;
		
		private double[][] data;
		private NeuralGas gas;
		
		public NeuralGasAdapter(int epochs, int neurons) {
			this.epochs = epochs;
			this.neurons = neurons;
		}
		
		@Override
		public VectorQuantizer execute(double[][] data) {
			
			this.data = data;
			
			int T = data.length * epochs;
			
	        gas = new NeuralGas(NeuralGas.seed(neurons, data),
	                TimeFunction.exp(0.3, T / 2.0),
	                TimeFunction.exp(30, T / 8.0),
	                TimeFunction.constant(data.length * 2));

		    for (int e = 0; e < epochs; e++) {
		        for (int i : MathEx.permutate(data.length)) {
		            gas.update(data[i]);
		        }
		    }
	    
		    setData();
		    return gas;		
		}
		
		private void setData() {
			double[][] neurons = gas.neurons();
			
			setData2D(data, neurons, null);
			clearData3D();
			if(componentSize != 2)
				setData3D(data, neurons, null);
		}
				
		@Override
		public Canvas getCanvas(boolean SOMType) {
			ScatterPlot scatter = ScatterPlot.of(gas.neurons(), '#', Color.BLUE);
			ScatterPlot scatterData = ScatterPlot.of(data, '#', Color.BLACK);
	        
			Canvas canvas = scatter.canvas();
	        canvas.add(scatterData);
	        
			return canvas;
		}
		
		
		
	}
}
