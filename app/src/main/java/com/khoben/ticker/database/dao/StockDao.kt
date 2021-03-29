package com.khoben.ticker.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.khoben.ticker.model.Stock

@Dao
interface StockDao {
    @get:Query("SELECT * FROM stock")
    val all: LiveData<List<Stock>>?

    @get:Query("SELECT * FROM stock WHERE isFavourite == 1")
    val favorite: LiveData<List<Stock>>?

    @Query("SELECT * FROM stock WHERE ticker = :ticker")
    suspend fun getByTicker(ticker: String): Stock?

    @Query("SELECT * FROM stock")
    suspend fun all(): List<Stock>?

    @Query("SELECT * FROM stock WHERE ticker LIKE '%' || :searchQuery  || '%' OR companyName LIKE '%' || :searchQuery  || '%'")
    fun searchDatabase(searchQuery: String?): LiveData<List<Stock>>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stock: Stock)

    @Update
    suspend fun update(stock: Stock)

    @Delete
    suspend fun delete(stock: Stock)
}