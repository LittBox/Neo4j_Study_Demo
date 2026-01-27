<template>
  <div class="ai-qa-container">
    <div class="page-header">
      <h1 class="page-title">AI健康问答</h1>
      <p class="page-description">向AI咨询健康相关问题，获取专业建议</p>
    </div>
    
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>对话窗口</span>
          </template>
          <div class="chat-messages" ref="messagesRef">
            <div
              v-for="(message, index) in messages"
              :key="index"
              :class="['message-item', message.role]"
            >
              <div class="message-content">
                <div v-if="message.role === 'user'" class="message-text">
                  {{ message.content }}
                </div>
                <div v-else class="message-text" v-html="renderMarkdown(message.content)"></div>
              </div>
            </div>
            <div v-if="loading" class="message-item assistant">
              <div class="message-content">
                <div class="message-text">AI正在思考...</div>
              </div>
            </div>
          </div>
          <div class="chat-input">
            <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="3"
              placeholder="请输入您的问题..."
              @keydown.ctrl.enter="sendMessage"
            />
            <div class="input-actions">
              <el-button type="primary" @click="sendMessage" :loading="loading">
                发送
              </el-button>
              <el-button @click="clearMessages">清空</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>历史记录</span>
          </template>
          <div class="history-list">
            <div
              v-for="item in history"
              :key="item.id"
              class="history-item"
              @click="loadHistoryItem(item)"
            >
              <div class="history-question">{{ item.question }}</div>
              <div class="history-time">{{ formatTime(item.createTime) }}</div>
            </div>
            <el-empty v-if="history.length === 0" description="暂无历史记录" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { aiApi } from '@/api/ai'
import MarkdownIt from 'markdown-it'
import { ElMessage } from 'element-plus'

const md = new MarkdownIt()
const messages = ref([])
const history = ref([])
const inputMessage = ref('')
const loading = ref(false)
const messagesRef = ref(null)

onMounted(() => {
  loadHistoryList()
})

const loadHistoryList = async () => {
  try {
    const res = await aiApi.getHistory()
    history.value = res.data || []
  } catch (error) {
    console.error('加载历史记录失败', error)
  }
}

const sendMessage = async () => {
  if (!inputMessage.value.trim()) {
    ElMessage.warning('请输入问题')
    return
  }
  
  const question = inputMessage.value.trim()
  messages.value.push({
    role: 'user',
    content: question
  })
  
  inputMessage.value = ''
  loading.value = true
  
  // 构建历史对话
  const historyMessages = messages.value
    .filter(m => m.role === 'assistant')
    .slice(-5)
    .map(m => m.content)
  
  try {
    const res = await aiApi.chat({
      question,
      history: historyMessages
    })
    
    messages.value.push({
      role: 'assistant',
      content: res.data.answer
    })
    
    // 刷新历史记录
    loadHistoryList()
    
    // 滚动到底部
    await nextTick()
    scrollToBottom()
  } catch (error) {
    ElMessage.error('发送失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

const clearMessages = () => {
  messages.value = []
}

const loadHistoryItem = (item) => {
  messages.value = [
    {
      role: 'user',
      content: item.question
    },
    {
      role: 'assistant',
      content: item.answer
    }
  ]
}

const renderMarkdown = (text) => {
  return md.render(text || '')
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

const scrollToBottom = () => {
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}
</script>

<style scoped>
.ai-qa-container {
  width: 100%;
}

.chat-messages {
  height: 500px;
  overflow-y: auto;
  padding: 24px;
  background-color: #f9f9f9;
  border-radius: 8px;
  margin-bottom: 20px;
}

.message-item {
  margin-bottom: 24px;
}

.message-item.user {
  text-align: right;
}

.message-content {
  display: inline-block;
  max-width: 70%;
  text-align: left;
}

.message-item.user .message-content {
  background-color: var(--primary-color);
  color: #ffffff;
  padding: 12px 18px;
  border-radius: 12px;
}

.message-item.assistant .message-content {
  background-color: #ffffff;
  padding: 16px 20px;
  border-radius: 12px;
  border: 1px solid var(--border-color);
  line-height: 1.6;
}

.message-text {
  word-wrap: break-word;
  line-height: 1.6;
}

.message-text :deep(p) {
  margin: 8px 0;
}

.message-text :deep(ul),
.message-text :deep(ol) {
  margin: 8px 0;
  padding-left: 24px;
}

.message-text :deep(li) {
  margin: 4px 0;
}

.chat-input {
  margin-top: 20px;
  padding: 0 4px;
}

.input-actions {
  margin-top: 10px;
  text-align: right;
}

.history-list {
  max-height: 600px;
  overflow-y: auto;
}

.history-item {
  padding: 12px 16px;
  border-bottom: 1px solid var(--border-color);
  cursor: pointer;
  transition: background-color 0.3s;
}

.history-item:hover {
  background-color: var(--primary-light);
}

.history-question {
  font-size: 14px;
  color: var(--text-color);
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.5;
}

.history-time {
  font-size: 12px;
  color: var(--text-light);
}
</style>
