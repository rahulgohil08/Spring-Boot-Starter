package com.world.spring.auth


import com.world.spring.user.UserService
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * Loads user details and maps user.role -> ROLE_{ROLE_NAME} authority
 */

@Service
class LocalUserDetailsService(private val userService: UserService) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userService.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found: $username")

        val authority = SimpleGrantedAuthority("ROLE_${user.role.name}")


        return org.springframework.security.core.userdetails.User.builder()
            .username(user.username)
            .password(user.password) // encoded password
            .authorities(listOf(authority))
            .build()
    }

}