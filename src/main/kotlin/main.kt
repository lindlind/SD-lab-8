import events_statistic.IEventsStatistic
import events_statistic.EventsStatistic

fun main(args: Array<String>) {
    val eventsStatistic: IEventsStatistic = EventsStatistic()
    eventsStatistic.incEvent("event1")
    eventsStatistic.incEvent("event1")
    eventsStatistic.incEvent("event2")
    eventsStatistic.printStatistic()
}