package com.world.spring.user


import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * UserService:
 * - register(username, rawPassword): creates a USER by default
 * - registerAdmin(...) helper (used by DataInitializer) to create ADMIN
 */


@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    @Transactional
    fun register(username: String, password: String, role: Role = Role.USER): User {

        if (userRepository.existsByUsername(username)) {
            throw IllegalArgumentException("username already taken")
        }
        val encoded = passwordEncoder.encode(password)
        val user = User(username = username, password = encoded, role = role)
        return userRepository.save(user)
    }

    fun findByUsername(username: String): User? =
        userRepository.findByUsername(username).orElse(null)
}