package com.world.spring.todo

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InMemoryTodoRepositoryTest {

    private lateinit var repository: InMemoryTodoRepository

    @BeforeEach
    fun setUp() {
        repository = InMemoryTodoRepository()
        repository.deleteAll() // Clear any sample data
    }

    @Test
    fun `should create and find all todos`() {
        val todo1 = Todo(title = "Test Todo 1", description = "Test Description 1")
        val todo2 = Todo(title = "Test Todo 2", description = "Test Description 2")

        val savedTodo1 = repository.save(todo1)
        val savedTodo2 = repository.save(todo2)

        val allTodos = repository.findAll()

        assertEquals(2, allTodos.size)
        assertTrue(allTodos.contains(savedTodo1))
        assertTrue(allTodos.contains(savedTodo2))
    }

    @Test
    fun `should find todo by id`() {
        val todo = Todo(title = "Test Todo", description = "Test Description")
        val savedTodo = repository.save(todo)

        val foundTodo = repository.findById(savedTodo.id!!)

        assertNotNull(foundTodo)
        assertEquals(savedTodo.id, foundTodo?.id)
        assertEquals(savedTodo.title, foundTodo?.title)
        assertEquals(savedTodo.description, foundTodo?.description)
    }

    @Test
    fun `should return null when todo not found`() {
        val foundTodo = repository.findById(999L)

        assertNull(foundTodo)
    }

    @Test
    fun `should update existing todo`() {
        val originalTodo = Todo(title = "Original Todo", description = "Original Description")
        val savedTodo = repository.save(originalTodo)

        val updatedTodo = repository.update(savedTodo.id!!, 
            savedTodo.copy(title = "Updated Todo", description = "Updated Description"))

        assertNotNull(updatedTodo)
        assertEquals("Updated Todo", updatedTodo?.title)
        assertEquals("Updated Description", updatedTodo?.description)
    }

    @Test
    fun `should return null when updating non-existent todo`() {
        val updatedTodo = repository.update(999L, 
            Todo(title = "Updated Todo", description = "Updated Description"))

        assertNull(updatedTodo)
    }

    @Test
    fun `should delete todo by id`() {
        val todo = Todo(title = "Test Todo", description = "Test Description")
        val savedTodo = repository.save(todo)

        val deleted = repository.deleteById(savedTodo.id!!)

        assertTrue(deleted)
        assertNull(repository.findById(savedTodo.id))
    }

    @Test
    fun `should return false when deleting non-existent todo`() {
        val deleted = repository.deleteById(999L)

        assertFalse(deleted)
    }

    @Test
    fun `should delete all todos`() {
        repository.save(Todo(title = "Test Todo 1"))
        repository.save(Todo(title = "Test Todo 2"))

        repository.deleteAll()

        assertTrue(repository.findAll().isEmpty())
    }
}