package org.example.desksharing.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.desksharing.entity.User;

import java.util.Base64;

@Data
@AllArgsConstructor
public class ProfilePictureDTO {
    private String username;
    private String email;
    private String profilePicture;

    public ProfilePictureDTO(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.profilePicture = user.getProfilePicture() != null
                ? Base64.getEncoder().encodeToString(user.getProfilePicture())
                : null; // Falls kein Bild vorhanden ist
    }
}
