package id.web.azammukhtar.multithreading.room

import androidx.room.*


@Dao
interface DataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(dataModel: DataModel)

    @Query(value = "SELECT * FROM data")
    suspend fun getAllData(): List<DataModel>

    @Delete
    suspend fun deleteData(dataModel: DataModel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateData(dataModel: DataModel)

    @Query(value = "DELETE FROM data")
    suspend fun deleteAllData()

    @Query(value = "SELECT * FROM data WHERE status = 1 ORDER BY id DESC")
    suspend fun getSuccessData(): List<DataModel>

    @Query(value = "SELECT * FROM data WHERE status = 0")
    suspend fun getProcessData(): List<DataModel>

    @Query(value = "SELECT * FROM data WHERE status > 1 ORDER BY id DESC")
    suspend fun getDefectData(): List<DataModel>

}
