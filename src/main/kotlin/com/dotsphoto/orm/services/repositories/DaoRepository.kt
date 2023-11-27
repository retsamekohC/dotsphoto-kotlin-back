package com.dotsphoto.orm.services.repositories

import com.dotsphoto.orm.util.*
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction

interface CreateRepository<ID : Comparable<ID>, T : IdTable<ID>, DTO : TableDto<ID, T>, CDTO : CreateDto<ID, T>> {
    fun create(cdto: CDTO): DTO
}

interface UpdateRepository<ID : Comparable<ID>, T : IdTable<ID>, DTO : TableDto<ID, T>, UDTO : UpdateDto<ID, T>> {
    fun update(id: ID, udto: UDTO): DTO?
}

interface DeleteRepository<ID : Comparable<ID>, T : IdTable<ID>> {
    fun delete(id: ID)
}

interface FindRepository<ID : Comparable<ID>, T : IdTable<ID>, DTO : TableDto<ID, T>> {
    fun findAll(op: SqlExpressionBuilder.() -> Op<Boolean>): List<DTO>
    fun findById(id: ID): DTO?
    fun findUnique(op: SqlExpressionBuilder.() -> Op<Boolean>): DTO?
}

interface NonUpdatableRepository<ID : Comparable<ID>, T : IdTable<ID>, DTO : TableDto<ID, T>, CDTO : CreateDto<ID, T>> :
    CreateRepository<ID, T, DTO, CDTO>,
    DeleteRepository<ID, T>,
    FindRepository<ID, T, DTO>

interface DaoRepository<ID : Comparable<ID>, T : IdTable<ID>, DTO : TableDto<ID, T>, CDTO : CreateDto<ID, T>, UDTO : UpdateDto<ID, T>> :
    NonUpdatableRepository<ID, T, DTO, CDTO>,
    UpdateRepository<ID, T, DTO, UDTO>


abstract class NonUpdatableLongIdRepository<T : LongIdTable, LDTO : LongIdTableDto<T>, CLDTO : CreateLongDto<T>> :
    NonUpdatableRepository<Long, T, LDTO, CLDTO> {
    protected abstract fun mapper(resultRow: ResultRow): LDTO

    protected abstract fun getTable(): T

    override fun findAll(op: SqlExpressionBuilder.() -> Op<Boolean>): List<LDTO> = transaction {
        getTable().select(op).map{ mapper(it) }
    }

    override fun findById(id: Long): LDTO? = transaction {
        getTable().select { getTable().id eq id }.mapLazy { mapper(it) }.single()
    }

    override fun findUnique(op: SqlExpressionBuilder.() -> Op<Boolean>): LDTO? = transaction {
        val res = getTable().select(op).mapLazy { mapper(it) }
        if (res.count() > 1 ) throw IllegalStateException("not unique, $op")
        res.firstOrNull()
    }

    override fun delete(id: Long) {
        getTable().deleteWhere { getTable().id eq id }
    }

    protected fun insertAndGetDto(body: T.(InsertStatement<Number>) -> Unit): LDTO = transaction {
        getTable().insert(body = body).resultedValues?.map { mapper(it) }?.single()!!
    }
}

abstract class LongIdDaoRepository<T : LongIdTable, LDTO : LongIdTableDto<T>, CLDTO : CreateLongDto<T>, ULDTO : UpdateLongDto<T>> :
    DaoRepository<Long, T, LDTO, CLDTO, ULDTO>,
    NonUpdatableLongIdRepository<T, LDTO, CLDTO>() {

    protected fun updateById(id: Long, body: T.(UpdateStatement) -> Unit): LDTO? = transaction {
        getTable().update({ getTable().id eq id }, body = body)
        findById(id)
    }
}