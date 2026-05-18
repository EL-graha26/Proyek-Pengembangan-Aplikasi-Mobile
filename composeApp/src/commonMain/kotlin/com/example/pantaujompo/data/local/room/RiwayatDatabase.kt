package com.example.pantaujompo.data.local.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "riwayat_lari")
data class RiwayatEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val jarak: Double,
    val kalori: Int,
    val durasi: Int,
    val pace: String,
    val tanggal: Long = System.currentTimeMillis(), // 🔥 PASTI KOMA DI SINI KETINGGALAN KAN KEMAREN? WKWK
    val ruteString: String
)

@Dao
interface RiwayatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRiwayat(riwayat: RiwayatEntity)

    @Query("SELECT * FROM riwayat_lari ORDER BY tanggal DESC")
    fun getAllRiwayat(): Flow<List<RiwayatEntity>>

    @Query("DELETE FROM riwayat_lari WHERE id = :id")
    suspend fun hapusRiwayatById(id: Int)
}

@Database(entities = [RiwayatEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun riwayatDao(): RiwayatDao
}