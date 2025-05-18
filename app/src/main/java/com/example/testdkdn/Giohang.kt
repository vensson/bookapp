package com.example.testdkdn

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

@Composable
fun GioHangScreen() {
    val context = LocalContext.current
    var cartItems by remember { mutableStateOf(CartManager.getCart(context)) }
    var firestoreBooks by remember { mutableStateOf<List<Book>>(emptyList()) }
    val user = Firebase.auth.currentUser
    var expanded by remember { mutableStateOf(true) }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var isFirestoreLoaded by remember { mutableStateOf(false) }
    var showAddressError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var addressError by remember { mutableStateOf<String?>(null) }

    // Load dữ liệu sách từ Firestore
    LaunchedEffect(true) {
        Firebase.firestore.collection("books")
            .get()
            .addOnSuccessListener { result ->
                firestoreBooks = result.mapNotNull { it.toObject(Book::class.java).copy(id = it.id) }
                isFirestoreLoaded = true
            }
    }

    // Cập nhật giỏ hàng với giá từ Firestore
    val updatedCartItems = remember(cartItems, firestoreBooks) {
        if (isFirestoreLoaded) {
            cartItems.map { cartBook ->
                firestoreBooks.find { it.title == cartBook.title }?.let { matchedBook ->
                    cartBook.copy(price = matchedBook.price)
                } ?: cartBook
            }
        } else {
            cartItems
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (updatedCartItems.isEmpty()) {
            EmptyCartView(onShopClick = {
                val intent = Intent(context, TrangchuActivity::class.java)
                context.startActivity(intent)
            })
        } else {
            // Phần thông tin giao hàng
            DeliveryInfoSection(
                phone = phone,
                address = address,
                expanded = expanded,
                showError = showAddressError,
                phoneError = phoneError,
                addressError = addressError,
                onPhoneChange = {
                    phone = it
                    phoneError = null
                    showAddressError = false
                },
                onAddressChange = {
                    address = it
                    addressError = null
                    showAddressError = false
                },
                onExpandChange = { expanded = it }
            )

            // Danh sách sản phẩm
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(updatedCartItems) { item ->
                    CartItem(
                        book = item,
                        onRemove = {
                            CartManager.removeFromCart(context, item)
                            cartItems = CartManager.getCart(context)
                        },
                        onQuantityChange = { newQuantity ->
                            CartManager.updateCartItem(context, item.copy(quantity = newQuantity))
                            cartItems = CartManager.getCart(context)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Phần thanh toán
            CheckoutSummary(
                totalPrice = updatedCartItems.sumOf { it.price * (it.quantity ?: 1) }.toInt(),
                onOrderClick = {
                    // Validate thông tin
                    var isValid = true

                    if (phone.isBlank()) {
                        phoneError = "Vui lòng nhập số điện thoại"
                        isValid = false
                    } else if (!phone.matches(Regex("^[0-9]{10,11}\$"))) {
                        phoneError = "Số điện thoại không hợp lệ"
                        isValid = false
                    }

                    if (address.isBlank()) {
                        addressError = "Vui lòng nhập địa chỉ"
                        isValid = false
                    } else if (address.length < 10) {
                        addressError = "Địa chỉ quá ngắn"
                        isValid = false
                    }

                    if (!isValid) {
                        showAddressError = true
                        expanded = true
                        return@CheckoutSummary
                    }

                    if (!isFirestoreLoaded) {
                        Toast.makeText(context, "Đang tải dữ liệu, vui lòng chờ...", Toast.LENGTH_SHORT).show()
                        return@CheckoutSummary
                    }

                    // Tạo đơn hàng
                    createOrder(context, user, phone, address, updatedCartItems)
                }
            )
        }
    }
}

@Composable
fun EmptyCartView(onShopClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.giohangrong),
            contentDescription = "Giỏ hàng trống",
            modifier = Modifier.size(200.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Ôi! Bạn chưa thêm sản phẩm nào vào giỏ hàng",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryInfoSection(
    phone: String,
    address: String,
    expanded: Boolean,
    showError: Boolean,
    phoneError: String?,
    addressError: String?,
    onPhoneChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onExpandChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (showError) Color(0xFFFFEBEE) else Color.White
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Thông tin giao hàng",
                    fontWeight = FontWeight.Bold,
                    color = if (showError) Color.Red else Color.Unspecified
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.clickable { onExpandChange(!expanded) }
                )
            }

            if (expanded) {
                if (showError) {
                    Text(
                        text = "Vui lòng kiểm tra lại thông tin giao hàng",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                OutlinedTextField(
                    value = phone,
                    onValueChange = onPhoneChange,
                    label = { Text("Số điện thoại*") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = phoneError != null,
                    supportingText = {
                        phoneError?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = onAddressChange,
                    label = { Text("Địa chỉ*") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = addressError != null,
                    supportingText = {
                        addressError?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )
            }
        }
    }
}

private fun createOrder(
    context: Context,
    user: FirebaseUser?,
    phone: String,
    address: String,
    items: List<Book>
) {
    val orderData = hashMapOf(
        "userId" to user?.uid,
        "userEmail" to user?.email,
        "phone" to phone,
        "address" to address,
        "items" to items.map {
            hashMapOf(
                "title" to it.title,
                "quantity" to (it.quantity ?: 1),
                "price" to it.price,
                "imageUrl" to it.image_url,
                "total" to (it.price * (it.quantity ?: 1)))
        },
        "totalAmount" to items.sumOf { it.price * (it.quantity ?: 1) },
        "status" to "pending",
        "createdAt" to System.currentTimeMillis()
    )

    Firebase.firestore.collection("orders")
        .add(orderData)
        .addOnSuccessListener { docRef ->
            CartManager.clearCart(context)
            Toast.makeText(context, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show()

            val intent = Intent(context, ThongBao::class.java).apply {
                putExtra("orderId", docRef.id)
            }
            context.startActivity(intent)
        }
        .addOnFailureListener {
            Toast.makeText(context, "Lỗi khi đặt hàng: ${it.message}", Toast.LENGTH_SHORT).show()
        }
}

// CartItem và CheckoutSummary giữ nguyên như trước

@Composable
fun CartItem(
    book: Book,
    onRemove: () -> Unit,
    onQuantityChange: (Int) -> Unit
) {
    var quantity by remember { mutableStateOf(book.quantity ?: 1) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = book.image_url,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(book.title, fontWeight = FontWeight.Bold)
                Text("${book.price.toInt()} VNĐ", color = Color(0xFF0077B6))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (quantity > 1) {
                                quantity--
                                onQuantityChange(quantity)
                            }
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Text("-", fontSize = 18.sp)
                    }

                    Text("$quantity", modifier = Modifier.padding(horizontal = 8.dp))

                    IconButton(
                        onClick = {
                            quantity++
                            onQuantityChange(quantity)
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Text("+", fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = onRemove) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Xóa",
                            tint = Color(0xFFD00000)
                        )
                    }
                }
            }
        }
    }
}

// Các composable khác giữ nguyên như bản gốc


@Composable
fun CheckoutOption(title: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(value, color = Color.Gray, fontSize = 13.sp)
        }
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Chọn")
    }
}

@Composable
fun CheckoutSummary(totalPrice: Int, onOrderClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White)
    ) {
        Text("Thanh toán", fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Tổng tiền:")
            Text("${totalPrice} VNĐ", fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = onOrderClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0077B6)
            )
        ) {
            Text("Đặt đơn hàng", color = Color.White)
        }
    }
}