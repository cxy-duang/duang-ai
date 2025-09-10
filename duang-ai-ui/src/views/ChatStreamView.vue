<script setup>
import api from '@/api/ai.js'
import {computed, reactive, ref} from 'vue'
import MarkdownIt from 'markdown-it'
import {MdEditor} from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';

const form = reactive({
  data: {
    system: '',
    user: ''
  }
})

const result = ref('')

const loading = ref(false)

const chat = () => {
  const data = form.data

  result.value = ''

  loading.value = true
  api.chatStream(data, c => {
    result.value += c
  }).finally(() => {
    loading.value = false
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
      <el-form :model="form.data" label-width="auto">
        <el-form-item label="提示词">
          <md-editor v-model="form.data.system" style="height: 300px"/>
        </el-form-item>
        <el-form-item label="用户输入">
          <md-editor v-model="form.data.user" style="height: 300px"/>
        </el-form-item>
      </el-form>
      <el-button @click="chat" :loading="loading" type="primary">Chat</el-button>
    </div>
    <div>
      <div v-html="html"/>
    </div>
  </div>
</template>

<style>
@media (min-width: 1024px) {
  .container {
    min-height: 100vh;
  }
}
</style>
