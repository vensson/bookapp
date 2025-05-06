package com.example.testdkdn

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GioHangScreen() {
    val cartItems = listOf(
        CartItemData("sach1", "Trân trọng chính mình", "120.000đ"),
        CartItemData("sach2", "Vấp ngã để trưởng thành", "45.000đ")
    )

    val totalPrice = cartItems.sumOf {
        it.price.replace("đ", "").replace(".", "").toInt()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD))
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp)
        ) {
            items(cartItems) { item ->
                CartItem(item.imageName, item.title, item.price)
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }

            item { CheckoutOptionsSection() }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        CheckoutSummary(totalPrice = totalPrice, onOrderClick = {})
    }
}

@Composable
fun CartItem(
    imageName: String,
    title: String,
    price: String
) {
    val context = LocalContext.current
    val imageId = remember(imageName) {
        context.resources.getIdentifier(imageName, "drawable", context.packageName)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .border(1.dp, Color.LightGray, shape = RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = title,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold)
            Text(text = price, color = Color.Black, fontWeight = FontWeight.SemiBold)

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    val intent = Intent(context, ChitietActivity::class.java).apply {
                        putExtra("imageName", imageName)
                        putExtra("title", title)
                        putExtra("price", price)
                    }
                    context.startActivity(intent)
                }) {
                    Icon(Icons.Default.Edit, contentDescription = "Sửa")
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Delete, contentDescription = "Xóa")
                }

                IconButton(onClick = {}) {
                    Text("-", fontSize = 20.sp, color = Color.Black)
                }
                Text("1")
                IconButton(onClick = {}) {
                    Text("+", fontSize = 20.sp, color = Color.Black)
                }
            }
        }
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
            Text("Giá:")
            Text("$totalPrice vnd")
        }

        Button(
            onClick = onOrderClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077B6))
        ) {
            Text("Đặt đơn hàng", color = Color.White)
        }
    }
}

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
            // Mở giao diện chọn địa chỉ giao hàng
        }

        Divider()

        CheckoutOption("Khuyến mãi", "Chưa áp dụng") {
            // Mở giao diện chọn khuyến mãi
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

data class CartItemData(val imageName: String, val title: String, val price: String)