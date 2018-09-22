package net.dvt32.DentistManager.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import net.dvt32.DentistManager.entity.Patient;
import net.dvt32.DentistManager.entity.Schedule;

public interface ScheduleRepository extends CrudRepository<Schedule, Long> {
	public List<Schedule> findAllByPatient(Patient patient);
	public List<Schedule> findAllByOrderByDateAscTimeAsc();
}