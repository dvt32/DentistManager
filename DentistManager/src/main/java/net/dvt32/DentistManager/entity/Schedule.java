package net.dvt32.DentistManager.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
	uniqueConstraints = @UniqueConstraint(
		columnNames={"schedule_date", "schedule_time"}
	)
)
public class Schedule {
	
	/*
	 * Fields
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="schedule_date", length=15)
	private String date;
	@Column(name="schedule_time", length=5)
	private String time;
	
	private Double priceToPay = 0.0d;
	private String paidStatus;
	private String note;
	
	@OneToOne
	private Patient patient;
	
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinTable
	private List<Manipulation> appliedManipulationsToday = new ArrayList<Manipulation>();

	/*
	 * Constructors
	 */
	public Schedule() {}

	/*
	 * Getters & setters
	 */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Double getPriceToPay() {
		return priceToPay;
	}

	public void setPriceToPay(Double priceToPay) {
		this.priceToPay = priceToPay;
	}

	public String getPaidStatus() {
		return paidStatus;
	}

	public void setPaidStatus(String paidStatus) {
		this.paidStatus = paidStatus;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public List<Manipulation> getAppliedManipulationsToday() {
		return appliedManipulationsToday;
	}

	public void setAppliedManipulationsToday(List<Manipulation> appliedManipulationsToday) {
		this.appliedManipulationsToday = appliedManipulationsToday;
	}

	/*
	 * For debugging
	 */
	@Override
	public String toString() {
		return String.format("Schedule [id=%s, date=%s, time=%s, paidStatus=%s, note=%s, patient=%s]", id, date, time, paidStatus, note, patient);
	}
	
}