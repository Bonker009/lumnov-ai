package com.renthouse.repository;

import com.renthouse.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByFloor_Id(Long floorId);
    List<Room> findByStatus(Room.RoomStatus status);
    List<Room> findByRenter_Id(Long renterId);
    
    @Query("SELECT r FROM Room r WHERE r.floor.renthouse.owner.id = :ownerId")
    List<Room> findByOwnerId(@Param("ownerId") Long ownerId);
    
    @Query("SELECT r FROM Room r WHERE " +
           "(:roomNumber IS NULL OR LOWER(r.roomNumber) LIKE LOWER(CONCAT('%', :roomNumber, '%'))) AND " +
           "(:username IS NULL OR LOWER(r.renter.username) LIKE LOWER(CONCAT('%', :username, '%'))) AND " +
           "r.floor.renthouse.owner.id = :ownerId")
    List<Room> searchRoomsByOwner(@Param("roomNumber") String roomNumber, 
                                @Param("username") String username, 
                                @Param("ownerId") Long ownerId);
    
    @Query("SELECT r FROM Room r WHERE r.floor.renthouse.id = :renthouseId AND r.status = 'AVAILABLE'")
    List<Room> findAvailableRoomsByRenthouse(@Param("renthouseId") Long renthouseId);

    Optional<Room> findFirstByRenterIdAndStatusIn(Long renterId, List<Room.RoomStatus> statuses);

    List<Room> findByRenterId(Long renterId);
    
    @Query("SELECT MAX(CAST(r.roomNumber AS INTEGER)) FROM Room r WHERE r.floor.id = :floorId")
    Integer findMaxRoomNumberByFloorId(@Param("floorId") Long floorId);
    
    @Query("SELECT COUNT(r) FROM Room r WHERE r.floor.renthouse.owner.id = :ownerId AND r.status != :status")
    long countByOwnerIdAndStatusNot(@Param("ownerId") Long ownerId, @Param("status") Room.RoomStatus status);
}
