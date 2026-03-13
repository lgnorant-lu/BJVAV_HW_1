package com.example.demo.hw0312;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cs")
@CrossOrigin(origins = "*")
public class CustomerServiceController {

    private final ChatClient chatClient;
    private final MongoDBChatMemory chatMemory;
    private final ContextService contextService; // 注入新服务

    public CustomerServiceController(ChatClient.Builder builder, 
                                   MongoDBChatMemory chatMemory, 
                                   ContextService contextService) {
        this.chatMemory = chatMemory;
        this.contextService = contextService;
        this.chatClient = builder
                .defaultSystem("你是一个企业级的智能服务助手。请礼貌、专业地回答用户的问题，提供高效的解决方案。")
                .build();
    }

    @PostMapping("/chat")
    public String chat(@RequestParam String userId, @RequestBody String userMessage) {
        var advisor = MessageChatMemoryAdvisor.builder(chatMemory)
                .conversationId(userId)
                .build();

        String aiResponse = chatClient.prompt()
                .user(userMessage)
                .advisors(advisor)
                .call()
                .content();

        // 调用外部服务的异步方法
        contextService.extractAndSaveContext(userId, userMessage);

        return aiResponse;
    }

    // 保留数据结构定义
    public record CustomerContext(String orderId, String productName, String address) {}
    public record MongoCustomerContext(String userId, String orderId, String productName, String address) {}
}