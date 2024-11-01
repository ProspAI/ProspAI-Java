import lombok.Value;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfig {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.endpoint}")
    private String endpoint;

    @Bean
    public ChatClient chatClient() {
        return ChatClient.builder()
                .apiKey(apiKey)
                .baseUrl(endpoint + "openai/")
                .build();
    }
}
