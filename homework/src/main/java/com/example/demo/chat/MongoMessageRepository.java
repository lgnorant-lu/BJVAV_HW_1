package com.example.demo.chat;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MongoMessageRepository extends MongoRepository<MongoMessageEntity, String> {
    // 根据会话ID查询，按时间戳倒序排列，用于获取最近的 N 条消息
    List<MongoMessageEntity> findByConversationIdOrderByTimestampDesc(String conversationId, Pageable pageable);
    
    // 清空特定会话的历史记录
    void deleteByConversationId(String conversationId);
}