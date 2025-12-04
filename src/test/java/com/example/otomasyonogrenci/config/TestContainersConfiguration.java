package com.example.otomasyonogrenci.config;

import org.springframework.boot.test.context.TestConfiguration;

/**
 * TestContainers yapılandırması artık AbstractIntegrationTest base class'ında yapılıyor.
 * Bu sınıf geriye dönük uyumluluk için tutuluyor.
 */
@TestConfiguration
public class TestContainersConfiguration {
    // TestContainers yapılandırması AbstractIntegrationTest'te yapılıyor
}

