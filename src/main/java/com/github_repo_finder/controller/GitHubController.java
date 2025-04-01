package com.github_repo_finder.controller;


import com.github_repo_finder.entity.RepositoryData;
import com.github_repo_finder.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GitHubController {
    @Autowired
    GitHubService gitHubService;

    @PostMapping("/search")
    public List<RepositoryData> searchRepositories(@RequestParam String query,
                                                   @RequestParam(required = false) String language,
                                                   @RequestParam(required = false) String sort) {
        return gitHubService.searchAndSaveRepositories(query, language, sort);
    }

    @GetMapping("/repositories")
    public List<RepositoryData> getRepositories(@RequestParam(required = false) String language,
                                                @RequestParam(required = false, defaultValue = "0") int minStars,
                                                @RequestParam(required = false, defaultValue = "stars") String sort) {
        return gitHubService.getRepositories(language, minStars, sort);
    }
}
