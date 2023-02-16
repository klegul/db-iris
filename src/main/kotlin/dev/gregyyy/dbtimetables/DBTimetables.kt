package dev.gregyyy.dbtimetables

import dev.gregyyy.dbtimetables.model.*
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DBTimetables(val clientId: String, val apiKey: String) {

    companion object {
        private const val PATH_DELIMITER = "|"
    }

    fun getTimetable(stationEva: String, dateTime: LocalDateTime): Timetable {
        val irisTimetable = IrisCaller.getCurrentTimetable(stationEva, dateTime)
        val irisChangeTimetable = IrisCaller.getFullChangesTimetable(stationEva)

        val journeys = mutableListOf<Journey>()

        for (irisJourney in irisTimetable.journeys) {
            val platform: String = irisJourney.arrival?.platform ?: irisJourney.departure?.platform ?: ""
            var arrivalDelay: Int? = null
            var departureDelay: Int? = null
            val messages = mutableListOf<Message>()
            val changedPath = mutableListOf<String>()

            for (irisChangeJourney in irisChangeTimetable.journeys) {
                if (irisChangeJourney.id == irisJourney.id) {
                    if (irisJourney.arrival != null) {
                        val delayedArrival = getDateTime(irisChangeJourney.arrival?.changedTime)
                        arrivalDelay = Duration.between(getDateTime(irisJourney.arrival.dateTime), delayedArrival)
                            .toMinutes().toInt()
                    }

                    if (irisJourney.departure != null) {
                        val delayedDeparture = getDateTime(irisChangeJourney.departure?.changedTime)
                        departureDelay = Duration.between(getDateTime(irisJourney.departure.dateTime), delayedDeparture)
                            .toMinutes().toInt()
                    }

                    val irisMessages = mutableListOf<IrisMessage>()

                    irisChangeJourney.messages?.let { irisMessages.addAll(it) }
                    irisChangeJourney.arrival?.messages?.let { irisMessages.addAll(it) }
                    irisChangeJourney.departure?.messages?.let { irisMessages.addAll(it) }

                    for (irisMessage in irisMessages) {
                        messages.add(
                            Message(
                                timestamp = getDateTime(irisMessage.timestamp)!!,
                                type = MessageType.getByCode(irisMessage.code),
                                from = getDateTime(irisMessage.from),
                                to = getDateTime(irisMessage.to),
                                text = null,
                            )
                        )
                    }

                    if (irisChangeJourney.arrival?.path != null) {
                        changedPath.addAll(irisChangeJourney.arrival.path.split(PATH_DELIMITER))
                    }

                    if (irisChangeJourney.departure?.path != null) {
                        changedPath.addAll(irisChangeJourney.departure.path.split(PATH_DELIMITER))
                    }

                    if (irisChangeJourney.arrival?.path != null || irisChangeJourney.departure?.path != null) {
                        changedPath.add(irisChangeTimetable.station)
                    }
                }
            }

            val path = mutableListOf<String>()

            if (irisJourney.arrival?.path != null) {
                path.addAll(irisJourney.arrival.path.split(PATH_DELIMITER))
            }

            if (irisJourney.departure?.path != null) {
                path.addAll(irisJourney.departure.path.split("|"))
            }

            path.add(irisTimetable.station)

            val journey = Journey(
                trainType = TrainType.valueOf(irisJourney.trainLine!!.trainClass),
                line = irisJourney.trainLine.tripType,
                number = irisJourney.trainLine.number,
                platform = platform,
                arrivalTime = getDateTime(irisJourney.arrival?.dateTime),
                departureTime = getDateTime(irisJourney.departure?.dateTime),
                arrivalDelay = arrivalDelay,
                departureDelay = departureDelay,
                messages = messages,
                path = path,
                changedPath = emptyList()
            )
            journeys.add(journey)
        }

        val timetable = Timetable(irisTimetable.station, journeys)
        return timetable;
    }

    private fun getDateTime(dateTime: String?): LocalDateTime? {
        if (dateTime == null) return null
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyMMddHHmm"))
    }

}