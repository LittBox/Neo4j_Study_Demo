<template>
  <div class="wrapper">
    <div class="chatting-box">
      <div class="chatting-header">
        <h2>Medical Q&A System</h2>
      </div>
      <div class="controls">
        <textarea v-model="prompt" placeholder="输入你的问题..."></textarea>
        <button @click="send" :disabled="loading">{{ loading ? '发送中...' : '发送' }}</button>
      </div>
      <div class="reply-box">
        <pre>{{ reply }}</pre>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ChatView',
  data() {
    return {
      prompt: '',
      reply: '',
      loading: false,
    }
  },
  methods: {
    async send() {
      if (!this.prompt.trim()) return;
      this.loading = true;
      this.reply = '';
      try {
        const res = await axios.post('http://localhost:8080/api/chat', { prompt: this.prompt });
        const data = res.data;
        if (data && data.success) {
          this.reply = data.content || JSON.stringify(data, null, 2);
        } else if (data && data.error) {
          this.reply = `Error: ${data.error}`;
        } else {
          this.reply = JSON.stringify(data, null, 2);
        }
      } catch (e) {
        this.reply = `Request failed: ${e.response?.data?.error || e.message}`;
      } finally {
        this.loading = false;
      }
    }
  }
}
</script>

<style scoped>
.wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
.chatting-box {
  width: 600px;
  height: 600px;
  border: 1px solid #ccc;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}
.chatting-header {
  padding: 12px;
  border-bottom: 1px solid #eee;
}
.controls {
  padding: 12px;
}
.controls textarea {
  width: 100%;
  height: 100px;
  resize: vertical;
}
.controls button {
  margin-top: 8px;
}
.reply-box {
  padding: 12px;
  border-top: 1px solid #eee;
  overflow: auto;
  flex: 1;
}
</style>
