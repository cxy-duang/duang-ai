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

const defaultResult = {
  model: '',
  usage: {
    promptTokens: null,
    completionTokens: null,
    totalTokens: null
  },
  content: '',
  timeMillis: null
}

const result = reactive({...defaultResult})

const loading = ref(false)
// 防止连点
const computedDisabled = computed(() => {
  return loading.value
})

const uploadRef = ref()

const chat = (files) => {
  const p = formRef.value.validate()
  p.then(() => {
    const data = new FormData()
    data.append('system', form.data.system)
    data.append('user', form.data.user)
    files.forEach(file => {
      data.append('files', file)
    })

    Object.assign(result, defaultResult)

    loading.value = true
    const p = api.vl(data)
    p.then(res => {
      console.log('返回结果', res)
      Object.assign(result, res)
    }).finally(() => {
      loading.value = false
    })
  })
}

const editor = new MarkdownIt({
  breaks: true
})

const html = computed(() => {
  return editor.render(result.content)
})

const upload = ({type, files}) => {
  if (type === 'change') {
    uploadRef.value.clearFiles()
  }
  chat(files)
}
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
      <el-upload
        ref="uploadRef"
        :loading="loading"
        :auto-upload="false"
        :show-file-list="false"
        :action="''"
        :limit="1"
        :multiple="true"
        :accept="'.png, .jpg, .pdf'"
        :on-change="(file) => {
          upload({type: 'change', files: [file.raw]})
        }"
        :on-exceed="(files) => {
          upload({type: 'exceed', files: Array.from(files)})
        }"
        :disabled="computedDisabled"
      >
        <el-button :loading="loading" type="primary">Chat</el-button>
      </el-upload>
    </div>
    <div ref="scrollContainerRef" class="result-container">
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

.result-container {
  max-height: 500px;
  overflow: auto;
}
</style>
