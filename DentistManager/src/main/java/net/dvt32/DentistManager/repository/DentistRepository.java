package net.dvt32.DentistManager.repository;

import org.springframework.data.repository.CrudRepository;

import net.dvt32.DentistManager.entity.Dentist;

public interface DentistRepository extends CrudRepository<Dentist, Integer> {
	// Auto-implemented by Spring Boot
}