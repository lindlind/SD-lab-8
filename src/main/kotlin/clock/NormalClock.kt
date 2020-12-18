package clock

import clock.IClock
import java.time.Instant

class NormalClock : IClock {
    override fun now() = Instant.now()!!
}