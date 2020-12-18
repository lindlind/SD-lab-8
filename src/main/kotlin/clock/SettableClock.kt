package clock

import clock.IClock
import java.time.Instant

class SettableClock(private var time: Instant) : IClock {

    fun setTime(now: Instant) { time = now }

    override fun now() = time

}