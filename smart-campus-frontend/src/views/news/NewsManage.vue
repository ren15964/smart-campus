<template>
  <div class="news-manage">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>新闻管理</span>
          <el-button type="primary" @click="addNews">发布新闻</el-button>
        </div>
      </template>

      <el-table :data="newsList" style="width: 100%" border>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="title" label="标题"></el-table-column>
        <el-table-column prop="categoryName" label="分类"></el-table-column>
        <el-table-column prop="publishDate" label="发布日期" width="180"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="editNews(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteNews(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      ></el-pagination>
    </el-card>

    <!-- 新闻发布/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑新闻' : '发布新闻'"
      width="600px"
      :before-close="handleClose"
    >
      <el-form :model="newsForm" :rules="newsRules" ref="newsFormRef" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="newsForm.title"></el-input>
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="newsForm.categoryId" placeholder="请选择分类" style="width: 100%;">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input type="textarea" v-model="newsForm.content" :rows="8"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitNews">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新闻分类管理 -->
    <el-card class="box-card category-manage-card">
      <template #header>
        <div class="card-header">
          <span>新闻分类管理</span>
          <el-button type="primary" @click="addCategory">新增分类</el-button>
        </div>
      </template>
      <el-table :data="categories" style="width: 100%" border>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="分类名称"></el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" @click="editCategory(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteCategory(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 分类新增/编辑对话框 -->
    <el-dialog
      v-model="categoryDialogVisible"
      :title="isEditCategory ? '编辑分类' : '新增分类'"
      width="400px"
      :before-close="handleCategoryClose"
    >
      <el-form :model="categoryForm" :rules="categoryRules" ref="categoryFormRef" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="categoryDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitCategory">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

// 新闻列表数据
const newsList = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 新闻发布/编辑
const dialogVisible = ref(false);
const isEdit = ref(false);
const newsFormRef = ref(null);
const newsForm = reactive({
  id: null,
  title: '',
  categoryId: null,
  content: '',
  publishDate: ''
});
const newsRules = {
  title: [{ required: true, message: '请输入新闻标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择新闻分类', trigger: 'change' }],
  content: [{ required: true, message: '请输入新闻内容', trigger: 'blur' }]
};

// 新闻分类数据
const categories = ref([]);
const categoryDialogVisible = ref(false);
const isEditCategory = ref(false);
const categoryFormRef = ref(null);
const categoryForm = reactive({
  id: null,
  name: ''
});
const categoryRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
};

// --- 新闻相关操作 ---
const fetchNewsList = () => {
  // 模拟API调用
  newsList.value = [
    { id: 1, title: '校园招聘会通知', categoryName: '通知', publishDate: '2026-02-01', categoryId: 1, content: '内容1' },
    { id: 2, title: '图书馆闭馆公告', categoryName: '公告', publishDate: '2026-02-05', categoryId: 2, content: '内容2' },
    { id: 3, title: '最新学术讲座预告', categoryName: '讲座', publishDate: '2026-02-08', categoryId: 3, content: '内容3' }
  ];
  total.value = newsList.value.length;
};

const addNews = () => {
  isEdit.value = false;
  resetNewsForm();
  dialogVisible.value = true;
};

const editNews = (row) => {
  isEdit.value = true;
  Object.assign(newsForm, row);
  dialogVisible.value = true;
};

const submitNews = () => {
  newsFormRef.value.validate(async (valid) => {
    if (valid) {
      if (isEdit.value) {
        ElMessage.success('新闻编辑成功！');
      } else {
        newsForm.id = newsList.value.length + 1; // 模拟ID
        newsForm.publishDate = new Date().toISOString().slice(0, 10); // 模拟发布日期
        const selectedCategory = categories.value.find(cat => cat.id === newsForm.categoryId);
        newsForm.categoryName = selectedCategory ? selectedCategory.name : '未知';
        newsList.value.push({ ...newsForm });
        ElMessage.success('新闻发布成功！');
      }
      dialogVisible.value = false;
      fetchNewsList();
    }
  });
};

const deleteNews = (row) => {
  ElMessageBox.confirm(`确定删除新闻 "${row.title}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    newsList.value = newsList.value.filter(item => item.id !== row.id);
    ElMessage.success('删除成功！');
    fetchNewsList();
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};

const resetNewsForm = () => {
  newsForm.id = null;
  newsForm.title = '';
  newsForm.categoryId = null;
  newsForm.content = '';
  newsForm.publishDate = '';
  if (newsFormRef.value) {
    newsFormRef.value.resetFields();
  }
};

const handleClose = (done) => {
  resetNewsForm();
  done();
};

const handleSizeChange = (val) => {
  pageSize.value = val;
  fetchNewsList();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  fetchNewsList();
};

// --- 分类相关操作 ---
const fetchCategories = () => {
  // 模拟API调用
  categories.value = [
    { id: 1, name: '通知' },
    { id: 2, name: '公告' },
    { id: 3, name: '讲座' },
    { id: 4, name: '活动' }
  ];
};

const addCategory = () => {
  isEditCategory.value = false;
  resetCategoryForm();
  categoryDialogVisible.value = true;
};

const editCategory = (row) => {
  isEditCategory.value = true;
  Object.assign(categoryForm, row);
  categoryDialogVisible.value = true;
};

const submitCategory = () => {
  categoryFormRef.value.validate(async (valid) => {
    if (valid) {
      if (isEditCategory.value) {
        ElMessage.success('分类编辑成功！');
      } else {
        categoryForm.id = categories.value.length + 1; // 模拟ID
        categories.value.push({ ...categoryForm });
        ElMessage.success('分类新增成功！');
      }
      categoryDialogVisible.value = false;
      fetchCategories();
      fetchNewsList(); // 分类改变可能影响新闻列表显示
    }
  });
};

const deleteCategory = (row) => {
  ElMessageBox.confirm(`确定删除分类 "${row.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    categories.value = categories.value.filter(item => item.id !== row.id);
    ElMessage.success('删除成功！');
    fetchCategories();
    fetchNewsList(); // 分类改变可能影响新闻列表显示
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};

const resetCategoryForm = () => {
  categoryForm.id = null;
  categoryForm.name = '';
  if (categoryFormRef.value) {
    categoryFormRef.value.resetFields();
  }
};

const handleCategoryClose = (done) => {
  resetCategoryForm();
  done();
};

onMounted(() => {
  fetchCategories();
  fetchNewsList();
});
</script>

<style scoped>
.news-manage {
  padding: 20px;
}
.box-card {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.category-manage-card {
  margin-top: 30px;
}
</style>
