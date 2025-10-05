export default {
  chat(data) {
    return fetch('/api/ai/chat', {
      method: 'post',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    }).then(res => {
      return res.text()
    })
  },
  chatStream(data, chunkFunc) {
    const decoder = new TextDecoder('utf-8')

    return new Promise((resolve, reject) => {
      fetch('/api/ai/chat/stream', {
        method: 'post',
        dataType: "text/event-stream",
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
      }).then(response => {
        // 检查响应是否成功
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        // 返回一个可读流
        return response.body;
      }).then(body => {
        const reader = body.getReader();

        let buffer = ''
        // 读取数据流
        const read = () => {
          return reader.read().then(({done, value}) => {
            // 检查是否读取完毕
            if (done) {
              // console.log('已传输完毕');
              resolve()
              return;
            }

            buffer += decoder.decode(value, {stream: true});

            // 处理每个数据块
            // console.log('收到分块数据:', buffer);

            // 按换行符分割缓冲区内容
            const lines = buffer.split('\n');
            // 最后一个元素可能是不完整的行，放回缓冲区
            buffer = lines.pop() || '';

            // 处理完整的行
            lines.forEach(line => processLine(line));

            // 继续读取下一个数据块
            read();
          });
        }

        const processLine = (line) => {
          if (line.startsWith('data:')) {
            try {
              const json = line.slice(5).trim();
              if (json) {
                const jsonObj = JSON.parse(json);

                // 一次完整的数据
                // console.log('收到的完整数据:', jsonObj.c);

                chunkFunc && chunkFunc(jsonObj.c || '');
              }
            } catch (e) {
              console.error('解析SSE数据出错:', e, '行内容:', line);
            }
          }
        }

        // 开始读取数据流
        read();
      }).catch(error => {
        console.error('Fetch error:', error);
        reject(error)
      })
    })
  }
}
