package net.dvt32.DentistManager.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import net.dvt32.DentistManager.entity.Dentist;
import net.dvt32.DentistManager.entity.Manipulation;
import net.dvt32.DentistManager.entity.Patient;
import net.dvt32.DentistManager.entity.Schedule;
import net.dvt32.DentistManager.entity.Tooth;
import net.dvt32.DentistManager.repository.DentistRepository;
import net.dvt32.DentistManager.repository.ManipulationRepository;
import net.dvt32.DentistManager.repository.PatientRepository;
import net.dvt32.DentistManager.repository.ScheduleRepository;
import net.dvt32.DentistManager.service.MysqlExportService;
import net.dvt32.DentistManager.service.MysqlImportService;

@Controller
public class MainController {
	
	@Autowired
	private DentistRepository dentistRepository;
	@Autowired
	private ManipulationRepository manipulationRepository;
	@Autowired
	private PatientRepository patientRepository;
	@Autowired
	private ScheduleRepository scheduleRepository;	
	@Autowired
	private Environment environment;
	
	/**
	 * Index controller method
	 */
	@RequestMapping("/")
	public String index() {
		return "index";
	}
	
	/**
	 * Settings controller methods (dentist & manipulations info)
	 */
	@RequestMapping("/settings")
	public String settings(Model model) {
		Dentist dentist;
		try {
			dentist = dentistRepository.findById(1).get();
		}
		catch (NoSuchElementException e) {
			dentist = new Dentist();
		}
		model.addAttribute("dentist", dentist);
		
		Iterable<Manipulation> manipulations = manipulationRepository.findAll();
		model.addAttribute("manipulations", manipulations);
		
		return "settings";
	}
	
	@PostMapping("/submitDentistInfo")
	public RedirectView submitDentistInfo(@ModelAttribute Dentist dentist) {
		dentist.setId(1);
		dentistRepository.save(dentist);
		return new RedirectView("settings");
	}
	
	@PostMapping(value = "/submitManipulationInfo", params="createManipulation")
	public RedirectView createManipulation(@ModelAttribute Manipulation manipulation, RedirectAttributes redirectAttributes) {
		try {
			int manipulationId = manipulation.getId();
			
			if (manipulationId != -1) {
				manipulation.setId(-1);
			}
			manipulationRepository.save(manipulation);
			
			return new RedirectView("settings");
		}
		catch (DataIntegrityViolationException e) {
			redirectAttributes.addFlashAttribute("manipulationStatusMessage", "duplicateNameError");
			return new RedirectView("error");
		}
	}
	
	@PostMapping(value = "/submitManipulationInfo", params="updateManipulation")
	public RedirectView updateManipulation(@ModelAttribute Manipulation manipulation, RedirectAttributes redirectAttributes) {
		try {
			manipulationRepository.save(manipulation);
			return new RedirectView("settings");
		}
		catch (DataIntegrityViolationException e) {
			redirectAttributes.addFlashAttribute("manipulationStatusMessage", "duplicateNameError");
			return new RedirectView("error");
		}
	}
	
	@PostMapping(value = "/submitManipulationInfo", params="deleteManipulation")
	public RedirectView deleteManipulation(@ModelAttribute Manipulation manipulation) {
		int manipulationId = manipulation.getId();
		if (manipulationId != -1) {
			manipulationRepository.deleteById(manipulationId);
		}
		
		return new RedirectView("settings");
	}
	
	/**
	 * Patient controller methods
	 */
	@RequestMapping("/patients")
	public String patients(Model model) {
		Iterable<Patient> patients = patientRepository.findAll();
		model.addAttribute("patients", patients);
		
		return "patients";
	}
	
	@PostMapping(value = "/submitPatientInfo", params="createPatient")
	public RedirectView createPatient(@ModelAttribute Patient patient, RedirectAttributes redirectAttributes) {
		try {
			long patientId = patient.getId();
			if (patientId != -1L) {
				patient.setId(-1L);
			}
			for (Tooth tooth : patient.getTeeth()) {
				tooth.setPatient(patient);
			}
			patientRepository.save(patient);

			return new RedirectView("patients");
		}
		catch (DataIntegrityViolationException e) {
			redirectAttributes.addFlashAttribute("patientStatusMessage", "duplicateEgnError");
			return new RedirectView("error");
		}
	}
	
	@PostMapping(value = "/submitPatientInfo", params="updatePatient")
	public RedirectView updatePatient(@ModelAttribute Patient patient, RedirectAttributes redirectAttributes) {
		try {
			long patientId = patient.getId();
			if (patientId == -1L) {
				for (Tooth tooth : patient.getTeeth()) {
					tooth.setPatient(patient);
				}
				patientRepository.save(patient);
			}
			else {
				Patient updatedPatient = patientRepository.findById(patientId).get();
				
				// Update patient fields (besides teeth)
				updatedPatient.setFullName( patient.getFullName() );
				updatedPatient.setPhoneNumber( patient.getPhoneNumber() );
				updatedPatient.setEgn( patient.getEgn() );
				updatedPatient.setGender( patient.getGender() );
				updatedPatient.setAddress( patient.getAddress() );
				updatedPatient.setBloodType( patient.getBloodType() );
				updatedPatient.setRhFactor( patient.getRhFactor() );
				updatedPatient.setAllergies( patient.getAllergies() );
				updatedPatient.setHeartFailureStatus( patient.getHeartFailureStatus() );
				updatedPatient.setDiabetesStatus( patient.getDiabetesStatus() );
				updatedPatient.setHivStatus( patient.getHivStatus() );
				updatedPatient.setNote( patient.getNote() );
				
				// Update teeth status
				List<Tooth> updatedTeeth = updatedPatient.getTeeth();
				List<Tooth> newTeeth = patient.getTeeth();
				for (int i = 0; i < updatedTeeth.size(); ++i) {
					String newToothStatus = newTeeth.get(i).getStatus();
					updatedTeeth.get(i).setStatus(newToothStatus);
				}
				updatedPatient.setTeeth(updatedTeeth);

				patientRepository.save(updatedPatient);
			}

			return new RedirectView("patients");
		}
		catch (DataIntegrityViolationException e) {
			redirectAttributes.addFlashAttribute("patientStatusMessage", "duplicateEgnError");
			return new RedirectView("error");
		}
	}
	
	@PostMapping(value = "/submitPatientInfo", params="deletePatient")
	public RedirectView deletePatient(@ModelAttribute Patient patient) {
		long patientId = patient.getId();
		
		if (patientId != -1L) {
			List<Schedule> schedules = scheduleRepository.findAllByPatient(patient);
			for (Schedule schedule : schedules) {
				scheduleRepository.delete(schedule);
			}
			patientRepository.deleteById(patientId);
		}
		
		return new RedirectView("patients");
	}
	
	/**
	 * Schedule controller methods
	 */
	@RequestMapping("/schedule")
	public String schedule(Model model) {	
		Iterable<Patient> patients = patientRepository.findAll();
		model.addAttribute("patients", patients);
		
		Iterable<Manipulation> manipulations = manipulationRepository.findAll();
		model.addAttribute("manipulations", manipulations);
		
		Iterable<Schedule> schedules = scheduleRepository.findAllByOrderByDateAscTimeAsc();
		model.addAttribute("schedules", schedules);
		
		return "schedule";
	}
	
	@PostMapping(value = "/submitScheduleInfo", params="createSchedule")
	public RedirectView createSchedule(@ModelAttribute Schedule schedule, RedirectAttributes redirectAttributes) {
		try {
			long scheduleId = schedule.getId();
			Patient schedulePatient = schedule.getPatient();
			long schedulePatientId =  schedulePatient.getId();
			
			if (scheduleId != -1L) {
				schedule.setId(-1L);
			}
			
			schedule.setPriceToPay(0.00d);
		
			/*
			 * Update patient info via patientRepository
			 */
			Patient updatedPatient = patientRepository.findById(schedulePatientId).get();
			List<Tooth> updatedTeeth = updatedPatient.getTeeth();
			
			List<Tooth> newTeeth = schedulePatient.getTeeth();
			for (int i = 0; i < updatedTeeth.size(); ++i) {
				// Update tooth status & add manipulation date
				String newToothStatus = newTeeth.get(i).getStatus();
				updatedTeeth.get(i).setStatus(newToothStatus);
				
				// Update patient manipulations
				List<Manipulation> updatedManipulations = updatedTeeth.get(i).getAppliedManipulations();
				Manipulation newManipulation = newTeeth.get(i).getAppliedManipulations().get(0);
				try {
					newManipulation = manipulationRepository.findById(newManipulation.getId()).get();
				}
				catch (NullPointerException npe) {
					continue;
				}
				updatedTeeth.get(i).getAppliedManipulationDates().add("(" + schedule.getDate() + ") @ " + schedule.getTime());
				newManipulation.getManipulatedTeeth().add(updatedTeeth.get(i));
				updatedManipulations.add(newManipulation);
				updatedTeeth.get(i).setAppliedManipulations(updatedManipulations);
				
				// Update schedule manipulations
				schedule.getAppliedManipulationsToday().add(newManipulation);
				
				// Update price to pay
				schedule.setPriceToPay(
					schedule.getPriceToPay() +
					newManipulation.getPrice() +
					( newManipulation.getPrice() * (newManipulation.getVat() / 100.00d) )
				);
			}
			updatedPatient.setTeeth(updatedTeeth);
			patientRepository.save(updatedPatient);
			
			schedule.setPatient(updatedPatient);
			scheduleRepository.save(schedule);

			return new RedirectView("schedule");
		}
		catch (DataIntegrityViolationException e) {
			redirectAttributes.addFlashAttribute("scheduleStatusMessage", "duplicateDateTimeError");
			return new RedirectView("error");
		}
	}
	
	@PostMapping(value = "/submitScheduleInfo", params="updateSchedule")
	public RedirectView updateSchedule(@ModelAttribute Schedule schedule, RedirectAttributes redirectAttributes) {
		try {
			long scheduleId = schedule.getId();
			Patient schedulePatient = schedule.getPatient();
			long schedulePatientId = schedulePatient.getId();
			
			// If patient was changed, reset price to pay
			if (scheduleId != -1L) {
				if (schedulePatientId != scheduleRepository.findById(scheduleId).get().getPatient().getId()) {
					schedule.setPriceToPay(0.0d);
				}
				else {
					schedule.setAppliedManipulationsToday(
						scheduleRepository.findById(scheduleId).get().getAppliedManipulationsToday()
					);
				}
			}
			
			/*
			 *  Update patient info via patientRepository
			 */
			Patient updatedPatient = patientRepository.findById(schedulePatientId).get();
			List<Tooth> updatedTeeth = updatedPatient.getTeeth();
			
			List<Tooth> newTeeth = schedulePatient.getTeeth();
			for (int i = 0; i < updatedTeeth.size(); ++i) {
				// Update tooth status
				String newToothStatus = newTeeth.get(i).getStatus();
				updatedTeeth.get(i).setStatus(newToothStatus);

				// Update manipulations
				List<Manipulation> updatedManipulations = updatedTeeth.get(i).getAppliedManipulations();
				Manipulation newManipulation = newTeeth.get(i).getAppliedManipulations().get(0);
				try {
					newManipulation = manipulationRepository.findById(newManipulation.getId()).get();
				}
				catch (NullPointerException npe) {
					continue;
				}
				updatedTeeth.get(i).getAppliedManipulationDates().add("(" + schedule.getDate() + ") @ " + schedule.getTime());
				newManipulation = manipulationRepository.findById(newManipulation.getId()).get();
				newManipulation.getManipulatedTeeth().add(updatedTeeth.get(i));
				updatedManipulations.add(newManipulation);
				updatedTeeth.get(i).setAppliedManipulations(updatedManipulations);
				
				// Update schedule manipulations
				if (scheduleId != -1L) {
					List<Manipulation> updatedScheduleManipulations = scheduleRepository.findById(scheduleId).get().getAppliedManipulationsToday();
					updatedScheduleManipulations.add(newManipulation);
					schedule.setAppliedManipulationsToday(updatedScheduleManipulations);
				}
				else {
					schedule.getAppliedManipulationsToday().add(newManipulation);
				}
				
				// Update price to pay
				schedule.setPriceToPay(
					schedule.getPriceToPay() +
					newManipulation.getPrice() +
					( newManipulation.getPrice() * (newManipulation.getVat() / 100.00d) )
				);
			}
			
			updatedPatient.setTeeth(updatedTeeth);
			patientRepository.save(updatedPatient);
			
			schedule.setPatient(updatedPatient);
			scheduleRepository.save(schedule);
			
			return new RedirectView("schedule");
		}
		catch (DataIntegrityViolationException e) {
			redirectAttributes.addFlashAttribute("scheduleStatusMessage", "duplicateDateTimeError");
			return new RedirectView("error");
		}
	}
	
	@PostMapping(value = "/submitScheduleInfo", params="deleteSchedule")
	public RedirectView deleteSchedule(@ModelAttribute Schedule schedule) {
		long scheduleId = schedule.getId();
		if (scheduleId != -1L) {
			scheduleRepository.deleteById(scheduleId);
		}
		
		return new RedirectView("schedule");
	}
	
	/*
	 * Archive controller methods
	 */
	@RequestMapping("/archive")
	public String archive() {
		return "archive";
	}
	
	@RequestMapping("/archive-status")
    public String archiveStatus() {
        return "archive-status";
    }
	
	@RequestMapping("/exportDatabase")
	public ResponseEntity<byte[]> exportDatabase() 
		throws ClassNotFoundException, IOException, SQLException, Exception 
	{
		String jdbcConnectionString = environment.getProperty("spring.datasource.url");
		String username = environment.getProperty("spring.datasource.username");
		String password = environment.getProperty("spring.datasource.password");
		
		Properties properties = new Properties();
		properties.setProperty(MysqlExportService.JDBC_CONNECTION_STRING, jdbcConnectionString);
		properties.setProperty(MysqlExportService.DB_USERNAME, username);
		properties.setProperty(MysqlExportService.DB_PASSWORD, password);

		MysqlExportService mysqlExportService = new MysqlExportService(properties);
		String sql = mysqlExportService.export();
	    byte[] output = sql.getBytes();
	    String fileName = new SimpleDateFormat("d_M_Y_H_mm_ss").format(new Date()) + "_dentistmanager_db_dump.sql";

	    HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.set("charset", "utf-8");
	    responseHeaders.setContentType(MediaType.valueOf("text/html"));
	    responseHeaders.setContentLength(output.length);
	    responseHeaders.set("Content-disposition", "attachment; filename=" + fileName);

	    return new ResponseEntity<byte[]>(output, responseHeaders, HttpStatus.OK);
	}
	
	@PostMapping("/importDatabase")
	public String importDatabase(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) 
		throws ClassNotFoundException, SQLException, IOException 
	{
		boolean isValidFile = file.getContentType().equals("application/octet-stream");
		if (!isValidFile) {
			redirectAttributes.addFlashAttribute("archiveStatusMessage", "importDatabaseFailure");
			return "redirect:/archive-status";
		}
		
		String jdbcConnectionString = environment.getProperty("spring.datasource.url");
		String username = environment.getProperty("spring.datasource.username");
		String password = environment.getProperty("spring.datasource.password");
		
		String sql = new String(file.getBytes());
		
		MysqlImportService.builder()
			.setJdbcConnString(jdbcConnectionString)
	        .setSqlString(sql)
	        .setUsername(username)
	        .setPassword(password)
	        .setDeleteExisting(true)
	        .setDropExisting(false)
	        .importDatabase();
		
		redirectAttributes.addFlashAttribute("archiveStatusMessage", "importDatabaseSuccess");

		return "redirect:/archive-status";
	}
	
	@PostMapping("/emptyDatabase")
	public String emptyDatabase(RedirectAttributes redirectAttributes) 
		throws ClassNotFoundException, SQLException 
	{
		String jdbcConnectionString = environment.getProperty("spring.datasource.url");
		String username = environment.getProperty("spring.datasource.username");
		String password = environment.getProperty("spring.datasource.password");
		
		String sql = " ";
		
		MysqlImportService.builder()
			.setJdbcConnString(jdbcConnectionString)
	        .setSqlString(sql)
	        .setUsername(username)
	        .setPassword(password)
	        .setDeleteExisting(true)
	        .setDropExisting(false)
	        .importDatabase();
		
		redirectAttributes.addFlashAttribute("archiveStatusMessage", "emptyDatabaseSuccess");
		
		return "redirect:/archive-status";
	}
	
}