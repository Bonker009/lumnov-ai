package com.renthouse.repository;

import com.renthouse.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUser_Id(Long userId);
    List<Payment> findByRoom_Id(Long roomId);
    List<Payment> findByRoom_IdOrderByPaymentMonthDesc(Long roomId);
    List<Payment> findByPaymentMonth(LocalDate paymentMonth);
    
    // Get payments by status for a user using Spring Data JPA method names
    List<Payment> findByUser_IdAndStatusOrderByPaymentMonthDesc(Long userId, Payment.PaymentStatus status);
    List<Payment> findByUser_IdAndStatusNotOrderByPaymentMonthAsc(Long userId, Payment.PaymentStatus status);
    
    // Get pending payments specifically (PENDING and OVERDUE, excluding CANCELLED and PAID)
    List<Payment> findByUser_IdAndStatusInOrderByPaymentMonthAsc(Long userId, List<Payment.PaymentStatus> statuses);
    
    @Query("SELECT p FROM Payment p WHERE p.room.floor.renthouse.owner.id = :ownerId")
    List<Payment> findByOwnerId(@Param("ownerId") Long ownerId);
    
    @Query("SELECT SUM(p.totalAmount) FROM Payment p WHERE " +
           "p.room.floor.renthouse.owner.id = :ownerId AND " +
           "p.status = 'PAID' AND " +
           "YEAR(p.paymentMonth) = :year AND " +
           "MONTH(p.paymentMonth) = :month")
    BigDecimal getMonthlyIncomeByOwner(@Param("ownerId") Long ownerId, 
                                     @Param("year") int year, 
                                     @Param("month") int month);
    
    @Query("SELECT SUM(p.totalAmount) FROM Payment p WHERE " +
           "p.room.floor.renthouse.owner.id = :ownerId AND " +
           "p.status = 'PAID' AND " +
           "YEAR(p.paymentMonth) = :year")
    BigDecimal getYearlyIncomeByOwner(@Param("ownerId") Long ownerId, 
                                    @Param("year") int year);

    @Query("SELECT p FROM Payment p WHERE p.room.floor.renthouse.owner.id = :ownerId AND p.paymentMonth = :month")
    List<Payment> findByOwnerAndMonth(@Param("ownerId") Long ownerId, @Param("month") LocalDate month);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.room.floor.renthouse.owner.id = :ownerId AND p.status != :status")
    long countByOwnerIdAndStatusNot(@Param("ownerId") Long ownerId, @Param("status") Payment.PaymentStatus status);
}
