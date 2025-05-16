package com.example.testdkdn

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.testdkdn.ui.theme.TestDKDNTheme

class ChitietActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imageUrl = intent.getStringExtra("image_url") ?: ""
        val title = intent.getStringExtra("title") ?: ""
        val author = intent.getStringExtra("author") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val category = intent.getStringExtra("category") ?: ""
        val formattedPrice = intent.getStringExtra("price") ?: ""
        val rating = intent.getDoubleExtra("rating", 0.0)

        setContent {
            TestDKDNTheme {
                ChitietScreen(
                    imageUrl = imageUrl,
                    title = title,
                    author = author,
                    description = description,
                    category = category,
                    rating = rating,
                    formattedPrice = formattedPrice
                )
            }
        }
    }
}

@Composable
fun ChitietScreen(
    imageUrl: String,
    title: String,
    author: String,
    description: String,
    category: String,
    rating: Double,
    formattedPrice: String
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F8FF))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF023E8A)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Tác giả: $author",
            fontSize = 16.sp,
            color = Color(0xFF0077B6)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Thể loại: $category",
            fontSize = 14.sp,
            color = Color(0xFF023E8A)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Đánh giá: $rating ★",
            fontSize = 14.sp,
            color = Color(0xFF0077B6)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Giá: $formattedPrice VNĐ",
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFd00000)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = description,
            fontSize = 14.sp,
            color = Color.DarkGray,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val book = Book(
                    id = title + author,
                    title = title,
                    author = author,
                    description = description,
                    category = category,
                    rating = rating,
                    image_url = imageUrl,
                    price = formattedPrice.toDoubleOrNull() ?: 0.0
                )

                CartManager.addToCart(context, book)
                Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077B6)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Thêm vào giỏ hàng", fontSize = 16.sp, color = Color.White)
        }
    }
}
