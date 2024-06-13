package es.ubu.lsi.ubumonitor.clustering.data;

import es.ubu.lsi.ubumonitor.util.I18n;

/**
 * Enumeración de los diferentes parametros para un algoritmo de clustering.
 * 
 * @author Xing Long Ji
 *
 */
public enum ClusteringParameter {

	NUM_CLUSTER("numberOfClusters", 2), MAX_ITERATIONS("maxIterations", 1), DISTANCE_TYPE("distance", -1),
	FUZZINESS("fuzziness", 1.1), EPS("eps", 0), MIN_POINTS("minPts", 1), NUM_TRIALS("numTrials", 1),
	TOLERANCE("tol", 0), MAX_NUM_CLUSTER("maxNumberOfClusters", 2), ANNELING_CONTROL("anneling_control", 0, 1),
	SPLIT_TOLERANCE("splitTol", 0), SMOOTH("smooth", 0),
	//MAPS
	EPOCHS("epochs", 1), NUM_ROW("numberOfRows", 2), NUM_COL("numberOfColumns", 2), SOM_TYPE("type", -1),
	//BIRCH
	BRANCHING("branchingFactor", 0), CF_ENTRIES("clusteringFeatureEntries", 1), MAX_RADIUS("maxRadius", 0),
	//GROWING_NEURAL_GAS
	EPS_BEST("learnigRateBest", 0, 1), EPS_NEIGHBOR("learnigRateNeighbors", 0, 1), 
	EDGE_LIFETIME("maxAgeEdges", 1), LAMBDA("lambda", 1), ALPHA("alpha", 0, 1), BETA("beta", 0, 1), 
	DISTANCE("distanceThreshold", 1), NEURONS("neurons", 1),
	;

	private String name;
	private double min;
	private double max;

	private ClusteringParameter(String name, double min) {
		this(name, min, Double.POSITIVE_INFINITY);
	}

	private ClusteringParameter(String name, double min, double max) {
		this.name = name;
		this.min = min;
		this.max = max;
	}

	/**
	 * Devuleve el nombre del parámetro internacionalizado.
	 * 
	 * @return nombre del parámetro
	 */
	public String getName() {
		return I18n.get("clustering." + name);
	}

	/**
	 * Devulve una pequeña nombre del parámetro.
	 * 
	 * @return nombre del parámetro
	 */
	public String getDescription() {
		return I18n.get("clustering." + name + ".tooltip");
	}

	/**
	 * Comprueba si un valor es valido para este parametro.
	 * 
	 * @param value valor a comproabar
	 * @return true si es valido, si no false
	 */
	public boolean isValid(Number value) {
		return value.doubleValue() >= min && value.doubleValue() <= max;
	}

	/**
	 * Devuelde el valor mínimo que puede ser este parametro.
	 * 
	 * @return valor mínimo
	 */
	public double getMin() {
		return min;
	}

	@Override
	public String toString() {
		return getName();
	}

}
