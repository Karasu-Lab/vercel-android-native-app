package com.karasu256.vcnative.data.repository

abstract class CacheRepository<T : Any>(
    private val source: suspend () -> T?,
) : ICacheable<T> {
    override suspend fun restore(): T? = read() ?: source()?.also { write(it) }
}
