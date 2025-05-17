package com.example.testdkdn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.*

class ThongBao : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val orderId = intent.getStringExtra("orderId")
        setContent {
            OrderConfirmationTheme {
                orderId?.let {
                    OrderConfirmationScreen(orderId = it)
                }
            }
        }
    }
}

@Composable
fun OrderConfirmationScreen(orderId: String) {
    var orderData by remember { mutableStateOf<Map<String, Any>?>(null) }
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

    LaunchedEffect(orderId) {
        FirebaseFirestore.getInstance().collection("orders").document(orderId)
            .get()
            .addOnSuccessListener {
                orderData = it.data
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Hình nền với hiệu ứng mờ
        Image(
            painter = painterResource(id = R.drawable.nen3),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.1f)
        )

        // Card chính chứa thông tin hóa đơn
        orderData?.let { data ->
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    // Header hóa đơn
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(8.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFF0077B6), Color(0xFF023E8A))
                                ),
                                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                            )
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = "Success",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "ĐẶT HÀNG THÀNH CÔNG",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Mã đơn hàng: $orderId",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                    }

                    // Thông tin khách hàng
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        Text(
                            "THÔNG TIN KHÁCH HÀNG",
                            color = Color(0xFF0077B6),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        InfoRow("Họ tên:", data["userName"]?.toString() ?: "Không có")
                        InfoRow("Email:", data["userEmail"]?.toString() ?: "Không có")
                        InfoRow("SĐT:", data["phone"]?.toString() ?: "Không có")
                        InfoRow("Địa chỉ:", data["address"]?.toString() ?: "Không có")
                    }

                    // Danh sách sản phẩm header
                    Text(
                        "CHI TIẾT ĐƠN HÀNG",
                        color = Color(0xFF0077B6),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                    )
                }

                // Danh sách sản phẩm
                val items = data["items"] as? List<Map<String, Any>> ?: emptyList()
                items(items) { item ->
                    OrderItemCard(item, numberFormat)
                }

                // Tổng thanh toán
                item {
                    val total = data["total"]?.toString()?.toDoubleOrNull() ?: 0.0
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            "TỔNG CỘNG:",
                            fontSize = 16.sp,
                            color = Color(0xFF555555)
                        )
                        Text(
                            "${numberFormat.format(total)} VNĐ",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD00000)
                        )
                    }

                    // Footer
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8F8F8))
                            .padding(16.dp)
                            .shadow(4.dp, RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Cảm ơn bạn đã mua hàng!",
                            fontSize = 16.sp,
                            color = Color(0xFF0077B6),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Đơn hàng sẽ được giao trong 2-3 ngày làm việc",
                            fontSize = 14.sp,
                            color = Color(0xFF777777),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        } ?: run {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF0077B6)
            )
        }
    }
}

@Composable
fun OrderItemCard(item: Map<String, Any>, numberFormat: NumberFormat) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .shadow(2.dp, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item["imageUrl"]?.toString(),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item["title"]?.toString() ?: "",
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "Số lượng: ${item["quantity"]}",
                    fontSize = 14.sp,
                    color = Color(0xFF555555)
                )
            }

            Text(
                text = "${numberFormat.format(
                    (item["price"] as? Number)?.toDouble()?.times(
                        (item["quantity"] as? Number)?.toLong() ?: 1L
                    ) ?: 0.0
                )}đ",
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0077B6)
            )
                    }


    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            color = Color(0xFF555555),
            fontWeight = FontWeight.Medium
        )
        Text(
            value,
            color = Color.Black,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun OrderConfirmationTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF0077B6),
            secondary = Color(0xFF023E8A),
            background = Color(0xFFF5F5F5)
        ),
        content = content
    )
}