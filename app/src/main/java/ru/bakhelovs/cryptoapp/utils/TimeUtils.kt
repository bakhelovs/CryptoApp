package ru.bakhelovs.cryptoapp.utils

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

// метод преобразует секунды с 1 января 1970 года
fun convertTimestampToTime(timestamp: Int?): String {
    if (timestamp == null) return ""
    // т.к с сервера приходит ответ в секундах,
    // а метод считает в милисекундах, то умножаем на 1000
    val stamp = Timestamp(timestamp.toLong() * 1000)
    val date = Date(stamp.time)
    // с больших букв- 24 часовой формат, с маленьких - 12 часовой
    val pattern = "HH:mm:ss"
    // текущее местоположение
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    // чтобы временная зона была не по нулевому гринвичу
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(date)
}