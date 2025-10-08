<script setup>
import api from '@/api/ai.js'
import {computed, reactive, ref} from 'vue'
import MarkdownIt from 'markdown-it'
import {MdEditor} from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';

const form = reactive({
  rules: {
    user: [{
      required: true,
      trigger: 'blur',
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('用户输入不能为空'))
        }

        callback()
      }
    }]
  },
  data: {
    system: '',
    user: ''
  }
})

const formRef = ref()
const scrollContainerRef = ref()

const result = ref('')

const loading = ref(false)

const chat = () => {
  const p = formRef.value.validate()
  p.then(() => {
    const data = form.data
    result.value = ''

    loading.value = true
    api.chatStream(data, c => {
      result.value += c

      // 文字超出时滚动条随动
      const container = scrollContainerRef.value
      container.scrollTop = container.scrollHeight
    }).finally(() => {
      loading.value = false
    })
  })
}

const editor = new MarkdownIt({
  breaks: true
})

const html = computed(() => {
  return editor.render(result.value)
})
</script>

<template>
  <div class="container">
    <div>
      <el-form ref="formRef" :model="form.data" :rules="form.rules" label-width="auto">
        <el-form-item label="提示词" prop="system">
          <md-editor v-model="form.data.system" style="height: 300px"/>
        </el-form-item>
        <el-form-item label="用户输入" prop="user">
          <md-editor v-model="form.data.user" style="height: 300px"/>
        </el-form-item>
      </el-form>
      <el-button @click="chat" :loading="loading" type="primary">Chat</el-button>
    </div>
    <div ref="scrollContainerRef" class="result-container">
      <div v-html="html"/>
    </div>
  </div>
</template>

<style scoped>
@media (min-width: 1024px) {
  .container {
    min-height: 100vh;
  }
}

.result-container{
  max-height: 300px;
  overflow: auto;
}
</style>
