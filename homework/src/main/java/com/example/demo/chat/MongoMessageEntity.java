package com.example.demo.chat;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "chat_messages")
public class MongoMessageEntity {
    @Id
    private String id;
    private String conversationId;
    private String messageType; // 区分 USER, ASSISTANT, SYSTEM
    private String content;
    private Instant timestamp;

    public MongoMessageEntity(String conversationId, String messageType, String content) {
        this.conversationId = conversationId;
        this.messageType = messageType;
        this.content = content;
        this.timestamp = Instant.now();
    }

    // Getters
    public String getMessageType() { return messageType; }
    public String getContent() { return content; }
}