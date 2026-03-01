package com.example.jwt_demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jwt_demo.model.AppSetting;
public interface AppSettingRepository extends JpaRepository<AppSetting, Long> {

    Optional<AppSetting> findByConfigKey(String key);
}