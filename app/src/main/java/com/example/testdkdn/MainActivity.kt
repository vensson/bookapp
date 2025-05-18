package com.example.testdkdn

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        // Kiểm tra nếu đã đăng nhập thì chuyển thẳng đến trang chủ
        if (auth.currentUser != null) {
            checkUserRoleAndNavigate()
            return
        }

        setContent {
            LoginScreen(
                onLoginSuccess = { checkUserRoleAndNavigate() },
                onRegisterClick = { navigateToRegister() }
            )
        }
    }

    private fun checkUserRoleAndNavigate() {
        val currentUser = auth.currentUser
        currentUser?.email?.let { email ->
            db.collection("users").document(email)
                .get()
                .addOnSuccessListener { document ->
                    val role = document.getLong("role")?.toInt() ?: 1
                    navigateToHome(role)
                    finish()
                }
                .addOnFailureListener {
                    // Nếu không lấy được role, mặc định là user thường
                    navigateToHome(1)
                    finish()
                }
        }
    }

    private fun navigateToHome(role: Int) {
        val intent = Intent(this, TrangchuActivity::class.java).apply {
            putExtra("ROLE", role)
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToRegister() {
        startActivity(Intent(this, DangKyActivity::class.java))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val context = LocalContext.current
    val auth = Firebase.auth
    val db = FirebaseFirestore.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        // Hình nền từ drawable
        Image(
            painter = painterResource(id = R.drawable.nenlogin),
            contentDescription = "Login Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Lớp phủ màu
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "ĐĂNG NHẬP",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // Form đăng nhập
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = "Email Icon",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mật khẩu") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = "Password Icon",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Forgot Password Link
                    TextButton(
                        onClick = { showForgotPasswordDialog = true },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            "Quên mật khẩu?",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Login Button
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true
                                try {
                                    auth.signInWithEmailAndPassword(email.trim(), password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                onLoginSuccess()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Đăng nhập thất bại: ${task.exception?.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                "ĐĂNG NHẬP",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Register Link
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            "Chưa có tài khoản?",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        TextButton(onClick = onRegisterClick) {
                            Text(
                                "Đăng ký ngay",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog quên mật khẩu
    // Thay thế toàn bộ Dialog quên mật khẩu bằng code này
    if (showForgotPasswordDialog) {
        var resetEmail by remember { mutableStateOf("") }
        var isCheckingEmail by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("Đặt lại mật khẩu") },
            text = {
                Column {
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = {
                            resetEmail = it
                            errorMessage = null // Xóa thông báo lỗi khi người dùng nhập
                        },
                        label = { Text("Nhập email của bạn") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = errorMessage != null,
                        supportingText = {
                            errorMessage?.let {
                                Text(text = it, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )

                    if (isCheckingEmail) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (resetEmail.isBlank()) {
                            errorMessage = "Vui lòng nhập email"
                            return@TextButton
                        }

                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(resetEmail).matches()) {
                            errorMessage = "Email không hợp lệ"
                            return@TextButton
                        }

                        isCheckingEmail = true
                        errorMessage = null

                        auth.fetchSignInMethodsForEmail(resetEmail)
                            .addOnCompleteListener { task ->
                                isCheckingEmail = false
                                if (task.isSuccessful) {
                                    val methods = task.result?.signInMethods
                                    if (methods.isNullOrEmpty()) {
                                        errorMessage = "Email chưa được đăng ký"
                                    } else {
                                        auth.sendPasswordResetEmail(resetEmail)
                                            .addOnCompleteListener { resetTask ->
                                                if (resetTask.isSuccessful) {
                                                    Toast.makeText(
                                                        context,
                                                        "Email đặt lại mật khẩu đã được gửi đến $resetEmail",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    showForgotPasswordDialog = false
                                                } else {
                                                    errorMessage = "Lỗi gửi email: ${resetTask.exception?.message}"
                                                }
                                            }
                                    }
                                } else {
                                    errorMessage = "Lỗi kiểm tra email: ${task.exception?.message}"
                                }
                            }
                    },
                    enabled = !isCheckingEmail
                ) {
                    Text("Gửi yêu cầu")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showForgotPasswordDialog = false },
                    enabled = !isCheckingEmail
                ) {
                    Text("Hủy")
                }
            }
        )
    }
}