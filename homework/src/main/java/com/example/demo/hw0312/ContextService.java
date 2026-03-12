package com.example.demo.hw0312;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ai.converter.BeanOutputConverter;

@Service
public class ContextService {
    private final ChatClient chatClient;
    private final MongoTemplate mongoTemplate;

    public ContextService(ChatClient.Builder builder, MongoTemplate mongoTemplate) {
        this.chatClient = builder.build();
        this.mongoTemplate = mongoTemplate;
    }

    @Async
    public void extractAndSaveContext(String userId, String message) {
        var converter = new BeanOutputConverter<>(CustomerServiceController.CustomerContext.class);
        String prompt = "提取订单号、商品名和地址：\n" + converter.getFormat() + "\n内容：" + message;
        
        try {
            var context = chatClient.prompt().user(prompt).call().entity(CustomerServiceController.CustomerContext.class);
            if (context != null) {
                var toSave = new CustomerServiceController.MongoCustomerContext(userId, context.orderId(), context.productName(), context.address());
                mongoTemplate.save(toSave, "customer_contexts");
            }
        } catch (Exception e) {
            System.err.println("Extraction failed: " + e.getMessage());
        }
    }
}