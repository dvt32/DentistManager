package net.dvt32.DentistManager.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;

@Entity
public class Patient {
	
	/*
	 * Fields
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty
	private String fullName;
	
	private String phoneNumber;
	
	@NotEmpty
	@Column(unique=true) 
	private String egn;
	
	private String gender;
	private String address;
	private String bloodType;
	private String rhFactor;
	private String allergies;
	private String heartFailureStatus;
	private String diabetesStatus;
	private String hivStatus;
	
	@OneToMany(
		cascade = CascadeType.ALL, 
	    mappedBy = "patient", 
	    orphanRemoval = true
	)
	private List<Tooth> teeth = new ArrayList<Tooth>();
	
	private String note;
	
	/*
	 * Constructors
	 */
	public Patient() {}

	/*
	 * Getters & setters
	 */
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getFullName() {
		return fullName;
	}
	
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getEgn() {
		return egn;
	}
	
	public void setEgn(String egn) {
		this.egn = egn;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getBloodType() {
		return bloodType;
	}
	
	public void setBloodType(String bloodType) {
		this.bloodType = bloodType;
	}
	
	public String getRhFactor() {
		return rhFactor;
	}
	
	public void setRhFactor(String rhFactor) {
		this.rhFactor = rhFactor;
	}
	
	public String getAllergies() {
		return allergies;
	}
	
	public void setAllergies(String allergies) {
		this.allergies = allergies;
	}
	
	public String getHeartFailureStatus() {
		return heartFailureStatus;
	}
	
	public void setHeartFailureStatus(String heartFailureStatus) {
		this.heartFailureStatus = heartFailureStatus;
	}
	
	public String getDiabetesStatus() {
		return diabetesStatus;
	}
	
	public void setDiabetesStatus(String diabetesStatus) {
		this.diabetesStatus = diabetesStatus;
	}
	
	public String getHivStatus() {
		return hivStatus;
	}
	
	public void setHivStatus(String hivStatus) {
		this.hivStatus = hivStatus;
	}
	
	public List<Tooth> getTeeth() {
		return teeth;
	}
	
	public void setTeeth(List<Tooth> teeth) {
		this.teeth = teeth;
	}
	
	public String getNote() {
		return note;
	}
	
	public void setNote(String note) {
		this.note = note;
	}
	
	/*
	 * For debugging
	 */
	@Override
	public String toString() {
		return String.format(
			"Patient [id=%s, fullName=%s, phoneNumber=%s, egn=%s, gender=%s, address=%s, bloodType=%s, rhFactor=%s, allergies=%s, heartFailureStatus=%s, diabetesStatus=%s, hivStatus=%s, note=%s, teeth=%s]",
			id, fullName, phoneNumber, egn, gender, address, bloodType, rhFactor, allergies, heartFailureStatus,
			diabetesStatus, hivStatus, note, teeth
		);
	}
}