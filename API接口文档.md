# 智慧校园平台API接口文档

## 1. 接口说明

### 1.1 基本信息

- **Base URL**: `http://localhost:8080/api`
- **接口协议**: HTTP/HTTPS
- **数据格式**: JSON
- **字符编码**: UTF-8

### 1.2 认证方式

- 使用JWT（JSON Web Token）进行身份认证
- 除登录接口外，其他接口需要在请求头中携带Token
- Header格式：`Authorization: Bearer {token}`

### 1.3 统一响应格式

#### 成功响应

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

#### 失败响应

```json
{
  "code": 400,
  "message": "错误信息",
  "data": null
}
```

#### 响应码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权，需要登录 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 1.4 分页格式

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

---

## 2. 用户管理模块

### 2.1 用户登录

**接口地址**: `POST /user/login`

**接口描述**: 用户登录接口，支持学生、教师、管理员登录

**请求参数**:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | string | 是 | 用户名（学号/工号） |
| password | string | 是 | 密码 |

**响应示例**:

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "realName": "系统管理员",
      "role": "admin",
      "avatar": "http://example.com/avatar.jpg"
    }
  }
}
```

### 2.2 用户注册

**接口地址**: `POST /user/register`

**接口描述**: 用户注册接口，支持学生/教师自助注册（管理员账号请由管理员在后台添加）

**请求参数**:

```json
{
  "username": "2025001",
  "password": "123456",
  "confirmPassword": "123456",
  "realName": "张三",
  "role": "student",
  "email": "zhangsan@example.com",
  "phone": "13800138000"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | string | 是 | 用户名（学号/工号） |
| password | string | 是 | 密码 |
| confirmPassword | string | 是 | 确认密码 |
| realName | string | 是 | 真实姓名 |
| role | string | 是 | 角色：student/teacher |
| email | string | 否 | 邮箱 |
| phone | string | 否 | 手机号 |

**响应示例**（注册成功后直接返回token，等同于自动登录）:

```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 4,
      "username": "2025001",
      "realName": "张三",
      "role": "student",
      "avatar": null
    }
  }
}
```

### 2.2 获取当前用户信息

**接口地址**: `GET /user/info`

**接口描述**: 获取当前登录用户的详细信息

**请求头**:

```
Authorization: Bearer {token}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "系统管理员",
    "role": "admin",
    "email": "admin@example.com",
    "phone": "13800138000",
    "avatar": "http://example.com/avatar.jpg",
    "status": 1,
    "createTime": "2024-01-01 10:00:00"
  }
}
```

### 2.3 修改密码

**接口地址**: `PUT /user/password`

**接口描述**: 修改当前用户密码

**请求参数**:

```json
{
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| oldPassword | string | 是 | 原密码 |
| newPassword | string | 是 | 新密码 |

**响应示例**:

```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": null
}
```

### 2.4 更新个人信息

**接口地址**: `PUT /user/profile`

**接口描述**: 更新当前用户的个人信息

**请求参数**:

```json
{
  "realName": "张三",
  "email": "zhangsan@example.com",
  "phone": "13800138000",
  "avatar": "http://example.com/avatar.jpg"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 2.5 用户列表（管理员）

**接口地址**: `GET /user/list`

**接口描述**: 获取用户列表，支持分页和搜索

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| current | int | 否 | 当前页码，默认1 |
| size | int | 否 | 每页数量，默认10 |
| role | string | 否 | 角色筛选：student/teacher/admin |
| keyword | string | 否 | 搜索关键词（用户名/姓名） |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "username": "2024001",
        "realName": "张三",
        "role": "student",
        "email": "zhangsan@example.com",
        "phone": "13800138000",
        "status": 1,
        "createTime": "2024-01-01 10:00:00"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

### 2.6 添加用户（管理员）

**接口地址**: `POST /user/add`

**接口描述**: 添加新用户

**请求参数**:

```json
{
  "username": "2024001",
  "password": "123456",
  "realName": "张三",
  "role": "student",
  "email": "zhangsan@example.com",
  "phone": "13800138000"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "添加成功",
  "data": {
    "id": 1
  }
}
```

### 2.7 更新用户（管理员）

**接口地址**: `PUT /user/{id}`

**接口描述**: 更新用户信息

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | long | 是 | 用户ID |

**请求参数**:

```json
{
  "realName": "张三",
  "email": "zhangsan@example.com",
  "phone": "13800138000",
  "status": 1
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 2.8 删除用户（管理员）

**接口地址**: `DELETE /user/{id}`

**接口描述**: 删除用户（逻辑删除）

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | long | 是 | 用户ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 3. 课程管理模块

### 3.1 课程列表

**接口地址**: `GET /course/list`

**接口描述**: 获取课程列表，支持分页和搜索

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| current | int | 否 | 当前页码，默认1 |
| size | int | 否 | 每页数量，默认10 |
| keyword | string | 否 | 搜索关键词（课程名称/课程编码） |
| courseType | string | 否 | 课程类型：必修/选修 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "courseCode": "CS101",
        "courseName": "数据结构",
        "credit": 3.0,
        "hours": 48,
        "courseType": "必修",
        "description": "数据结构课程介绍..."
      }
    ],
    "total": 50,
    "size": 10,
    "current": 1,
    "pages": 5
  }
}
```

### 3.2 课程详情

**接口地址**: `GET /course/{id}`

**接口描述**: 获取课程详细信息

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | long | 是 | 课程ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "courseCode": "CS101",
    "courseName": "数据结构",
    "credit": 3.0,
    "hours": 48,
    "courseType": "必修",
    "description": "数据结构课程介绍...",
    "createTime": "2024-01-01 10:00:00"
  }
}
```

### 3.3 添加课程（管理员）

**接口地址**: `POST /course/add`

**接口描述**: 添加新课程

**请求参数**:

```json
{
  "courseCode": "CS101",
  "courseName": "数据结构",
  "credit": 3.0,
  "hours": 48,
  "courseType": "必修",
  "description": "数据结构课程介绍..."
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "添加成功",
  "data": {
    "id": 1
  }
}
```

### 3.4 更新课程（管理员）

**接口地址**: `PUT /course/{id}`

**接口描述**: 更新课程信息

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | long | 是 | 课程ID |

**请求参数**:

```json
{
  "courseName": "数据结构",
  "credit": 3.0,
  "hours": 48,
  "courseType": "必修",
  "description": "数据结构课程介绍..."
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 3.5 删除课程（管理员）

**接口地址**: `DELETE /course/{id}`

**接口描述**: 删除课程（逻辑删除）

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | long | 是 | 课程ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 4. 选课管理模块

### 4.1 可选课程列表（学生）

**接口地址**: `GET /course-selection/available`

**接口描述**: 获取当前学期可选课程列表

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| current | int | 否 | 当前页码，默认1 |
| size | int | 否 | 每页数量，默认10 |
| keyword | string | 否 | 搜索关键词 |
| semester | string | 否 | 学期，如2024-2025-1 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "scheduleId": 1,
        "courseCode": "CS101",
        "courseName": "数据结构",
        "teacherName": "李老师",
        "credit": 3.0,
        "weekDay": 1,
        "startTime": "08:00",
        "endTime": "09:40",
        "classroom": "A101",
        "capacity": 50,
        "selectedCount": 30,
        "isSelected": false
      }
    ],
    "total": 20,
    "size": 10,
    "current": 1,
    "pages": 2
  }
}
```

### 4.2 我的选课（学生）

**接口地址**: `GET /course-selection/my-courses`

**接口描述**: 获取当前学生已选课程列表

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| semester | string | 否 | 学期，如2024-2025-1 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "selectionId": 1,
      "scheduleId": 1,
      "courseCode": "CS101",
      "courseName": "数据结构",
      "teacherName": "李老师",
      "credit": 3.0,
      "weekDay": 1,
      "startTime": "08:00",
      "endTime": "09:40",
      "classroom": "A101",
      "selectionTime": "2024-09-01 10:00:00"
    }
  ]
}
```

### 4.3 选课（学生）

**接口地址**: `POST /course-selection/select`

**接口描述**: 学生选课

**请求参数**:

```json
{
  "scheduleId": 1
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "选课成功",
  "data": null
}
```

### 4.4 退课（学生）

**接口地址**: `DELETE /course-selection/{id}`

**接口描述**: 学生退课

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | long | 是 | 选课记录ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "退课成功",
  "data": null
}
```

---

## 5. 成绩管理模块

### 5.1 成绩查询（学生）

**接口地址**: `GET /grade/my-grades`

**接口描述**: 学生查询个人成绩

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| semester | string | 否 | 学期，如2024-2025-1 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "courseName": "数据结构",
      "courseCode": "CS101",
      "credit": 3.0,
      "teacherName": "李老师",
      "usualScore": 85.0,
      "examScore": 90.0,
      "totalScore": 88.0,
      "gpa": 3.8,
      "semester": "2024-2025-1"
    }
  ]
}
```

### 5.2 成绩统计（学生）

**接口地址**: `GET /grade/statistics`

**接口描述**: 获取学生成绩统计信息

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalCredits": 30.0,
    "averageGpa": 3.5,
    "passedCourses": 10,
    "failedCourses": 0
  }
}
```

### 5.3 课程成绩列表（教师）

**接口地址**: `GET /grade/course/{scheduleId}`

**接口描述**: 教师查看所授课程的学生成绩列表

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| scheduleId | long | 是 | 开课计划ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "gradeId": 1,
      "studentNo": "2024001",
      "studentName": "张三",
      "usualScore": 85.0,
      "examScore": 90.0,
      "totalScore": 88.0,
      "gpa": 3.8
    }
  ]
}
```

### 5.4 录入成绩（教师）

**接口地址**: `POST /grade/input`

**接口描述**: 教师录入学生成绩

**请求参数**:

```json
{
  "scheduleId": 1,
  "studentId": 1,
  "usualScore": 85.0,
  "examScore": 90.0
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "成绩录入成功",
  "data": null
}
```

### 5.5 批量录入成绩（教师）

**接口地址**: `POST /grade/batch-input`

**接口描述**: 教师批量录入成绩

**请求参数**:

```json
{
  "scheduleId": 1,
  "grades": [
    {
      "studentId": 1,
      "usualScore": 85.0,
      "examScore": 90.0
    },
    {
      "studentId": 2,
      "usualScore": 80.0,
      "examScore": 85.0
    }
  ]
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "批量录入成功",
  "data": null
}
```

---

## 6. 课表管理模块

### 6.1 我的课表（学生/教师）

**接口地址**: `GET /schedule/my-schedule`

**接口描述**: 获取个人课表

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| semester | string | 否 | 学期，如2024-2025-1 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "scheduleId": 1,
      "courseName": "数据结构",
      "teacherName": "李老师",
      "weekDay": 1,
      "startWeek": 1,
      "endWeek": 16,
      "startTime": "08:00",
      "endTime": "09:40",
      "classroom": "A101"
    }
  ]
}
```

### 6.2 周课表视图

**接口地址**: `GET /schedule/week-view`

**接口描述**: 获取按周显示的课表

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| semester | string | 否 | 学期 |
| week | int | 否 | 周次，默认当前周 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "week": 1,
    "courses": {
      "1": [
        {
          "courseName": "数据结构",
          "teacherName": "李老师",
          "startTime": "08:00",
          "endTime": "09:40",
          "classroom": "A101"
        }
      ],
      "2": [],
      "3": [],
      "4": [],
      "5": [],
      "6": [],
      "7": []
    }
  }
}
```

---

## 7. 通知公告模块

### 7.1 通知列表

**接口地址**: `GET /notice/list`

**接口描述**: 获取通知公告列表

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| current | int | 否 | 当前页码，默认1 |
| size | int | 否 | 每页数量，默认10 |
| priority | int | 否 | 优先级筛选：1-普通, 2-重要, 3-紧急 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "关于期末考试安排的通知",
        "publisherName": "教务处",
        "priority": 2,
        "publishTime": "2024-06-01 10:00:00",
        "isRead": false
      }
    ],
    "total": 50,
    "size": 10,
    "current": 1,
    "pages": 5
  }
}
```

### 7.2 通知详情

**接口地址**: `GET /notice/{id}`

**接口描述**: 获取通知详情

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | long | 是 | 通知ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "关于期末考试安排的通知",
    "content": "通知内容...",
    "publisherName": "教务处",
    "priority": 2,
    "attachment": "http://example.com/file.pdf",
    "publishTime": "2024-06-01 10:00:00",
    "readCount": 100
  }
}
```

### 7.3 发布通知（管理员）

**接口地址**: `POST /notice/publish`

**接口描述**: 发布通知公告

**请求参数**:

```json
{
  "title": "关于期末考试安排的通知",
  "content": "通知内容...",
  "priority": 2,
  "targetRole": "all",
  "attachment": "http://example.com/file.pdf"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "发布成功",
  "data": {
    "id": 1
  }
}
```

### 7.4 删除通知（管理员）

**接口地址**: `DELETE /notice/{id}`

**接口描述**: 删除通知

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | long | 是 | 通知ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 8. 新闻管理模块

### 8.1 新闻列表

**接口地址**: `GET /news/list`

**接口描述**: 获取新闻列表

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| current | int | 否 | 当前页码，默认1 |
| size | int | 否 | 每页数量，默认10 |
| category | string | 否 | 分类筛选 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "我校举办科技创新大赛",
        "coverImage": "http://example.com/cover.jpg",
        "category": "校园活动",
        "publisherName": "宣传部",
        "viewCount": 500,
        "publishTime": "2024-06-01 10:00:00"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

### 8.2 新闻详情

**接口地址**: `GET /news/{id}`

**接口描述**: 获取新闻详情

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | long | 是 | 新闻ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "我校举办科技创新大赛",
    "coverImage": "http://example.com/cover.jpg",
    "content": "新闻内容...",
    "category": "校园活动",
    "publisherName": "宣传部",
    "viewCount": 500,
    "publishTime": "2024-06-01 10:00:00"
  }
}
```

### 8.3 发布新闻（管理员）

**接口地址**: `POST /news/publish`

**接口描述**: 发布新闻

**请求参数**:

```json
{
  "title": "我校举办科技创新大赛",
  "coverImage": "http://example.com/cover.jpg",
  "content": "新闻内容...",
  "category": "校园活动"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "发布成功",
  "data": {
    "id": 1
  }
}
```

### 8.4 删除新闻（管理员）

**接口地址**: `DELETE /news/{id}`

**接口描述**: 删除新闻

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | long | 是 | 新闻ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 9. 课程资源模块

### 9.1 资源列表

**接口地址**: `GET /resource/list`

**接口描述**: 获取课程资源列表

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| scheduleId | long | 是 | 开课计划ID |
| chapter | string | 否 | 章节筛选 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "resourceName": "第一章课件.ppt",
      "resourceType": "ppt",
      "filePath": "http://example.com/file.ppt",
      "fileSize": 1024000,
      "chapter": "第一章",
      "description": "数据结构基础",
      "downloadCount": 50,
      "createTime": "2024-09-01 10:00:00"
    }
  ]
}
```

### 9.2 上传资源（教师）

**接口地址**: `POST /resource/upload`

**接口描述**: 教师上传课程资源

**请求参数**: multipart/form-data

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| scheduleId | long | 是 | 开课计划ID |
| file | file | 是 | 文件 |
| resourceName | string | 是 | 资源名称 |
| resourceType | string | 是 | 资源类型 |
| chapter | string | 否 | 章节 |
| description | string | 否 | 描述 |

**响应示例**:

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "id": 1,
    "filePath": "http://example.com/file.ppt"
  }
}
```

### 9.3 下载资源

**接口地址**: `GET /resource/download/{id}`

**接口描述**: 下载课程资源

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | long | 是 | 资源ID |

**响应**: 文件流

### 9.4 删除资源（教师）

**接口地址**: `DELETE /resource/{id}`

**接口描述**: 删除课程资源

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | long | 是 | 资源ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 10. 作业管理模块

### 10.1 作业列表（学生）

**接口地址**: `GET /homework/my-homework`

**接口描述**: 学生查看作业列表

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| scheduleId | long | 否 | 开课计划ID |
| status | int | 否 | 状态：0-未提交, 1-已提交, 2-已批改 |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "homeworkId": 1,
      "title": "数据结构第一次作业",
      "courseName": "数据结构",
      "teacherName": "李老师",
      "deadline": "2024-09-15 23:59:59",
      "totalScore": 100.0,
      "submitStatus": 1,
      "score": null,
      "isLate": false
    }
  ]
}
```

### 10.2 作业详情

**接口地址**: `GET /homework/{id}`

**接口描述**: 查看作业详情

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | long | 是 | 作业ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "title": "数据结构第一次作业",
    "content": "作业内容...",
    "attachment": "http://example.com/homework.pdf",
    "deadline": "2024-09-15 23:59:59",
    "totalScore": 100.0,
    "courseName": "数据结构",
    "teacherName": "李老师",
    "submitInfo": {
      "submitId": 1,
      "content": "我的答案...",
      "attachment": "http://example.com/submit.pdf",
      "submitTime": "2024-09-14 20:00:00",
      "status": 2,
      "score": 95.0,
      "comment": "完成得很好"
    }
  }
}
```

### 10.3 提交作业（学生）

**接口地址**: `POST /homework/submit`

**接口描述**: 学生提交作业

**请求参数**: multipart/form-data

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| homeworkId | long | 是 | 作业ID |
| content | string | 否 | 提交内容 |
| file | file | 否 | 附件 |

**响应示例**:

```json
{
  "code": 200,
  "message": "提交成功",
  "data": {
    "submitId": 1
  }
}
```

### 10.4 发布作业（教师）

**接口地址**: `POST /homework/publish`

**接口描述**: 教师发布作业

**请求参数**:

```json
{
  "scheduleId": 1,
  "title": "数据结构第一次作业",
  "content": "作业内容...",
  "attachment": "http://example.com/homework.pdf",
  "deadline": "2024-09-15 23:59:59",
  "totalScore": 100.0
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "发布成功",
  "data": {
    "id": 1
  }
}
```

### 10.5 作业提交列表（教师）

**接口地址**: `GET /homework/{id}/submissions`

**接口描述**: 教师查看作业提交情况

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | long | 是 | 作业ID |

**响应示例**:

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "submitId": 1,
      "studentNo": "2024001",
      "studentName": "张三",
      "content": "我的答案...",
      "attachment": "http://example.com/submit.pdf",
      "submitTime": "2024-09-14 20:00:00",
      "isLate": false,
      "status": 1,
      "score": null
    }
  ]
}
```

### 10.6 批改作业（教师）

**接口地址**: `POST /homework/grade`

**接口描述**: 教师批改作业

**请求参数**:

```json
{
  "submitId": 1,
  "score": 95.0,
  "comment": "完成得很好"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "批改成功",
  "data": null
}
```

---

## 11. 文件上传

### 11.1 通用文件上传

**接口地址**: `POST /file/upload`

**接口描述**: 通用文件上传接口

**请求参数**: multipart/form-data

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | file | 是 | 文件 |
| type | string | 否 | 文件类型：avatar/document/image |

**响应示例**:

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "fileName": "file.jpg",
    "filePath": "http://example.com/file.jpg",
    "fileSize": 102400
  }
}
```

---

**文档版本**: v1.0
**编写日期**: 2026-02-09
**最后更新**: 2026-02-09
