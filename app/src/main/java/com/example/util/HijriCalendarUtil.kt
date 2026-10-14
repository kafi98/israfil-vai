package com.example.util

import java.util.Calendar
import java.util.Locale
import kotlin.math.floor

data class HijriDate(
    val day: Int,
    val month: Int,
    val year: Int,
    val monthName: String
)

object HijriCalendarUtil {
    val BENGALI_MONTHS = listOf(
        "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
        "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
    )

    // Monday-first index matches Flutter (0: সোম, 1: মঙ্গল, 2: বুধ, 3: বৃহস্পতি, 4: শুক্র, 5: শনি, 6: রবি)
    val BENGALI_WEEKDAYS = listOf(
        "সোম", "মঙ্গল", "বুধ", "বৃহস্পতি", "শুক্র", "শনি", "রবি"
    )

    val HIJRI_MONTH_NAMES = listOf(
        "মুহররম", "সফর", "রবিউল আউয়াল", "রবিউস সানি",
        "জমাদিউল আউয়াল", "জমাদিউস সানি", "রজব", "শাবান",
        "রমজান", "শাওয়াল", "জিলকদ", "জিলহজ"
    )

    fun toBengaliDigits(number: Any): String {
        val s = number.toString()
        val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        val sb = java.lang.StringBuilder()
        for (c in s) {
            if (c in '0'..'9') {
                sb.append(bengaliDigits[c - '0'])
            } else {
                sb.append(c)
            }
        }
        return sb.toString()
    }

    fun calculateHijri(calendar: Calendar): HijriDate {
        val millis = calendar.timeInMillis
        val jd = millis.toDouble() / 86400000.0 + 2440587.5
        val z = floor(jd + 0.5).toLong()
        val l = z - 1948440 + 10632
        val n = floor((l - 1).toDouble() / 10631.0).toLong()
        val ll = l - 10631 * n + 354
        val j = floor((10985.0 - ll) / 5316.0).toLong() * floor((50.0 * ll) / 17719.0).toLong() +
                floor(ll.toDouble() / 5670.0).toLong() * floor((43.0 * ll) / 15238.0).toLong()
        val q = ll - floor((30.0 - j) / 15.0).toLong() * floor((17719.0 * j) / 50.0).toLong() -
                floor(j.toDouble() / 16.0).toLong() * floor((15238.0 * j) / 43.0).toLong() + 29
        val m = floor((24.0 * q) / 709.0).toInt()
        val day = (q - floor((709.0 * m) / 24.0).toLong()).toInt()
        val year = (30 * n + j - 30).toInt()

        val safeMonth = m.coerceIn(1, 12)
        val name = HIJRI_MONTH_NAMES[safeMonth - 1]
        return HijriDate(day, safeMonth, year, name)
    }

    fun calculateHijri(year: Int, month: Int, day: Int): HijriDate {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month - 1)
        cal.set(Calendar.DAY_OF_MONTH, day)
        cal.set(Calendar.HOUR_OF_DAY, 12)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return calculateHijri(cal)
    }

    /**
     * Replicates the exact dateLine formatting from the Flutter app:
     * '${d.day} ${months[d.month-1]} ${d.year}  •  ${weekdays[d.weekday-1]}  •  ${h.d} ${h.name} ${h.y} হিজরি'
     */
    fun formatDateLine(year: Int, month: Int, day: Int, useBengaliNumerals: Boolean = true): String {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month - 1)
        cal.set(Calendar.DAY_OF_MONTH, day)
        cal.set(Calendar.HOUR_OF_DAY, 12)

        // Java Day of Week: Sunday = 1, Monday = 2, ..., Saturday = 7
        // Monday-first index: Monday -> 0, ..., Sunday -> 6
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        val weekdayIndex = if (dow == Calendar.SUNDAY) 6 else dow - 2
        val weekdayName = BENGALI_WEEKDAYS[weekdayIndex]
        val monthName = BENGALI_MONTHS[(month - 1).coerceIn(0, 11)]

        val hijri = calculateHijri(year, month, day)

        return if (useBengaliNumerals) {
            "${toBengaliDigits(day)} $monthName ${toBengaliDigits(year)}  •  $weekdayName  •  ${toBengaliDigits(hijri.day)} ${hijri.monthName} ${toBengaliDigits(hijri.year)} হিজরি"
        } else {
            "$day $monthName $year  •  $weekdayName  •  ${hijri.day} ${hijri.monthName} ${hijri.year} হিজরি"
        }
    }

    fun formatTime(hour: Int, minute: Int, useBengaliNumerals: Boolean = true): String {
        val isPm = hour >= 12
        val period = if (isPm) "দুপুর/সন্ধ্যা" else "সকাল"
        val hour12 = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val minStr = String.format(Locale.US, "%02d", minute)
        return if (useBengaliNumerals) {
            "${toBengaliDigits(hour12)}:${toBengaliDigits(minStr)} ${if (isPm) "PM" else "AM"}"
        } else {
            String.format(Locale.US, "%d:%02d %s", hour12, minute, if (isPm) "PM" else "AM")
        }
    }
}
