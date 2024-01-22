package ru.heumn.repository.entities;

import jakarta.persistence.*;
import ru.heumn.repository.enums.SeatType;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
public class FlightEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ElementCollection
    Map<SeatType,Integer> Cost;

    Instant dateStart;

    Instant dateFinish;

    @ManyToOne
    PlaneEntity plane;

//    Route route;


}
