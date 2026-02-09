<template>
  <div class="news-list-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>校园新闻</span>
        </div>
      </template>

      <div class="filter-section">
        <el-select v-model="selectedCategory" placeholder="选择分类" clearable @change="fetchNewsList">
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          ></el-option>
        </el-select>
        <el-input
          v-model="searchQuery"
          placeholder="搜索新闻标题"
          clearable
          @clear="fetchNewsList"
          @keyup.enter="fetchNewsList"
          style="width: 200px; margin-left: 10px;"
        ></el-input>
        <el-button type="primary" style="margin-left: 10px;" @click="fetchNewsList">搜索</el-button>
      </div>

      <el-row :gutter="20">
        <el-col :span="18">
          <div class="news-items">
            <el-card
              v-for="news in paginatedNewsList"
              :key="news.id"
              class="news-item-card"
              shadow="hover"
              @click="goToNewsDetail(news.id)"
            >
              <div class="news-item-content">
                <h3>{{ news.title }}</h3>
                <div class="news-meta">
                  <span class="category-tag">{{ news.categoryName }}</span>
                  <span class="publish-date">{{ news.publishDate }}</span>
                </div>
                <p class="news-summary">{{ news.content.substring(0, 100) }}...</p>
              </div>
            </el-card>
            <AppEmpty v-if="paginatedNewsList.length === 0" description="暂无新闻" />
          </div>
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="currentPage"
            :page-sizes="[5, 10, 15, 20]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="filteredNewsList.length"
            v-if="filteredNewsList.length > 0"
          ></el-pagination>
        </el-col>

        <el-col :span="6">
          <el-card class="hot-news-card">
            <template #header>
              <div class="card-header">
                <span>热门新闻</span>
              </div>
            </template>
            <ul class="hot-news-list">
              <li v-for="hotNews in hotNewsList" :key="hotNews.id" @click="goToNewsDetail(hotNews.id)">
                {{ hotNews.title }}
              </li>
            </ul>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import AppEmpty from '@/components/AppEmpty.vue';

const router = useRouter();

const newsList = ref([]);
const categories = ref([]);
const hotNewsList = ref([]);

const selectedCategory = ref(null);
const searchQuery = ref('');

const currentPage = ref(1);
const pageSize = ref(10);

// 模拟获取新闻列表
const fetchNewsList = () => {
  // 模拟API调用
  newsList.value = [
    { id: 1, title: '校园招聘会通知', categoryId: 1, categoryName: '通知', publishDate: '2026-02-01', content: '详细内容1...' },
    { id: 2, title: '图书馆闭馆公告', categoryId: 2, categoryName: '公告', publishDate: '2026-02-05', content: '详细内容2...' },
    { id: 3, title: '最新学术讲座预告', categoryId: 3, categoryName: '讲座', publishDate: '2026-02-08', content: '详细内容3...' },
    { id: 4, title: '春季运动会报名', categoryId: 4, categoryName: '活动', publishDate: '2026-01-28', content: '详细内容4...' },
    { id: 5, title: '学生会招新通知', categoryId: 1, categoryName: '通知', publishDate: '2026-01-20', content: '详细内容5...' },
    { id: 6, title: '关于寒假放假通知', categoryId: 2, categoryName: '公告', publishDate: '2026-01-15', content: '详细内容6...' },
    { id: 7, title: '软件工程前沿技术讲座', categoryId: 3, categoryName: '讲座', publishDate: '2026-02-10', content: '详细内容7...' },
    { id: 8, title: '英语角活动预告', categoryId: 4, categoryName: '活动', publishDate: '2026-02-09', content: '详细内容8...' },
    { id: 9, title: '校医院调整作息时间', categoryId: 2, categoryName: '公告', publishDate: '2026-02-07', content: '详细内容9...' },
    { id: 10, title: 'ACM编程大赛通知', categoryId: 1, categoryName: '通知', publishDate: '2026-02-03', content: '详细内容10...' },
  ];
};

// 模拟获取新闻分类
const fetchCategories = () => {
  // 模拟API调用
  categories.value = [
    { id: 1, name: '通知' },
    { id: 2, name: '公告' },
    { id: 3, name: '讲座' },
    { id: 4, name: '活动' }
  ];
};

// 模拟获取热门新闻
const fetchHotNews = () => {
  // 模拟API调用，这里简单地取最新的几条作为热门新闻
  hotNewsList.value = newsList.value.slice(0, 5);
};

const filteredNewsList = computed(() => {
  let filtered = newsList.value;
  if (selectedCategory.value) {
    filtered = filtered.filter(news => news.categoryId === selectedCategory.value);
  }
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    filtered = filtered.filter(news => news.title.toLowerCase().includes(query));
  }
  return filtered;
});

const paginatedNewsList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredNewsList.value.slice(start, end);
});

const handleSizeChange = (val) => {
  pageSize.value = val;
  currentPage.value = 1; // 改变每页大小时重置到第一页
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
};

const goToNewsDetail = (id) => {
  router.push({ name: 'NewsDetail', params: { id } });
};

onMounted(() => {
  fetchCategories();
  fetchNewsList();
  fetchHotNews();
});
</script>

<style scoped>
.news-list-container {
  padding: 20px;
}
.box-card {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}
.filter-section {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}
.news-items {
  min-height: 400px; /* 保证高度，避免内容为空时布局跳动 */
}
.news-item-card {
  margin-bottom: 15px;
  cursor: pointer;
}
.news-item-content h3 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #303133;
}
.news-meta {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  font-size: 13px;
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
.news-summary {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}
.hot-news-card {
  margin-left: 20px;
}
.hot-news-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.hot-news-list li {
  padding: 8px 0;
  cursor: pointer;
  border-bottom: 1px dashed #ebeef5;
  color: #606266;
  font-size: 14px;
}
.hot-news-list li:last-child {
  border-bottom: none;
}
.hot-news-list li:hover {
  color: #409eff;
}
</style>
