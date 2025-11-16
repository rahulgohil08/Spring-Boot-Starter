package com.world.spring.todo

interface TodoRepository {
    fun findAll(): List<Todo>
    fun findById(id: Long): Todo?
    fun save(todo: Todo): Todo
    fun update(id: Long, todo: Todo): Todo?
    fun deleteById(id: Long): Boolean
    fun deleteAll()
}