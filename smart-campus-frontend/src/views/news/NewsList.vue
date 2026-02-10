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
        ></el-input>
        <el-button type="primary" @click="fetchNewsList">搜索</el-button>
      </div>

      <el-row :gutter="20">
        <el-col :span="18">
          <div class="news-items">
            <el-card
              v-for="news in filteredNewsList"
              :key="news.id"
              class="news-item-card"
              shadow="hover"
              @click="goToNewsDetail(news.id)"
            >
              <div class="news-item-content">
                <h3>{{ news.title }}</h3>
                <div class="news-meta">
                  <span class="category-tag">{{ news.category }}</span>
                  <span class="publish-date">{{ news.publishTime }}</span>
                </div>
                <p class="news-summary">点击查看详情...</p>
              </div>
            </el-card>
            <AppEmpty v-if="filteredNewsList.length === 0" description="暂无新闻" />
          </div>
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="currentPage"
            :page-sizes="[5, 10, 15, 20]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            v-if="total > 0"
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
import axios from 'axios';

// 配置axios基础URL
axios.defaults.baseURL = 'http://localhost:8080/api';

// 添加请求拦截器，统一设置Authorization头
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token'); // 假设token存储在localStorage中
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

// 辅助函数：根据分类ID获取分类名称
const getCategoryNameById = (categoryId) => {
  const category = categories.value.find(cat => cat.id === categoryId);
  return category ? category.name : '';
};


const router = useRouter();

const newsList = ref([]);
const allNewsList = ref([]); // 用于存储原始的所有新闻数据
const categories = ref([]);
const hotNewsList = ref([]);

const selectedCategory = ref(null);
const searchQuery = ref('');

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 获取新闻列表
const fetchNewsList = async () => {
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value,
    };
    if (selectedCategory.value) {
      params.category = getCategoryNameById(selectedCategory.value);
    }
    // 搜索功能仍然在前端处理，因为后端API没有明确的标题搜索参数
    const response = await axios.get('/news/list', { params });
    if (response.data.code === 200 && response.data.data) {
      newsList.value = response.data.data.records;
      total.value = response.data.data.total;
    } else {
      newsList.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('获取新闻列表失败:', error);
    newsList.value = [];
    total.value = 0;
  }
};

// 获取新闻分类
const fetchCategories = async () => {
  try {
    // 假设可以通过新闻列表接口获取所有分类
    const response = await axios.get('/news/list', { params: { current: 1, size: 999 } }); // 获取足够多的新闻来提取分类
    if (response.data.code === 200 && response.data.data && response.data.data.records) {
      const uniqueCategories = new Set();
      response.data.data.records.forEach(news => {
        if (news.category) {
          uniqueCategories.add(news.category);
        }
      });
      categories.value = Array.from(uniqueCategories).map((name, index) => ({
        id: index + 1, // 模拟ID
        name: name
      }));
    }
  } catch (error) {
    console.error('获取新闻分类失败:', error);
    categories.value = [];
  }
};

// 获取热门新闻
const fetchHotNews = async () => {
  try {
    const response = await axios.get('/news/list', { params: { current: 1, size: 10 } }); // 获取最新的10条新闻
    if (response.data.code === 200 && response.data.data && response.data.data.records) {
      // 假设根据viewCount排序，如果没有viewCount或者为0，则根据publishTime排序
      hotNewsList.value = response.data.data.records.sort((a, b) => {
        if (b.viewCount && a.viewCount) {
          return b.viewCount - a.viewCount;
        } else {
          return new Date(b.publishTime).getTime() - new Date(a.publishTime).getTime();
        }
      }).slice(0, 5); // 取前5条作为热门新闻
    } else {
      hotNewsList.value = [];
    }
  } catch (error) {
    console.error('获取热门新闻失败:', error);
    hotNewsList.value = [];
  }
};

const filteredNewsList = computed(() => {
  let filtered = newsList.value;
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    filtered = filtered.filter(news => news.title.toLowerCase().includes(query));
  }
  return filtered;
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
  padding: 24px;
}
.box-card {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 20px; 
  font-weight: 700; 
  color: var(--app-text); 
}
.filter-section {
  margin-bottom: 24px; 
  display: flex;
  align-items: center;
  flex-wrap: wrap; 
  gap: 10px; 
}

.filter-section .el-select,
.filter-section .el-input {
  width: 200px; 
}
.news-items {
  min-height: 400px; 
}
.news-item-card {
  margin-bottom: 15px;
  cursor: pointer;
}
.news-item-content h3 {
  margin-top: 0;
  margin-bottom: 12px;  
  color: var(--app-text); 
}
.news-meta {
  display: flex;
  align-items: center;
  margin-bottom: 12px; 
  font-size: 13px;
  color: var(--app-text-muted); 
}
.category-tag {
  background-color: var(--el-color-primary-light-9); 
  color: var(--el-color-primary); 
  padding: 3px 8px;
  border-radius: 4px;
  margin-right: 10px;
}
.publish-date {
  font-style: italic;
}
.news-summary {
  font-size: 14px;
  color: var(--app-text); 
  line-height: 1.6;
}
.hot-news-card {
  margin-left: 0; 
}
.hot-news-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.hot-news-list li {
  padding: 8px 0;
  cursor: pointer;
  border-bottom: 1px dashed var(--app-border); 
  color: var(--app-text); 
  font-size: 14px;
}
.hot-news-list li:last-child {
  border-bottom: none;
}
.hot-news-list li:hover {
  color: var(--el-color-primary); 
}
</style>
