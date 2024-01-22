package ru.heumn.repository.entities;

import jakarta.persistence.*;
import ru.heumn.repository.enums.PlaneType;
import ru.heumn.repository.enums.SeatType;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
public class PlaneEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    String name;

    PlaneType type;

    Instant DateLastCheck;

    @ElementCollection
    Map<SeatType,Integer> Seats;
}
