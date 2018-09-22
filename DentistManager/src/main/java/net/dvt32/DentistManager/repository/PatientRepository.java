package net.dvt32.DentistManager.repository;

import org.springframework.data.repository.CrudRepository;

import net.dvt32.DentistManager.entity.Patient;

public interface PatientRepository extends CrudRepository<Patient, Long> {
	// Auto-implemented by Spring Boot
}