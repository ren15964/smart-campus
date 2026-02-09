<template>
  <div class="admin-classroom-manage">
    <h2>教室资源管理</h2>
    <el-card>
      <div class="header-buttons" style="margin-bottom: 20px;">
        <el-button type="primary" @click="handleAddClassroom">添加教室</el-button>
      </div>

      <el-table :data="classroomList" border style="width: 100%">
        <el-table-column prop="name" label="教室名称" width="150"></el-table-column>
        <el-table-column prop="capacity" label="容量" width="100"></el-table-column>
        <el-table-column prop="location" label="位置"></el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleEditClassroom(scope.row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDeleteClassroom(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑教室弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑教室' : '添加教室'"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        :model="classroomForm"
        :rules="formRules"
        ref="classroomFormRef"
        label-width="100px"
      >
        <el-form-item label="教室名称" prop="name">
          <el-input v-model="classroomForm.name" placeholder="请输入教室名称"></el-input>
        </el-form-item>
        <el-form-item label="容量" prop="capacity">
          <el-input-number v-model="classroomForm.capacity" :min="1" :max="500"></el-input-number>
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="classroomForm.location" placeholder="请输入教室位置"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const classroomList = ref([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const classroomFormRef = ref(null);
const classroomForm = reactive({
  id: null,
  name: '',
  capacity: 30,
  location: '',
});

const formRules = {
  name: [{ required: true, message: '请输入教室名称', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入容量', trigger: 'change' }],
  location: [{ required: true, message: '请输入位置', trigger: 'blur' }],
};

// 模拟获取教室列表
const getClassroomList = () => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        code: 200,
        data: [
          { id: 1, name: '教1-101', capacity: 60, location: '教学楼1层' },
          { id: 2, name: '教2-205', capacity: 100, location: '教学楼2层' },
          { id: 3, name: '实训楼A-301', capacity: 40, location: '实训楼A座3层' },
        ],
      });
    }, 500);
  });
};

// 模拟添加教室
const addClassroom = (data) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      console.log('模拟添加教室:', data);
      resolve({ code: 200, msg: '添加成功' });
    }, 500);
  });
};

// 模拟更新教室
const updateClassroom = (id, data) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      console.log(`模拟更新教室 ${id}:`, data);
      resolve({ code: 200, msg: '更新成功' });
    }, 500);
  });
};

// 模拟删除教室
const deleteClassroom = (id) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      console.log(`模拟删除教室: ${id}`);
      resolve({ code: 200, msg: '删除成功' });
    }, 500);
  });
};

const fetchClassrooms = async () => {
  try {
    const res = await getClassroomList();
    if (res.code === 200) {
      classroomList.value = res.data;
    } else {
      ElMessage.error(res.msg || '获取教室列表失败');
    }
  } catch (error) {
    ElMessage.error('网络错误或服务器异常');
  }
};

const handleAddClassroom = () => {
  isEdit.value = false;
  dialogVisible.value = true;
  nextTick(() => {
    classroomFormRef.value?.resetFields();
    Object.assign(classroomForm, { id: null, name: '', capacity: 30, location: '' });
  });
};

const handleEditClassroom = (row) => {
  isEdit.value = true;
  dialogVisible.value = true;
  nextTick(() => {
    Object.assign(classroomForm, row);
  });
};

const handleDeleteClassroom = async (row) => {
  ElMessageBox.confirm(
    `确定要删除教室 [${row.name}] 吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
      try {
        const res = await deleteClassroom(row.id);
        if (res.code === 200) {
          ElMessage.success(res.msg);
          fetchClassrooms();
        } else {
          ElMessage.error(res.msg || '删除失败');
        }
      } catch (error) {
        ElMessage.error('网络错误或服务器异常');
      }
    })
    .catch(() => {
      ElMessage.info('已取消删除');
    });
};

const handleSubmit = () => {
  classroomFormRef.value?.validate(async (valid) => {
    if (valid) {
      try {
        let res;
        if (isEdit.value) {
          res = await updateClassroom(classroomForm.id, classroomForm);
        } else {
          res = await addClassroom(classroomForm);
        }
        if (res.code === 200) {
          ElMessage.success(res.msg);
          dialogVisible.value = false;
          fetchClassrooms();
        } else {
          ElMessage.error(res.msg || '操作失败');
        }
      } catch (error) {
        ElMessage.error('网络错误或服务器异常');
      }
    }
  });
};

const handleDialogClose = () => {
  classroomFormRef.value?.resetFields();
  Object.assign(classroomForm, { id: null, name: '', capacity: 30, location: '' });
};

onMounted(() => {
  fetchClassrooms();
});
</script>

<style scoped>
.admin-classroom-manage {
  padding: 20px;
}
.header-buttons {
  display: flex;
  justify-content: flex-end;
}
</style>
