package com.gladysz.kidspartymanager.service;


import com.gladysz.kidspartymanager.domain.Orderer;
import com.gladysz.kidspartymanager.dto.OrdererUpdateDto;
import com.gladysz.kidspartymanager.exception.OrdererNotFoundException;
import com.gladysz.kidspartymanager.mapper.OrdererMapper;
import com.gladysz.kidspartymanager.repository.OrdererRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrdererService {

    private final OrdererRepository ordererRepository;
    private final OrdererMapper ordererMapper;


    public Orderer createOrderer(final Orderer orderer) {

        return ordererRepository.save(orderer);
    }


    @Transactional(readOnly = true)
    public Orderer getOrdererById(final Long id) {

        return ordererRepository.findById(id)
                .orElseThrow(() -> new OrdererNotFoundException(id));
    }


    @Transactional(readOnly = true)
    public List<Orderer> getAllOrderers() {

        return ordererRepository.findAll();
    }


    public Orderer updateOrderer(final Long id, final OrdererUpdateDto OrdererUpdateDto) {

        Orderer fetchedOrderer = ordererRepository.findById(id)
                .orElseThrow(() -> new OrdererNotFoundException(id));

        ordererMapper.applyUpdate(fetchedOrderer, OrdererUpdateDto);

        return fetchedOrderer;
    }


    public void deleteOrdererById(final Long id) {

        Orderer fetchedOrderer = ordererRepository.findById(id)
                .orElseThrow(() -> new OrdererNotFoundException(id));

        ordererRepository.delete(fetchedOrderer);
    }
}



