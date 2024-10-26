package com.univ.tracedin.api.global.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        servers = {
            @Server(url = "https://tracedin.p-e.kr", description = "Dev Server"),
            @Server(url = "https://localhost:8080", description = "Local Server")
        })
@Configuration
public class SwaggerConfig {}
