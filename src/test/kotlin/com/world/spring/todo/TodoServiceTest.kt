package com.world.spring.todo

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.argThat
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

class TodoServiceTest {

    @Mock
    private lateinit var mockRepository: TodoRepository

    private lateinit var todoService: TodoService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        todoService = TodoService(mockRepository)
    }

    @Test
    fun `should return all todos from repository`() {
        val todos = listOf(
            Todo(id = 1, title = "Todo 1"),
            Todo(id = 2, title = "Todo 2")
        )
        `when`(mockRepository.findAll()).thenReturn(todos)

        val result = todoService.getAllTodos()

        assertEquals(todos, result)
        verify(mockRepository).findAll()
    }

    @Test
    fun `should return todo by id from repository`() {
        val todo = Todo(id = 1, title = "Test Todo")
        `when`(mockRepository.findById(1L)).thenReturn(todo)

        val result = todoService.getTodoById(1L)

        assertEquals(todo, result)
        verify(mockRepository).findById(1L)
    }

    @Test
    fun `should return null when todo not found by id`() {
        `when`(mockRepository.findById(1L)).thenReturn(null)

        val result = todoService.getTodoById(1L)

        assertNull(result)
        verify(mockRepository).findById(1L)
    }

    @Test
    fun `should create a new todo with null id`() {
        val inputTodo = Todo(title = "New Todo", description = "New Description")
        val savedTodo = Todo(id = 1, title = "New Todo", description = "New Description")
        whenever(mockRepository.save(any())).thenReturn(savedTodo)

        val result = todoService.createTodo(inputTodo)

        assertEquals(savedTodo, result)
        verify(mockRepository).save(argThat { todo -> todo.id == null })
    }

    @Test
    fun `should create todo without validation in service layer`() {
        // The service layer should not do validation, controller handles it
        val inputTodo = Todo(title = "", description = "Description") // Invalid but service accepts it
        val savedTodo = Todo(id = 1, title = "", description = "Description")
        whenever(mockRepository.save(any())).thenReturn(savedTodo)

        val result = todoService.createTodo(inputTodo)

        assertEquals(savedTodo, result)
    }

    @Test
    fun `should update existing todo`() {
        val existingTodo = Todo(id = 1, title = "Old Title", description = "Old Description")
        val updatedTodo = Todo(id = 1, title = "New Title", description = "New Description")
        whenever(mockRepository.findById(1L)).thenReturn(existingTodo)
        whenever(mockRepository.update(eq(1L), any())).thenReturn(updatedTodo)

        val result = todoService.updateTodo(1L, Todo(title = "New Title", description = "New Description"))

        assertNotNull(result)
        assertEquals(updatedTodo, result)
        verify(mockRepository).update(eq(1L), any())
    }

    @Test
    fun `should update todo without validation in service layer`() {
        // The service layer should not do validation, controller handles it
        val existingTodo = Todo(id = 1, title = "Old Title", description = "Old Description")
        val updatedTodo = Todo(id = 1, title = "", description = "New Description") // Invalid but service accepts it
        whenever(mockRepository.findById(1L)).thenReturn(existingTodo)
        whenever(mockRepository.update(eq(1L), any())).thenReturn(updatedTodo)

        val result = todoService.updateTodo(1L, Todo(title = "", description = "New Description"))

        assertNotNull(result)
        assertEquals(updatedTodo, result)
    }

    @Test
    fun `should delete todo by id`() {
        `when`(mockRepository.deleteById(1L)).thenReturn(true)

        val result = todoService.deleteTodo(1L)

        assertTrue(result)
        verify(mockRepository).deleteById(1L)
    }

    @Test
    fun `should return todo when id is valid`() {
        val todo = Todo(id = 1, title = "Test Todo")
        whenever(mockRepository.findById(1L)).thenReturn(todo)

        val result = todoService.getTodoById(1L)

        assertEquals(todo, result)
        verify(mockRepository).findById(1L)
    }
}