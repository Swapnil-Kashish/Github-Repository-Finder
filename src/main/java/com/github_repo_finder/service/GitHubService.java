package com.github_repo_finder.service;
import com.github_repo_finder.config.GitHubConfig;
import com.github_repo_finder.repository.GitHubRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.github_repo_finder.entity.RepositoryData;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GitHubService {
    private final GitHubConfig gitHubConfig;
    private final GitHubRepository gitHubRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public GitHubService(GitHubConfig gitHubConfig, GitHubRepository gitHubRepository,RestTemplate restTemplate) {
        this.gitHubConfig = gitHubConfig;
        this.gitHubRepository = gitHubRepository;
        this.restTemplate = restTemplate;
    }
    @Transactional
    public List<RepositoryData> searchAndSaveRepositories(String query, String language, String sort) {
        String url = gitHubConfig.getGithubApiUrl() + "?q=" + query;

        if (language != null && !language.isEmpty()) {
            url += "+language:" + language;
        }
        if (sort != null) {
            url += "&sort=" + sort;
        }

        // Set request headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + gitHubConfig.getGithubApiToken());
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Call GitHub API
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Failed to fetch data from GitHub API");
        }

        List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");

        for (Map<String, Object> item : items) {
            Long repoId = Long.valueOf(item.get("id").toString());

            // Check if repository already exists in DB
            Optional<RepositoryData> existingRepo = gitHubRepository.findById(repoId);

            RepositoryData repo = existingRepo.orElse(new RepositoryData());
            repo.setId(repoId);
            repo.setName((String) item.get("name"));
            repo.setDescription(item.get("description") != null ? item.get("description").toString() : "No description");
            repo.setOwner(((Map<String, Object>) item.get("owner")).get("login").toString());
            repo.setLanguage(item.get("language") != null ? item.get("language").toString() : "Unknown");
            repo.setStars((int) item.get("stargazers_count"));
            repo.setForks((int) item.get("forks_count"));
            repo.setLastUpdated(Instant.parse((String) item.get("updated_at")));

            // Save or update the repository
            gitHubRepository.save(repo);
        }

        return gitHubRepository.findAll();
    }

    public List<RepositoryData> getRepositories(String language, int minStars, String sort) {
        if (sort == null || sort.isEmpty()) {
            sort = "stars"; // Default sorting
        }

        switch (sort) {
            case "forks":
                return (language != null && !language.isEmpty())
                        ? gitHubRepository.findByLanguageAndForksGreaterThanEqual(language, minStars)
                        : gitHubRepository.findByForksGreaterThanEqual(minStars);
            case "updated":
                Instant minUpdated = Instant.now().minusSeconds(minStars * 86400L); // Convert days to seconds
                return (language != null && !language.isEmpty())
                        ? gitHubRepository.findByLanguageAndLastUpdatedAfter(language, minUpdated)
                        : gitHubRepository.findByLastUpdatedAfter(minUpdated);
            case "stars":
            default:
                return (language != null && !language.isEmpty())
                        ? gitHubRepository.findByLanguageAndStarsGreaterThanEqual(language, minStars)
                        : gitHubRepository.findByStarsGreaterThanEqual(minStars);
        }
    }
}
