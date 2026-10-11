package com.karasu256.vcnative.data.repository

interface IRepository<T : Any> {
    suspend fun read(): T?

    suspend fun write(value: T?)
}
