package net.dvt32.DentistManager.repository;

import org.springframework.data.repository.CrudRepository;

import net.dvt32.DentistManager.entity.Manipulation;

public interface ManipulationRepository extends CrudRepository<Manipulation, Integer> {
	// Auto-implemented by Spring Boot
}