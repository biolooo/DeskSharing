package org.example.desksharing.service;

import jakarta.annotation.PostConstruct;
import org.example.desksharing.entity.*;
import org.example.desksharing.repository.BookingRepository;
import org.example.desksharing.repository.DeskRepository;
import org.example.desksharing.repository.RoleRepository;
import org.example.desksharing.repository.UserRepository;
import org.example.desksharing.request.BookDeskRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    private DeskRepository deskRepository;

    @PostConstruct
    public void initRoles(){
        if (roleRepository.findByName(RoleName.USER).isEmpty()) {
            Role userRole = new Role();
            userRole.setName(RoleName.USER);
            roleRepository.save(userRole);
        }
        if (roleRepository.findByName(RoleName.ADMIN).isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName(RoleName.ADMIN);
            roleRepository.save(adminRole);
        }
    }

    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role userRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new RuntimeException("Rolle nicht gefunden"));
        user.setRoles(Collections.singleton(userRole));
        userRepository.save(user);
    }

    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username);
        if(user!=null && passwordEncoder.matches(password,user.getPassword())){
            return user;
        }
        return null;
    }

    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user==null){
            throw new UsernameNotFoundException("Benutzer nicht gefunden");
        }
        return user;
    }

    public Booking bookDesk(BookDeskRequest bookDeskRequest, Authentication authentication) {
        // Step 2: Fetch User and Desk from the database
        User user = userRepository.findById(loadUserByUsername(authentication.getName()).getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Desk desk = deskRepository.findById(bookDeskRequest.getDeskId())
                .orElseThrow(() -> new IllegalArgumentException("Desk not found"));

        // Step 3: Check if the desk is available
        boolean isDeskAvailable = true; // Assume availability until proven otherwise
        for (Booking b : bookingRepository.findByDeskId(desk.getId())) {
            if (b.getBookedDate() == bookDeskRequest.getBookedDate()) {
                isDeskAvailable = false;
                break; // No need to check further
            }
        }

        if (!isDeskAvailable) {
            throw new IllegalArgumentException("Desk is not available for the selected time");
        }

        // Step 4: Save and return the booking
        return bookingRepository.save(
                Booking.builder()
                        .desk(desk)
                        .user(user)
                        .bookedDate(bookDeskRequest.getBookedDate())
                        .build()
        );
    }

    public List<Booking> bookingsByUsername(String name) {
        User user = loadUserByUsername(name);
        return bookingRepository.findByUser(user);
    }

    public Void deleteBooking(long bookingId) {
        bookingRepository.deleteById(bookingId);
        return null;
    }

    public User updateUser(User updatedUser, Authentication authentication) {
        User user = loadUserByUsername(authentication.getName());
        if (updatedUser.getUsername() != null && !updatedUser.getUsername().trim().isEmpty()) {
            user.setUsername(updatedUser.getUsername());
        }

        // E-Mail aktualisieren
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().trim().isEmpty()) {
            user.setEmail(updatedUser.getEmail());
        }

        // Passwort aktualisieren (falls mitgegeben und nicht leer)
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        // Und am Ende speichern
        return userRepository.save(user);
    }

    public User loadUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden"));
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}