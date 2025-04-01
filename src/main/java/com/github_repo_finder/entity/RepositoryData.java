package com.github_repo_finder.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "repositories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryData {
    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String owner;

    private String language;

    private int stars;

    private int forks;

    private Instant lastUpdated;
}
