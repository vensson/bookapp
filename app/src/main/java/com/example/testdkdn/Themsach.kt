package com.example.testdkdn

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

class ThemsachActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThemsachScreen()
        }
    }
}

@Composable
fun ThemsachScreen() {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var title by remember { mutableStateOf(TextFieldValue("")) }
    var author by remember { mutableStateOf(TextFieldValue("")) }
    var category by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var imageUrl by remember { mutableStateOf(TextFieldValue("")) }
    var rating by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Thêm Sách", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Tiêu đề") })
        OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Tác giả") })
        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Thể loại") })
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Mô tả") })
        OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Link ảnh (image_url)") })
        OutlinedTextField(value = rating, onValueChange = { rating = it }, label = { Text("Đánh giá (rating)") })

        Button(
            onClick = {
                val book = hashMapOf(
                    "title" to title.text,
                    "author" to author.text,
                    "category" to category.text,
                    "description" to description.text,
                    "image_url" to imageUrl.text,
                    "rating" to rating.text
                )

                db.collection("books")
                    .add(book)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Đã thêm sách!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Lỗi khi thêm sách!", Toast.LENGTH_SHORT).show()
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Thêm")
        }
    }
}
