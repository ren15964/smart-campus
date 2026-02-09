<template>
  <div class="personal-center-container">
    <h1>个人中心</h1>
    <el-card class="box-card">
      <template #header>
        <div class="clearfix">
          <span>用户信息</span>
          <el-button style="float: right; padding: 3px 0" link @click="editUserInfo">编辑</el-button>
        </div>
      </template>
      <div class="user-info-item">
        <label>用户名:</label>
        <span>{{ userInfo.username }}</span>
      </div>
      <div class="user-info-item">
        <label>姓名:</label>
        <span>{{ userInfo.name }}</span>
      </div>
      <div class="user-info-item">
        <label>学号/工号:</label>
        <span>{{ userInfo.id }}</span>
      </div>
      <div class="user-info-item">
        <label>邮箱:</label>
        <span>{{ userInfo.email }}</span>
      </div>
      <div class="user-info-item">
        <label>电话:</label>
        <span>{{ userInfo.phone }}</span>
      </div>
    </el-card>

    <el-dialog v-model="dialogFormVisible" title="编辑用户信息" append-to-body>
      <el-form :model="form">
        <el-form-item label="姓名" :label-width="formLabelWidth">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" :label-width="formLabelWidth">
          <el-input v-model="form.email" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="电话" :label-width="formLabelWidth">
          <el-input v-model="form.phone" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取 消</el-button>
          <el-button type="primary" @click="saveUserInfo">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'; // 导入request工具
import { ElMessage } from 'element-plus'

export default {
  name: 'PersonalCenter',
  data() {
    return {
      userInfo: {
        username: '',
        name: '',
        id: '',
        email: '',
        phone: ''
      },
      dialogFormVisible: false,
      form: {
        name: '',
        email: '',
        phone: ''
      },
      formLabelWidth: '120px'
    };
  },
  created() {
    this.getUserInfo();
  },
  methods: {
    getUserInfo() {
      request.get('/user/info').then(res => {
        this.userInfo = {
          username: res.data.username,
          name: res.data.realName,
          id: res.data.id, // Assuming 'id' is for student/teacher ID
          email: res.data.email,
          phone: res.data.phone
        };
      }).catch(error => {
        ElMessage.error('获取用户信息失败')
        console.error('获取用户信息失败:', error);
      });
    },
    editUserInfo() {
      this.form = { ...this.userInfo, name: this.userInfo.name }; // Initialize form with current userInfo, specifically mapping 'name' to 'realName'
      this.dialogFormVisible = true;
    },
    saveUserInfo() {
      request.put('/user/profile', {
        realName: this.form.name, // Map 'name' from form back to 'realName' for API
        email: this.form.email,
        phone: this.form.phone
      }).then(res => {
        this.userInfo.name = this.form.name; // Update local userInfo with realName
        this.userInfo.email = this.form.email;
        this.userInfo.phone = this.form.phone;
        this.dialogFormVisible = false;
        ElMessage.success('用户信息更新成功！')
      }).catch(error => {
        ElMessage.error('用户信息更新失败')
        console.error('用户信息更新失败:', error);
      });
    }
  }
};
</script>

<style scoped>
.personal-center-container {
  padding: 20px;
}

.box-card {
  width: 600px;
  margin-top: 20px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both
}

.user-info-item {
  margin-bottom: 10px;
  font-size: 14px;
}

.user-info-item label {
  font-weight: bold;
  margin-right: 10px;
}
</style>
