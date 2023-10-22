package com.dotsphoto.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DateUtils {
    companion object {
        fun now(): LocalDateTime {
            return Clock.System.now().toLocalDateTime(TimeZone.of("Europe/Moscow"))
        }
    }
}