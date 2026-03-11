package com.example.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class HomeworkController {

    private final ChatClient chatClient;

    public HomeworkController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    // ==========================================
    // 作业一：混乱文本整理 API
    // 接口地址: POST /api/task1
    // ==========================================
    @PostMapping("/task1")
    public String formatText(@RequestBody String rawText) {
        String prompt = "请作为专业的排版助手，将以下混乱的文本整理成清晰的Markdown格式，包含适当的标题、序号和分段：\n" + rawText;
        
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    // ==========================================
    // 作业二：非结构化文本转 JSON API
    // 接口地址: POST /api/task2
    // ==========================================
    
    // 定义 JSON 数据结构 (Java Record)
    public record JobRequirement(String jobTitle, String salaryRange, List<String> skillsNeeded) {}

    @PostMapping("/task2")
    public JobRequirement extractJson(@RequestBody String rawText) {
        // 结构化输出转换器
        var converter = new BeanOutputConverter<>(JobRequirement.class);
        String formatRequirement = converter.getFormat(); // 自动生成要求 AI 输出 JSON 的 Prompt

        String prompt = "请从以下招聘 JD 文本中提取关键信息。\n" 
                      + formatRequirement + "\n文本内容：\n" + rawText;

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        // 将返回的 JSON 字符串自动转换为 Java 对象
        return converter.convert(response);
    }
}