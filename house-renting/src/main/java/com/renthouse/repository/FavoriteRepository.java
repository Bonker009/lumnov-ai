package com.renthouse.repository;

import com.renthouse.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser_Id(Long userId);
    Optional<Favorite> findByUser_IdAndRoom_Id(Long userId, Long roomId);
    Boolean existsByUser_IdAndRoom_Id(Long userId, Long roomId);
    void deleteByUser_IdAndRoom_Id(Long userId, Long roomId);
}