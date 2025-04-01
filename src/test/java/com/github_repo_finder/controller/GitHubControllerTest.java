package com.github_repo_finder.controller;

import com.github_repo_finder.entity.RepositoryData;
import com.github_repo_finder.service.GitHubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class GitHubControllerTest {
    private MockMvc mockMvc;

    @Mock
    private GitHubService gitHubService;

    @InjectMocks
    private GitHubController gitHubController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(gitHubController).build();
    }

    @Test
    void testSearchRepositories() throws Exception {
        String query = "spring boot";
        String language = "Java";
        String sort = "stars";

        RepositoryData mockRepo = new RepositoryData(
                123456L, "spring-boot-example", "An example repository", "user123",
                "Java", 450, 120, Instant.parse("2024-01-01T12:00:00Z")
        );

        when(gitHubService.searchAndSaveRepositories(query, language, sort))
                .thenReturn(List.of(mockRepo));

        mockMvc.perform(post("/api/github/search")
                        .param("query", query)
                        .param("language", language)
                        .param("sort", sort)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("spring-boot-example"));
    }

    @Test
    void testGetRepositories() throws Exception {
        RepositoryData repo1 = new RepositoryData(1L, "Repo1", "Desc1", "Owner1", "Java", 500, 50, Instant.now());
        RepositoryData repo2 = new RepositoryData(2L, "Repo2", "Desc2", "Owner2", "Java", 400, 30, Instant.now());

        when(gitHubService.getRepositories("Java", 100, "stars")).thenReturn(List.of(repo1, repo2));

        mockMvc.perform(get("/api/github/repositories")
                        .param("language", "Java")
                        .param("minStars", "100")
                        .param("sort", "stars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
