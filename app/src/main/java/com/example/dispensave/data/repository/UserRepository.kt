package com.example.dispensave.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.dispensave.domain.model.StoreCategory
import com.example.dispensave.domain.model.User
import com.example.dispensave.domain.model.UserRole
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

sealed class LoginResult {
    data class Success(val user: User) : LoginResult()
    object EmailNotRegistered : LoginResult()
    object IncorrectPassword : LoginResult()
}

sealed class RegisterResult {
    data class Success(val user: User) : RegisterResult()
    data class AlreadyExists(val existingUser: User) : RegisterResult()
}

class UserRepository(context: Context? = null) {

    private val prefs: SharedPreferences? = context?.getSharedPreferences("dispensave_users_pref", Context.MODE_PRIVATE)
    private val users = mutableListOf<User>()

    init {
        // Cuentas precargadas: Clientes y Comercios con categoría asignada
        users.add(
            User(
                name = "Maykel Bernaola",
                email = "maykel@universidad.edu.pe",
                password = "Password123*",
                role = UserRole.CLIENTE
            )
        )
        users.add(
            User(
                name = "Gerencia La Baguette",
                email = "labaguette@gmail.com",
                password = "Password123*",
                role = UserRole.COMERCIO,
                storeName = "Panadería & Pastelería La Baguette",
                storeCategory = StoreCategory.PANADERIA
            )
        )
        users.add(
            User(
                name = "Chef Carlos Sabor Criollo",
                email = "saborcriollo@gmail.com",
                password = "Password123*",
                role = UserRole.COMERCIO,
                storeName = "Restaurante Sabor Criollo",
                storeCategory = StoreCategory.RESTAURANTE
            )
        )
        users.add(
            User(
                name = "Mayquel Bernaola",
                email = "maykelcb095@gmail.com",
                password = "Password123*",
                role = UserRole.CLIENTE
            )
        )
        loadFromPreferences()
    }

    private fun loadFromPreferences() {
        prefs?.getString("saved_registered_users", null)?.let { jsonStr ->
            try {
                val array = JSONArray(jsonStr)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val email = obj.getString("email")
                    if (users.none { it.email.equals(email, ignoreCase = true) }) {
                        val roleStr = obj.optString("role", UserRole.CLIENTE.name)
                        val role = try { UserRole.valueOf(roleStr) } catch (e: Exception) { UserRole.CLIENTE }
                        val catStr = if (obj.has("storeCategory") && !obj.isNull("storeCategory")) obj.getString("storeCategory") else null
                        val storeCat = catStr?.let { try { StoreCategory.valueOf(it) } catch (e: Exception) { null } }

                        users.add(
                            User(
                                name = obj.getString("name"),
                                email = email,
                                password = obj.getString("password"),
                                role = role,
                                storeName = if (obj.has("storeName") && !obj.isNull("storeName")) obj.getString("storeName") else null,
                                storeCategory = storeCat,
                                registeredAt = obj.optLong("registeredAt", System.currentTimeMillis())
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveToPreferences() {
        prefs?.let {
            try {
                val array = JSONArray()
                for (user in users) {
                    val obj = JSONObject().apply {
                        put("name", user.name)
                        put("email", user.email)
                        put("password", user.password)
                        put("role", user.role.name)
                        if (user.storeName != null) {
                            put("storeName", user.storeName)
                        }
                        if (user.storeCategory != null) {
                            put("storeCategory", user.storeCategory.name)
                        }
                        put("registeredAt", user.registeredAt)
                    }
                    array.put(obj)
                }
                it.edit().putString("saved_registered_users", array.toString()).apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun findUserByEmail(email: String): User? {
        val cleanEmail = email.trim().lowercase(Locale.ROOT)
        return users.firstOrNull { it.email.lowercase(Locale.ROOT) == cleanEmail }
    }

    fun isEmailRegistered(email: String): Boolean {
        return findUserByEmail(email) != null
    }

    @Synchronized
    fun registerUser(user: User): RegisterResult {
        val existing = findUserByEmail(user.email)
        if (existing != null) {
            return RegisterResult.AlreadyExists(existing)
        }
        users.add(user)
        saveToPreferences()
        return RegisterResult.Success(user)
    }

    fun login(email: String, password: String): LoginResult {
        val user = findUserByEmail(email) ?: return LoginResult.EmailNotRegistered
        if (user.password != password) {
            return LoginResult.IncorrectPassword
        }
        return LoginResult.Success(user)
    }
}
