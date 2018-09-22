package net.dvt32.DentistManager.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class Tooth {
	
	/*
	 * Fields
	 */
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="patient_id")
	private Patient patient;
	
	private String status;
	
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinTable
	private List<Manipulation> appliedManipulations = new ArrayList<Manipulation>();

	@ElementCollection
	private List<String> appliedManipulationDates = new ArrayList<String>();
	
	/*
	 * Constructors
	 */
	public Tooth() {}

	/*
	 * Getters & setters
	 */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Manipulation> getAppliedManipulations() {
		return appliedManipulations;
	}

	public void setAppliedManipulations(List<Manipulation> appliedManipulations) {
		this.appliedManipulations = appliedManipulations;
	}
	
	public List<String> getAppliedManipulationDates() {
		return appliedManipulationDates;
	}

	public void setAppliedManipulationDates(List<String> appliedManipulationDates) {
		this.appliedManipulationDates = appliedManipulationDates;
	}
	
	/*
	 * For debugging
	 */
	@Override
	public String toString() {
		return String.format("Tooth [id=%s, status=%s, appliedManipulations=%s]", id, status, appliedManipulations);
	}
	
}