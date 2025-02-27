package com.geppetto.MediRecords.service.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import com.geppetto.MediRecords.dao.AppointmentDao;
import com.geppetto.MediRecords.dto.AppointmentDto;
import com.geppetto.MediRecords.exception.EntityNotFoundException;
import com.geppetto.MediRecords.model.Appointment;
import com.geppetto.MediRecords.repository.AppointmentRepository;
import com.geppetto.MediRecords.util.PatientDetailsUtil;

public class AppointmentServiceImplTest {

    @Mock
    private AppointmentDao appointmentDao;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientDetailsUtil patientDetailsUtil;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Appointment appointment;
    private AppointmentDto appointmentDto;


    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        Random random = new Random();
        String generatedId = String.valueOf(random.nextInt(100));

        appointment = new Appointment();
        appointment.setId(generatedId);          
        appointment.setAppointmentid(random.nextInt(1000));              
        appointment.setPatientid(random.nextInt(1000));                   
        appointment.setDoctorname("test_value_" + random.nextInt(100)); 

        appointmentDto = new AppointmentDto();
        appointmentDto.setId(generatedId);         
        appointmentDto.setAppointmentid(random.nextInt(1000));               
        appointmentDto.setPatientid(random.nextInt(1000));                   
        appointmentDto.setDoctorname("test_value_" + random.nextInt(100)); 
    }

  
    @Test
    void testCreateAppointment() {
        when(patientDetailsUtil.toEntity(any(AppointmentDto.class))).thenReturn(appointment);
        when(appointmentDao.createAppointment(any(Appointment.class))).thenReturn(appointment);
        when(patientDetailsUtil.toDto(any(Appointment.class))).thenReturn(appointmentDto);

        AppointmentDto created = appointmentService.createAppointment(appointmentDto);

        assertNotNull(created);
        assertEquals(appointmentDto.getId(), created.getId());
        verify(appointmentDao).createAppointment(any(Appointment.class));
    }

    @Test
    void testGetAppointmentById() {
        when(appointmentDao.getAppointmentById(appointment.getId())).thenReturn(Optional.of(appointment));
        when(patientDetailsUtil.toDto(any(Appointment.class))).thenReturn(appointmentDto);

        AppointmentDto found = appointmentService.getAppointmentById(appointment.getId());

        assertNotNull(found);
        assertEquals(appointmentDto.getId(), found.getId());
        verify(appointmentDao).getAppointmentById(appointment.getId());
    }

    @Test
    void testGetAllAppointment() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Appointment> appointments = Collections.singletonList(appointment);
        Page<Appointment> page = new PageImpl<>(appointments);

        when(appointmentDao.getAllAppointment(pageable)).thenReturn(page);
        when(patientDetailsUtil.toDto(any(Appointment.class))).thenReturn(appointmentDto);

        Page<AppointmentDto> result = appointmentService.getAllAppointment(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(appointmentDao).getAllAppointment(pageable);
    }

    @Test
    void testSearchAppointment() {
        Map<String, String> params = new HashMap<>();
        params.put("doctorname", appointment.getDoctorname());

        List<Appointment> appointments = Collections.singletonList(appointment);
        Specification<Appointment> specification = mock(Specification.class);

        when(patientDetailsUtil.constructSearchQuery(anyMap(), eq(Appointment.class))).thenReturn(specification);
        when(appointmentRepository.findAll(specification)).thenReturn(appointments);
        when(patientDetailsUtil.toDto(any(Appointment.class))).thenReturn(appointmentDto);

        List<AppointmentDto> result = appointmentService.searchAppointment(params);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(appointmentRepository).findAll(specification);
    }

    @Test
    void testUpdateAppointment() {
        when(appointmentDao.getAppointmentById(appointment.getId())).thenReturn(Optional.of(appointment));
        when(patientDetailsUtil.toEntity(any(AppointmentDto.class))).thenReturn(appointment);
        when(appointmentDao.createAppointment(any(Appointment.class))).thenReturn(appointment);
        when(patientDetailsUtil.toDto(any(Appointment.class))).thenReturn(appointmentDto);

        AppointmentDto updated = appointmentService.updateAppointment(appointmentDto);

        assertNotNull(updated);
        assertEquals(appointmentDto.getId(), updated.getId());
        verify(appointmentDao).createAppointment(any(Appointment.class));
    }

    @Test
    void testDeleteAppointment() {
        when(appointmentDao.getAppointmentById(appointment.getId())).thenReturn(Optional.of(appointment));

        String result = appointmentService.deleteAppointment(appointment.getId());

        assertEquals("Appointment deleted successfully", result);
        verify(appointmentDao).deleteAppointment(appointment.getId());
    }

    @Test
    void testGetAppointmentById_NotFound() {
        when(appointmentDao.getAppointmentById("999")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> appointmentService.getAppointmentById("999"));
    }

    @Test
    void testUpdateAppointment_NotFound() {
        when(appointmentDao.getAppointmentById("999")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> appointmentService.updateAppointment(appointmentDto));
    }

    @Test
    void testDeleteAppointment_NotFound() {
        when(appointmentDao.getAppointmentById("999")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> appointmentService.deleteAppointment("999"));
    }
    
}

