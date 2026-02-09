<template>
  <div class="news-detail-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <el-button type="text" icon="Back" @click="goBack">返回</el-button>
          <span>新闻详情</span>
        </div>
      </template>

      <div v-if="news">
        <h2 class="news-title">{{ news.title }}</h2>
        <div class="news-meta">
          <span class="category-tag">{{ news.categoryName }}</span>
          <span class="publish-date">发布日期: {{ news.publishDate }}</span>
        </div>
        <div class="news-content" v-html="news.content"></div>
      </div>
      <AppEmpty v-else description="新闻不存在或已删除" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import AppEmpty from '@/components/AppEmpty.vue';

const route = useRoute();
const router = useRouter();
const news = ref(null);

// 模拟所有新闻数据，这里需要和 NewsList.vue 中的模拟数据保持一致
const allNews = [
  { id: 1, title: '校园招聘会通知', categoryId: 1, categoryName: '通知', publishDate: '2026-02-01', content: '<h3>校园招聘会通知</h3><p>具体招聘会时间、地点及参与企业列表，请关注就业指导中心官网通知。</p><p>请同学们提前准备简历，积极参加！</p>' },
  { id: 2, title: '图书馆闭馆公告', categoryId: 2, categoryName: '公告', publishDate: '2026-02-05', content: '<h3>图书馆闭馆公告</h3><p>因进行年度设备维护，图书馆将于2026年2月10日至2月15日期间闭馆，届时所有服务暂停。</p><p>请各位读者提前办理图书借阅手续，给您带来的不便敬请谅解！</p>' },
  { id: 3, title: '最新学术讲座预告', categoryId: 3, categoryName: '讲座', publishDate: '2026-02-08', content: '<h3>最新学术讲座预告</h3><p>主题：人工智能在智慧校园的应用</p><p>主讲人：张教授</p><p>时间：2026年3月1日 下午2:00</p><p>地点：学术报告厅</p><p>欢迎全校师生参加！</p>' },
  { id: 4, title: '春季运动会报名', categoryId: 4, categoryName: '活动', publishDate: '2026-01-28', content: '<h3>春季运动会报名</h3><p>2026年春季运动会即将开始，各项赛事报名工作已全面启动。</p><p>报名截止日期：2026年2月20日</p><p>详情请咨询体育部。</p>' },
  { id: 5, title: '学生会招新通知', categoryId: 1, categoryName: '通知', publishDate: '2026-01-20', content: '<h3>学生会招新通知</h3><p>有志于服务同学、锻炼能力的同学，欢迎加入学生会！</p><p>报名时间：2026年2月1日至2月15日</p><p>面试时间另行通知。</p>' },
  { id: 6, title: '关于寒假放假通知', categoryId: 2, categoryName: '公告', publishDate: '2026-01-15', content: '<h3>关于寒假放假通知</h3><p>根据学校校历安排，2026年寒假将于2026年1月20日开始，2026年2月28日结束。</p><p>请同学们提前做好出行安排。</p>' },
  { id: 7, title: '软件工程前沿技术讲座', categoryId: 3, categoryName: '讲座', publishDate: '2026-02-10', content: '<h3>软件工程前沿技术讲座</h3><p>特邀业内专家分享软件工程领域的最新技术进展和发展趋势。</p><p>时间：2026年3月10日 晚上7:00</p><p>地点：计算机学院报告厅</p><p>期待您的参与！</p>' },
  { id: 8, title: '英语角活动预告', categoryId: 4, categoryName: '活动', publishDate: '2026-02-09', content: '<h3>英语角活动预告</h3><p>每周三晚上8:00，在学校咖啡厅举办英语角活动。</p><p>欢迎所有对英语学习感兴趣的同学参加，共同提高口语能力！</p>' },
  { id: 9, title: '校医院调整作息时间', categoryId: 2, categoryName: '公告', publishDate: '2026-02-07', content: '<h3>校医院调整作息时间</h3><p>自2026年3月1日起，校医院将调整作息时间，具体详情请见校医院公告栏。</p><p>请大家注意调整就诊时间。</p>' },
  { id: 10, title: 'ACM编程大赛通知', categoryId: 1, categoryName: '通知', publishDate: '2026-02-03', content: '<h3>ACM编程大赛通知</h3><p>一年一度的ACM国际大学生程序设计竞赛校内选拔赛即将启动。</p><p>报名截止日期：2026年2月25日</p><p>期待您的挑战！</p>' },
];

const fetchNewsDetail = (id) => {
  // 模拟API调用，根据ID获取新闻详情
  news.value = allNews.find(item => item.id === parseInt(id));
};

const goBack = () => {
  router.back();
};

onMounted(() => {
  const newsId = route.params.id;
  if (newsId) {
    fetchNewsDetail(newsId);
  }
});
</script>

<style scoped>
.news-detail-container {
  padding: 20px;
}
.box-card {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}
.card-header .el-button {
  margin-right: 10px;
}
.news-title {
  text-align: center;
  margin-bottom: 20px;
  color: #303133;
}
.news-meta {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
  font-size: 14px;
  color: #909399;
}
.category-tag {
  background-color: #ecf5ff;
  color: #409eff;
  padding: 3px 8px;
  border-radius: 4px;
  margin-right: 10px;
}
.publish-date {
  font-style: italic;
}
.news-content {
  line-height: 1.8;
  color: #606266;
  font-size: 16px;
}
.news-content h3 {
  margin-top: 15px;
  margin-bottom: 10px;
  color: #303133;
}
.news-content p {
  margin-bottom: 10px;
}
</style>
