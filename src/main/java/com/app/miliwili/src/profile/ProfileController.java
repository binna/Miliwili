package com.app.miliwili.src.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/profiles")
public class ProfileController {
    private final Environment environment;

    /**
     * 어떤 프로필을 쓰고 있는지 확인
     */
    @GetMapping
    public String proflie(){
        final List<String> profiles = Arrays.asList(environment.getActiveProfiles());
        final List<String> prodProfiles = Arrays.asList("release");
        final String defaultProfile = profiles.get(0);

        return Arrays.stream(environment.getActiveProfiles())
                .filter(prodProfiles::contains)
                .findAny()
                .orElse(defaultProfile);
    }
}
