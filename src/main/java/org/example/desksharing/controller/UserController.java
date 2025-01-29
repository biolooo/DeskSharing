package org.example.desksharing.controller;

import org.example.desksharing.entity.Booking;
import org.example.desksharing.entity.User;
import org.example.desksharing.request.BookDeskRequest;
import org.example.desksharing.request.ProfilePictureDTO;
import org.example.desksharing.request.UserDTO;
import org.example.desksharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile-picture")
    public ResponseEntity<ProfilePictureDTO> getProfilePicture(Authentication authentication) {
        User user = userService.loadUserByUsername(authentication.getName());
        return ResponseEntity.ok(new ProfilePictureDTO(user));
    }

    @PostMapping("/profile-picture")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("file") MultipartFile file,
                                                  Authentication authentication) throws IOException {
        User user = userService.loadUserByUsername(authentication.getName());
        user.setProfilePicture(file.getBytes());
        userService.saveUser(user);
        return ResponseEntity.ok("Profilbild hochgeladen!");
    }

    @DeleteMapping("/profile-picture")
    public ResponseEntity<?> deleteProfilePicture(Authentication authentication) {
        User user = userService.loadUserByUsername(authentication.getName());
        user.setProfilePicture(null);
        userService.saveUser(user);
        return ResponseEntity.ok("Profilbild gelöscht!");
    }
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.loadUserByUsername(username);
        UserDTO userDTO = new UserDTO(user.getUsername(),user.getEmail(),user.getProfilePicture());
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody User updatedUser, Authentication authentication) {
        User user = userService.updateUser(updatedUser, authentication);

        // Sicherheitskontext aktualisieren
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        return ResponseEntity.ok(user);
    }
    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getBookings(Authentication authentication) {
        return new ResponseEntity<>(
                userService.bookingsByUsername(authentication.getName()),
                HttpStatus.OK);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable long bookingId) {
        return new ResponseEntity<>(userService.deleteBooking(bookingId),HttpStatus.OK);
    }

    @PostMapping("/BookDesk")
    public ResponseEntity<Booking> addBookDesk(Authentication authentication,@RequestBody BookDeskRequest bookDeskRequest) {
        return new ResponseEntity<>(userService.bookDesk(
                bookDeskRequest, authentication),
                HttpStatus.OK);
    }


}
