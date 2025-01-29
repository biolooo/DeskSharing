package org.example.desksharing.config;

import org.example.desksharing.entity.*;
import org.example.desksharing.repository.*;
import org.example.desksharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private DeskRepository deskRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 1. Gebäude "Landratsamt Ortenaukreis" erstellen
        createBuildingAndDesks();

        // 2. Beispielbenutzer erstellen
        createUsers();
    }

    private void createBuildingAndDesks() {
        // Gebäude erstellen
        Building building = Building.builder()
                .name("Landratsamt Ortenaukreis")
                .address("Adresse 1")
                .build();
        buildingRepository.save(building);

        // Stockwerk erstellen
        Floor floor = Floor.builder()
                .name("4.OG")
                .floorPlanImage("http://localhost:8080/floorplans/building1_floor1.png")
                .building(building)
                .build();
        floorRepository.save(floor);

        // Tische erstellen
        List<Desk> desks = Arrays.asList(
                Desk.builder().name("451 Fenster").description("Der Tisch links").floor(floor).x(918.1).y(305.6).available(true).build(),
                Desk.builder().name("451 Fenster").description("Der Tisch rechts").floor(floor).x(972.6).y(351.1).available(true).build(),
                Desk.builder().name("451 Wand").description("Der Tisch an der Wand").floor(floor).x(828.1).y(388.9).available(true).build(),
                Desk.builder().name("451/1").description("Tisch").floor(floor).x(1023.7).y(408.9).available(true).build(),
                Desk.builder().name("451/1").description("Tisch").floor(floor).x(1070.3).y(455.6).available(true).build(),
                Desk.builder().name("369 Fenster").description("Tisch am Fenster").floor(floor).x(1509.2).y(480.0).available(true).build(),
                Desk.builder().name("369 Tür").description("Tisch an der Tür").floor(floor).x(1373.7).y(543.4).available(true).build(),
                Desk.builder().name("456/1").description("Tisch").floor(floor).x(1428.1).y(1151.1).available(true).build(),
                Desk.builder().name("456/6").description("Tisch").floor(floor).x(1838.1).y(1151.1).available(true).build(),
                Desk.builder().name("456/3").description("Tisch").floor(floor).x(1597.0).y(1151.1).available(true).build(),
                Desk.builder().name("456/4").description("Tisch").floor(floor).x(1673.7).y(1151.1).available(true).build(),
                Desk.builder().name("456/2").description("Tisch").floor(floor).x(1500.3).y(1151.1).available(true).build(),
                Desk.builder().name("456/5").description("Tisch").floor(floor).x(1740.3).y(1151.1).available(true).build(),
                Desk.builder().name("456/9").description("Tisch").floor(floor).x(1738.1).y(1085.6).available(true).build(),
                Desk.builder().name("456/10").description("Tisch").floor(floor).x(1838.1).y(1088.9).available(true).build(),
                Desk.builder().name("456/7").description("Tisch").floor(floor).x(1428.1).y(1083.4).available(true).build(),
                Desk.builder().name("456/8").description("Tisch").floor(floor).x(1500.3).y(1084.5).available(true).build(),
                Desk.builder().name("455").description("Tisch").floor(floor).x(2018.1).y(880.0).available(true).build(),
                Desk.builder().name("454/1 Fenster").description("Rechts").floor(floor).x(2037.0).y(808.9).available(true).build(),
                Desk.builder().name("454/1 Fenster").description("Links").floor(floor).x(2038.1).y(697.8).available(true).build(),
                Desk.builder().name("454/1 Wand").description("Links").floor(floor).x(1950.3).y(701.1).available(true).build(),
                Desk.builder().name("Peter").description("Der Tisch von Peter").floor(floor).x(1811.5).y(697.8).available(true).build(),
                Desk.builder().name("454 Fenster").description("Rechts").floor(floor).x(1892.6).y(697.8).available(true).build(),
                Desk.builder().name("454").description("Gang").floor(floor).x(1852.6).y(797.8).available(true).build(),
                Desk.builder().name("453/1 Fenster").description("Hinten").floor(floor).x(1676.9).y(761.6).available(true).build(),
                Desk.builder().name("453/1 Fenster").description("Vorne").floor(floor).x(1746.9).y(760.3).available(true).build(),
                Desk.builder().name("453/1 Wand").description("Hinten").floor(floor).x(1670.7).y(872.8).available(true).build(),
                Desk.builder().name("453/1 Wand").description("Vorne").floor(floor).x(1739.4).y(871.6).available(true).build(),
                Desk.builder().name("453 Fenster").description("Rechts").floor(floor).x(1556.9).y(762.8).available(true).build(),
                Desk.builder().name("453 Wand").description("Rechts").floor(floor).x(1558.2).y(877.8).available(true).build(),
                Desk.builder().name("453 Fenster").description("Links").floor(floor).x(1458.2).y(765.3).available(true).build(),
                Desk.builder().name("453 Wand").description("Links").floor(floor).x(1459.4).y(867.8).available(true).build(),
                Desk.builder().name("451/4").description("Focus").floor(floor).x(1267.2).y(646.9).available(true).build(),
                Desk.builder().name("451/3").description("Tisch").floor(floor).x(1203.4).y(583.1).available(true).build(),
                Desk.builder().name("451/2").description("Fenster").floor(floor).x(1130.9).y(518.1).available(true).build(),
                Desk.builder().name("451/1").description("Tür").floor(floor).x(977.2).y(496.9).available(true).build()
        );

        deskRepository.saveAll(desks);
    }

    private void createUsers() {
        // Admin-Benutzer erstellen
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .email("admin@example.com")
                .role(Role.builder().name(RoleName.ADMIN).build()) // Vorausgesetzt, eine ADMIN-Rolle existiert
                .build();
        userService.registerUser(admin);

        // Normaler Benutzer erstellen
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("user"))
                .email("user@example.com")
                .role(Role.builder().name(RoleName.USER).build()) // Vorausgesetzt, eine USER-Rolle existiert
                .build();
        userService.registerUser(user);
    }
}
