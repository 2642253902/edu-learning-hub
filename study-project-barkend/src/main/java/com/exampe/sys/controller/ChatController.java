package com.exampe.sys.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    private static final String BASE_SYSTEM = """
            这段系统提示词用于约束 AI 在本教学平台的回答边界、角色与表达风格，避免偏离学习支持场景。
            【你能做的】
            ·解答系统功能和页面使用问题（如：如何播放/暂停/进度跳转、切换字幕、调整倍速、下载资料）
            ·引导学习流程（如：如何开始课程、查看章节、提交作业、查看学习进度与成绩）
            ·帮助定位教学资源（如：查找章节视频、练习题、讲义、直播回放）
            ·协助排查播放或访问问题并给出可操作的建议（如：网络/浏览器/缓存排查），必要时建议联系人工客服或教师
            【你不能做的】
            ·不回答与本学习平台无关的问题；遇到此类问题请礼貌拒绝并指向通用帮助渠道
            ·不能替用户执行任何平台操作（如替用户提交作业、修改成绩或代为下载付费内容）
            ·不提供法律、医疗、财务等专业建议
            【回答风格】
            ·简洁友好、口语化，遇到操作类问题请分步列出可执行步骤
            ·默认不超过200字；若步骤较多可分条展示并标注重点
            ·对不确定或需要人工处理的问题，诚实说明并建议后续行动（例如联系教师或人工客服）
            """;

    public ChatController(ChatClient.Builder builder, ObjectMapper objectMapper) {
        // 通过 Builder 构造 ChatClient，保持 Spring 配置的灵活性，便于后续切换模型或参数。
        this.chatClient = builder.build();
        // SSE 返回的内容要手动转 JSON，这里复用统一的 ObjectMapper。
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamPost(
            @RequestBody(required = false) Map<String, Object> body,
            @RequestParam(required = false) String message,
            @RequestParam(defaultValue = "default") String sessionId) {
        // 同时兼容前端 JSON body 和 query 参数，方便不同调用方式复用同一接口。
        return streamInternal(resolveMessage(body, message), resolveSessionId(body, sessionId));
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(
            @RequestParam(required = false) String message,
            @RequestParam(defaultValue = "default") String sessionId) {
        // GET 形式主要给简单调试或轻量集成使用。
        return streamInternal(message, sessionId);
    }

    private SseEmitter streamInternal(String message, String sessionId) {
        // SSE 连接保活时间设置得稍长一些，避免模型响应慢时连接被提前断开。
        SseEmitter emitter = new SseEmitter(180_000L);

        if (!StringUtils.hasText(message)) {
            // 没有输入就直接返回错误事件，不进入模型调用，减少无效开销。
            sendError(emitter, "message不能为空", null);
            return emitter;
        }

        // 这里走流式输出，前端可以边收边渲染，体验上比等完整回复更自然。
        chatClient.prompt()
                .system(BASE_SYSTEM)
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, sessionId))
                .stream()
                .content()
                .subscribe(
                        chunk -> {
                            try {
                                sendChunk(emitter, chunk);
                            } catch (Exception e) {
                                sendError(emitter, "发送流式数据失败", e);
                            }
                        },
                        error -> sendError(emitter, "AI服务暂时不可用，请稍后重试", error),
                        () -> sendEnd(emitter)
                );

        return emitter;
    }

    private void sendChunk(SseEmitter emitter, String chunk) throws JsonProcessingException, java.io.IOException {
        // 单个 token / chunk 包装成统一结构，前端只需要关注 result 和 is_end。
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("code", 0);
        payload.put("result", chunk);
        payload.put("is_end", false);
        emitter.send(SseEmitter.event().data(toJson(payload)));
    }

    private void sendError(SseEmitter emitter, String message, Throwable error) {
        try {
            // 错误事件也保持和正常事件一致的 JSON 结构，前端处理逻辑更简单。
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("code", 4);
            payload.put("result", "");
            payload.put("is_end", true);
            payload.put("message", message);
            payload.put("error", error == null ? message : error.getMessage());
            emitter.send(SseEmitter.event().data(toJson(payload)));
        } catch (Exception ignored) {
        } finally {
            emitter.complete();
        }
    }

    private void sendEnd(SseEmitter emitter) {
        try {
            // 结束事件只负责通知前端收流完成，不再携带额外内容。
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("code", 0);
            payload.put("result", "");
            payload.put("is_end", true);
            emitter.send(SseEmitter.event().data(toJson(payload)));
        } catch (Exception ignored) {
        } finally {
            emitter.complete();
        }
    }

    private String resolveMessage(Map<String, Object> body, String message) {
        if (StringUtils.hasText(message)) {
            return message;
        }
        if (body == null || body.isEmpty()) {
            return null;
        }

        // 兼容多个常见字段名，减少前端传参格式不一致导致的对接成本。
        Object value = firstNonBlank(body, "message", "query", "prompt", "content", "text", "input");
        return value == null ? null : String.valueOf(value);
    }

    private String resolveSessionId(Map<String, Object> body, String sessionId) {
        if (StringUtils.hasText(sessionId)) {
            return sessionId;
        }
        if (body == null || body.isEmpty()) {
            return "default";
        }

        // 优先从请求体里找会话标识，保证上下文能够按用户或会话维度稳定隔离。
        Object value = firstNonBlank(body, "sessionId", "conversationId", "domainName", "domain", "userId");
        return value == null ? "default" : String.valueOf(value);
    }

    private Object firstNonBlank(Map<String, Object> body, String... keys) {
        for (String key : keys) {
            Object value = body.get(key);
            if (value != null && StringUtils.hasText(String.valueOf(value))) {
                return value;
            }
        }
        return null;
    }

    private String toJson(Map<String, Object> payload) throws JsonProcessingException {
        // 统一通过 Jackson 序列化，避免手写字符串拼接带来的转义问题。
        return objectMapper.writeValueAsString(payload);
    }
}
