package model;

import java.io.Serializable;

/**
 * Clase Group para distinguir los grupos que hay en un curso, as� como los
 * grupos en los que se encuentra un usuario.
 * 
 * @author Claudia Mart�nez Herrero
 * @author Yi Peng Ji
 * @version 2.0.1
 * @since 2.0.1
 */
public class Group implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String description;

	/**
	 * Constructor de la clase Group. Establece un grupo.
	 * 
	 * @param id
	 * 		Id del grupo.
	 * @param name
	 * 		Nombre del grupo.
	 * @param description
	 * 		Descripci�n del grupo.
	 */
	public Group(int id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	/**
	 * Devuelve el id del grupo.
	 * 
	 * @return id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Modifica el id del grupo.
	 * 
	 * @param id
	 * 		El id.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Devuelve el nombre del grupo.
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Modifica el nombre del grupo.
	 * 
	 * @param name
	 * 		El nombre.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Devuelve la descripci�n del grupo.
	 * 
	 * @return description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Modifica la descripci�n del grupo.
	 * 
	 * @param description
	 * 		La descripci�n.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
