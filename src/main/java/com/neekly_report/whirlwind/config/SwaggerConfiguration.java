package com.neekly_report.whirlwind.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI openAPI() {
        Server server = new Server();   // API 서버 설정
        server.setUrl("/");

        Info info = new Info()
                .version("v1.0") //버전
                .title("Backend API") //이름
                .description("Neekly Report API"); //설명

        return new OpenAPI()
                .components(new Components())
                .info(info)
                .servers(List.of(server));
    }

}
