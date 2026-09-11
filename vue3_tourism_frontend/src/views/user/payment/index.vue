<template>
  <div class="payment-container">
    <!-- 面包屑导航 -->
    <div class="breadcrumb-section">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item
          @click="$router.push('/user/spot')"
          style="cursor: pointer"
        >
          {{ isCartMode ? "购物车结算" : "景点预订" }}
        </el-breadcrumb-item>
        <el-breadcrumb-item>订单确认</el-breadcrumb-item>
      </el-breadcrumb>

      <!-- 购物车模式切换 -->
      <div class="mode-switch">
        <el-switch
          v-model="isCartMode"
          active-text="购物车模式"
          inactive-text="单项支付"
          @change="handleModeChange"
        />
        <span class="cart-count" v-if="cartItems.length > 0">
          购物车 ({{ cartItems.length }})
        </span>
      </div>
    </div>

    <div class="payment-content">
      <!-- 左侧订单详情 -->
      <div class="order-section">
        <el-card class="order-card">
          <template #header>
            <div class="card-header">
              <h3>{{ isCartMode ? "购物车订单" : "订单详情" }}</h3>
              <div class="header-actions" v-if="isCartMode">
                <el-button
                  size="small"
                  @click="clearCart"
                  :disabled="cartItems.length === 0"
                >
                  清空购物车
                </el-button>
              </div>
            </div>
          </template>

          <!-- 购物车模式 -->
          <div v-if="isCartMode" class="cart-section">
            <div v-if="cartItems.length === 0" class="empty-cart">
              <el-empty description="购物车为空">
                <el-button type="primary" @click="$router.push('/user/spot')">
                  去选购
                </el-button>
              </el-empty>
            </div>

            <div v-else class="cart-items">
              <div
                v-for="(item, index) in cartItems"
                :key="item.id"
                class="cart-item"
              >
                <div class="item-header">
                  <el-tag
                    :type="item.type === 'hotel' ? 'warning' : 'success'"
                    size="small"
                  >
                    {{ item.type === "hotel" ? "酒店" : "景点" }}
                  </el-tag>
                  <el-button
                    type="danger"
                    size="small"
                    text
                    @click="removeFromCart(index)"
                  >
                    删除
                  </el-button>
                </div>

                <div class="item-info-section">
                  <div class="item-image">
                    <img :src="item.image" :alt="item.name" />
                  </div>
                  <div class="item-details">
                    <h4 class="item-name">{{ item.name }}</h4>
                    <p class="item-location">
                      <el-icon><LocationFilled /></el-icon>
                      {{ item.location }}
                    </p>
                    <p class="item-description">{{ item.description }}</p>
                    <div class="item-price">
                      <span class="price-label">{{
                        item.type === "hotel" ? "房间价格：" : "门票价格："
                      }}</span>
                      <span class="price-value">¥{{ item.price }}</span>
                      <span class="price-unit">{{
                        item.type === "hotel" ? "/晚" : "/人"
                      }}</span>
                    </div>
                  </div>
                </div>

                <el-divider />

                <!-- 数量选择 -->
                <div class="quantity-section">
                  <div class="quantity-row">
                    <span class="quantity-label">购买数量：</span>
                    <div class="quantity-controls">
                      <el-button
                        size="small"
                        :disabled="item.quantity <= 1"
                        @click="decreaseItemQuantity(index)"
                        circle
                        >-</el-button
                      >
                      <span class="quantity-display">{{ item.quantity }}</span>
                      <el-button
                        size="small"
                        @click="increaseItemQuantity(index)"
                        circle
                        >+</el-button
                      >
                    </div>
                  </div>
                  <div class="subtotal-row">
                    <span class="subtotal-label">小计：</span>
                    <span class="subtotal-value"
                      >¥{{ item.price * item.quantity }}</span
                    >
                  </div>
                </div>

                <el-divider />

                <!-- 游客信息 -->
                <div class="visitor-section">
                  <el-collapse v-model="item.activeCollapse">
                    <el-collapse-item title="游客信息" name="visitors">
                      <div class="visitor-list">
                        <div
                          v-for="(visitor, vIndex) in item.visitors"
                          :key="vIndex"
                          class="visitor-item"
                        >
                          <div class="visitor-header">
                            <span class="visitor-title"
                              >游客{{ vIndex + 1 }}</span
                            >
                            <el-button
                              v-if="item.visitors.length > 1"
                              type="danger"
                              size="small"
                              text
                              @click="removeItemVisitor(index, vIndex)"
                            >
                              删除
                            </el-button>
                          </div>
                          <el-form
                            :model="visitor"
                            label-width="80px"
                            size="small"
                          >
                            <el-row :gutter="16">
                              <el-col :span="12">
                                <el-form-item label="姓名" required>
                                  <el-input
                                    v-model="visitor.name"
                                    placeholder="请输入真实姓名"
                                  />
                                </el-form-item>
                              </el-col>
                              <el-col :span="12">
                                <el-form-item label="手机号" required>
                                  <el-input
                                    v-model="visitor.phone"
                                    placeholder="请输入手机号"
                                  />
                                </el-form-item>
                              </el-col>
                            </el-row>
                            <el-row :gutter="16">
                              <el-col :span="12">
                                <el-form-item label="身份证" required>
                                  <el-input
                                    v-model="visitor.idCard"
                                    placeholder="请输入身份证号"
                                  />
                                </el-form-item>
                              </el-col>
                              <el-col :span="12">
                                <el-form-item
                                  :label="
                                    item.type === 'hotel'
                                      ? '入住日期'
                                      : '游览日期'
                                  "
                                  required
                                >
                                  <el-date-picker
                                    v-model="visitor.visitDate"
                                    type="date"
                                    :placeholder="
                                      item.type === 'hotel'
                                        ? '选择入住日期'
                                        : '选择游览日期'
                                    "
                                    :disabled-date="disabledDate"
                                    style="width: 100%"
                                  />
                                </el-form-item>
                              </el-col>
                            </el-row>
                          </el-form>
                        </div>
                      </div>
                      <el-button
                        v-if="item.visitors.length < item.quantity"
                        type="primary"
                        plain
                        size="small"
                        @click="addItemVisitor(index)"
                        style="margin-top: 10px"
                      >
                        + 添加游客信息
                      </el-button>
                    </el-collapse-item>
                  </el-collapse>
                </div>
              </div>
            </div>
          </div>

          <!-- 单项支付模式 -->
          <div v-else class="single-item-section">
            <!-- 景点信息 -->
            <div class="spot-info-section">
              <div class="spot-image">
                <img :src="spotInfo.image" :alt="spotInfo.name" />
              </div>
              <div class="spot-details">
                <h4 class="spot-name">{{ spotInfo.name }}</h4>
                <p class="spot-location">
                  <el-icon><LocationFilled /></el-icon>
                  {{ spotInfo.location }}
                </p>
                <p class="spot-description">{{ spotInfo.description }}</p>
                <div class="spot-price">
                  <span class="price-label">门票价格：</span>
                  <span class="price-value">¥{{ spotInfo.price }}</span>
                  <span class="price-unit">/人</span>
                </div>
              </div>
            </div>

            <el-divider />

            <!-- 数量选择 -->
            <div class="quantity-section">
              <div class="quantity-row">
                <span class="quantity-label">购买数量：</span>
                <div class="quantity-controls">
                  <el-button
                    size="small"
                    :disabled="quantity <= 1"
                    @click="decreaseQuantity"
                    circle
                    >-</el-button
                  >
                  <span class="quantity-display">{{ quantity }}</span>
                  <el-button size="small" @click="increaseQuantity" circle
                    >+</el-button
                  >
                </div>
              </div>
              <div class="subtotal-row">
                <span class="subtotal-label">小计：</span>
                <span class="subtotal-value">¥{{ subtotal }}</span>
              </div>
            </div>

            <el-divider />

            <!-- 游客信息 -->
            <div class="visitor-section">
              <h4>游客信息</h4>
              <div class="visitor-list">
                <div
                  v-for="(visitor, index) in visitorList"
                  :key="index"
                  class="visitor-item"
                >
                  <div class="visitor-header">
                    <span class="visitor-title">游客{{ index + 1 }}</span>
                    <el-button
                      v-if="visitorList.length > 1"
                      type="danger"
                      size="small"
                      text
                      @click="removeVisitor(index)"
                    >
                      删除
                    </el-button>
                  </div>
                  <el-form :model="visitor" label-width="80px" size="small">
                    <el-row :gutter="16">
                      <el-col :span="12">
                        <el-form-item label="姓名" required>
                          <el-input
                            v-model="visitor.name"
                            placeholder="请输入真实姓名"
                          />
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="手机号" required>
                          <el-input
                            v-model="visitor.phone"
                            placeholder="请输入手机号"
                          />
                        </el-form-item>
                      </el-col>
                    </el-row>
                    <el-row :gutter="16">
                      <el-col :span="12">
                        <el-form-item label="身份证" required>
                          <el-input
                            v-model="visitor.idCard"
                            placeholder="请输入身份证号"
                          />
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="游览日期" required>
                          <el-date-picker
                            v-model="visitor.visitDate"
                            type="date"
                            placeholder="选择游览日期"
                            :disabled-date="disabledDate"
                            style="width: 100%"
                          />
                        </el-form-item>
                      </el-col>
                    </el-row>
                  </el-form>
                </div>
              </div>
              <el-button
                v-if="visitorList.length < quantity"
                type="primary"
                plain
                size="small"
                @click="addVisitor"
                style="margin-top: 10px"
              >
                + 添加游客信息
              </el-button>

              <!-- 添加到购物车按钮 -->
              <div class="cart-actions" style="margin-top: 16px">
                <el-button
                  type="success"
                  @click="addCurrentToCart"
                  :disabled="!spotInfo.id"
                >
                  添加到购物车
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧支付信息 -->
      <div class="payment-section">
        <!-- 订单金额 -->
        <el-card class="amount-card">
          <template #header>
            <h3>订单金额</h3>
          </template>
          <div class="amount-details">
            <div class="amount-row">
              <span>{{ isCartMode ? "商品费用：" : "门票费用：" }}</span>
              <span>¥{{ isCartMode ? cartSubtotal : subtotal }}</span>
            </div>
            <div class="amount-row">
              <span>服务费：</span>
              <span>¥{{ isCartMode ? cartServiceFee : serviceFee }}</span>
            </div>
            <div class="amount-row discount">
              <span>优惠减免：</span>
              <span>-¥{{ isCartMode ? cartDiscount : discount }}</span>
            </div>
            <el-divider />
            <div class="amount-row total">
              <span>总计：</span>
              <span class="total-price"
                >¥{{ isCartMode ? cartTotalAmount : totalAmount }}</span
              >
            </div>
          </div>
        </el-card>

        <!-- 支付方式 -->
        <el-card class="payment-method-card">
          <template #header>
            <h3>支付方式</h3>
          </template>
          <el-radio-group v-model="paymentMethod" class="payment-methods">
            <el-radio value="alipay" class="payment-option">
              <div class="payment-item">
                <div class="payment-icon">💰</div>
                <span>支付宝</span>
              </div>
            </el-radio>
            <el-radio value="wechat" class="payment-option">
              <div class="payment-item">
                <div class="payment-icon">💚</div>
                <span>微信支付</span>
              </div>
            </el-radio>
            <el-radio value="bankcard" class="payment-option">
              <div class="payment-item">
                <div class="payment-icon">💳</div>
                <span>银行卡支付</span>
              </div>
            </el-radio>
          </el-radio-group>
        </el-card>

        <!-- 联系人信息 -->
        <el-card class="contact-card">
          <template #header>
            <h3>联系人信息</h3>
          </template>
          <el-form :model="contactInfo" label-width="80px" size="small">
            <el-form-item label="联系人" required>
              <el-input
                v-model="contactInfo.name"
                placeholder="请输入联系人姓名"
              />
            </el-form-item>
            <el-form-item label="手机号" required>
              <el-input
                v-model="contactInfo.phone"
                placeholder="请输入联系人手机号"
              />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input
                v-model="contactInfo.email"
                placeholder="请输入邮箱（可选）"
              />
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 确认支付按钮 -->
        <div class="payment-actions">
          <el-button size="large" @click="$router.go(-1)">返回修改</el-button>
          <el-button
            type="primary"
            size="large"
            @click="handlePayment"
            :loading="paymentLoading"
            :disabled="isCartMode && cartItems.length === 0"
            class="pay-button"
          >
            确认支付 ¥{{ isCartMode ? cartTotalAmount : totalAmount }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 支付确认对话框 -->
    <el-dialog
      v-model="paymentDialogVisible"
      title="确认支付"
      width="500px"
      :close-on-click-modal="false"
    >
      <div class="payment-confirm">
        <div class="confirm-info">
          <h4>订单信息确认</h4>
          <div v-if="isCartMode">
            <p><strong>订单类型：</strong>购物车订单</p>
            <p><strong>商品数量：</strong>{{ cartItems.length }}项</p>
            <p><strong>总数量：</strong>{{ cartTotalQuantity }}件</p>
          </div>
          <div v-else>
            <p><strong>景点：</strong>{{ spotInfo.name }}</p>
            <p><strong>数量：</strong>{{ quantity }}张</p>
          </div>
          <p>
            <strong>支付金额：</strong
            ><span class="highlight"
              >¥{{ isCartMode ? cartTotalAmount : totalAmount }}</span
            >
          </p>
          <p>
            <strong>支付方式：</strong>{{ getPaymentMethodName(paymentMethod) }}
          </p>
        </div>
        <div class="confirm-warning">
          <el-alert
            title="请确认订单信息无误后进行支付"
            type="warning"
            :closable="false"
            show-icon
          />
        </div>
      </div>
      <template #footer>
        <el-button @click="paymentDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="confirmPayment"
          :loading="paymentLoading"
        >
          确认支付
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { LocationFilled } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";

const route = useRoute();
const router = useRouter();

// 购物车模式
const isCartMode = ref(false);

// 购物车数据
interface CartItem {
  id: string;
  type: "hotel" | "spot";
  name: string;
  price: number;
  image: string;
  location: string;
  description: string;
  quantity: number;
  visitors: Array<{
    name: string;
    phone: string;
    idCard: string;
    visitDate: Date | null;
  }>;
  activeCollapse?: string[];
}

const cartItems = ref<CartItem[]>([]);

// 景点信息（单项支付模式）
const spotInfo = ref({
  id: "",
  name: "",
  price: 0,
  image: "",
  location: "",
  description: ""
});

// 数量（单项支付模式）
const quantity = ref(1);

// 游客信息（单项支付模式）
const visitorList = ref([
  {
    name: "",
    phone: "",
    idCard: "",
    visitDate: null
  }
]);

// 联系人信息
const contactInfo = ref({
  name: "",
  phone: "",
  email: ""
});

// 支付方式
const paymentMethod = ref("alipay");

// 支付相关
const paymentLoading = ref(false);
const paymentDialogVisible = ref(false);

// 购物车相关计算属性
const cartSubtotal = computed(() => {
  return cartItems.value.reduce(
    (total, item) => total + item.price * item.quantity,
    0
  );
});

const cartServiceFee = computed(() => {
  return Math.ceil(cartSubtotal.value * 0.05);
});

const cartDiscount = computed(() => {
  if (cartSubtotal.value >= 200) return 30;
  if (cartSubtotal.value >= 100) return 10;
  return 0;
});

const cartTotalAmount = computed(() => {
  return cartSubtotal.value + cartServiceFee.value - cartDiscount.value;
});

const cartTotalQuantity = computed(() => {
  return cartItems.value.reduce((total, item) => total + item.quantity, 0);
});

// 单项支付相关计算属性
const subtotal = computed(() => {
  return spotInfo.value.price * quantity.value;
});

const serviceFee = computed(() => {
  return Math.ceil(subtotal.value * 0.05);
});

const discount = computed(() => {
  if (subtotal.value >= 100) return 10;
  return 0;
});

const totalAmount = computed(() => {
  return subtotal.value + serviceFee.value - discount.value;
});

// 购物车管理方法
const saveCartToStorage = () => {
  localStorage.setItem("tourismCart", JSON.stringify(cartItems.value));
};

const loadCartFromStorage = () => {
  const stored = localStorage.getItem("tourismCart");
  if (stored) {
    try {
      cartItems.value = JSON.parse(stored);
    } catch (error) {
      console.error("Failed to load cart from storage:", error);
      cartItems.value = [];
    }
  }
};

const addToCart = (item: Omit<CartItem, "visitors" | "activeCollapse">) => {
  const existingIndex = cartItems.value.findIndex(
    (cartItem) => cartItem.id === item.id && cartItem.type === item.type
  );

  if (existingIndex >= 0) {
    // 如果已存在，增加数量
    cartItems.value[existingIndex].quantity += item.quantity;
    // 添加对应的游客信息
    for (let i = 0; i < item.quantity; i++) {
      cartItems.value[existingIndex].visitors.push({
        name: "",
        phone: "",
        idCard: "",
        visitDate: null
      });
    }
  } else {
    // 如果不存在，添加新项目
    const newItem: CartItem = {
      ...item,
      visitors: Array(item.quantity)
        .fill(null)
        .map(() => ({
          name: "",
          phone: "",
          idCard: "",
          visitDate: null
        })),
      activeCollapse: []
    };
    cartItems.value.push(newItem);
  }

  saveCartToStorage();
  ElMessage.success("已添加到购物车");
};

const removeFromCart = (index: number) => {
  cartItems.value.splice(index, 1);
  saveCartToStorage();
  ElMessage.success("已从购物车删除");
};

const clearCart = () => {
  ElMessageBox.confirm("确定要清空购物车吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    cartItems.value = [];
    saveCartToStorage();
    ElMessage.success("购物车已清空");
  });
};

const increaseItemQuantity = (index: number) => {
  cartItems.value[index].quantity++;
  // 添加游客信息
  cartItems.value[index].visitors.push({
    name: "",
    phone: "",
    idCard: "",
    visitDate: null
  });
  saveCartToStorage();
};

const decreaseItemQuantity = (index: number) => {
  if (cartItems.value[index].quantity > 1) {
    cartItems.value[index].quantity--;
    // 删除最后一个游客信息
    cartItems.value[index].visitors.pop();
    saveCartToStorage();
  }
};

const addItemVisitor = (itemIndex: number) => {
  cartItems.value[itemIndex].visitors.push({
    name: "",
    phone: "",
    idCard: "",
    visitDate: null
  });
};

const removeItemVisitor = (itemIndex: number, visitorIndex: number) => {
  cartItems.value[itemIndex].visitors.splice(visitorIndex, 1);
  if (
    cartItems.value[itemIndex].quantity >
    cartItems.value[itemIndex].visitors.length
  ) {
    cartItems.value[itemIndex].quantity =
      cartItems.value[itemIndex].visitors.length;
  }
  saveCartToStorage();
};

// 单项支付模式的方法
const increaseQuantity = () => {
  quantity.value++;
  if (visitorList.value.length < quantity.value) {
    addVisitor();
  }
};

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--;
    if (visitorList.value.length > quantity.value) {
      visitorList.value.pop();
    }
  }
};

const addVisitor = () => {
  visitorList.value.push({
    name: "",
    phone: "",
    idCard: "",
    visitDate: null
  });
};

const removeVisitor = (index: number) => {
  visitorList.value.splice(index, 1);
  if (quantity.value > visitorList.value.length) {
    quantity.value = visitorList.value.length;
  }
};

// 添加当前项目到购物车
const addCurrentToCart = () => {
  if (!spotInfo.value.id) {
    ElMessage.error("景点信息缺失");
    return;
  }

  addToCart({
    id: spotInfo.value.id,
    type: "spot",
    name: spotInfo.value.name,
    price: spotInfo.value.price,
    image: spotInfo.value.image,
    location: spotInfo.value.location,
    description: spotInfo.value.description,
    quantity: quantity.value
  });
};

// 模式切换
const handleModeChange = (value: boolean) => {
  isCartMode.value = value;
  if (value) {
    loadCartFromStorage();
  }
};

// 日期禁用
const disabledDate = (time: Date) => {
  return time.getTime() < Date.now() - 8.64e7;
};

// 获取支付方式名称
const getPaymentMethodName = (method: string) => {
  const names: Record<string, string> = {
    alipay: "支付宝",
    wechat: "微信支付",
    bankcard: "银行卡支付"
  };
  return names[method] || method;
};

// 处理支付
const handlePayment = () => {
  if (!validateForm()) {
    return;
  }
  paymentDialogVisible.value = true;
};

// 表单验证
const validateForm = () => {
  if (isCartMode.value) {
    // 验证购物车项目
    for (let i = 0; i < cartItems.value.length; i++) {
      const item = cartItems.value[i];
      for (let j = 0; j < item.visitors.length; j++) {
        const visitor = item.visitors[j];
        if (
          !visitor.name ||
          !visitor.phone ||
          !visitor.idCard ||
          !visitor.visitDate
        ) {
          ElMessage.error(`请完善${item.name}的游客${j + 1}信息`);
          return false;
        }
        if (!/^1[3-9]\d{9}$/.test(visitor.phone)) {
          ElMessage.error(`${item.name}的游客${j + 1}手机号格式不正确`);
          return false;
        }
        if (!/^\d{17}[\dX]$/.test(visitor.idCard)) {
          ElMessage.error(`${item.name}的游客${j + 1}身份证号格式不正确`);
          return false;
        }
      }
    }
  } else {
    // 验证单项订单
    for (let i = 0; i < visitorList.value.length; i++) {
      const visitor = visitorList.value[i];
      if (
        !visitor.name ||
        !visitor.phone ||
        !visitor.idCard ||
        !visitor.visitDate
      ) {
        ElMessage.error(`请完善游客${i + 1}的信息`);
        return false;
      }
      if (!/^1[3-9]\d{9}$/.test(visitor.phone)) {
        ElMessage.error(`游客${i + 1}的手机号格式不正确`);
        return false;
      }
      if (!/^\d{17}[\dX]$/.test(visitor.idCard)) {
        ElMessage.error(`游客${i + 1}的身份证号格式不正确`);
        return false;
      }
    }
  }

  // 验证联系人信息
  if (!contactInfo.value.name || !contactInfo.value.phone) {
    ElMessage.error("请完善联系人信息");
    return false;
  }
  if (!/^1[3-9]\d{9}$/.test(contactInfo.value.phone)) {
    ElMessage.error("联系人手机号格式不正确");
    return false;
  }

  return true;
};

// 确认支付
const confirmPayment = async () => {
  paymentLoading.value = true;

  try {
    await new Promise((resolve) => setTimeout(resolve, 2000));

    paymentDialogVisible.value = false;

    const amount = isCartMode.value ? cartTotalAmount.value : totalAmount.value;

    if (isCartMode.value) {
      // 清空购物车
      cartItems.value = [];
      saveCartToStorage();
    }

    await ElMessageBox.alert(`支付成功！订单金额：¥${amount}`, "支付成功", {
      type: "success",
      confirmButtonText: "查看订单"
    });

    router.push("/user/spot");
  } catch (error) {
    ElMessage.error("支付失败，请重试");
  } finally {
    paymentLoading.value = false;
  }
};

// 监听购物车变化，自动保存
watch(
  cartItems,
  () => {
    saveCartToStorage();
  },
  { deep: true }
);

// 初始化数据
onMounted(() => {
  // 加载购物车数据
  loadCartFromStorage();

  // 从路由参数获取景点信息
  const query = route.query;
  if (query.spotId) {
    spotInfo.value = {
      id: query.spotId as string,
      name: (query.spotName as string) || "未知景点",
      price: Number(query.spotPrice) || 0,
      image: (query.spotImage as string) || "",
      location: (query.spotLocation as string) || "",
      description: (query.spotDescription as string) || ""
    };
  } else if (cartItems.value.length > 0) {
    // 如果没有单项信息但有购物车数据，切换到购物车模式
    isCartMode.value = true;
  }
});
</script>

<style scoped lang="scss">
.payment-container {
  background: #f5f7fa;
  min-height: 100vh;
  padding: 20px;
}

.breadcrumb-section {
  background: #fff;
  padding: 16px 24px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

  .mode-switch {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-top: 12px;

    .cart-count {
      color: #409eff;
      font-size: 14px;
      font-weight: bold;
    }
  }
}

.payment-content {
  display: flex;
  gap: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.order-section {
  flex: 2;
}

.payment-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    h3 {
      margin: 0;
      color: #333;
    }
  }
}

// 购物车样式
.cart-section {
  .empty-cart {
    text-align: center;
    padding: 40px 0;
  }

  .cart-items {
    .cart-item {
      border: 1px solid #e4e7ed;
      border-radius: 8px;
      padding: 16px;
      margin-bottom: 16px;
      background: #fafafa;

      .item-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;
      }
    }
  }
}

.item-info-section {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;

  .item-image {
    width: 120px;
    height: 90px;
    border-radius: 8px;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .item-details {
    flex: 1;

    .item-name {
      font-size: 16px;
      font-weight: bold;
      color: #333;
      margin: 0 0 8px 0;
    }

    .item-location {
      display: flex;
      align-items: center;
      gap: 4px;
      color: #666;
      font-size: 14px;
      margin: 0 0 8px 0;
    }

    .item-description {
      color: #999;
      font-size: 14px;
      line-height: 1.4;
      margin: 0 0 12px 0;
    }

    .item-price {
      .price-label {
        color: #666;
        font-size: 14px;
      }

      .price-value {
        color: #ff6b35;
        font-size: 18px;
        font-weight: bold;
        margin: 0 4px;
      }

      .price-unit {
        color: #999;
        font-size: 14px;
      }
    }
  }
}

// 原有样式保持不变
.spot-info-section {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;

  .spot-image {
    width: 120px;
    height: 90px;
    border-radius: 8px;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .spot-details {
    flex: 1;

    .spot-name {
      font-size: 18px;
      font-weight: bold;
      color: #333;
      margin: 0 0 8px 0;
    }

    .spot-location {
      display: flex;
      align-items: center;
      gap: 4px;
      color: #666;
      font-size: 14px;
      margin: 0 0 8px 0;
    }

    .spot-description {
      color: #999;
      font-size: 14px;
      line-height: 1.4;
      margin: 0 0 12px 0;
    }

    .spot-price {
      .price-label {
        color: #666;
        font-size: 14px;
      }

      .price-value {
        color: #ff6b35;
        font-size: 20px;
        font-weight: bold;
        margin: 0 4px;
      }

      .price-unit {
        color: #999;
        font-size: 14px;
      }
    }
  }
}

.quantity-section {
  .quantity-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;

    .quantity-label {
      font-size: 16px;
      color: #333;
    }

    .quantity-controls {
      display: flex;
      align-items: center;
      gap: 12px;

      .quantity-display {
        font-size: 18px;
        font-weight: bold;
        min-width: 30px;
        text-align: center;
      }
    }
  }

  .subtotal-row {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .subtotal-label {
      font-size: 16px;
      color: #333;
    }

    .subtotal-value {
      font-size: 18px;
      font-weight: bold;
      color: #ff6b35;
    }
  }
}

.visitor-section {
  h4 {
    margin: 0 0 16px 0;
    color: #333;
  }

  .visitor-item {
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 12px;

    .visitor-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;

      .visitor-title {
        font-weight: bold;
        color: #333;
      }
    }
  }
}

.cart-actions {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px dashed #ddd;
  text-align: center;
}

.amount-card {
  .amount-details {
    .amount-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;
      font-size: 14px;

      &.discount {
        color: #67c23a;
      }

      &.total {
        font-size: 18px;
        font-weight: bold;
        color: #333;

        .total-price {
          color: #ff6b35;
          font-size: 24px;
        }
      }
    }
  }
}

.payment-method-card {
  .payment-methods {
    display: flex;
    flex-direction: column;
    gap: 12px;

    .payment-option {
      width: 100%;
      margin: 0;

      :deep(.el-radio__label) {
        width: 100%;
      }

      .payment-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 8px;
        border: 1px solid #e4e7ed;
        border-radius: 8px;
        transition: all 0.3s;

        &:hover {
          border-color: #409eff;
          background: #f0f9ff;
        }

        .payment-icon {
          width: 32px;
          height: 32px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 20px;
        }
      }
    }

    :deep(.el-radio.is-checked) {
      .payment-item {
        border-color: #409eff;
        background: #f0f9ff;
      }
    }
  }
}

.payment-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;

  .pay-button {
    flex: 1;
    height: 48px;
    font-size: 16px;
    font-weight: bold;
  }
}

.payment-confirm {
  .confirm-info {
    margin-bottom: 20px;

    h4 {
      margin: 0 0 12px 0;
      color: #333;
    }

    p {
      margin: 0 0 8px 0;
      color: #666;

      .highlight {
        color: #ff6b35;
        font-weight: bold;
        font-size: 18px;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .payment-content {
    flex-direction: column;
  }

  .spot-info-section,
  .item-info-section {
    flex-direction: column;

    .spot-image,
    .item-image {
      width: 100%;
      height: 160px;
    }
  }

  .quantity-row {
    flex-direction: column;
    align-items: flex-start !important;
    gap: 12px;
  }

  .payment-actions {
    flex-direction: column;
  }
}
</style>
