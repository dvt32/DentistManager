package net.dvt32.DentistManager.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Dentist {
	
	/*
	 * Fields
	 */
	@Id
	private Integer id;
	
	private String fullName;
	private String phoneNumber;
	private String egn;
	private String gender;
	private String address;
	private String company;
	private String object;
	private String mol;
	private String bulstatNumber;
	private String taxNumber;
	private String regNumber;
	private String rzokAgreement;
	
	/*
	 * Constructors
	 */
	public Dentist() {
		fullName = "";
		phoneNumber = "";
		egn = "";
		gender = "";
		address = "";
		company = "";
		object = "";
		mol = "";
		bulstatNumber = "";
		taxNumber = "";
		regNumber = "";
		rzokAgreement = "";
	}

	/*
	 * Getters & setters
	 */
	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getMol() {
		return mol;
	}

	public void setMol(String mol) {
		this.mol = mol;
	}

	public String getBulstatNumber() {
		return bulstatNumber;
	}

	public void setBulstatNumber(String bulstatNumber) {
		this.bulstatNumber = bulstatNumber;
	}

	public String getTaxNumber() {
		return taxNumber;
	}

	public void setTaxNumber(String taxNumber) {
		this.taxNumber = taxNumber;
	}

	public String getRegNumber() {
		return regNumber;
	}

	public void setRegNumber(String regNumber) {
		this.regNumber = regNumber;
	}

	public String getRzokAgreement() {
		return rzokAgreement;
	}

	public void setRzokAgreement(String rzokAgreement) {
		this.rzokAgreement = rzokAgreement;
	}
	
}