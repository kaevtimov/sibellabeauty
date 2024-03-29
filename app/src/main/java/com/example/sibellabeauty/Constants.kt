package com.example.sibellabeauty

import java.time.format.DateTimeFormatter

object Constants {

    const val FIREBASE_DATABASE_URL = "https://sibellabeauty-default-rtdb.europe-west1.firebasedatabase.app/"
    val procedureDurations = mapOf(
        "0:30" to 1_800_000L,
        "0:45" to 2_700_000L,
        "1:00" to 3_600_000L,
        "1:15" to 4_500_000L,
        "1:30" to 5_400_000L,
        "1:45" to 6_300_000L,
        "2:00" to 7_200_000L,
        "2:15" to 8_100_000L,
        "2:30" to 9_000_000L,
        "2:45" to 9_900_000L,
        "3:00" to 10_800_000L,
        "3:15" to 11_700_000L,
        "3:30" to 12_600_000L,
        "3:45" to 13_500_000L,
        "4:00" to 14_400_000L
    )
    const val USER_DEVICE_IDS_SPLIT_DELIMETER = "|"
    const val LOCAL_DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm"
    const val LOCAL_DATE_FORMATTER = "yyyy-MM-dd"
    const val LOCAL_TIME_FORMATTER = "HH:mm"
}