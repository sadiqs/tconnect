package io.github.sadiqs.tconnect.project.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Data
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Customer implements AppUser {
    @Id
    @GeneratedValue
    UUID id;
    String username;
    String name;

    @OneToMany(mappedBy = "customer")
    List<Project> projects = new ArrayList<>();
}
