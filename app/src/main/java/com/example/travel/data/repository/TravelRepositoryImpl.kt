package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.ExpenseDao
import com.example.travel.data.local.db.dao.TripNoteDao
import com.example.travel.data.mapper.toDomain
import com.example.travel.data.mapper.toEntity
import com.example.travel.domain.model.Expense
import com.example.travel.domain.model.TripNote
import com.example.travel.domain.repository.TravelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

@Singleton
class TravelRepositoryImpl @Inject constructor(
    private val tripNoteDao: TripNoteDao,
    private val expenseDao: ExpenseDao
) : TravelRepository {

    override fun getTripNotes(): Flow<List<TripNote>> {
        return tripNoteDao.getAllNotes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addTripNote(note: TripNote): Resource<Long> {
        return try {
            val id = tripNoteDao.insertNote(note.toEntity())
            Timber.d("Trip note added with ID: $id")
            Resource.Success(id)
        } catch (e: Exception) {
            Timber.e(e, "Error adding trip note")
            Resource.Error(e.message ?: "Failed to add trip note", e)
        }
    }

    override suspend fun deleteTripNote(id: Long): Resource<Unit> {
        return try {
            tripNoteDao.deleteNoteById(id)
            Timber.d("Trip note deleted ID: $id")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting trip note")
            Resource.Error(e.message ?: "Failed to delete trip note", e)
        }
    }

    override fun getExpenses(): Flow<List<Expense>> {
        return expenseDao.getAllExpenses().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addExpense(expense: Expense): Resource<Long> {
        return try {
            val id = expenseDao.insertExpense(expense.toEntity())
            Timber.d("Expense added with ID: $id")
            Resource.Success(id)
        } catch (e: Exception) {
            Timber.e(e, "Error adding expense")
            Resource.Error(e.message ?: "Failed to add expense", e)
        }
    }

    override suspend fun deleteExpense(id: Long): Resource<Unit> {
        return try {
            expenseDao.deleteExpenseById(id)
            Timber.d("Expense deleted ID: $id")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting expense")
            Resource.Error(e.message ?: "Failed to delete expense", e)
        }
    }

    override fun calculateSunriseSunset(lat: Double, lon: Double, dateMillis: Long): Pair<String, String> {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.timeInMillis = dateMillis
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        val zenith = 90.833
        val degToRad = Math.PI / 180.0
        val radToDeg = 180.0 / Math.PI

        val n1 = Math.floor(275.0 * calendar.get(Calendar.MONTH) / 9.0)
        val n2 = Math.floor((calendar.get(Calendar.MONTH) + 9.0) / 12.0)
        val n3 = (1.0 + Math.floor((calendar.get(Calendar.YEAR) - 4.0 * Math.floor(calendar.get(Calendar.YEAR) / 4.0) + 2.0) / 3.0))
        val n = n1 - (n2 * n3) + calendar.get(Calendar.DAY_OF_MONTH) - 30.0

        val lngHour = lon / 15.0

        val tSunrise = n + ((6.0 - lngHour) / 24.0)
        val mSunrise = (0.9856 * tSunrise) - 3.289
        var lSunrise = mSunrise + (1.916 * sin(mSunrise * degToRad)) + (0.020 * sin(2.0 * mSunrise * degToRad)) + 282.634
        if (lSunrise >= 360.0) lSunrise -= 360.0
        if (lSunrise < 0.0) lSunrise += 360.0

        var raSunrise = radToDeg * Math.atan(0.91764 * tan(lSunrise * degToRad))
        if (raSunrise >= 360.0) raSunrise -= 360.0
        if (raSunrise < 0.0) raSunrise += 360.0

        val lQuadrant = Math.floor(lSunrise / 90.0) * 90.0
        val raQuadrant = Math.floor(raSunrise / 90.0) * 90.0
        raSunrise += (lQuadrant - raQuadrant)
        raSunrise /= 15.0

        val sinDec = 0.39782 * sin(lSunrise * degToRad)
        val cosDec = cos(asin(sinDec))

        val cosH = (cos(zenith * degToRad) - (sinDec * sin(lat * degToRad))) / (cosDec * cos(lat * degToRad))
        if (cosH > 1.0) return Pair("Sun never rises", "Sun never sets")
        if (cosH < -1.0) return Pair("Sun never sets", "Sun never rises")

        val hSunrise = 360.0 - (radToDeg * acos(cosH))
        val hSunriseHours = hSunrise / 15.0
        val tSunriseUtc = hSunriseHours + raSunrise - (0.06571 * tSunrise) - 6.622

        var utcSunrise = tSunriseUtc - lngHour
        if (utcSunrise >= 24.0) utcSunrise -= 24.0
        if (utcSunrise < 0.0) utcSunrise += 24.0

        val localOffset = TimeZone.getDefault().getOffset(dateMillis) / 3600000.0
        var localSunrise = utcSunrise + localOffset
        if (localSunrise >= 24.0) localSunrise -= 24.0
        if (localSunrise < 0.0) localSunrise += 24.0

        val sunriseHour = Math.floor(localSunrise).toInt()
        val sunriseMin = Math.floor((localSunrise - sunriseHour) * 60.0).toInt()

        val tSunset = n + ((18.0 - lngHour) / 24.0)
        val mSunset = (0.9856 * tSunset) - 3.289
        var lSunset = mSunset + (1.916 * sin(mSunset * degToRad)) + (0.020 * sin(2.0 * mSunset * degToRad)) + 282.634
        if (lSunset >= 360.0) lSunset -= 360.0
        if (lSunset < 0.0) lSunset += 360.0

        var raSunset = radToDeg * Math.atan(0.91764 * tan(lSunset * degToRad))
        if (raSunset >= 360.0) raSunset -= 360.0
        if (raSunset < 0.0) raSunset += 360.0

        val lQuadrantSet = Math.floor(lSunset / 90.0) * 90.0
        val raQuadrantSet = Math.floor(raSunset / 90.0) * 90.0
        raSunset += (lQuadrantSet - raQuadrantSet)
        raSunset /= 15.0

        val sinDecSet = 0.39782 * sin(lSunset * degToRad)
        val cosDecSet = cos(asin(sinDecSet))

        val cosHSet = (cos(zenith * degToRad) - (sinDecSet * sin(lat * degToRad))) / (cosDecSet * cos(lat * degToRad))

        val hSunset = radToDeg * acos(cosHSet)
        val hSunsetHours = hSunset / 15.0
        val tSunsetUtc = hSunsetHours + raSunset - (0.06571 * tSunset) - 6.622

        var utcSunset = tSunsetUtc - lngHour
        if (utcSunset >= 24.0) utcSunset -= 24.0
        if (utcSunset < 0.0) utcSunset += 24.0

        var localSunset = utcSunset + localOffset
        if (localSunset >= 24.0) localSunset -= 24.0
        if (localSunset < 0.0) localSunset += 24.0

        val sunsetHour = Math.floor(localSunset).toInt()
        val sunsetMin = Math.floor((localSunset - sunsetHour) * 60.0).toInt()

        val sunriseStr = String.format("%02d:%02d AM", if (sunriseHour % 12 == 0) 12 else sunriseHour % 12, sunriseMin)
        val sunsetStr = String.format("%02d:%02d PM", if (sunsetHour % 12 == 0) 12 else sunsetHour % 12, sunsetMin)

        return Pair(sunriseStr, sunsetStr)
    }
}
