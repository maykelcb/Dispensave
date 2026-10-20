package com.example.dispensave.util

import java.text.Normalizer
import java.util.Locale

object ValidationUtils {

    // Lista de términos ofensivos, groserías e insultos para restringir en el registro de nombres
    private val OFFENSIVE_TERMS = setOf(
        "puta", "puto", "putita", "putito", "mierda", "pendejo", "pendeja", "pendejos",
        "carajo", "concha", "conchudo", "conchuda", "chucha", "culero", "culera", "culo",
        "perra", "cabron", "cabrona", "idiota", "imbecil", "estupido", "estupida",
        "maricon", "marica", "tarado", "tarada", "zorra", "verga", "coño",
        "maldito", "maldita", "bastardo", "bastarda", "mamaguevo", "gonorrea", "cabro",
        "pinga", "csm", "tmr", "ctm", "fuck", "shit", "bitch", "tarao", "chupame", "hdp",
        "malparido", "malparida", "pelotudo", "pelotuda", "forro", "boludo", "boluda"
    )

    // Dominios de proveedores de correo válidos y reconocidos
    private val VALID_DOMAINS = setOf(
        "gmail.com",
        "outlook.com",
        "outlook.es",
        "hotmail.com",
        "hotmail.es",
        "live.com",
        "msn.com",
        "yahoo.com",
        "yahoo.es",
        "icloud.com"
    )

    fun normalizeText(input: String): String {
        var str = input.lowercase(Locale.ROOT)
            .replace('@', 'a')
            .replace('0', 'o')
            .replace('1', 'i')
            .replace('3', 'e')
            .replace('4', 'a')
            .replace('5', 's')
            .replace('7', 't')
            .replace('$', 's')

        val normalized = Normalizer.normalize(str, Normalizer.Form.NFD)
        return Regex("\\p{InCombiningDiacriticalMarks}+").replace(normalized, "")
    }

    fun isOffensiveName(name: String): Boolean {
        val normalized = normalizeText(name)
        val tokens = normalized.split(Regex("[^a-z0-9]+")).filter { it.isNotBlank() }

        for (token in tokens) {
            if (OFFENSIVE_TERMS.contains(token)) return true
        }
        for (term in OFFENSIVE_TERMS) {
            if (term.length >= 4 && normalized.contains(term)) return true
        }
        return false
    }

    /**
     * Valida el nombre completo:
     * - Mínimo 3 letras.
     * - Prohíbe 3 letras iguales consecutivas (ej. "aaa", "nnn", "aaan", "eeeeeeeo").
     * - Prohíbe que el nombre esté compuesto por una sola letra repetida.
     * - Prohíbe términos ofensivos o insultos.
     */
    fun validateName(name: String): String? {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return "El nombre completo es requerido"

        val lettersOnly = trimmed.filter { it.isLetter() }
        if (lettersOnly.length < 3) {
            return "El nombre debe tener al menos 3 letras"
        }
        if (trimmed.length > 50) {
            return "El nombre no puede superar los 50 caracteres"
        }
        if (!trimmed.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s.'-]+$"))) {
            return "El nombre solo puede contener letras y espacios"
        }

        // Restricción: No permitir 3 o más letras iguales consecutivas (ej. "aaa", "nnn", "aaan", "eeeeeeeo")
        val tripleRepeatedRegex = Regex("(?i)(.)\\1{2,}")
        if (tripleRepeatedRegex.containsMatchIn(trimmed)) {
            return "El nombre no puede contener 3 o más letras iguales repetidas (ej. 'aaa', 'nnn')"
        }

        // Restricción: No permitir que todas las letras sean idénticas
        val uniqueLetters = lettersOnly.lowercase(Locale.ROOT).toSet()
        if (uniqueLetters.size < 2) {
            return "El nombre debe contener al menos dos letras diferentes (ej. 'Ana', 'Noa')"
        }

        if (isOffensiveName(trimmed)) {
            return "El nombre contiene términos no permitidos u ofensivos. Usa un nombre adecuado."
        }
        return null
    }

    /**
     * Valida el correo de forma estricta:
     * - Nombre de usuario antes del @ de al menos 3 caracteres.
     * - Solo permite proveedores reales (gmail.com, outlook.com, hotmail.com, etc.)
     *   o correos institucionales/universitarios (.edu.pe, .edu, .gob.pe).
     * - Rechaza correos inventados como a@a.com o maykel123@sjad.som.
     */
    fun validateEmailStrict(email: String): String? {
        val trimmed = email.trim()
        if (trimmed.isEmpty()) return "El correo electrónico es requerido"
        if (trimmed.contains(" ") || !trimmed.contains("@")) return "Formato de correo no válido"

        val parts = trimmed.split("@")
        if (parts.size != 2) return "Formato de correo no válido"

        val username = parts[0]
        val domain = parts[1].lowercase(Locale.ROOT)

        if (username.length < 3) {
            return "El usuario del correo debe tener al menos 3 caracteres"
        }
        if (!username.matches(Regex("^[a-zA-Z0-9][a-zA-Z0-9._%+-]*$"))) {
            return "El usuario del correo contiene caracteres no válidos"
        }

        val isValid = isValidDomain(domain)
        if (!isValid) {
            return "Dominio no permitido. Usa un correo válido (@gmail.com, @outlook.com, @hotmail.com o institucional .edu.pe)"
        }

        return null
    }

    private fun isValidDomain(domain: String): Boolean {
        if (VALID_DOMAINS.contains(domain)) return true

        // Soporte para correos universitarios / institucionales
        if (domain.endsWith(".edu.pe") || domain.endsWith(".edu") || domain.endsWith(".gob.pe")) {
            val parts = domain.split(".")
            if (domain.endsWith(".edu.pe") && parts.size >= 3 && parts[0].length >= 2) return true
            if (domain.endsWith(".edu") && parts.size >= 2 && parts[0].length >= 2) return true
            if (domain.endsWith(".gob.pe") && parts.size >= 3 && parts[0].length >= 2) return true
        }
        return false
    }

    /**
     * Valida la contraseña:
     * - Mínimo 6 caracteres.
     * - Al menos una letra mayúscula.
     * - Al menos un número.
     * - Al menos un carácter especial (@, #, $, *, !, etc.).
     */
    fun validatePassword(password: String): String? {
        if (password.isBlank()) return "La contraseña es requerida"
        if (password.length < 6) {
            return "La contraseña debe tener al menos 6 caracteres"
        }
        if (!password.any { it.isUpperCase() }) {
            return "La contraseña debe incluir al menos una letra mayúscula"
        }
        if (!password.any { it.isDigit() }) {
            return "La contraseña debe incluir al menos un número"
        }
        if (!password.any { !it.isLetterOrDigit() && !it.isWhitespace() }) {
            return "La contraseña debe incluir al menos un carácter especial (ej. @, #, $, *, !)"
        }
        return null
    }

    fun validateConfirmPassword(password: String, confirm: String): String? {
        if (confirm.isBlank()) return "Confirma tu contraseña"
        if (password != confirm) return "Las contraseñas no coinciden"
        return null
    }
}
