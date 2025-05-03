package com.example.testdkdn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testdkdn.ui.theme.TestDKDNTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.foundation.clickable



class TrangchuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lấy role từ Intent
        val role = intent.getIntExtra("ROLE", 1) // Default role = 1 nếu không có

        setContent {
            TestDKDNTheme {
                MainScreen(role)
            }
        }
    }

    @Composable
    fun MainScreen(role: Int) {
        var selectedIndex by remember { mutableStateOf(0) }

        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                when (selectedIndex) {
                    0 -> Trangchu(role)
                    1 -> GioHang()
                    2 -> CaNhan()
                }
            }

            NavigationBar(containerColor = Color(0xFF0077B6)) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Trang chủ") },
                    label = { Text("Trang chủ", color = Color.White) },
                    selected = selectedIndex == 0,
                    onClick = { selectedIndex = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Giỏ hàng") },
                    label = { Text("Giỏ hàng", color = Color.White) },
                    selected = selectedIndex == 1,
                    onClick = { selectedIndex = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Cá nhân") },
                    label = { Text("Cá nhân", color = Color.White) },
                    selected = selectedIndex == 2,
                    onClick = { selectedIndex = 2 }
                )
            }
        }
    }

    @Composable
    fun Trangchu(role: Int) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .background(Color(0xFFE3F2FD)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sách nổi bật",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0077B6),
                modifier = Modifier.padding(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                BookCard("sach1", "Trân trọng chính mình", "120.000đ", role)
                BookCard("sach2", "Vấp ngã để trưởng thành ", "95.000đ", role)
            }
        }
    }

    @Composable
    fun BookCard(imageName: String, title: String, price: String, role: Int) {
        val context = LocalContext.current
        val imageId = remember(imageName) {
            context.resources.getIdentifier(imageName, "drawable", context.packageName)
        }

        Card(
            modifier = Modifier
                .width(180.dp)
                .padding(4.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = imageId),
                    contentDescription = title,
                    modifier = Modifier
                        .height(190.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = price,
                    color = Color(0xFF0077B6),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        val intent = Intent(context, ChitietActivity::class.java).apply {
                            putExtra("imageName", imageName)
                            putExtra("title", title)
                            putExtra("price", price)
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077B6)),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Text("Xem", fontSize = 12.sp)
                }

                // Chỉ hiển thị nút sửa nếu role là admin (role == 2)
                if (role == 2) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            // Xử lý nút sửa
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        Text("Sửa", fontSize = 12.sp)
                    }
                }
            }
        }
    }




    data class CartItemData(val imageName: String, val title: String, val price: String)

    @Composable
    fun GioHang() {
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

            // Tổng kết thanh toán sẽ luôn hiển thị dưới cùng
            CheckoutSummary(totalPrice = totalPrice, onOrderClick = {
                /* xử lý đặt hàng */
            })
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
            // Ảnh sách
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
               // Text(text = "Tiểu thuyết", fontSize = 12.sp, color = Color.Gray)
                Text(text = price, color = Color.Black, fontWeight = FontWeight.SemiBold)


                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick =  {
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
                    // xóa
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa")
                    }



//gaim so luong
                    IconButton(onClick = {}) {
                        Text("-", fontSize = 20.sp, color = Color.Black)
                    }
                    Text("1")
                    //tang soluong
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
            // Thêm mục: khuyến mãi, địa chỉ, phương thức

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
        Column(modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp)) {

            CheckoutOption("Phương thức thanh toán", "Chưa chọn") { /* mở chọn */ }
            Divider()
            CheckoutOption("Địa chỉ giao hàng", "Chưa chọn") { /* mở chọn */ }
            Divider()
            CheckoutOption("Khuyến mãi", "Chưa áp dụng") { /* mở chọn */ }
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
    fun CaNhan() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Thông tin cá nhân", fontSize = 20.sp, color = Color.Gray)
        }
    }
}

