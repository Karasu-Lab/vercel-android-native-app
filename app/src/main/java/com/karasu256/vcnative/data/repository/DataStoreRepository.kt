package com.karasu256.vcnative.data.repository

abstract class DataStoreRepository<T : Any> : IRepository<T> {
    override suspend fun restore(): T? = read()
}
