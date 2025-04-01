package com.github_repo_finder.repository;

import com.github_repo_finder.entity.RepositoryData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface GitHubRepository extends JpaRepository<RepositoryData, Long> {

    List<RepositoryData> findByLanguageAndStarsGreaterThanEqual(String language, int minStars);

    List<RepositoryData> findByStarsGreaterThanEqual(int minStars);

    List<RepositoryData> findByLanguageAndForksGreaterThanEqual(String language, int minForks);

    List<RepositoryData> findByForksGreaterThanEqual(int minForks);

    List<RepositoryData> findByLanguageAndLastUpdatedAfter(String language, Instant minUpdated);

    List<RepositoryData> findByLastUpdatedAfter(Instant minUpdated);
}
