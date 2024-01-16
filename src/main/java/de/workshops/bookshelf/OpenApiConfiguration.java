package de.workshops.bookshelf;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@Profile("dev")
public class OpenApiConfiguration {
//    @Autowired
//    OpenAiProperties openAiProperties;
    @Bean
    public OpenAPI api(OpenAiProperties openAiProperties) {
        return new OpenAPI()
                .info(
                        new Info()
                                .title(openAiProperties.getTitle())
                                .version(openAiProperties.getVersion())
                                .description("The capacity is %d books.".formatted(openAiProperties.getCapacity()))
                                .license(new License()
                                        .name(openAiProperties.getLicense().getName())
                                        .url(openAiProperties.getLicense().getUrl().toString())
                                )
                );
    }
}
