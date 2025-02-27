package com.geppetto.MediRecords.service.serviceimpl;

import com.geppetto.MediRecords.dao.BillingdetailsDao;
import com.geppetto.MediRecords.dto.BillingdetailsDto;
import com.geppetto.MediRecords.exception.EntityNotFoundException;
import com.geppetto.MediRecords.model.Billingdetails;
import com.geppetto.MediRecords.service.BillingdetailsService;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import java.util.Map;
import org.springframework.data.domain.Page;
import com.geppetto.MediRecords.util.PatientDetailsUtil;
import org.springframework.data.domain.Pageable;
import com.geppetto.MediRecords.repository.BillingdetailsRepository;
import java.util.stream.Collectors;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

/**
* Implementation of the {@link BillingdetailsService} interface.
* Provides services related to Billingdetails, including CRUD operations and file uploads/downloads.
*/

@Service
@Slf4j
@RequiredArgsConstructor
public class BillingdetailsServiceImpl implements BillingdetailsService {


    /**
    * Constructs a {@code BillingdetailsServiceImpl} with the specified DAO.
    *
    * @param billingdetailsDao The DAO for accessing the data.
    */
    private final BillingdetailsDao billingdetailsDao;
    private final BillingdetailsRepository billingdetailsRepository;
    private final PatientDetailsUtil patientDetailsUtil;

  
    /**
    * Creates new billingdetails.
    *
    * @param billingdetailsDto The {@link BillingdetailsDto} to be created.
    * @return The created {@link BillingdetailsDto}.
    */
    @Override
    public BillingdetailsDto  createBillingdetails(BillingdetailsDto billingdetailsDto) {
        log.info("Entering createBillingdetails method");
    
        Billingdetails billingdetails = patientDetailsUtil.toEntity(billingdetailsDto);
        Billingdetails createdBillingdetails = billingdetailsDao.createBillingdetails(billingdetails);
        billingdetailsDto = patientDetailsUtil.toDto(createdBillingdetails);
        
        log.info("Exiting createBillingdetails method");
        return billingdetailsDto;
    }
  
    /**
    * Retrieves billingdetails by its ID.
    *
    * @param id The ID of the billingdetails to retrieve. Must not be {@code null}.
    * @return The billingdetails data transfer object associated with the specified ID.
    * @throws EntityNotFoundException If no billingdetails with the specified ID is found.
    */
    @Override
    public BillingdetailsDto  getBillingdetailsById(String id) {
        log.info("Entering getBillingdetailsById method for ID: {}", id);
        
        Billingdetails billingdetails = billingdetailsDao.getBillingdetailsById(id)
            .orElseThrow(() -> {
            log.warn("No billingdetails found for ID: {}", id);
            return new EntityNotFoundException("Data not found for ID: " + id);
        });

        BillingdetailsDto billingdetailsDto = patientDetailsUtil.toDto(billingdetails);
        
        log.info("Exiting getBillingdetailsById method for ID: {}", id);
        return billingdetailsDto;
    }
  
    /**
    * Retrieves all billingdetails.
    *
    * @return A list of {@link BillingdetailsDto} representing all billingdetails.
    */
    @Override
    public Page<BillingdetailsDto>  getAllBillingdetails(int page, int size) {
        log.info("Entering getAllBillingdetails method");
    
        Pageable pageable = PageRequest.of(page, size);
        Page<Billingdetails> billingdetailsPage = billingdetailsDao.getAllBillingdetails(pageable);
        Page<BillingdetailsDto> billingdetailsDtoPage = billingdetailsPage.map(billingdetails -> {
            BillingdetailsDto dto = patientDetailsUtil.toDto(billingdetails);
            return dto;
        });
        
        log.info("Exiting getAllBillingdetails method");
        return billingdetailsDtoPage;
    }
  
    /**
    * Searches for billingdetails based on provided parameters.
    *
    * @param allParams A map of search parameters.
    * @return A list of {@link BillingdetailsDto} matching the search parameters.
    */
    @Override
    public List<BillingdetailsDto>  searchBillingdetails(Map<String, String> allParams) {
        log.info("Entering searchBillingdetails method for SQL");
        
        Specification<Billingdetails> specification = patientDetailsUtil.constructSearchQuery(allParams, Billingdetails.class);
        List<Billingdetails> results = billingdetailsRepository.findAll(specification);
        List<BillingdetailsDto> billingdetailsDtos = results.stream()
                .map(billingdetails -> patientDetailsUtil.toDto(billingdetails))
                .collect(Collectors.toList());

        log.info("Exiting searchBillingdetails method for SQL. Results found: {}", billingdetailsDtos.size());
        return billingdetailsDtos;
    }
  
    /**
    * Updates existing billingdetails.
    *
    * @param billingdetailsDto The {@link BillingdetailsDto} containing updated information.
    * @return The updated {@link BillingdetailsDto}.
    * @throws EntityNotFoundException If no billingdetails with the specified ID is found.
    */
    @Override
    public BillingdetailsDto updateBillingdetails(BillingdetailsDto billingdetailsDto) {
        String id = billingdetailsDto.getId();
        log.info("Entering updateBillingdetails method for ID: {}", id);

        billingdetailsDao.getBillingdetailsById(id).orElseThrow(() -> {
            log.warn("No billingdetails found for update with ID: {}", id);
            return new EntityNotFoundException("Data not found for update with ID: " + id);
        });

        Billingdetails updatedBillingdetails = patientDetailsUtil.toEntity(billingdetailsDto);
        updatedBillingdetails = billingdetailsDao.createBillingdetails(updatedBillingdetails);
        BillingdetailsDto responseDto = patientDetailsUtil.toDto(updatedBillingdetails);

        log.info("Exiting updateBillingdetails method for ID: {}", id);
        return responseDto;
    }
  
    /**
    * Deletes billingdetails by ID.
    *
    * @param id The ID of the billingdetails to delete.
    * @return A message indicating the result of the deletion.
    * @throws EntityNotFoundException If no billingdetails with the specified ID is found.
    */
    @Override
    public String  deleteBillingdetails(String id) {
        log.info("Entering deleteBillingdetails method for ID: {}", id);

        if (billingdetailsDao.getBillingdetailsById(id).isEmpty()) {
            log.warn("No billingdetails found with ID: {}. Deletion failed.", id);
            throw new EntityNotFoundException("No billingdetails found with ID: " + id + ". Unable to delete.");
        }

        billingdetailsDao.deleteBillingdetails(id);

        log.info("Successfully deleted Billingdetails with ID: {}", id);
        return "Billingdetails deleted successfully";
    }
  
}



