package ru.practicum.ewm.server.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uq_email", columnNames = {"email"})
})
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 254)
    private String email;
    @Column(nullable = false, length = 250)
    private String name;
}
