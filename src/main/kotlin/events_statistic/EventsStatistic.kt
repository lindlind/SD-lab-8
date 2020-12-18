package events_statistic

import clock.IClock
import clock.NormalClock
import java.util.*

class EventsStatistic(private val clock: IClock = NormalClock()) : IEventsStatistic {

    private val eventsTimestamps = mutableMapOf<String, Queue<Long>>()

    private fun removeOldTimestamps(name: String, now: Long = clock.now().epochSecond) {
        eventsTimestamps[name]?.let {
            while (it.isNotEmpty()) {
                if (now - it.peek() < expiredSeconds) { break }
                it.remove()
            }
        }
    }

    private fun convertTimestampsToStats(timestamps: Collection<Long>, now: Long = clock.now().epochSecond): IntArray {
        val stats = IntArray(60)
        timestamps
            .filter { now - it < expiredSeconds }
            .forEach { stats[(now - it).toInt() / 60]++ }
        return stats
    }

    override fun incEvent(name: String) {
        val now = clock.now().epochSecond
        eventsTimestamps.getOrPut(name){ ArrayDeque() }.offer(now)
    }

    override fun getEventStatisticByName(name: String): IntArray {
        val now = clock.now().epochSecond
        removeOldTimestamps(name, now)
        return convertTimestampsToStats(eventsTimestamps.getOrDefault(name, ArrayDeque()), now)
    }

    override fun getAllEventStatistic(): Map<String, IntArray> {
        val now = clock.now().epochSecond
        val stats = mutableMapOf<String, IntArray>()
        eventsTimestamps.forEach { (name, _) ->
            removeOldTimestamps(name, now)
            stats[name] = (convertTimestampsToStats(eventsTimestamps.getOrDefault(name, ArrayDeque()), now))
        }
        return stats
    }

    override fun printStatistic() {
        val stats = getAllEventStatistic()
        stats.forEach { (name, rpms) ->
            println("Statistic of \"$name\" per hour:")
            for (minute in 0..59) {
                println("\t${rpms[minute]} requests per minute $minute minutes ago")
            }
        }
    }

    companion object {
        const val expiredSeconds = 3600L
    }
}