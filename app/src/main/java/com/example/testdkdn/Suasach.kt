package com.example.testdkdn

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.testdkdn.ui.theme.TestDKDNTheme
import com.google.firebase.firestore.FirebaseFirestore

class SuaSachActivity : ComponentActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val documentId = intent.getStringExtra("docId") ?: ""
        val imageUrl = intent.getStringExtra("image_url") ?: ""
        val title = intent.getStringExtra("title") ?: ""
        val author = intent.getStringExtra("author") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val category = intent.getStringExtra("category") ?: ""
        val rating = intent.getStringExtra("rating") ?: ""

        setContent {
            TestDKDNTheme {
                SuaSachScreen(
                    imageUrl = imageUrl,
                    initTitle = title,
                    initAuthor = author,
                    initDescription = description,
                    initCategory = category,
                    initRating = rating,
                    documentId = documentId,
                    onUpdateSuccess = {
                        Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    onUpdateFail = {
                        Toast.makeText(this, "Lỗi khi cập nhật!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun SuaSachScreen(
    imageUrl: String,
    initTitle: String,
    initAuthor: String,
    initDescription: String,
    initCategory: String,
    initRating: String,
    documentId: String,
    onUpdateSuccess: () -> Unit,
    onUpdateFail: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(initTitle) }
    var author by remember { mutableStateOf(initAuthor) }
    var description by remember { mutableStateOf(initDescription) }
    var category by remember { mutableStateOf(initCategory) }
    var rating by remember { mutableStateOf(initRating) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F8FF))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentScale = ContentScale.Fit
        )

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Tên sách") })
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Tác giả") })
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Thể loại") })
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = rating, onValueChange = { rating = it }, label = { Text("Đánh giá") })
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Mô tả") },
            modifier = Modifier.height(120.dp),
            maxLines = 6
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val updatedBook = hashMapOf(
                    "title" to title,
                    "author" to author,
                    "description" to description,
                    "category" to category,
                    "rating" to rating,
                    "image_url" to imageUrl
                )

                FirebaseFirestore.getInstance()
                    .collection("books")
                    .document(documentId)
                    .update(updatedBook as Map<String, Any>)
                    .addOnSuccessListener { onUpdateSuccess() }
                    .addOnFailureListener { onUpdateFail() }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077B6)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Lưu chỉnh sửa", fontSize = 16.sp, color = Color.White)
        }
    }
}
