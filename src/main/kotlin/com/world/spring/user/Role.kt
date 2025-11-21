package com.world.spring.user

/**
 * Role enum for simple RBAC.
 *
 * We keep two roles for now:
 * - USER: regular user (can create/read/update todos but cannot delete others)
 * - ADMIN: full access (including delete)
 */
enum class Role {
    USER,
    ADMIN
}
