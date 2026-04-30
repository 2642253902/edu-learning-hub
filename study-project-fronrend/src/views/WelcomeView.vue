<template>
  <div class="welcome-container">
    <div class="left-side">
      <div class="logo-box">
        <el-icon class="logo-icon">
          <Reading />
        </el-icon>
        <span class="logo-text">EduLearn Hub</span>
      </div>
      <div class="welcome-info">
        <h2 class="welcome-title">开启您的在线学习之旅</h2>
        <p class="welcome-desc">在这里，您可以随时随地观看高质量课程视频、查阅课件，与同学们共同进步。</p>
        <div class="feature-list">
          <div class="feature-item">
            <el-icon>
              <VideoPlay />
            </el-icon>
            <span>海量视频课程资源</span>
          </div>
          <div class="feature-item">
            <el-icon>
              <Fold />
            </el-icon>
            <span>实时进度同步追踪</span>
          </div>
          <div class="feature-item">
            <el-icon>
              <Files />
            </el-icon>
            <span>专业课件讲义预览</span>
          </div>
        </div>
      </div>
    </div>
    <div class="right-side">
      <div class="auth-card">
        <router-view v-slot="{ Component }">
          <transition name="el-zoom-in-center" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Reading, VideoPlay, Fold, Files } from '@element-plus/icons-vue'
</script>

<style scoped>
.welcome-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  overflow: hidden;
  background: #f0f2f5;
  font-family: 'PingFang SC', 'Helvetica Neue', Helvetica, Arial, sans-serif;
}

.left-side {
  flex: 1.2;
  position: relative;
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
  display: flex;
  flex-direction: column;
  padding: 40px;
  color: white;
}

.logo-box {
  display: flex;
  align-items: center;
  gap: 12px;
  z-index: 2;
}

.logo-icon {
  font-size: 32px;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 1px;
}

.welcome-info {
  margin-top: auto;
  margin-bottom: auto;
  z-index: 2;
  max-width: 500px;
}

.welcome-title {
  font-size: 40px;
  margin-bottom: 24px;
  line-height: 1.2;
}

.welcome-desc {
  font-size: 18px;
  opacity: 0.9;
  line-height: 1.6;
  margin-bottom: 40px;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
}

.feature-item .el-icon {
  font-size: 20px;
  background: rgba(255, 255, 255, 0.2);
  padding: 8px;
  border-radius: 50%;
}

.left-side::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: url('https://gw.alipayobjects.com/zos/rmsportal/TVirScCBeYINnyiNGitT.svg');
  background-repeat: no-repeat;
  background-position: center 110px;
  background-size: 100%;
  opacity: 0.1;
}

.right-side {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #fff;
}

.auth-card {
  width: 100%;
  max-width: 400px;
  padding: 0 40px;
}

.footer-info {
  position: absolute;
  bottom: 24px;
  color: #8c8c8c;
  font-size: 14px;
}
</style>