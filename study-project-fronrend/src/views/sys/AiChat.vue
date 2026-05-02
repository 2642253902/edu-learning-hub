<template>
    <div id="app">
        <!-- 该组件由第三方聊天球插件渲染，当前页面只负责把后端地址和业务参数传进去。 -->
        <SuspendedBallChat :url="apiUrl" :app-name="appName" :domain-name="domainName" :enable-streaming="true"
            :enable-context="true" :enable-local-storage="true" :enable-voice-input="true" :callbacks="callbacks" />
    </div>
</template>

<script>
import { SuspendedBallChat } from 'ai-suspended-ball-chat'
import axios from 'axios'

export default {
    name: 'App',
    components: {
        SuspendedBallChat
    },
    data() {
        // 统一从 axios 的 baseURL 取后端地址，避免本地和部署环境写死不同域名。
        const base = (axios.defaults.baseURL || '').replace(/\/$/, '')
        // 这里先用固定会话标识，后续如果接入真实登录态，可替换成当前用户 ID 或 token 解析结果。
        const sessionId = 'user123'

        // 这些回调主要用于埋点、日志和异常观察，不介入插件内部的消息发送逻辑。
        const callbacks = {
            onUserMessage: (message) => {
                console.log('用户发送消息:', message)
                // 组件会根据 url 自行发起请求，这里只保留业务侧埋点。
            },
            onAssistantMessage: (message, res) => {
                console.log('AI回复:', message, res)
            },
            onError: (error) => {
                console.error('发生错误:', error)
            }
        }

        return {
            // 后端流式接口地址，保持和 SecurityConfiguration、ChatController 的路径一致。
            apiUrl: base ? `${base}/api/chat/stream` : '/api/chat/stream',
            // 插件用于展示的应用名称，可根据实际项目品牌调整。
            appName: 'my-app',
            // domainName 在这里承担会话隔离标识的作用，确保上下文不串会话。
            domainName: sessionId,
            callbacks,
        }
    }
}
</script>