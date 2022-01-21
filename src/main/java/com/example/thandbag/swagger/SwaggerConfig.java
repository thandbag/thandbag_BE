package com.example.thandbag.swagger;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final String API_NAME = "Thandbag API";
    private static final String API_DESCRIPTION = "Thandbag API";

    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())         /* 스웨거 정보 등록 */
                .forCodeGeneration(true)
                .genericModelSubstitutes(ResponseEntity.class)
                .ignoredParameterTypes(java.sql.Date.class)
                .securityContexts(Lists.newArrayList(securityContext()))
                .securitySchemes(Lists.newArrayList(apiKey()))
                .useDefaultResponseMessages(true)
                .select()
                .apis(RequestHandlerSelectors
                        .basePackage("com.example.thandbag.controller"))
                .paths(PathSelectors.any()) /* 정해진 path만 보여짐 */
                .build();
    }

    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(API_NAME)
                .description(API_DESCRIPTION)
                .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT",
                "Authorization",
                "header");
    }

    private SecurityContext securityContext() {
        return springfox.documentation.spi.service.contexts.SecurityContext
                .builder()
                .securityReferences(defaultAuth()).forPaths(PathSelectors.any())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope(
                "global", "accessEverything");

        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference(
                "JWT", authorizationScopes));
    }
}