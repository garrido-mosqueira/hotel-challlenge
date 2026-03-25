package com.fran.hotel.persistence.adapter;

import com.fran.hotel.domain.model.Hotel;
import com.fran.hotel.domain.port.HotelPersistencePort;
import com.fran.hotel.persistence.entity.HotelEntity;
import com.fran.hotel.persistence.mapper.HotelEntityMapper;
import com.fran.hotel.persistence.repository.HotelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Repository
@RequiredArgsConstructor
@CacheConfig(cacheNames = "hotelCache")
public class HotelPersistenceAdapter implements HotelPersistencePort {

    private final HotelRepository hotelRepository;
    private final HotelEntityMapper hotelMapper;

    @Override
    @Cacheable(key = "#id")
    public Optional<Hotel> findById(String id) {
        return hotelRepository.findById(id)
                .map(hotelMapper::toDomain);
    }

    @Override
    @Cacheable(value = "searchCache", key = "'all'")
    public List<Hotel> findAll() {
        return hotelRepository.findAll().stream()
                .map(hotelMapper::toDomain)
                .collect(toList());
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "#hotel.id", condition = "#hotel.id != null"),
            @CacheEvict(value = "searchCache", allEntries = true)
    })
    public Hotel save(Hotel hotel) {
        HotelEntity entity = hotelMapper.toEntity(hotel);
        if (entity.getRooms() != null) {
            entity.getRooms().forEach(room -> room.setHotel(entity));
        }
        HotelEntity saved = hotelRepository.save(entity);
        return hotelMapper.toDomain(saved);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "#id"),
            @CacheEvict(value = "searchCache", allEntries = true)
    })
    public void deleteById(String id) {
        hotelRepository.deleteById(id);
    }

    @Override
    @Cacheable(value = "searchCache", key = "#city")
    public List<Hotel> findByCity(String city) {
        return hotelRepository.findByCityContainingIgnoreCase(city).stream()
                .map(hotelMapper::toDomain)
                .collect(toList());
    }

}