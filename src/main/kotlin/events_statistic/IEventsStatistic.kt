package events_statistic

interface IEventsStatistic {
    fun incEvent(name: String)
    fun getEventStatisticByName(name: String): IntArray
    fun getAllEventStatistic(): Map<String, IntArray>
    fun printStatistic()
}