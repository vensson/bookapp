package com.example.testdkdn

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun GioHangScreen() {
    val context = LocalContext.current
    var cartItems by remember { mutableStateOf(CartManager.getCart(context)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD)) // Giữ nguyên màu nền như thiết kế gốc
    ) {
        // Danh sách sản phẩm
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(cartItems) { item ->
                CartItem(
                    book = item,
                    onRemove = {
                        CartManager.removeFromCart(context, item)
                        cartItems = CartManager.getCart(context)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // PHẦN BẠN THIẾT KẾ - GIỮ NGUYÊN
            item {
                Spacer(modifier = Modifier.height(12.dp))
                CheckoutOptionsSection() // Giữ nguyên phần chọn phương thức thanh toán
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Thanh toán - Giữ nguyên style của bạn
        CheckoutSummary(
            totalPrice = cartItems.sumOf { it.price * (it.quantity ?: 1) }.toInt(),
            onOrderClick = {
                Toast.makeText(context, "Đặt hàng thành công", Toast.LENGTH_SHORT).show()
            }
        )

    }
}

// CartItem được điều chỉnh để phù hợp với Book class
@Composable
fun CartItem(

    book: Book,
    onRemove: () -> Unit
) {
    LaunchedEffect(Unit) {
        println("Giá sách: ${book.title} - ${book.price}")
    }

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

                // Phần chỉnh số lượng - Giữ nguyên phong cách của bạn
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Text("-", fontSize = 18.sp)
                    }

                    Text("$quantity", modifier = Modifier.padding(horizontal = 8.dp))

                    IconButton(
                        onClick = { quantity++ },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Text("+", fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = onRemove) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Xóa",
                            tint = Color(0xFFD00000) // Màu đỏ như thiết kế của bạn
                        )
                    }
                }
            }
        }
    }
}

// Giữ nguyên nguyên bản các composable bạn đã thiết kế
@Composable
fun CheckoutOptionsSection() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp)
    ) {
        CheckoutOption("Phương thức thanh toán", "Chưa chọn") {
            val intent = Intent(context, PhuongThucTT::class.java)
            context.startActivity(intent)
        }
        Divider()
        CheckoutOption("Địa chỉ giao hàng", "Chưa chọn") {
            /* Mở màn hình chọn địa chỉ */
        }
        Divider()
        CheckoutOption("Khuyến mãi", "Chưa áp dụng") {
            /* Mở màn hình khuyến mãi */
        }
    }
}

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
                containerColor = Color(0xFF0077B6) // Màu xanh như thiết kế của bạn
            )
        ) {
            Text("Đặt đơn hàng", color = Color.White)
        }
    }
}