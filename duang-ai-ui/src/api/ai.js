export default {
  async chatStream(data, func, doneFunc) {
    const response = await fetch('/api/ai/chat/stream', {
      method: 'post',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    })

    if (!response.ok) {
      // 如果后台报错也视为完成
      doneFunc && doneFunc()
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder('utf-8')

    let buffer = ''; // 用于保存不完整的行

    while (true) {
      const { done, value } = await reader.read();
      if (done) {
        // 处理缓冲区中剩余的内容
        if (buffer) {
          processLine(buffer);
        }
        doneFunc && doneFunc();
        break;
      }

      // 将新接收的数据解码并添加到缓冲区
      buffer += decoder.decode(value, { stream: true });

      // 按换行符分割缓冲区内容
      const lines = buffer.split('\n');
      // 最后一个元素可能是不完整的行，放回缓冲区
      buffer = lines.pop() || '';

      // 处理完整的行
      lines.forEach(line => processLine(line));
    }

    function processLine (line) {
      if (line.startsWith('data:')) {
        try {
          const json = line.slice(5).trim();
          if (json) {
            const jsonObj = JSON.parse(json);
            func && func(jsonObj.c || '');
          }
        } catch (e) {
          console.error('解析SSE数据出错:', e, '行内容:', line);
        }
      }
    }

    // while (true) {
    //   const { done, value } = await reader.read()
    //   if (done) {
    //     doneFunc && doneFunc()
    //     break
    //   }
    //
    //   const datetime = new Date().getTime()
    //
    //   const chunk = decoder.decode(value)
    //   // 处理 SSE 格式数据（如 "data: {...}\n\n"）
    //   console.log('chunk.split chunk', chunk, datetime)
    //   console.log('chunk.split chunk.split', chunk.split('\n'), datetime)
    //   chunk.split('\n').forEach(line => {
    //     console.log('chunk.split line', line, datetime)
    //     if (line.startsWith('data:')) {
    //       try {
    //         const json = line.slice(5).trim()
    //         console.log('chunk.split json', json, datetime)
    //         if (json) {
    //           const jsonObj = JSON.parse(json)
    //           console.log('chunk.split jsonObj', jsonObj, datetime)
    //           func && func(jsonObj.c || '')
    //         }
    //       } catch (e) {
    //         console.error(e, line)
    //       }
    //     }
    //   })
    // }
  }
}
