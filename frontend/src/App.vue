<template>
  <router-view />
</template>

<script setup>
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

:root {
  --primary: #6366f1;
  --primary-hover: #4f46e5;
  --primary-light: rgba(99, 102, 241, 0.1);
  --sidebar-bg: #0f0f10;
  --sidebar-hover: rgba(255, 255, 255, 0.06);
  --sidebar-active: rgba(99, 102, 241, 0.15);
  --sidebar-text: rgba(255, 255, 255, 0.7);
  --sidebar-text-active: #fff;
  --content-bg: #fafafa;
  --card-bg: #ffffff;
  --border: rgba(0, 0, 0, 0.08);
  --text-primary: #111827;
  --text-secondary: #6b7280;
  --text-muted: #9ca3af;
  --success: #10b981;
  --warning: #f59e0b;
  --danger: #ef4444;
  --shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.04);
  --shadow-md: 0 4px 12px rgba(0, 0, 0, 0.08);
  --shadow-lg: 0 12px 40px rgba(0, 0, 0, 0.12);
  --radius-sm: 6px;
  --radius-md: 10px;
  --radius-lg: 16px;
}

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background-color: var(--content-bg);
  color: var(--text-primary);
  font-size: 14px;
  line-height: 1.6;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* Element Plus 覆盖样式 */
.el-button--primary {
  background: var(--primary) !important;
  border-color: var(--primary) !important;
}
.el-button--primary:hover {
  background: var(--primary-hover) !important;
  border-color: var(--primary-hover) !important;
}

.el-input__wrapper {
  border-radius: var(--radius-sm) !important;
  box-shadow: 0 0 0 1px var(--border) !important;
}
.el-input__wrapper:hover {
  box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.15) !important;
}
.el-input__wrapper.is-focus {
  box-shadow: 0 0 0 2px var(--primary) !important;
}

.el-card {
  border: none !important;
  border-radius: var(--radius-md) !important;
  box-shadow: var(--shadow-sm) !important;
}

.el-dialog {
  border-radius: var(--radius-lg) !important;
  overflow: hidden;
}
.el-dialog__header {
  border-bottom: 1px solid var(--border);
  padding: 20px 24px !important;
  margin: 0 !important;
}
.el-dialog__body {
  padding: 24px !important;
}
.el-dialog__footer {
  border-top: 1px solid var(--border);
  padding: 16px 24px !important;
}

.el-table {
  --el-table-border-color: var(--border);
  --el-table-header-bg-color: #f9fafb;
}
.el-table th.el-table__cell {
  font-weight: 600 !important;
  color: var(--text-secondary) !important;
  font-size: 12px !important;
  text-transform: uppercase !important;
  letter-spacing: 0.5px !important;
}

.el-tag {
  border-radius: 20px !important;
  border: none !important;
  font-weight: 500 !important;
}

.el-message-box {
  border-radius: var(--radius-lg) !important;
  border: none !important;
}

/* 滚动条美化 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
::-webkit-scrollbar-track {
  background: transparent;
}
::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.15);
  border-radius: 3px;
}
::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.25);
}
</style>
