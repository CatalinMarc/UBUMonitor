package es.ubu.lsi.ubumonitor.clustering.algorithm.smile.maps;

import java.awt.Color;
import java.util.Arrays;

import org.apache.commons.math3.ml.clustering.Clusterer;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.SmileAdapter;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import smile.math.MathEx;
import smile.math.TimeFunction;
import smile.plot.swing.Canvas;
import smile.plot.swing.ScatterPlot;
import smile.vq.BIRCH;
import smile.vq.VectorQuantizer;
import smile.vq.hebb.Neuron;

public class BIRCHAlgorithm extends Algorithm {

	private static final String NAME = "BIRCH";
	private static final String LIBRARY = "Smile";

	private BIRCHAdapter adapter;
	
	/**
	 * Constructor del algoritmo UMatrix.
	 */
	public BIRCHAlgorithm() {
		super(NAME, LIBRARY);
		addParameter(ClusteringParameter.BRANCHING, 20);
		addParameter(ClusteringParameter.CF_ENTRIES, 20);
		addParameter(ClusteringParameter.MAX_RADIUS, 20);
		
	}
	
	@Override
	public Clusterer<UserData> getClusterer() {
		int branching = getParameters().getValue(ClusteringParameter.BRANCHING);
		int cf = getParameters().getValue(ClusteringParameter.CF_ENTRIES);
		int radius = getParameters().getValue(ClusteringParameter.MAX_RADIUS);
		
		checkParameter(ClusteringParameter.BRANCHING, branching);
		checkParameter(ClusteringParameter.CF_ENTRIES, cf);
		checkParameter(ClusteringParameter.MAX_RADIUS, radius);
		
		adapter = new BIRCHAdapter(branching, cf, radius);
		
		return adapter; 
	}
	
	public class BIRCHAdapter extends SmileAdapter {

		private int B;
		private int L;
		private int T;
		
		private double[][] data;		
		private BIRCH birch;
		
		public BIRCHAdapter(int B, int L, int T) {
			this.B = B;
	        this.L = L;
	        this.T = T;
		}
		
		@Override
		public VectorQuantizer execute(double[][] data) {
			
			this.data = data;
			
			birch = new BIRCH(data[0].length, B, L, T);
			
			for (double[] x : data) {
	            birch.update(x);
	        }
			
			setData();
			return birch;
		}	
		
		private void setData() {
			double[][] centroids = birch.centroids();
			
			setData2D(data, centroids, null);
			clearData3D();
			if(componentSize != 2)
				setData3D(centroids);
		}
		
		@Override
		public Canvas getCanvas(boolean SOMType) {
			ScatterPlot scatter = ScatterPlot.of(birch.centroids(), '#', Color.BLUE);
			ScatterPlot scatterData = ScatterPlot.of(data, '#', Color.BLACK);
	        
			Canvas canvas = scatter.canvas();
	        canvas.add(scatterData);
	        
			return canvas;
		}
	}
}
