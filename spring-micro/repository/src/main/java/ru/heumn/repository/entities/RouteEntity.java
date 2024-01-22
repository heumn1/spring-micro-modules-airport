package ru.heumn.repository.entities;

import jakarta.persistence.*;

import java.util.Map;
import java.util.UUID;

@Entity
public class RouteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ElementCollection
    Map<String, String> stations;
}
