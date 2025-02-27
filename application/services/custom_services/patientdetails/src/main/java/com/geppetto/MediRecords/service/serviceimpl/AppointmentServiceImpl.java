package com.geppetto.MediRecords.service.serviceimpl;

import com.geppetto.MediRecords.dao.AppointmentDao;
import com.geppetto.MediRecords.dto.AppointmentDto;
import com.geppetto.MediRecords.exception.EntityNotFoundException;
import com.geppetto.MediRecords.model.Appointment;
import com.geppetto.MediRecords.service.AppointmentService;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import java.util.Map;
import org.springframework.data.domain.Page;
import com.geppetto.MediRecords.util.PatientDetailsUtil;
import org.springframework.data.domain.Pageable;
import java.util.stream.Collectors;
import com.geppetto.MediRecords.repository.AppointmentRepository;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

/**
* Implementation of the {@link AppointmentService} interface.
* Provides services related to Appointment, including CRUD operations and file uploads/downloads.
*/

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {


    /**
    * Constructs a {@code AppointmentServiceImpl} with the specified DAO.
    *
    * @param appointmentDao The DAO for accessing the data.
    */
    private final AppointmentDao appointmentDao;
    private final AppointmentRepository appointmentRepository;
    private final PatientDetailsUtil patientDetailsUtil;

  
    /**
    * Creates new appointment.
    *
    * @param appointmentDto The {@link AppointmentDto} to be created.
    * @return The created {@link AppointmentDto}.
    */
    @Override
    public AppointmentDto  createAppointment(AppointmentDto appointmentDto) {
        log.info("Entering createAppointment method");
    
        Appointment appointment = patientDetailsUtil.toEntity(appointmentDto);
        Appointment createdAppointment = appointmentDao.createAppointment(appointment);
        appointmentDto = patientDetailsUtil.toDto(createdAppointment);

        log.info("Exiting createAppointment method");
        return appointmentDto;
    }
  
    /**
    * Retrieves appointment by its ID.
    *
    * @param id The ID of the appointment to retrieve. Must not be {@code null}.
    * @return The appointment data transfer object associated with the specified ID.
    * @throws EntityNotFoundException If no appointment with the specified ID is found.
    */
    @Override
    public AppointmentDto  getAppointmentById(String id) {
        log.info("Entering getAppointmentById method for ID: {}", id);

        Appointment appointment = appointmentDao.getAppointmentById(id)
        .orElseThrow(() -> {
            log.warn("No appointment found for ID: {}", id);
            return new EntityNotFoundException("Data not found for ID: " + id);
        });

        AppointmentDto appointmentDto = patientDetailsUtil.toDto(appointment);
        
        log.info("Exiting getAppointmentById method for ID: {}", id);
        return appointmentDto;
            
    }
  
    /**
    * Retrieves all appointment.
    *
    * @return A list of {@link AppointmentDto} representing all appointment.
    */
    @Override
    public Page<AppointmentDto>  getAllAppointment(int page, int size) {
        log.info("Entering getAllAppointment method");

        Pageable pageable = PageRequest.of(page, size);
        Page<Appointment> appointmentPage = appointmentDao.getAllAppointment(pageable);
        Page<AppointmentDto> appointmentDtoPage = appointmentPage.map(appointment -> {
            AppointmentDto dto = patientDetailsUtil.toDto(appointment);
            return dto;
        });

        log.info("Exiting getAllAppointment method");
        return appointmentDtoPage;
    }
  
    /**
    * Searches for appointment based on provided parameters.
    *
    * @param allParams A map of search parameters.
    * @return A list of {@link AppointmentDto} matching the search parameters.
    */
    @Override
    public List<AppointmentDto>  searchAppointment(Map<String, String> allParams) {
        log.info("Entering searchAppointment method for SQL");

        Specification<Appointment> specification = patientDetailsUtil.constructSearchQuery(allParams, Appointment.class);
        List<Appointment> results = appointmentRepository.findAll(specification);
        List<AppointmentDto> appointmentDtos = results.stream()
                .map(appointment -> patientDetailsUtil.toDto(appointment))
                .collect(Collectors.toList());

        log.info("Exiting searchAppointment method for SQL. Results found: {}", appointmentDtos.size());
        return appointmentDtos;
    }
  
    /**
    * Updates existing appointment.
    *
    * @param appointmentDto The {@link AppointmentDto} containing updated information.
    * @return The updated {@link AppointmentDto}.
    * @throws EntityNotFoundException If no appointment with the specified ID is found.
    */
    @Override
    public AppointmentDto updateAppointment(AppointmentDto appointmentDto) {
        String id = appointmentDto.getId();
        log.info("Entering updateAppointment method for ID: {}", id);

        appointmentDao.getAppointmentById(id).orElseThrow(() -> {
            log.warn("No appointment found for update with ID: {}", id);
            return new EntityNotFoundException("Data not found for update with ID: " + id);
        });

        Appointment updatedAppointment = patientDetailsUtil.toEntity(appointmentDto);
        updatedAppointment = appointmentDao.createAppointment(updatedAppointment);
        AppointmentDto responseDto = patientDetailsUtil.toDto(updatedAppointment);

        log.info("Exiting updateAppointment method for ID: {}", id);
        return responseDto;
    }
  
    /**
    * Deletes appointment by ID.
    *
    * @param id The ID of the appointment to delete.
    * @return A message indicating the result of the deletion.
    * @throws EntityNotFoundException If no appointment with the specified ID is found.
    */
    @Override
    public String  deleteAppointment(String id) {
        log.info("Entering deleteAppointment method for ID: {}", id);

        if (appointmentDao.getAppointmentById(id).isEmpty()) {
            log.warn("No appointment found with ID: {}. Deletion failed.", id);
            throw new EntityNotFoundException("No appointment found with ID: " + id + ". Unable to delete.");
        }
        appointmentDao.deleteAppointment(id);

        log.info("Successfully deleted Appointment with ID: {}", id);
        return "Appointment deleted successfully";
    }
  
}



