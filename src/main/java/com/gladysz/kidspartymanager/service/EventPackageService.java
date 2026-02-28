package com.gladysz.kidspartymanager.service;

import com.gladysz.kidspartymanager.domain.EventPackage;
import com.gladysz.kidspartymanager.dto.EventPackageUpdateDto;
import com.gladysz.kidspartymanager.exception.EventPackageNotFoundException;
import com.gladysz.kidspartymanager.mapper.EventPackageMapper;
import com.gladysz.kidspartymanager.repository.EventPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EventPackageService {

    private final EventPackageRepository eventPackageRepository;
    private final EventPackageMapper eventPackageMapper;


    public EventPackage createEventPackage(final EventPackage eventPackage) {

        return eventPackageRepository.save(eventPackage);
    }


    @Transactional(readOnly = true)
    public EventPackage getEventPackageById(final Long id) {

        return eventPackageRepository.findById(id)
                .orElseThrow(() -> new EventPackageNotFoundException(id));
    }


    @Transactional(readOnly = true)
    public List<EventPackage> getAllEventPackages() {

        return eventPackageRepository.findAll();
    }


    public EventPackage updateEventPackage(final Long id, final EventPackageUpdateDto eventPackageUpdateDto) {

        EventPackage fetchedEventPackage = eventPackageRepository.findById(id)
                .orElseThrow(() -> new EventPackageNotFoundException(id));

        eventPackageMapper.applyUpdate(fetchedEventPackage, eventPackageUpdateDto);

        return fetchedEventPackage;
    }


    public void deleteEventPackageById(final Long id) {

        EventPackage fetchedEventPackage = eventPackageRepository.findById(id)
                .orElseThrow(() -> new EventPackageNotFoundException(id));

        eventPackageRepository.delete(fetchedEventPackage);
    }
}
