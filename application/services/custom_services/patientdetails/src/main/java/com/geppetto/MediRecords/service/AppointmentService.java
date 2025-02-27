package com.geppetto.MediRecords.service;

import java.util.List;
import com.geppetto.MediRecords.dto.AppointmentDto;
import java.util.Map;
import org.springframework.data.domain.Page;

public interface AppointmentService {

    AppointmentDto createAppointment(AppointmentDto appointmentDto);

    AppointmentDto getAppointmentById(String id);

    Page<AppointmentDto> getAllAppointment(int page, int size);

    List<AppointmentDto> searchAppointment(Map<String, String> allParams);

    AppointmentDto updateAppointment(AppointmentDto appointmentDto);

    String deleteAppointment(String id);

}
