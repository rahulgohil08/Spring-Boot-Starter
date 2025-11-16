package com.world.spring.todo

import java.time.LocalDateTime
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import jakarta.validation.constraints.Max

// DTO for creating a new todo
data class CreateTodoRequest(
    @field:NotBlank(message = "Title cannot be blank")
    @field:Size(max = 255, message = "Title cannot exceed 255 characters")
    val title: String,

    @field:Size(max = 1000, message = "Description cannot exceed 1000 characters")
    val description: String? = null,

    val completed: Boolean = false
)

// DTO for updating an existing todo
data class UpdateTodoRequest(
    @field:Size(max = 255, message = "Title cannot exceed 255 characters")
    val title: String? = null,

    @field:Size(max = 1000, message = "Description cannot exceed 1000 characters")
    val description: String? = null,

    val completed: Boolean? = null
)

// Response DTO to return data to clients
data class TodoResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val completed: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

// Extension function to convert Todo entity to TodoResponse DTO
fun Todo.toResponse(): TodoResponse {
    return TodoResponse(
        id = this.id!!,
        title = this.title,
        description = this.description,
        completed = this.completed,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

// Extension function to apply update fields to a Todo entity
fun Todo.applyUpdate(updateRequest: UpdateTodoRequest): Todo {
    return this.copy(
        title = updateRequest.title ?: this.title,
        description = updateRequest.description ?: this.description,
        completed = updateRequest.completed ?: this.completed
    )
}