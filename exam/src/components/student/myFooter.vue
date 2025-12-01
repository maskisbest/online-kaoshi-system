<!--页脚部分-->
<template>
  <footer id="footer" class="glass-panel footer-card">
    <ul>
      <li><a href="javascript:;">关于我们</a></li>
      <li><a href="javascript:;">免责声明</a></li>
      <li><a href="javascript:;">使用协议</a></li>
      <li><a href="http://beian.miit.gov.cn/" target="_blank">粤ICP备2020号</a></li>
      <li>@Copyright 2020-2023. ALL Rights Reserved</li>
    </ul>
  </footer>
</template>

<script>
export default {
  name: "myFooter"
}
</script>

<style scoped>
#footer {
  margin-top: 24px;
  padding: 24px;
  text-align: center;
}

#footer ul {
  margin: 0;
  padding: 0;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 16px;
}

#footer ul li,
#footer a {
  color: var(--text-muted);
  font-size: 13px;
  letter-spacing: 0.02em;
}

#footer a:hover {
  color: var(--accent);
}
</style>
