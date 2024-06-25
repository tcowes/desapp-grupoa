package ar.edu.unq.desapp.grupoa.backenddesappapi.service.dto

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.User
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class UserDetailsDTO(private val user: User) : UserDetails {
    override fun getAuthorities() = emptyList<SimpleGrantedAuthority>()
    override fun getPassword() = user.password
    override fun getUsername() = user.email
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true
}
