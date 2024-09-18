package br.com.fiap.prospai.service;

import io.spring.ai.openai.OpenAiService;
import io.spring.ai.openai.completion.ChatCompletionRequest;
import io.spring.ai.openai.completion.ChatMessage;
import io.spring.ai.openai.completion.ChatMessageRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class OpenAIService {

    private final OpenAiService openAiService;

    @Autowired
    public OpenAIService(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    public String generateMarketingContent(String prediction) {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(Collections.singletonList(
                        ChatMessage.builder()
                                .role(ChatMessageRole.USER)
                                .content(prediction)
                                .build()
                ))
                .build();

        return openAiService.createChatCompletion(request)
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();
    }
}
