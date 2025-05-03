package com.example.testdkdn

import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ThemSachActivity : ComponentActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var imageUri by remember { mutableStateOf<Uri?>(null) }
            var title by remember { mutableStateOf("") }
            var author by remember { mutableStateOf("") }
            var description by remember { mutableStateOf("") }
            var category by remember { mutableStateOf("") }
            var rating by remember { mutableStateOf(0.0) }
            var imageUrl by remember { mutableStateOf("") }

            // Launcher for image selection
            val imagePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                imageUri = uri
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {
                    imagePickerLauncher.launch("image/*")
                }) {
                    Text(text = "Chọn ảnh")
                }

                imageUri?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Ảnh đã chọn",
                        modifier = Modifier.height(200.dp).fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nhập thông tin sách
                TextField(value = title, onValueChange = { title = it }, label = { Text("Tên sách") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = author, onValueChange = { author = it }, label = { Text("Tác giả") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = description, onValueChange = { description = it }, label = { Text("Mô tả") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = category, onValueChange = { category = it }, label = { Text("Thể loại") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = rating.toString(),
                    onValueChange = { rating = it.toDoubleOrNull() ?: 0.0 },
                    label = { Text("Đánh giá") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    if (imageUri != null && title.isNotEmpty() && author.isNotEmpty()) {
                        uploadImageToCloudinary(imageUri!!) { url ->
                            imageUrl = url
                            val book = Book(
                                title = title,
                                author = author,
                                description = description,
                                category = category,
                                rating = rating,
                                image_url = imageUrl
                            )
                            saveBookToFirestore(book)
                        }
                    } else {
                        Toast.makeText(this@ThemSachActivity, "Vui lòng chọn ảnh và nhập đầy đủ thông tin sách", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Lưu sách")
                }
            }
        }
    }

    private fun uploadImageToCloudinary(uri: Uri, onSuccess: (String) -> Unit) {
        // Tải ảnh lên Cloudinary và lấy link
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imageUrl = uploadImage(uri)
                withContext(Dispatchers.Main) {
                    onSuccess(imageUrl)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ThemSachActivity, "Lỗi khi upload ảnh: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadImage(uri: Uri): String {
        // Logic upload ảnh lên Cloudinary và trả về `secure_url`
        // (Dùng hàm tương tự như trong app upload ảnh của bạn)
        // Trả về secure_url ảnh sau khi upload thành công
        return "https://res.cloudinary.com/dujmhnsee/image/upload/v1/example_image.jpg"  // Giả sử đây là URL trả về từ Cloudinary
    }

    private fun saveBookToFirestore(book: Book) {
        db.collection("books")
            .add(book)
            .addOnSuccessListener {
                Toast.makeText(this, "Thêm sách thành công!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi khi thêm sách: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
