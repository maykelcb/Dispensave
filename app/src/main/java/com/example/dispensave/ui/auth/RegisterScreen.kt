package com.example.dispensave.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.example.dispensave.data.repository.RegisterResult
import com.example.dispensave.domain.model.StoreCategory
import com.example.dispensave.domain.model.UserRole
import com.example.dispensave.ui.pantry.PantryViewModel
import com.example.dispensave.ui.theme.GreenPrimary
import com.example.dispensave.util.ValidationUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: PantryViewModel,
    onRegisterSuccess: () -> Unit,
    onBackClick: () -> Unit,
    onGoToLogin: (String) -> Unit
) {
    var selectedRole by remember { mutableStateOf(UserRole.CLIENTE) }
    var selectedStoreCategory by remember { mutableStateOf(StoreCategory.PANADERIA) }
    var name by remember { mutableStateOf("") }
    var storeName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }

    var showAccountExistsDialog by remember { mutableStateOf(false) }
    var existingEmailConflict by remember { mutableStateOf("") }

    val nameError = if (hasAttemptedSubmit) ValidationUtils.validateName(name) else null
    val storeNameError = if (hasAttemptedSubmit && selectedRole == UserRole.COMERCIO && storeName.trim().length < 3) {
        "El nombre del comercio debe tener al menos 3 letras"
    } else null
    val emailError = if (hasAttemptedSubmit) ValidationUtils.validateEmailStrict(email) else null
    val passwordError = if (hasAttemptedSubmit) ValidationUtils.validatePassword(password) else null
    val confirmPasswordError = if (hasAttemptedSubmit) ValidationUtils.validateConfirmPassword(password, confirmPassword) else null
    val termsError = if (hasAttemptedSubmit && !termsAccepted) "Debes aceptar los términos y condiciones" else null

    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.DarkGray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF8F9FA))
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crear Cuenta",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Únete a DispenSave y sé parte del rescate de comida",
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Selector de Rol: Cliente vs Comercio
            Text(
                text = "Selecciona tu tipo de perfil:",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    onClick = { selectedRole = UserRole.CLIENTE },
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedRole == UserRole.CLIENTE) GreenPrimary.copy(alpha = 0.12f) else Color.White,
                    border = BorderStroke(1.5.dp, if (selectedRole == UserRole.CLIENTE) GreenPrimary else Color.LightGray),
                    modifier = Modifier.weight(1f).height(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Consumidor",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedRole == UserRole.CLIENTE) GreenPrimary else Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
                Surface(
                    onClick = { selectedRole = UserRole.COMERCIO },
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedRole == UserRole.COMERCIO) GreenPrimary.copy(alpha = 0.12f) else Color.White,
                    border = BorderStroke(1.5.dp, if (selectedRole == UserRole.COMERCIO) GreenPrimary else Color.LightGray),
                    modifier = Modifier.weight(1f).height(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Comercio",
                            fontWeight = FontWeight.Bold,
                            color = if (selectedRole == UserRole.COMERCIO) GreenPrimary else Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Campo Nombre del Responsable
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(if (selectedRole == UserRole.COMERCIO) "Nombre del Administrador / Contacto" else "Nombre Completo") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Nombre",
                        tint = if (nameError != null) MaterialTheme.colorScheme.error else GreenPrimary
                    )
                },
                isError = nameError != null,
                supportingText = {
                    if (nameError != null) {
                        Text(nameError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                shape = RoundedCornerShape(12.dp)
            )

            // Si es comercio, pedir Nombre del Establecimiento y Selección Obligatoria de Categoría
            if (selectedRole == UserRole.COMERCIO) {
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = storeName,
                    onValueChange = { storeName = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre del Local o Negocio") },
                    placeholder = { Text("Ej: Panadería El Molino") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = "Comercio",
                            tint = if (storeNameError != null) MaterialTheme.colorScheme.error else GreenPrimary
                        )
                    },
                    isError = storeNameError != null,
                    supportingText = {
                        if (storeNameError != null) {
                            Text(storeNameError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Selector de Rubro / Tipo de Comercio (Panadería, Restaurante, Cafetería, Pastelería)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Rubro de tu Establecimiento (Obligatorio):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "Plataforma adaptada a gastronomía y panadería fresca",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val categories = listOf(
                        StoreCategory.PANADERIA to "🥖 Panadería",
                        StoreCategory.RESTAURANTE to "🍲 Restaurante",
                        StoreCategory.CAFETERIA to "☕ Cafetería",
                        StoreCategory.PASTELERIA to "🍰 Pastelería"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            categories.take(2).forEach { (cat, label) ->
                                val isSelected = selectedStoreCategory == cat
                                Surface(
                                    onClick = { selectedStoreCategory = cat },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) GreenPrimary.copy(alpha = 0.15f) else Color.White,
                                    border = BorderStroke(1.5.dp, if (isSelected) GreenPrimary else Color(0xFFE0E0E0)),
                                    modifier = Modifier.weight(1f).height(42.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    ) {
                                        Text(
                                            text = label,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) GreenPrimary else Color.DarkGray,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            categories.drop(2).forEach { (cat, label) ->
                                val isSelected = selectedStoreCategory == cat
                                Surface(
                                    onClick = { selectedStoreCategory = cat },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) GreenPrimary.copy(alpha = 0.15f) else Color.White,
                                    border = BorderStroke(1.5.dp, if (isSelected) GreenPrimary else Color(0xFFE0E0E0)),
                                    modifier = Modifier.weight(1f).height(42.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    ) {
                                        Text(
                                            text = label,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) GreenPrimary else Color.DarkGray,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Campo Correo Electrónico
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Correo Electrónico") },
                placeholder = { Text("ejemplo@gmail.com") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Correo",
                        tint = if (emailError != null) MaterialTheme.colorScheme.error else GreenPrimary
                    )
                },
                isError = emailError != null,
                supportingText = {
                    if (emailError != null) {
                        Text(emailError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Campo Contraseña
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
                        Text(passwordError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Campo Confirmar Contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Confirmar Contraseña") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LockReset,
                        contentDescription = "Confirmar Contraseña",
                        tint = if (confirmPasswordError != null) MaterialTheme.colorScheme.error else GreenPrimary
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (confirmPasswordVisible) "Ocultar" else "Mostrar"
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = confirmPasswordError != null,
                supportingText = {
                    if (confirmPasswordError != null) {
                        Text(confirmPasswordError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Casilla Obligatoria de Términos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { termsAccepted = !termsAccepted }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it },
                    colors = CheckboxDefaults.colors(checkedColor = GreenPrimary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Acepto los Términos y Condiciones y la Política de Privacidad de DispenSave.",
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    lineHeight = 16.sp
                )
            }
            if (termsError != null) {
                Text(
                    text = termsError,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    hasAttemptedSubmit = true
                    val validN = ValidationUtils.validateName(name) == null
                    val validStore = selectedRole == UserRole.CLIENTE || storeName.trim().length >= 3
                    val validE = ValidationUtils.validateEmailStrict(email) == null
                    val validP = ValidationUtils.validatePassword(password) == null
                    val validC = ValidationUtils.validateConfirmPassword(password, confirmPassword) == null

                    if (validN && validStore && validE && validP && validC && termsAccepted) {
                        val result = viewModel.registerUser(
                            name = name.trim(),
                            email = email.trim(),
                            password = password,
                            role = selectedRole,
                            storeName = storeName.trim(),
                            storeCategory = if (selectedRole == UserRole.COMERCIO) selectedStoreCategory else null
                        )
                        when (result) {
                            is RegisterResult.Success -> {
                                onRegisterSuccess()
                            }
                            is RegisterResult.AlreadyExists -> {
                                existingEmailConflict = email.trim()
                                showAccountExistsDialog = true
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text(
                    text = if (selectedRole == UserRole.COMERCIO) "Registrar Comercio" else "Registrarme",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Ya tienes una cuenta?",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                TextButton(onClick = { onGoToLogin(email.trim()) }) {
                    Text(
                        text = "Inicia sesión",
                        color = GreenPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showAccountExistsDialog) {
        AlertDialog(
            onDismissRequest = { showAccountExistsDialog = false },
            icon = {
                Icon(Icons.Default.Info, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(36.dp))
            },
            title = {
                Text(
                    text = "Cuenta ya registrada",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Ya existe una cuenta registrada con el correo:\n\n$existingEmailConflict\n\nPuedes iniciar sesión directamente con tu contraseña o volver para corregir el correo.",
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAccountExistsDialog = false
                        onGoToLogin(existingEmailConflict)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text("Iniciar sesión")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAccountExistsDialog = false }
                ) {
                    Text("Cancelar / Atrás", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}
