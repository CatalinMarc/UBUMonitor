package es.ubu.lsi.ubumonitor.clustering.algorithm;

import java.util.List;

import org.apache.commons.math3.ml.clustering.Clusterer;

import com.jujutsu.tsne.PrincipalComponentAnalysis;

import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.maps.BIRCHAlgorithm;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.maps.NeuralMapAlgorithm;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.clustering.exception.IllegalParamenterException;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import smile.vq.VectorQuantizer;
import smile.vq.hebb.Edge;
import smile.vq.hebb.Neuron;

/**
 * Clase base de los algoritmos de clustering.
 * 
 * @author Xing Long Ji
 *
 */
public abstract class Algorithm {

	private String name;
	private String library;
	private AlgorithmParameters parameters = new AlgorithmParameters();
	private PrincipalComponentAnalysis pca = new PrincipalComponentAnalysis();
	
	private List<EnrolledUser> users;
	protected String data2D;
	protected String data3D;
	
	/**
	 * Constructor de un algoritmo.
	 * 
	 * @param name    nombre del algoritmo
	 * @param library biblioteca a la que pertecence
	 */
	protected Algorithm(String name, String library) {
		setName(name);
		setLibrary(library);
	}

	/**
	 * Añade un nuevo parametros al algortimo.
	 * 
	 * @param parameter parametro
	 * @param value     valor por defecto
	 */
	protected void addParameter(ClusteringParameter parameter, Object value) {
		parameters.addParameter(parameter, value);
	}

	/**
	 * Comprueba si el valor es válido para el parametro.
	 * 
	 * @param parameter parametro
	 * @param value     valor a comprobar
	 * @throws IllegalParamenterException si el valor no es válido
	 */
	protected void checkParameter(ClusteringParameter parameter, Number value) {
		if (!parameter.isValid(value))
			throw new IllegalParamenterException(parameter, value);
	}

	/**
	 * Devulve el clusterer del algoritmo.
	 * 
	 * @return clusterer del algoritmo
	 */
	public abstract Clusterer<UserData> getClusterer();

	protected void setData2D(double[][] data, double[][] neuronsArray, Neuron[] neurons) {
		
		if(data[0].length > 2) {
			data = pca.pca(data, 2);
			if (!(this instanceof BIRCHAlgorithm))
				neuronsArray = pca.pca(neuronsArray, 2);
		}

		StringBuilder xData = new StringBuilder();
        StringBuilder yData = new StringBuilder();
        StringBuilder labelData = new StringBuilder();
        StringBuilder dataSize = new StringBuilder();
        String edges = "[]";
    
        xData.append("[");
        yData.append("[");
        labelData.append("[");

        // Data
        for (int i = 0; i < data.length; i++) {
            xData.append(data[i][0]);
            yData.append(data[i][1]);
            labelData.append("\"").append(users.get(i).getFullName()).append("\"");
            
            if (i < data.length - 1) {
                xData.append(",");
                yData.append(",");
                labelData.append(",");
            }
        }

        xData.append(",");
        yData.append(",");
        
        // Neurons
        for (int i = 0; i < neuronsArray.length; i++) {
        	
            xData.append(neuronsArray[i][0]);
            yData.append(neuronsArray[i][1]);
            
            if (i < neuronsArray.length - 1) {
                xData.append(",");
                yData.append(",");
            }
        }
        
        xData.append("]");
        yData.append("]");
        labelData.append("]");
        
        dataSize.append("[" + data.length  + "]");
        
        if(neurons != null) 
        	edges = edgesToJSON(neurons);
        
        data2D = "{\"x\":" + xData.toString() 
        			+ ",\"y\":" + yData.toString() 
        			+ ",\"labels\":" + labelData.toString()
        			+ ",\"size\":" + dataSize.toString() 
        			+ ",\"edges\":" + edges
        			+ "}";

	}
	
	private String edgesToJSON(Neuron[] neurons) {
	    String neuronA = "";
	    String neuronB = "";
	    StringBuilder edges = new StringBuilder();

	    edges.append("[");

	    for (Neuron neuron : neurons) {
	        for (Edge edge : neuron.edges) {
	            double[] coordA;
	            double[] coordB;

	            // if the dimension is > 2 and we have to apply dimension reduction
	            if (neuron.w.length > 2) {
	                coordA = pca.sampleToEigenSpace(neuron.w);
	                coordB = pca.sampleToEigenSpace(edge.neighbor.w);
	            } else {
	                coordA = neuron.w;
	                coordB = edge.neighbor.w;
	            }

	            neuronA = "[" + changeSign(coordA[0]) + "," + changeSign(coordA[1]) + "]";
	            neuronB = "[" + changeSign(coordB[0]) + "," + changeSign(coordB[1]) + "]";

	            edges.append("[" + neuronA + "," + neuronB + "],");
	        }
	    }

	    edges.deleteCharAt(edges.length() - 1);
	    edges.append("]");

	    return edges.toString();
	}
	
	private double changeSign(double value) {
		return value * -1;
	}
	
	protected void setData3D(double[][] data, double[][] neuronsArray, Neuron[] neurons) {
		
		 if(data[0].length > 3 
			// if algorithm isn't BIRCH or NeuralMap
			&& !((this instanceof BIRCHAlgorithm) || (this instanceof NeuralMapAlgorithm))) {
			 	data = pca.pca(data, 3);
		 }
		
		 StringBuilder xData = new StringBuilder();
		 StringBuilder yData = new StringBuilder();
		 StringBuilder zData = new StringBuilder();
	     StringBuilder labelData = new StringBuilder();
	     StringBuilder dataSize = new StringBuilder();
	     String edges = "[]";

		 xData.append("[");
		 yData.append("[");
		 zData.append("[");
		 labelData.append("[");
		 
		 for (int i = 0; i < data.length; i++) {

		     double xValue = data[i][0];
		     double yValue = data[i][1];
		     double zValue = data[i][2];

		     xData.append(xValue);
		     yData.append(yValue);
		     zData.append(zValue);
	         labelData.append("\"").append(users.get(i).getFullName()).append("\"");

		     if (i < data.length - 1) {
		    	 xData.append(",");
		         yData.append(",");
		         zData.append(",");
		         labelData.append(",");
		     }
		 }

		 xData.append("]");
		 yData.append("]");
		 zData.append("]");
		 labelData.append("]");
	        
	     dataSize.append("[" + data.length  + "]");

		 if(neurons != null) 
	        	edges = edgesToJSON3D(neurons);
		 
		 data3D = "{\"x\":" + xData.toString() 
		 	+ ",\"y\":" + yData.toString() 
		 	+ ",\"z\":" + zData.toString()
			+ ",\"labels\":" + labelData.toString()
			+ ",\"size\":" + dataSize.toString() 
			+ ",\"edges\":" + edges
		 	+ "}";
		 System.out.println(data3D);
	}
	
	private String edgesToJSON3D(Neuron[] neurons) {
	    String neuronA = "";
	    String neuronB = "";
	    StringBuilder edges = new StringBuilder();

	    edges.append("[");

	    for (Neuron neuron : neurons) {
	        for (Edge edge : neuron.edges) {
	            double[] coordA;
	            double[] coordB;

	            // if the dimension is > 3 and we have to apply dimension reduction
	            if (neuron.w.length > 3) {
	                coordA = pca.sampleToEigenSpace(neuron.w);
	                coordB = pca.sampleToEigenSpace(edge.neighbor.w);
	            } else {
	                coordA = neuron.w;
	                coordB = edge.neighbor.w;
	            }

	            neuronA = "[" + changeSign(coordA[0]) + "," + changeSign(coordA[1]) + "," + changeSign(coordA[2]) + "]";
	            neuronB = "[" + changeSign(coordB[0]) + "," + changeSign(coordB[1]) + "," + changeSign(coordB[2]) + "]";

	            edges.append("[" + neuronA + "," + neuronB + "],");
	        }
	    }

	    edges.deleteCharAt(edges.length() - 1);
	    edges.append("]");

	    return edges.toString();
	}
	
	public String getData2D() {
		return data2D;	
	}
	
	public String getData3D() {
		return data3D;
	}
	
	protected void clearData3D() {
		data3D = null;
	}
	
	public void setUsers(List<EnrolledUser> users) {
		this.users = users;
	}
	
	/**
	 * Establece el nombre del algoritmo.
	 * 
	 * @param name nombre del algoritmo
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Establece el nombre de la biblioteca del algoritmo.
	 * 
	 * @param library nombre de la libreria
	 */
	public void setLibrary(String library) {
		this.library = library;
	}

	/**
	 * Devuleve el nombre del algoritmo.
	 * 
	 * @return nombre del algoritmo
	 */
	public String getName() {
		return name;
	}

	/**
	 * Devuleve el nombre de la biblioteca del algoritmo.
	 * 
	 * @return nombre de la biblioteca
	 */
	public String getLibrary() {
		return library;
	}

	/**
	 * Devulve los parametros del algoritmo.
	 * 
	 * @return parametros del algoritmo
	 */
	public AlgorithmParameters getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		return name + " (" + library + ")";
	}
	
	public boolean equals(Algorithm algorithm) {
		return (name.equals(algorithm.getName()) 
				&& library.equals(algorithm.getLibrary()));
	}
}
