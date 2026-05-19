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
@Entity(tableName = "riwayat_makanan")
data class MakananEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val namaMakanan: String,
    val protein: Int,
    val karbo: Int,
    val lemak: Int,
    val info: String,
    val tanggal: Long = System.currentTimeMillis()
)

@Dao
interface MakananDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMakanan(makanan: MakananEntity)

    @Query("SELECT * FROM riwayat_makanan ORDER BY tanggal DESC")
    fun getAllMakanan(): Flow<List<MakananEntity>>
}

@Database(entities = [RiwayatEntity::class, MakananEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun riwayatDao(): RiwayatDao
    abstract fun makananDao(): MakananDao
}