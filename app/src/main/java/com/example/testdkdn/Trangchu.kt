package com.example.testdkdn
import android.content.Intent
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.items
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter


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
        val isAdmin = role == 2 // Kiểm tra có phải admin không

        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                when (selectedIndex) {
                    0 -> Trangchu(role)
                    1 -> GioHang()
                    2 -> CaNhan()
                    3 -> AdminScreen() // Màn hình Admin
                }
            }

            NavigationBar(containerColor = Color(0xFF0077B6)) {
                // Trang chủ
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Trang chủ") },
                    label = { Text("Trang chủ", color = Color.White) },
                    selected = selectedIndex == 0,
                    onClick = { selectedIndex = 0 }
                )

                // Giỏ hàng
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Giỏ hàng") },
                    label = { Text("Giỏ hàng", color = Color.White) },
                    selected = selectedIndex == 1,
                    onClick = { selectedIndex = 1 }
                )

                // Cá nhân
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Cá nhân") },
                    label = { Text("Cá nhân", color = Color.White) },
                    selected = selectedIndex == 2,
                    onClick = { selectedIndex = 2 }
                )

                // Admin (chỉ hiển thị khi là admin)
                if (isAdmin) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                        label = { Text("Admin", color = Color.White) },
                        selected = selectedIndex == 3,
                        onClick = { selectedIndex = 3 }
                    )
                }
            }
        }
    }

    @Composable
    fun AdminScreen() {
        val context = LocalContext.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color(0xFFE3F2FD)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )  {
            Text(
                text = "Trang Quản Trị",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0077B6)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Các chức năng quản trị
            AdminFunctionButton(
                icon = Icons.Default.List,
                text = "Quản lý Sách",
                onClick = {
                    val intent = Intent(context, QuanLySachActivity::class.java)
                    context.startActivity(intent)
                }
            )

            AdminFunctionButton(
                icon = Icons.Default.People,
                text = "Quản lý Người dùng",
                onClick = {
                    // Mở màn hình quản lý người dùng
                }
            )

            AdminFunctionButton(
                icon = Icons.Default.BarChart,
                text = "Thống kê Doanh thu",
                onClick = {
                    // Mở màn hình thống kê
                }
            )
        }
    }

    @Composable
    fun AdminFunctionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: () -> Unit) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF0077B6)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = text, fontSize = 16.sp)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowForward,
                    contentDescription = "Xem chi tiết",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Trangchu(role: Int) {
        val context = LocalContext.current
        val books = remember { mutableStateListOf<Book>() }
        val categories = remember { mutableStateListOf<String>() }
        var selectedCategory by remember { mutableStateOf("Tất cả") }
        var expanded by remember { mutableStateOf(false) }

        // Lấy dữ liệu sách và danh mục từ Firebase Firestore
        LaunchedEffect(Unit) {
            // Lấy danh sách sách
            FirebaseFirestore.getInstance().collection("books")
                .get()
                .addOnSuccessListener { result ->
                    books.clear()
                    for (document in result) {
                        val book = document.toObject(Book::class.java).apply {
                            id = document.id
                        }
                        books.add(book)

                        // Thêm category vào danh sách nếu chưa có
                        if (!categories.contains(book.category)) {
                            categories.add(book.category)
                        }
                    }
                }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .background(Color(0xFFE3F2FD)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Phần dropdown để chọn category
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        label = { Text("Chọn thể loại") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        // Option "Tất cả"
                        DropdownMenuItem(
                            text = { Text("Tất cả") },
                            onClick = {
                                selectedCategory = "Tất cả"
                                expanded = false
                            }
                        )

                        // Các category từ Firebase
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Text(
                text = "Sách nổi bật",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0077B6),
                modifier = Modifier.padding(8.dp)
            )

            // Lọc sách theo category được chọn
            val filteredBooks = if (selectedCategory == "Tất cả") {
                books
            } else {
                books.filter { it.category == selectedCategory }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredBooks) { book ->
                    FirebaseBookCard(book = book, role = role)
                }
            }

            // Nút thêm sản phẩm (chỉ hiện với role == 2)
            if (role == 2) {
                Button(
                    onClick = {
                        val intent = Intent(context, ThemSachActivity::class.java)
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(0.9f)
                ) {
                    Text("Thêm sản phẩm", color = Color.White)
                }
            }
        }
    }

    @Composable
    fun BookItemAdmin(book: Book) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = book.image_url,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(book.title, fontWeight = FontWeight.Bold)
                    Text("Tác giả: ${book.author}")
                    Text("Giá: ${NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(book.price)}")
                }

                IconButton(onClick = { /* Sửa sách */ }) {
                    Icon(Icons.Default.Edit, contentDescription = "Sửa")
                }

                IconButton(onClick = { /* Xóa sách */ }) {
                    Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Red)
                }
            }
        }
    }
    @Composable
    fun FirebaseBookCard(book: Book, role: Int) {
        val context = LocalContext.current

        Card(
            modifier = Modifier
                .width(180.dp)
                .padding(4.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberAsyncImagePainter(book.image_url),
                    contentDescription = null,
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                val formattedPrice = formatter.format(book.price)
                Text(book.title, fontWeight = FontWeight.Bold)
                Text(text = "Giá: $formattedPrice")
                Text("Tác giả: ${book.author}", fontSize = 12.sp, color = Color.Gray)
                Text("Thể loại: ${book.category}", fontSize = 12.sp)
                Text("Rating: ${book.rating}", fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        val intent = Intent(context, ChitietActivity::class.java).apply {
                            putExtra("title", book.title)
                            putExtra("author", book.author)
                            putExtra("description", book.description)
                            putExtra("image_url", book.image_url)
                            putExtra("category", book.category)
                            putExtra("rating", book.rating)
                            putExtra("description", book.description)
                            putExtra("price", book.price)
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077B6))
                ) {
                    Text("Xem", fontSize = 12.sp)
                }

                if (role == 2) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            val intent = Intent(context, SuaSachActivity::class.java).apply {
                                putExtra("docId", book.id)
                                putExtra("title", book.title)
                                putExtra("author", book.author)
                                putExtra("description", book.description)
                                putExtra("image_url", book.image_url)
                                putExtra("category", book.category)
                                putExtra("rating", book.rating)
                                putExtra("price", book.price)
                            }
                            startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
                    ) {
                        Text("Sửa", fontSize = 12.sp)
                    }
                }
            }
        }
    }

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

    @Composable
    fun CaNhan() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Thông tin cá nhân", fontSize = 20.sp, color = Color.Gray)
        }
    }

    data class CartItemData(val imageName: String, val title: String, val price: String)
}