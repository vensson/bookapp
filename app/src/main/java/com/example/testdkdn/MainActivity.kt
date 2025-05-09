package com.example.testdkdn

import com.google.firebase.firestore.FirebaseFirestore

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var isLoggedIn by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                isLoggedIn = auth.currentUser != null
            }

            if (isLoggedIn) {
                goToNoteScreen()
            } else {
                LoginRegisterScreen(
                    onRegister = { email, password -> registerUser(email, password) },
                    onLogin = { email, password -> loginUser(email, password) }
                )
            }
        }
    }

//    private fun registerUser(email: String, password: String) {
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
//                    goToNoteScreen()
//                } else {
//                    Toast.makeText(this, "Lỗi: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
private fun registerUser(email: String, password: String) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val db = FirebaseFirestore.getInstance()

                val userData = hashMapOf(
                    "email" to email,
                    "name" to "Người dùng mới", // bạn có thể cho nhập tên nếu muốn
                    "role" to 1 // mặc định là người thường
                )

                user?.email?.let {
                    db.collection("users").document(it)
                        .set(userData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                            goToTrangchu() // mặc định về trang chủ
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Đăng ký thất bại khi lưu role", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(this, "Lỗi: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}


//    private fun loginUser(email: String, password: String) {
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
//                    goToTrangchu()
//                } else {
//                    Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
// Đoạn code trong Activity đăng nhập (LoginActivity hoặc tương tự)
private fun loginUser(email: String, password: String) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userEmail = auth.currentUser?.email
                val db = FirebaseFirestore.getInstance()

                db.collection("users").document(userEmail ?: "")
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val role = document.getLong("role")?.toInt()

                            if (role != null) {
                                // Truyền role vào Intent khi đăng nhập thành công
                                val intent = Intent(this, TrangchuActivity::class.java).apply {
                                    putExtra("ROLE", role)
                                }
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, "Không tìm thấy thông tin quyền", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show()
            }
        }
}



    private fun goToNoteScreen() {
        startActivity(Intent(this, NoteActivity::class.java))
        finish()
    }
    private fun goToTrangchu() {
        startActivity(Intent(this, TrangchuActivity::class.java))
        finish()
    }
}


@Composable
fun LoginRegisterScreen(onRegister: (String, String) -> Unit, onLogin: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val background: Painter = painterResource(id = R.drawable.nen1)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = background,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Chào mừng",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mật khẩu") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { onRegister(email, password) },
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Đăng ký")
                        }

                        Button(
                            onClick = { onLogin(email, password) },
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Đăng nhập")
                        }
                    }
                }
            }
        }
    }
}