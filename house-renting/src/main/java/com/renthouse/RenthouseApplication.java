package com.renthouse;

import com.renthouse.entity.*;
import com.renthouse.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootApplication
public class RenthouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(RenthouseApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(
            @Autowired UserRepository userRepository,
            @Autowired RenthouseRepository renthouseRepository,
            @Autowired FloorRepository floorRepository,
            @Autowired RoomRepository roomRepository) {
        return args -> {
            // Only initialize if no data exists
            if (userRepository.count() == 0) {
                System.out.println("Initializing sample data...");
                
                // Create owner
                User owner = new User();
                owner.setUsername("owner1");
                owner.setEmail("owner1@example.com");
                owner.setPassword("$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi"); // password
                owner.setFullName("John Owner");
                owner.setPhoneNumber("1234567890");
                owner.setRole(User.Role.OWNER);
                owner = userRepository.save(owner);
                
                // Create user
                User user = new User();
                user.setUsername("user1");
                user.setEmail("user1@example.com");
                user.setPassword("$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi"); // password
                user.setFullName("Jane User");
                user.setPhoneNumber("0987654321");
                user.setRole(User.Role.USER);
                user = userRepository.save(user);
                
                // Create renthouse
                Renthouse renthouse = new Renthouse();
                renthouse.setName("Sunset Apartments");
                renthouse.setAddress("123 Main Street, City Center");
                renthouse.setDescription("A beautiful apartment complex with modern amenities");
                renthouse.setLatitude(40.7128);
                renthouse.setLongitude(-74.0060);
                renthouse.setBaseRent(new BigDecimal("1200.00"));
                renthouse.setWaterFee(new BigDecimal("50.00"));
                renthouse.setElectricityFee(new BigDecimal("80.00"));
                renthouse.setOwner(owner);
                renthouse = renthouseRepository.save(renthouse);
                
                // Create floor 1
                Floor floor1 = new Floor();
                floor1.setFloorNumber(1);
                floor1.setDescription("Ground Floor");
                floor1.setRenthouse(renthouse);
                floor1 = floorRepository.save(floor1);
                
                // Create room 101
                Room room101 = new Room();
                room101.setRoomNumber("101");
                room101.setDescription("Spacious 1-bedroom apartment with balcony");
                room101.setMonthlyRent(new BigDecimal("800.00"));
                room101.setDeposit(new BigDecimal("1600.00"));
                room101.setStatus(Room.RoomStatus.AVAILABLE);
                room101.setFloor(floor1);
                roomRepository.save(room101);
                
                // Create room 102
                Room room102 = new Room();
                room102.setRoomNumber("102");
                room102.setDescription("Cozy studio apartment");
                room102.setMonthlyRent(new BigDecimal("600.00"));
                room102.setDeposit(new BigDecimal("1200.00"));
                room102.setStatus(Room.RoomStatus.BOOKED);
                room102.setRenter(user);
                room102.setBookedAt(LocalDateTime.now());
                room102.setFloor(floor1);
                roomRepository.save(room102);
                
                // Create floor 2
                Floor floor2 = new Floor();
                floor2.setFloorNumber(2);
                floor2.setDescription("Second Floor");
                floor2.setRenthouse(renthouse);
                floor2 = floorRepository.save(floor2);
                
                // Create room 201
                Room room201 = new Room();
                room201.setRoomNumber("201");
                room201.setDescription("Luxury 2-bedroom apartment");
                room201.setMonthlyRent(new BigDecimal("1200.00"));
                room201.setDeposit(new BigDecimal("2400.00"));
                room201.setStatus(Room.RoomStatus.AVAILABLE);
                room201.setFloor(floor2);
                roomRepository.save(room201);
                
                System.out.println("Sample data initialized successfully!");
                System.out.println("Owner username: owner1, password: password");
                System.out.println("User username: user1, password: password");
                System.out.println("Renthouse ID: " + renthouse.getId());
            }
        };
    }
}