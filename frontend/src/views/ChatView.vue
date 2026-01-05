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
        <div class="markdown-content" v-html="renderedReply"></div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import MarkdownIt from 'markdown-it'

const md = new MarkdownIt({
  html: true,
  linkify: true,
  breaks: true
})

export default {
  name: 'ChatView',
  data() {
    return {
      prompt: '',
      reply: '',
      loading: false,
    }
  },
  computed: {
    renderedReply() {
      if (!this.reply) return '';
      try {
        return md.render(this.reply);
      } catch (e) {
        return md.render(`\`\`\`\n${this.reply}\n\`\`\``);
      }
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
          this.reply = `**Error:** ${data.error}`;
        } else {
          this.reply = '```json\n' + JSON.stringify(data, null, 2) + '\n```';
        }
      } catch (e) {
        this.reply = `**Request failed:** ${e.response?.data?.error || e.message}`;
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

/* Markdown 样式 */
.markdown-content {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
}

.markdown-content h1,
.markdown-content h2,
.markdown-content h3,
.markdown-content h4,
.markdown-content h5,
.markdown-content h6 {
  margin: 16px 0 8px 0;
  font-weight: bold;
}

.markdown-content h1 { font-size: 28px; }
.markdown-content h2 { font-size: 24px; }
.markdown-content h3 { font-size: 20px; }
.markdown-content h4 { font-size: 16px; }
.markdown-content h5 { font-size: 14px; }
.markdown-content h6 { font-size: 12px; }

.markdown-content p {
  margin: 8px 0;
}

.markdown-content code {
  background-color: #f5f5f5;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
}

.markdown-content pre {
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 10px;
  overflow-x: auto;
  margin: 8px 0;
}

.markdown-content pre code {
  background-color: transparent;
  padding: 0;
  border-radius: 0;
}

.markdown-content blockquote {
  border-left: 4px solid #ddd;
  margin: 8px 0;
  padding: 0 12px;
  color: #666;
}

.markdown-content ul,
.markdown-content ol {
  margin: 8px 0 8px 20px;
}

.markdown-content li {
  margin: 4px 0;
}

.markdown-content a {
  color: #0366d6;
  text-decoration: none;
}

.markdown-content a:hover {
  text-decoration: underline;
}

.markdown-content table {
  border-collapse: collapse;
  width: 100%;
  margin: 8px 0;
}

.markdown-content table th,
.markdown-content table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

.markdown-content table th {
  background-color: #f5f5f5;
  font-weight: bold;
}
</style>
