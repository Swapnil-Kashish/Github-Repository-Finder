package com.github_repo_finder.service;
import com.github_repo_finder.config.GitHubConfig;
import com.github_repo_finder.entity.RepositoryData;
import com.github_repo_finder.repository.GitHubRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GitHubServiceTest {
    @Mock
    private GitHubRepository gitHubRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private GitHubConfig gitHubConfig;

    @InjectMocks
    private GitHubService gitHubService;

    @BeforeEach
    void setUp() {
        lenient().when(gitHubConfig.getGithubApiUrl()).thenReturn("https://api.github.com/search/repositories");
        lenient().when(gitHubConfig.getGithubApiToken()).thenReturn("test-token");
    }

    @Test
    void testSearchAndSaveRepositories() {
        String query = "spring boot";
        String language = "Java";
        String sort = "stars";
        String url = "https://api.github.com/search/repositories?q=spring boot+language:Java&sort=stars";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer test-token");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        RepositoryData mockRepo = new RepositoryData(
                123456L, "spring-boot-example", "An example repository", "user123",
                "Java", 450, 120, Instant.parse("2024-01-01T12:00:00Z")
        );

        List<Map<String, Object>> mockResponse = List.of(Map.of(
                "id", 123456,
                "name", "spring-boot-example",
                "description", "An example repository",
                "owner", Map.of("login", "user123"),
                "language", "Java",
                "stargazers_count", 450,
                "forks_count", 120,
                "updated_at", "2024-01-01T12:00:00Z"
        ));

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(Map.of("items", mockResponse), HttpStatus.OK));

        when(gitHubRepository.findById(123456L)).thenReturn(Optional.empty());
        when(gitHubRepository.save(any(RepositoryData.class))).thenReturn(mockRepo);
        when(gitHubRepository.findAll()).thenReturn(List.of(mockRepo)); // ✅ Fix added

        List<RepositoryData> savedRepos = gitHubService.searchAndSaveRepositories(query, language, sort);


        assertFalse(savedRepos.isEmpty()); // ✅ Should now pass
        assertEquals(1, savedRepos.size());
        assertEquals("spring-boot-example", savedRepos.get(0).getName());

        verify(gitHubRepository, times(1)).save(any(RepositoryData.class));
    }

    @Test
    void testGetRepositories_ByStars() {
        RepositoryData repo1 = new RepositoryData(1L, "Repo1", "Desc1", "Owner1", "Java", 500, 50, Instant.now());
        RepositoryData repo2 = new RepositoryData(2L, "Repo2", "Desc2", "Owner2", "Java", 400, 30, Instant.now());

        when(gitHubRepository.findByLanguageAndStarsGreaterThanEqual("Java", 100))
                .thenReturn(Arrays.asList(repo1, repo2));

        List<RepositoryData> result = gitHubService.getRepositories("Java", 100, "stars");

        assertEquals(2, result.size());
        verify(gitHubRepository, times(1))
                .findByLanguageAndStarsGreaterThanEqual("Java", 100);
    }

    @Test
    void testGetRepositories_ByForks() {
        RepositoryData repo = new RepositoryData(3L, "Repo3", "Desc3", "Owner3", "Python", 200, 20, Instant.now());

        when(gitHubRepository.findByForksGreaterThanEqual(10))
                .thenReturn(List.of(repo));

        List<RepositoryData> result = gitHubService.getRepositories(null, 10, "forks");

        assertEquals(1, result.size());
        verify(gitHubRepository, times(1)).findByForksGreaterThanEqual(10);
    }
}
