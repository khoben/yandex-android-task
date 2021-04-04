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
    fun watchTicker(ticker: String): LiveData<Stock?>

    @Query("SELECT * FROM stock WHERE ticker = :ticker")
    suspend fun getByTicker(ticker: String): Stock?

    @Query("SELECT * FROM stock")
    suspend fun all(): List<Stock>?

    @Query("SELECT ticker FROM stock")
    suspend fun getAllTickerSymbols(): List<String>?

    @Query("SELECT COUNT(id) FROM stock")
    suspend fun count(): Int

    @Query("SELECT * FROM stock WHERE ticker LIKE '%' || :searchQuery  || '%' OR companyName LIKE '%' || :searchQuery  || '%'")
    suspend fun search(searchQuery: String?): List<Stock>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stock: Stock): Long

    @Update
    suspend fun update(stock: Stock)

    @Delete
    suspend fun delete(stock: Stock)
}