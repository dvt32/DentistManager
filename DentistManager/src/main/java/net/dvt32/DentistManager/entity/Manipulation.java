package net.dvt32.DentistManager.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Manipulation {

	/*
	 * Fields
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@NotEmpty
	@Column(unique=true)
	private String name;
	
	private Double price;
	private Double vat;
	private String note;
	
	@ManyToMany(mappedBy="appliedManipulations")
	@JsonIgnore
	List<Tooth> manipulatedTeeth = new ArrayList<Tooth>();
	
	@ManyToMany(mappedBy="appliedManipulationsToday")
	@JsonIgnore
	private List<Schedule> schedules = new ArrayList<Schedule>();

	/*
	 * Constructors
	 */
	public Manipulation() {}

	/*
	 * Getters & setters
	 */
	public int getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getVat() {
		return vat;
	}

	public void setVat(Double vat) {
		this.vat = vat;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	public List<Tooth> getManipulatedTeeth() {
		return manipulatedTeeth;
	}

	public void setManipulatedTeeth(List<Tooth> manipulatedTeeth) {
		this.manipulatedTeeth = manipulatedTeeth;
	}
	
	public List<Schedule> getSchedules() {
		return schedules;
	}

	public void setSchedules(List<Schedule> schedules) {
		this.schedules = schedules;
	}
	
	/*
	 * For debugging
	 */
	@Override
	public String toString() {
		return String.format("Manipulation [id=%s, name=%s, price=%s, vat=%s, note=%s]", id, name, price, vat, note);
	}
	
}