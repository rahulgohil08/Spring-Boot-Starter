package com.world.spring.todo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [TodoController::class])
class TodoControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var todoService: TodoService

    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        objectMapper = ObjectMapper().registerModule(JavaTimeModule())
    }

    @Test
    fun `should return all todos`() {
        val todos = listOf(
            Todo(id = 1, title = "Todo 1", description = "Description 1").toResponse(),
            Todo(id = 2, title = "Todo 2", description = "Description 2").toResponse()
        )
        whenever(todoService.getAllTodos()).thenReturn(todos.map {
            it.run { Todo(id, title, description, completed, createdAt, updatedAt) }
        })

        mockMvc.perform(get("/api/todos"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(true))
            .andExpect(jsonPath("$.message").value("Todos retrieved successfully"))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data[0].title").value("Todo 1"))
            .andExpect(jsonPath("$.data[1].title").value("Todo 2"))
    }

    @Test
    fun `should return todo by id`() {
        val todo = Todo(id = 1, title = "Test Todo", description = "Test Description").toResponse()
        whenever(todoService.getTodoById(1L)).thenReturn(
            todo.run { Todo(id, title, description, completed, createdAt, updatedAt) }
        )

        mockMvc.perform(get("/api/todos/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(true))
            .andExpect(jsonPath("$.message").value("Todo retrieved successfully"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.title").value("Test Todo"))
            .andExpect(jsonPath("$.data.description").value("Test Description"))
    }

    @Test
    fun `should return 404 when todo not found by id`() {
        whenever(todoService.getTodoById(999L)).thenReturn(null)

        mockMvc.perform(get("/api/todos/999"))
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(false))
            .andExpect(jsonPath("$.message").value("Todo with ID 999 not found"))
    }

    @Test
    fun `should create a new todo`() {
        val createRequest = CreateTodoRequest(title = "New Todo", description = "New Description")
        val savedTodo = Todo(id = 1, title = "New Todo", description = "New Description").toResponse()

        whenever(todoService.createTodo(any())).thenReturn(
            savedTodo.run { Todo(id, title, description, completed, createdAt, updatedAt) }
        )

        mockMvc.perform(
            post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
        )
            .andExpect(status().isCreated)  // 201 status
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(true))
            .andExpect(jsonPath("$.message").value("Todo created successfully"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.title").value("New Todo"))
            .andExpect(jsonPath("$.data.description").value("New Description"))
    }

    @Test
    fun `should return validation error when creating todo with blank title`() {
        val createRequest = CreateTodoRequest(title = "", description = "Valid Description")

        mockMvc.perform(
            post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
        )
            .andExpect(status().isUnprocessableEntity)  // 422 status
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(false))
            .andExpect(jsonPath("$.message").value("Validation failed: title: Title cannot be blank"))
    }

    @Test
    fun `should return validation error when creating todo with title exceeding max length`() {
        val longTitle = "A".repeat(256) // 256 characters, exceeding 255 limit
        val createRequest = CreateTodoRequest(title = longTitle, description = "Valid Description")

        mockMvc.perform(
            post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
        )
            .andExpect(status().isUnprocessableEntity)  // 422 status
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(false))
            .andExpect(jsonPath("$.message").value("Validation failed: title: Title cannot exceed 255 characters"))
    }

    @Test
    fun `should update existing todo`() {
        val updateRequest = UpdateTodoRequest(title = "Updated Title", description = "Updated Description")
        val existingTodo = Todo(id = 1, title = "Old Title", description = "Old Description")
        val updatedTodo = Todo(id = 1, title = "Updated Title", description = "Updated Description").toResponse()

        whenever(todoService.getTodoById(1L)).thenReturn(existingTodo)
        whenever(todoService.updateTodo(eq(1L), any())).thenReturn(
            updatedTodo.run { Todo(id, title, description, completed, createdAt, updatedAt) }
        )

        mockMvc.perform(
            put("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(true))
            .andExpect(jsonPath("$.message").value("Todo updated successfully"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.title").value("Updated Title"))
            .andExpect(jsonPath("$.data.description").value("Updated Description"))
    }

    @Test
    fun `should return validation error when updating with title exceeding max length`() {
        val longTitle = "A".repeat(256) // 256 characters, exceeding 255 limit
        val updateRequest = UpdateTodoRequest(title = longTitle, description = "Valid Description")
        val existingTodo = Todo(id = 1, title = "Old Title", description = "Old Description")

        whenever(todoService.getTodoById(1L)).thenReturn(existingTodo)

        mockMvc.perform(
            put("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
        )
            .andExpect(status().isUnprocessableEntity)  // 422 status
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(false))
            .andExpect(jsonPath("$.message").value("Validation failed: title: Title cannot exceed 255 characters"))
    }

    @Test
    fun `should delete todo by id`() {
        whenever(todoService.deleteTodo(1L)).thenReturn(true)

        mockMvc.perform(delete("/api/todos/1"))
            .andExpect(status().isOk)  // Changed from 204 to 200 with response wrapper
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(true))
            .andExpect(jsonPath("$.message").value("Todo deleted successfully"))
    }
}