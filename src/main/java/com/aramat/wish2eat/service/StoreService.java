package com.aramat.wish2eat.service;

import com.aramat.wish2eat.converter.StoreConverter;
import com.aramat.wish2eat.dto.LoginDTO;
import com.aramat.wish2eat.dto.StoreDTO;
import com.aramat.wish2eat.dto.StoreInsertDTO;
import com.aramat.wish2eat.entities.Store;
import com.aramat.wish2eat.repositories.StoreRepository;
import com.aramat.wish2eat.service.exceptions.DatabaseException;
import com.aramat.wish2eat.service.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class StoreService {

    @Autowired
    private StoreRepository repository;

    @Autowired
    private StoreConverter converter;

    @Transactional(readOnly = true)
    public List<StoreDTO> findAll() {
        return converter.fromEntityListToDtoList(repository.findAll());
    }

    @Transactional(readOnly = true)
    public StoreDTO findById(Long id) {
        Optional<Store> obj = repository.findById(id);
        Store entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return converter.fromEntityToDTO(entity);
    }

    @Transactional
    public StoreDTO insert(StoreInsertDTO dto) {
        Store store = repository.save(converter.fromDtoToEntity(dto));
        return converter.fromEntityToDTO(store);
    }

    @Transactional
    public StoreDTO update(Long id, StoreInsertDTO dto) {
        try {
            Store entity = repository.getById(id);
            entity = repository.save(copyDtoToEntity(dto, entity));
            return converter.fromEntityToDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found" + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Id not found" + id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity Violation");
        }
    }

    private Store copyDtoToEntity(StoreInsertDTO dto, Store store) {
        store.setName(dto.getName());
        store.setCep(dto.getCep());
        store.setNumber(dto.getNumber());
        store.setPhoneNumber(dto.getPhoneNumber());
        store.setInstagram(dto.getInstagram());
        store.setFacebook(dto.getFacebook());
        store.setType(dto.getType());
        store.setEmail(dto.getEmail());
        store.setPassword(dto.getPassword());
        return store;
    }

    public StoreDTO findByEmailAndPassword(LoginDTO dto) {
        Optional<Store> obj = repository.findByEmailAndPassword(dto.getEmail(), dto.getPassword());
        Store entity = obj.orElseThrow(() -> new ResourceNotFoundException("Email and/or password are invalid"));
        return converter.fromEntityToDTO(entity);
    }

}
