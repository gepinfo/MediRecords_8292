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

import com.geppetto.MediRecords.dao.BillingdetailsDao;
import com.geppetto.MediRecords.dto.BillingdetailsDto;
import com.geppetto.MediRecords.exception.EntityNotFoundException;
import com.geppetto.MediRecords.model.Billingdetails;
import com.geppetto.MediRecords.repository.BillingdetailsRepository;
import com.geppetto.MediRecords.util.PatientDetailsUtil;

public class BillingdetailsServiceImplTest {

    @Mock
    private BillingdetailsDao billingdetailsDao;

    @Mock
    private BillingdetailsRepository billingdetailsRepository;

    @Mock
    private PatientDetailsUtil patientDetailsUtil;

    @InjectMocks
    private BillingdetailsServiceImpl billingdetailsService;

    private Billingdetails billingdetails;
    private BillingdetailsDto billingdetailsDto;


    @BeforeEach
    void setUp() {
        
        MockitoAnnotations.openMocks(this);
        Random random = new Random();
        String generatedId = String.valueOf(random.nextInt(100));

        billingdetails = new Billingdetails();
        billingdetails.setId(generatedId);          
        billingdetails.setBillingid(random.nextInt(1000));              
        billingdetails.setPatientid(random.nextInt(1000));                   
        billingdetails.setPaymentstatus("test_value_" + random.nextInt(100)); 

        billingdetailsDto = new BillingdetailsDto();
        billingdetailsDto.setId(generatedId);         
        billingdetailsDto.setBillingid(random.nextInt(1000));               
        billingdetailsDto.setPatientid(random.nextInt(1000));                   
        billingdetailsDto.setPaymentstatus("test_value_" + random.nextInt(100)); 
    }

    @Test
    void testCreateBillingdetails() {
        when(patientDetailsUtil.toEntity(any(BillingdetailsDto.class))).thenReturn(billingdetails);
        when(billingdetailsDao.createBillingdetails(any(Billingdetails.class))).thenReturn(billingdetails);
        when(patientDetailsUtil.toDto(any(Billingdetails.class))).thenReturn(billingdetailsDto);

        BillingdetailsDto created = billingdetailsService.createBillingdetails(billingdetailsDto);

        assertNotNull(created);
        assertEquals(billingdetailsDto.getId(), created.getId());
        verify(billingdetailsDao).createBillingdetails(any(Billingdetails.class));
    }

    @Test
    void testGetBillingdetailsById() {
        when(billingdetailsDao.getBillingdetailsById(billingdetails.getId())).thenReturn(Optional.of(billingdetails));
        when(patientDetailsUtil.toDto(any(Billingdetails.class))).thenReturn(billingdetailsDto);

        BillingdetailsDto found = billingdetailsService.getBillingdetailsById(billingdetails.getId());

        assertNotNull(found);
        assertEquals(billingdetailsDto.getId(), found.getId());
        verify(billingdetailsDao).getBillingdetailsById(billingdetails.getId());
    }

    @Test
    void testGetAllBillingdetails() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Billingdetails> billingdetailsList = Collections.singletonList(billingdetails);
        Page<Billingdetails> page = new PageImpl<>(billingdetailsList);

        when(billingdetailsDao.getAllBillingdetails(pageable)).thenReturn(page);
        when(patientDetailsUtil.toDto(any(Billingdetails.class))).thenReturn(billingdetailsDto);

        Page<BillingdetailsDto> result = billingdetailsService.getAllBillingdetails(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(billingdetailsDao).getAllBillingdetails(pageable);
    }

    @Test
    void testSearchBillingdetails() {
        Map<String, String> params = new HashMap<>();
        params.put("paymentstatus", billingdetails.getPaymentstatus());

        List<Billingdetails> billingdetailsList = Collections.singletonList(billingdetails);
        Specification<Billingdetails> specification = mock(Specification.class);

        when(patientDetailsUtil.constructSearchQuery(anyMap(), eq(Billingdetails.class))).thenReturn(specification);
        when(billingdetailsRepository.findAll(specification)).thenReturn(billingdetailsList);
        when(patientDetailsUtil.toDto(any(Billingdetails.class))).thenReturn(billingdetailsDto);

        List<BillingdetailsDto> result = billingdetailsService.searchBillingdetails(params);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(billingdetailsRepository).findAll(specification);
    }

    @Test
    void testUpdateBillingdetails() {
        when(billingdetailsDao.getBillingdetailsById(billingdetails.getId())).thenReturn(Optional.of(billingdetails));
        when(patientDetailsUtil.toEntity(any(BillingdetailsDto.class))).thenReturn(billingdetails);
        when(billingdetailsDao.createBillingdetails(any(Billingdetails.class))).thenReturn(billingdetails);
        when(patientDetailsUtil.toDto(any(Billingdetails.class))).thenReturn(billingdetailsDto);

        BillingdetailsDto updated = billingdetailsService.updateBillingdetails(billingdetailsDto);

        assertNotNull(updated);
        assertEquals(billingdetailsDto.getId(), updated.getId());
        verify(billingdetailsDao).createBillingdetails(any(Billingdetails.class));
    }

    @Test
    void testDeleteBillingdetails() {
        when(billingdetailsDao.getBillingdetailsById(billingdetails.getId())).thenReturn(Optional.of(billingdetails));

        String result = billingdetailsService.deleteBillingdetails(billingdetails.getId());

        assertEquals("Billingdetails deleted successfully", result);
        verify(billingdetailsDao).deleteBillingdetails(billingdetails.getId());
    }

    @Test
    void testGetBillingdetailsById_NotFound() {
        when(billingdetailsDao.getBillingdetailsById("999")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> billingdetailsService.getBillingdetailsById("999"));
    }

    @Test
    void testUpdateBillingdetails_NotFound() {
        when(billingdetailsDao.getBillingdetailsById("999")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> billingdetailsService.updateBillingdetails(billingdetailsDto));
    }

    @Test
    void testDeleteBillingdetails_NotFound() {
        when(billingdetailsDao.getBillingdetailsById("999")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> billingdetailsService.deleteBillingdetails("999"));
    }
    
}
