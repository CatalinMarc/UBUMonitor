package es.ubu.lsi.ubumonitor.clustering.algorithm;

import java.util.List;

import org.apache.commons.math3.ml.clustering.Clusterer;

import com.jujutsu.tsne.PrincipalComponentAnalysis;

import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.maps.BIRCHAlgorithm;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.maps.GrowingNeuralGasAlgorithm;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.maps.NeuralGasAlgorithm;
import es.ubu.lsi.ubumonitor.clustering.algorithm.smile.maps.NeuralMapAlgorithm;
import es.ubu.lsi.ubumonitor.clustering.controller.collector.DataCollector;
import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.clustering.data.UserData;
import es.ubu.lsi.ubumonitor.clustering.exception.IllegalParamenterException;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;
import es.ubu.lsi.ubumonitor.util.I18n;
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

	/**
	 * Convierte los datos de los gráficos a un JSON indicando 
	 * las coordenadas de los datos, de las neuronas, sus etiquetas
	 * y sus conexiones. Este JSON se envia al gráfico 2D de los mapas.
	 * 
	 * @param data datos alumnos
	 * @param neuronsArray array de las neuronas
	 * @param neurons neuronas
	 */
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
        			+ ",\"dataName\":\"" + I18n.get("clustering.data") + "\""
        			+ ",\"neuronsName\":\"" + I18n.get("clustering.neurons") + "\""
        			+ "}";

	}
	
	/**
	 * Convierte las conexiones de las neuronas 2D a un JSON.
	 * 
	 * @param neurons neuronas
	 * @return String con las coordenadas de las conexiones.
	 */
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
	
	/**
	 * Cambia el signo de un valor.
	 * 
	 * @param value value
	 * @return valor cambiado de signo
	 */
	private double changeSign(double value) {
		return value * -1;
	}
	
	/**
	 * Convierte los datos de los gráficos a un JSON indicando 
	 * las coordenadas de los datos, de las neuronas, sus etiquetas
	 * y sus conexiones. Este JSON se envia al gráfico 3D de los mapas.
	 * 
	 * @param data datos alumnos
	 * @param neuronsArray array de las neuronas
	 * @param neurons neuronas
	 */
	protected void setData3D(double[][] data, double[][] neuronsArray, Neuron[] neurons) {
		
		String showNeurons = "true";
		
		 if(data[0].length > 3) 
			 	data = pca.pca(data, 3);
		 
		 if((this instanceof NeuralGasAlgorithm) || (this instanceof GrowingNeuralGasAlgorithm)) 
			 neuronsArray = pca.pca(neuronsArray, 3);
		 
		 if((this instanceof NeuralMapAlgorithm) || (this instanceof GrowingNeuralGasAlgorithm))
			 showNeurons = "false";
		
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
		 
		 //Data
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
			+ ",\"dataName\":\"" + I18n.get("clustering.data") + "\""
			+ ",\"show\":\"" + showNeurons + "\""
		 	+ "}";
		 System.out.println(data3D);
	}
	
	/**
	 * Convierte las conexiones de las neuronas 2D a un JSON.
	 * 
	 * @param neurons neuronas
	 * @return String con las coordenadas de las conexiones.
	 */
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
	
	/**
	 * Devuelve los datos JSON para gráfico 2D.
	 * 
	 * @return data2D
	 */
	public String getData2D() {
		return data2D;	
	}
	
	/**
	 * Devuelve los datos JSON para gráfico 3D.
	 * 
	 * @return data3D
	 */
	public String getData3D() {
		return data3D;
	}
	
	/**
	 * Elimina los datos del gráfico 3D
	 */
	protected void clearData3D() {
		data3D = null;
	}
	
	/**
	 * Estable los usuarios.
	 * 
	 * @param users usuarios
	 */
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

	/**
	 * Convierte el agoritmo a String.
	 * 
	 * @return algoritmo en String
	 */
	@Override
	public String toString() {
		return name + " (" + library + ")";
	}
	
	/**
	 * Compara si el algoritmo proporciado es igual a este.
	 * 
	 * @param algorithm algoritmo
	 * @return true si el algoritmo coincide con este
	 */
	public boolean equals(Algorithm algorithm) {
		return (name.equals(algorithm.getName()) 
				&& library.equals(algorithm.getLibrary()));
	}
}
