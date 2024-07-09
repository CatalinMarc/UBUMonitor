package es.ubu.lsi.ubumonitor.clustering.algorithm.smile.maps;

import smile.vq.Neighborhood;
import smile.vq.SOM;
import smile.vq.VectorQuantizer;
import smile.clustering.HierarchicalClustering;
import smile.clustering.PartitionClustering;
import smile.clustering.linkage.Linkage;
import smile.math.MathEx;
import smile.math.TimeFunction;
import smile.plot.swing.Canvas;
import smile.plot.swing.Palette;
import smile.plot.swing.Hexmap;
import smile.plot.swing.Grid;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.ml.clustering.Clusterer;

import es.ubu.lsi.ubumonitor.clustering.algorithm.Algorithm;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.SmileAdapter;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.SOMType;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;

public class SOMAlgorithm extends Algorithm {
	
	private static final String NAME = "SelfOrganizingMap";
	private static final String LIBRARY = "Smile";

	protected SOMAdapter adapter;	

	/**
	 * Constructor del algoritmo UMatrix.
	 */
	public SOMAlgorithm() {
		super(NAME, LIBRARY);
		addParameter(ClusteringParameter.EPOCHS, 20);
		addParameter(ClusteringParameter.NUM_ROW, 20);
		addParameter(ClusteringParameter.NUM_COL, 20);
		addParameter(ClusteringParameter.SOM_TYPE, SOMType.SOM_UMATRIX);
	}
	
	@Override
	public Clusterer<UserData> getClusterer() {
		int epochs = getParameters().getValue(ClusteringParameter.EPOCHS);
		int nrow = getParameters().getValue(ClusteringParameter.NUM_ROW);
		int ncol = getParameters().getValue(ClusteringParameter.NUM_COL);
		
		checkParameter(ClusteringParameter.EPOCHS, epochs);
		checkParameter(ClusteringParameter.NUM_ROW, nrow);
		checkParameter(ClusteringParameter.NUM_COL, ncol);
			
		return new SOMAdapter(epochs, nrow, ncol);
	}
	
	protected class SOMAdapter extends SmileAdapter {

		private int epochs;
		private int nrow;
		private int ncol;
		
		private SOM som;
		
		public SOMAdapter(int epochs, int nrow, int ncol) {
			this.epochs = epochs;
			this.nrow = nrow;
			this.ncol = ncol;
		}	


		@Override
		protected VectorQuantizer execute(double[][] data) {

			double[][][] lattice = smile.vq.SOM.lattice(nrow, ncol, data);
			
			som = new SOM(lattice,
	                TimeFunction.constant(0.1),
	                Neighborhood.Gaussian(1, data.length * epochs / 4));

		    for (int e = 0; e < epochs; e++) {
		        for (int i : MathEx.permutate(data.length)) {
		            som.update(data[i]);
		        }
		    }
		    
			return som;
		
		}
		
		@Override
		public Canvas getCanvas(boolean SOMType) {
			if (SOMType) {
				 Grid grid = new Grid(som.neurons(), Color.black);
				 return grid.canvas();
			}
			
			Hexmap hexmap = new Hexmap(som.umatrix(), Palette.jet(256), null);
			return hexmap.canvas();
		}
	}
}
