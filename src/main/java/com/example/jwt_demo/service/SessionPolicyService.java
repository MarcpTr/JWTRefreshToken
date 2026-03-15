package com.example.jwt_demo.service;

import org.springframework.stereotype.Service;
import com.example.jwt_demo.model.AppSetting;
import com.example.jwt_demo.repository.AppSettingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionPolicyService {

    private final AppSettingRepository repository;

    public int getMaxSessions() {
        return repository.findByConfigKey("security.max_sessions")
                .map(setting -> Integer.parseInt(setting.getConfigValue()))
                .orElse(1);
    }
    @Transactional
public void updateMaxSessions(int value) {
    if (value < 1) {
        throw new IllegalArgumentException("maxSessions must be >= 1");
    }

    AppSetting setting = repository.findByConfigKey("security.max_sessions")
            .orElseThrow(() -> new RuntimeException("Setting not found"));

    setting.setConfigValue(String.valueOf(value));
    repository.save(setting);
}
}