package com.github_repo_finder.config;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
public class GitHubConfig {

    @Value("${github.api.token}")
    private String githubApiToken;

    @Value("${github.api.url}")
    private String githubApiUrl;

}
