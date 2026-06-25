<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { User, Lock, Avatar, Setting } from "@element-plus/icons-vue";
import { register } from "@/api/auth";

const router = useRouter();

const registerForm = reactive({
  username: "",
  password: "",
  confirmPassword: "",
  role: "USER",
});

const loading = ref(false);
const registerFormRef = ref(null);

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

const rules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 3, max: 50, message: "用户名长度3-50个字符", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, max: 100, message: "密码长度6-100个字符", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, message: "请再次输入密码", trigger: "blur" },
    { validator: validateConfirmPassword, trigger: "blur" },
  ],
  role: [{ required: true, message: "请选择角色", trigger: "change" }],
};

async function handleRegister() {
  if (!registerFormRef.value) return;

  try {
    await registerFormRef.value.validate();
  } catch {
    return;
  }

  loading.value = true;
  try {
    const { data } = await register({
      username: registerForm.username,
      password: registerForm.password,
      role: registerForm.role,
    });
    if (data.success) {
      const roleText = registerForm.role === 'USER' ? '普通用户' : '管理员';
      ElMessage.success(`注册成功！您选择的角色是：${roleText}，请登录`);
      router.push("/login");
    } else {
      ElMessage.error(data.message || "注册失败");
    }
  } catch (error) {
    const message = error?.response?.data?.message || "注册失败，请稍后重试";
    ElMessage.error(message);
  } finally {
    loading.value = false;
  }
}

function goToLogin() {
  router.push("/login");
}
</script>

<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <h1>EcoCarbon-MRV</h1>
        <p>创建新账号</p>
      </div>

      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="rules"
        class="register-form"
        @submit.prevent="handleRegister"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            :prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item prop="role">
          <el-radio-group v-model="registerForm.role" class="role-group">
            <el-radio-button value="USER">
              <el-icon><User /></el-icon>
              <span>普通用户</span>
              <small>可使用业务功能</small>
            </el-radio-button>
            <el-radio-button value="ADMIN">
              <el-icon><Setting /></el-icon>
              <span>管理员</span>
              <small>系统管理 + 业务功能</small>
            </el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="register-button"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>

        <div class="role-description">
          <div v-if="registerForm.role === 'USER'" class="role-info">
            <h4>普通用户权限</h4>
            <ul>
              <li>碳盘查</li>
              <li>碳项目管理</li>
              <li>遥感监测</li>
              <li>环境监测</li>
              <li>数据查询与导出</li>
            </ul>
          </div>
          <div v-if="registerForm.role === 'ADMIN'" class="role-info">
            <h4>管理员权限</h4>
            <ul>
              <li>所有普通用户功能</li>
              <li>用户管理</li>
              <li>角色管理</li>
              <li>系统设置</li>
              <li>数据字典维护</li>
              <li>系统监控与日志</li>
            </ul>
          </div>
        </div>

        <div class="register-footer">
          <span>已有账号？</span>
          <el-link type="primary" underline="never" @click="goToLogin">
            立即登录
          </el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-card {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h1 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 8px;
}

.register-header p {
  font-size: 14px;
  color: #909399;
}

.register-form {
  width: 100%;
}

.register-button {
  width: 100%;
}

.register-footer {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #909399;
}

.register-footer .el-link {
  margin-left: 4px;
}

.role-group {
  width: 100%;
  display: flex;
  gap: 16px;
}

.role-group .el-radio-button {
  flex: 1;
}

.role-group .el-radio-button :deep(.el-radio-button__inner) {
  width: 100%;
  height: 80px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  border-radius: 8px;
  border: 2px solid #e4e7ed;
  background: white;
  transition: all 0.3s ease;
}

.role-group .el-radio-button :deep(.el-radio-button__inner:hover) {
  border-color: #409eff;
}

.role-group .el-radio-button :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.role-group .el-radio-button :deep(.el-radio-button__inner .el-icon) {
  font-size: 24px;
}

.role-group .el-radio-button :deep(.el-radio-button__inner span) {
  font-size: 14px;
  font-weight: 500;
}

.role-group .el-radio-button :deep(.el-radio-button__inner small) {
  font-size: 11px;
  opacity: 0.8;
}

.role-description {
  margin: 20px 0;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.role-info h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #303133;
  font-weight: 600;
}

.role-info ul {
  margin: 0;
  padding-left: 20px;
}

.role-info li {
  font-size: 13px;
  color: #606266;
  line-height: 1.8;
}

@media (max-width: 480px) {
  .role-group {
    flex-direction: column;
    gap: 12px;
  }
  
  .register-card {
    width: 90%;
    padding: 24px;
  }
  
  .register-header h1 {
    font-size: 24px;
  }
}
</style>
