package com.example.dispensave.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dispensave.ui.pantry.PantryViewModel
import com.example.dispensave.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: PantryViewModel,
    onLoginSuccess: () -> Unit,
    onBackClick: () -> Unit,
    onGuestClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }

    val emailError = viewModel.validateEmail(email)
    val passwordError = if (hasAttemptedSubmit && password.isBlank()) "La contraseña es requerida" else null
    val isEmailErrorVisible = hasAttemptedSubmit && emailError != null

    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(14.dp),
                color = GreenPrimary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("D", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Iniciar Sesión",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Ingresa tu correo para gestionar tu despensa y recibir alertas de vencimiento.",
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Campo Email con validación de Android EmailAddress
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Correo Electrónico") },
                placeholder = { Text("usuario@dominio.com") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Correo",
                        tint = if (isEmailErrorVisible) MaterialTheme.colorScheme.error else GreenPrimary
                    )
                },
                isError = isEmailErrorVisible,
                supportingText = {
                    if (isEmailErrorVisible && emailError != null) {
                        Text(
                            text = emailError,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = Color(0xFFDCDCDC),
                    errorBorderColor = MaterialTheme.colorScheme.error
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Contraseña") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Contraseña",
                        tint = if (passwordError != null) MaterialTheme.colorScheme.error else GreenPrimary
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Ocultar" else "Mostrar"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = passwordError != null,
                supportingText = {
                    if (passwordError != null) {
                        Text(
                            text = passwordError,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = Color(0xFFDCDCDC),
                    errorBorderColor = MaterialTheme.colorScheme.error
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    hasAttemptedSubmit = true
                    if (emailError == null && password.isNotBlank()) {
                        viewModel.setUserEmailAddress(email.trim())
                        onLoginSuccess()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
                onClick = onGuestClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenPrimary)
            ) {
                Text(
                    text = "Continuar como invitado",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}