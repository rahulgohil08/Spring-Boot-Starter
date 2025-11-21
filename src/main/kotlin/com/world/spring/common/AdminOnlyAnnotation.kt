package com.world.spring.common

import org.springframework.security.access.prepost.PreAuthorize


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasRole(T(com.world.spring.user.Role).ADMIN.name())")
annotation class AdminOnly

