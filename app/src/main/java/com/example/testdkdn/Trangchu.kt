package com.example.testdkdn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

class TrangchuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestDKDNTheme {
                MainScreen()
            }
        }
    }

    @Composable
    fun MainScreen() {
        var selectedIndex by remember { mutableStateOf(0) }

        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                when (selectedIndex) {
                    0 -> Trangchu()
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
    fun Trangchu() {
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
                BookCard("sach1", "Trân trọng chính mình", "120.000đ")
                BookCard("sach2", "Vấp ngã để trưởng thành ", "95.000đ")
            }
        }
    }

    @Composable
    fun BookCard(imageName: String, title: String, price: String) {
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
                    onClick = { /* TODO */ },
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077B6)),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Text("Xem", fontSize = 12.sp)
                }
            }
        }
    }


    @Composable
    fun GioHang() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Giỏ hàng đang trống!", fontSize = 20.sp, color = Color.Gray)
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
