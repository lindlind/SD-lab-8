package clock

import java.time.Instant

interface IClock {
    fun now(): Instant
}