package com.dotsphoto.orm.util

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.LongIdTable

interface TableDto<ID : Comparable<ID>, T : IdTable<ID>> {
    val id: ID
}

interface LongIdTableDto<E: LongIdTable> : TableDto<Long, E>

interface CreateDto<ID : Comparable<ID>, T : IdTable<ID>>

interface CreateLongDto<T : LongIdTable> : CreateDto<Long, T>

interface UpdateDto<ID : Comparable<ID>, T : IdTable<ID>>

interface UpdateLongDto<T : LongIdTable> : UpdateDto<Long, T>