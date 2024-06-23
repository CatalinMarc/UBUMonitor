package es.ubu.lsi.ubumonitor.clustering.algorithm.smile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.DoublePoint;

import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.maps.BIRCHAlgorithm.BIRCHAdapter;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import smile.clustering.CentroidClustering;
import smile.clustering.PartitionClustering;
import smile.plot.swing.Canvas;
import smile.vq.BIRCH;
import smile.vq.NeuralGas;
import smile.vq.NeuralMap;
import smile.vq.SOM;
import smile.vq.VectorQuantizer;
import smile.vq.hebb.Neuron;

public abstract class SmileAdapter extends Clusterer<UserData> {

	protected int componentSize;
	
	protected SmileAdapter() {
		super(null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<? extends Cluster<UserData>> cluster(Collection<UserData> points) {
		double[][] data = stream(points);
		PartitionClustering clustering = (PartitionClustering) execute(data);
		if (clustering instanceof CentroidClustering) {
			return adaptSmile(points, (CentroidClustering<double[], double[]>) clustering);
		}
		return adaptSmile(points, clustering);
	}
	
	public void executeMaps(Collection<UserData> points, int componentSize) {
		this.componentSize = componentSize;
		execute(stream(points));
	}
		
	private double[][] stream(Collection<UserData> points) {
		return points.stream().map(UserData::getPoint).toArray(double[][]::new);
	}
	
	protected abstract Serializable execute(double[][] data);
	
	private List<? extends Cluster<UserData>> adaptSmile(Collection<UserData> points,
			CentroidClustering<double[], double[]> centroidClustering) {
		List<UserData> users = new ArrayList<>(points);
		List<CentroidCluster<UserData>> result = new ArrayList<>();
		for (int i = 0; i < centroidClustering.k; i++) {
			DoublePoint center = new DoublePoint(convertNaNs(centroidClustering.centroids[i]));
			CentroidCluster<UserData> cluster = new CentroidCluster<>(center);
			addPoints(centroidClustering, users, i, cluster);
			result.add(cluster);
		}
		return result;
	}

	private void addPoints(PartitionClustering clustering, List<UserData> users, int i,
			Cluster<UserData> cluster) {
		for (int j = 0; j < clustering.y.length; j++) {
			if (i == clustering.y[j])
				cluster.addPoint(users.get(j));
		}
	}

	private List<? extends Cluster<UserData>> adaptSmile(Collection<UserData> points, PartitionClustering clustering) {
		List<UserData> users = new ArrayList<>(points);
		List<Cluster<UserData>> result = new ArrayList<>();
		for (int i = 0; i < clustering.k; i++) {
			Cluster<UserData> cluster = new Cluster<>();
			addPoints(clustering, users, i, cluster);
			result.add(cluster);
		}
		return result;
	}

	private double[] convertNaNs(double[] point) {
		for (int i = 0; i < point.length; i++) {
			if (Double.isNaN(point[i]))
				point[i] = 0;
		}
		return point;
	}

	public Canvas getCanvas(boolean a) {
		return null;
	}
	
}
