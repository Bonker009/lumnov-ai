package com.renthouse.repository;

import com.renthouse.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {
    List<Floor> findByRenthouse_Id(Long renthouseId);
    List<Floor> findByRenthouse_IdOrderByFloorNumberAsc(Long renthouseId);
    
    @Query("SELECT MAX(f.floorNumber) FROM Floor f WHERE f.renthouse.id = :renthouseId")
    Integer findMaxFloorNumberByRenthouseId(@Param("renthouseId") Long renthouseId);
}