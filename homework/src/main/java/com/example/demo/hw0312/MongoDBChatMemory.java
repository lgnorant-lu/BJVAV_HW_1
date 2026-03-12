package com.example.demo.hw0312;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MongoDBChatMemory implements ChatMemory {

    private final MongoMessageRepository repository;

    public MongoDBChatMemory(MongoMessageRepository repository) {
        this.repository = repository;
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        List<MongoMessageEntity> entities = new ArrayList<>();
        for (Message message : messages) {
            String messageType = getMessageType(message);
            entities.add(new MongoMessageEntity(conversationId, messageType, message.getText()));
        }
        repository.saveAll(entities);
    }

    @Override
    public List<Message> get(String conversationId) {
        // 接口不传 lastN ，默认查最近 100 条
        List<MongoMessageEntity> entities = repository.findByConversationIdOrderByTimestampDesc(
                conversationId, PageRequest.of(0, 100));
        
        List<Message> messages = new ArrayList<>();
        for (MongoMessageEntity entity : entities) {
            switch (entity.getMessageType()) {
                case "USER":
                    messages.add(new UserMessage(entity.getContent()));
                    break;
                case "ASSISTANT":
                    messages.add(new AssistantMessage(entity.getContent()));
                    break;
                case "SYSTEM":
                    messages.add(new SystemMessage(entity.getContent()));
                    break;
            }
        }
        Collections.reverse(messages);
        return messages;
    }

    @Override
    public void clear(String conversationId) {
        repository.deleteByConversationId(conversationId);
    }

    private String getMessageType(Message message) {
        if (message instanceof UserMessage) return "USER";
        if (message instanceof AssistantMessage) return "ASSISTANT";
        if (message instanceof SystemMessage) return "SYSTEM";
        return "UNKNOWN";
    }
}