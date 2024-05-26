package es.ubu.lsi.ubumonitor.clustering.data;

import es.ubu.lsi.ubumonitor.util.I18n;

public enum SOMType {
	SOM_UMATRIX("umatrix"), SOM_NEURONS("neurons");
	
	private String name;
	
	private SOMType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return I18n.get("clustering.som." + name);
	}
	
}
