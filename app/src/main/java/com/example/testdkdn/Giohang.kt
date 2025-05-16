package com.example.testdkdn

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImage
import com.example.testdkdn.CartManager.getCart
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GioHangScreen(onBackClick: () -> Unit = {}) {
    val context = LocalContext.current
    var cartItems by remember { mutableStateOf(CartManager.getCart(context)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // App Bar
        TopAppBar(
            title = { Text("Giỏ hàng") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { /* Xử lý tìm kiếm */ }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }
        )

        if (cartItems.isEmpty()) {
            EmptyCartView()
        } else {
            CartContent(
                cartItems = cartItems,
                onItemRemove = { book ->
                    CartManager.removeFromCart(context, book)
                    cartItems = CartManager.getCart(context)
                },
                onQuantityChange = { book, newQuantity ->
                    CartManager.updateQuantity(context, book, newQuantity)
                    cartItems = CartManager.getCart(context)
                }
            )
        }
    }
}

@Composable
fun EmptyCartView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = "Empty Cart",
            tint = Color.Gray,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Giỏ hàng của bạn đang trống", fontSize = 18.sp, color = Color.Gray)
    }
}

@Composable
fun CartContent(
    cartItems: List<Book>,
    onItemRemove: (Book) -> Unit,
    onQuantityChange: (Book, Int) -> Unit
) {
    val totalPrice = cartItems.sumOf { it.price * (it.quantity ?: 1) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(cartItems, key = { it.id }) { book ->
                CartItem(
                    book = book,
                    onRemove = { onItemRemove(book) },
                    onQuantityChange = { newQuantity ->
                        onQuantityChange(book, newQuantity)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Checkout Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White, shape = RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Tổng cộng:", fontWeight = FontWeight.Bold)
                Text(
                    "${totalPrice.toInt()} VNĐ",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD32F2F)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
//                    val intent = Intent(context, ThanhToanActivity::class.java)
//                    startActivity(context, intent, null)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF388E3C)
                )
            ) {
                Text("Tiến hành thanh toán", color = Color.White)
            }
        }
    }
}

@Composable
fun CartItem(
    book: Book,
    onRemove: () -> Unit,
    onQuantityChange: (Int) -> Unit
) {
    var quantity by remember { mutableStateOf(book.quantity ?: 1) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = book.image_url,
                contentDescription = book.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(book.title, fontWeight = FontWeight.Bold)
                Text("${book.price.toInt()} VNĐ", color = Color(0xFF757575))

                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
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

                    Text(
                        text = "$quantity",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        fontSize = 16.sp
                    )

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
                            contentDescription = "Remove",
                            tint = Color(0xFFD32F2F)
                        )
                    }
                }
            }
        }
    }
}

