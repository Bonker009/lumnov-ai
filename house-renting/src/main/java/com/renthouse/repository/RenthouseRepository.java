package com.renthouse.repository;

import com.renthouse.entity.Renthouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RenthouseRepository extends JpaRepository<Renthouse, Long> {
    List<Renthouse> findByOwner_Id(Long ownerId);
    
    @Query("SELECT r FROM Renthouse r")
    List<Renthouse> findAllRenthouses();

    @Query("SELECT r FROM Renthouse r WHERE " +
           "(:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:location IS NULL OR LOWER(r.address) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:minPrice IS NULL OR r.baseRent >= :minPrice) AND " +
           "(:maxPrice IS NULL OR r.baseRent <= :maxPrice)")
    List<Renthouse> searchRenthouses(@Param("name") String name, 
                                   @Param("location") String location,
                                   @Param("minPrice") BigDecimal minPrice, 
                                   @Param("maxPrice") BigDecimal maxPrice);

    @Query(value = "SELECT * FROM renthouses WHERE " +
           "(6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * " +
           "cos(radians(longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(latitude)))) < :radiusKm " +
           "ORDER BY " +
           "(6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * " +
           "cos(radians(longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(latitude))))", nativeQuery = true)
    List<Renthouse> findNearbyRenthouses(@Param("latitude") Double latitude, 
                                        @Param("longitude") Double longitude, 
                                        @Param("radiusKm") Double radiusKm);

    @Query("SELECT r FROM Renthouse r ORDER BY r.createdAt DESC")
    List<Renthouse> findFeaturedRenthouses(Pageable pageable);
    
    long countByOwner_Id(Long ownerId);
}