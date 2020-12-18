package events_statistic

import clock.SettableClock
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.Instant
import java.time.ZoneOffset

internal class EventsStatisticTest {

    private infix fun Map<String, IntArray>.contentEquals(other: Map<String, IntArray>): Boolean {
        var ok = true
        for (key in this.keys) {
            ok = ok && (this[key] contentEquals other[key])
        }
        for (key in other.keys) {
            ok = ok && (this[key] contentEquals other[key])
        }
        return ok
    }

    private var timer = Instant.now().atOffset(ZoneOffset.UTC)

    private fun getInstantOfTime(h: Int = 12, m: Int = 0, s: Int = 0): Instant {
        timer = timer.withHour(h).withMinute(m).withSecond(s)
        return timer.toInstant()
    }

    private fun addTimeToInstant(h: Long = 0, m: Long = 0, s: Long = 0): Instant {
        timer = timer.plusHours(h).plusMinutes(m).plusSeconds(s)
        return timer.toInstant()
    }

    @Test
    fun getAllEventStatisticEmpty() {
        val eventsStatistic = EventsStatistic()
        val allRpms = eventsStatistic.getAllEventStatistic()
        assertEquals(emptyMap<String, IntArray>(), allRpms)
    }

    @Test
    fun getEventStatisticByNotExistedName() {
        val eventsStatistic = EventsStatistic()
        val rpms = eventsStatistic.getEventStatisticByName("notExists")
        assertTrue(IntArray(60) contentEquals rpms)
    }

    @Test
    fun incSingleEvent() {
        val eventsStatistic = EventsStatistic()
        eventsStatistic.incEvent("mainEvent")
        val rpms = eventsStatistic.getEventStatisticByName("mainEvent")

        val expectedArray = IntArray(60)
        expectedArray[0] = 1
        assertTrue(expectedArray contentEquals rpms)
    }

    @Test
    fun incSingleEventWait20Minutes() {
        val clock = SettableClock(getInstantOfTime(12, 50, 0))
        val eventsStatistic = EventsStatistic(clock)
        eventsStatistic.incEvent("mainEvent")

        clock.setTime(addTimeToInstant(m = 20))
        val rpms = eventsStatistic.getEventStatisticByName("mainEvent")

        val expectedRpms = IntArray(60)
        expectedRpms[20] = 1
        assertTrue(expectedRpms contentEquals rpms)
    }

    @Test
    fun incSingleEventWait70Minutes() {
        val clock = SettableClock(getInstantOfTime(12, 0, 0))
        val eventsStatistic = EventsStatistic(clock)
        eventsStatistic.incEvent("mainEvent")

        clock.setTime(addTimeToInstant(m = 70))
        val rpms = eventsStatistic.getEventStatisticByName("mainEvent")

        val expectedRpms = IntArray(60)
        assertTrue(expectedRpms contentEquals rpms)
    }

    @Test
    fun incMultipleEvents() {
        val clock = SettableClock(getInstantOfTime(12, 0, 0))
        val eventsStatistic = EventsStatistic(clock)

        clock.setTime(addTimeToInstant(m = 10))
        eventsStatistic.incEvent("event1")
        eventsStatistic.incEvent("event2")

        clock.setTime(addTimeToInstant(m = 10))
        eventsStatistic.incEvent("event2")
        eventsStatistic.incEvent("event3")

        clock.setTime(addTimeToInstant(m = 10))
        eventsStatistic.incEvent("event3")
        eventsStatistic.incEvent("event1")
        eventsStatistic.incEvent("event1")

        clock.setTime(addTimeToInstant(m = 10))
        val allRpms = eventsStatistic.getAllEventStatistic()

        val expectedAllRpms = mapOf(
            "event1" to IntArray(60).let {
                it.set(10, 2)
                it.set(30, 1)
                it
            },
            "event2" to IntArray(60).let {
                it.set(20, 1)
                it.set(30, 1)
                it
            },
            "event3" to IntArray(60).let {
                it.set(10, 1)
                it.set(20, 1)
                it
            }
        )
        assertTrue(expectedAllRpms contentEquals allRpms)
    }

}