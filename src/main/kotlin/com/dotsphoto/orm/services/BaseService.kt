package com.dotsphoto.orm.services

import com.dotsphoto.orm.services.repositories.DaoRepository
import com.dotsphoto.orm.services.repositories.LongIdDaoRepository
import com.dotsphoto.orm.services.repositories.NonUpdatableLongIdRepository
import com.dotsphoto.orm.services.repositories.NonUpdatableRepository
import com.dotsphoto.orm.util.*
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.SqlExpressionBuilder

abstract class BaseService<ID : Comparable<ID>, T : IdTable<ID>, DTO : TableDto<ID, T>, CDTO : CreateDto<ID, T>>(
    open val repository: NonUpdatableRepository<ID, T, DTO, CDTO>
) {

    fun findAll(op: SqlExpressionBuilder.() -> Op<Boolean>): List<DTO> = repository.findAll(op)
    fun findById(id: ID): DTO? = repository.findById(id)

    fun create(cdto: CDTO): DTO = repository.create(cdto)

    fun delete(id: ID) = repository.delete(id)
}

abstract class BaseDaoService<ID : Comparable<ID>, T : IdTable<ID>, DTO : TableDto<ID, T>, CDTO : CreateDto<ID, T>, UDTO : UpdateDto<ID, T>>(
    override val repository: DaoRepository<ID, T, DTO, CDTO, UDTO>
) : BaseService<ID, T, DTO, CDTO>(repository) {
    fun update(id:ID, udto: UDTO): DTO? = repository.update(id, udto)
}

abstract class LongIdService<T: LongIdTable, LDTO : LongIdTableDto<T>, CLDTO : CreateLongDto<T>>(
    override val repository: NonUpdatableLongIdRepository<T, LDTO, CLDTO>
) : BaseService<Long, T, LDTO, CLDTO>(repository)

abstract class LongIdDaoService<T: LongIdTable, LDTO : LongIdTableDto<T>, CLDTO : CreateLongDto<T>, ULDTO: UpdateLongDto<T>>(
    override val repository: LongIdDaoRepository<T, LDTO, CLDTO, ULDTO>
) : BaseDaoService<Long, T, LDTO, CLDTO, ULDTO>(repository)