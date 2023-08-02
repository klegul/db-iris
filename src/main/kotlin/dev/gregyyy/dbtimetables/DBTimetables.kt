package dev.gregyyy.dbtimetables

import dev.gregyyy.dbtimetables.model.*
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DBTimetables {

    companion object {
        private const val PATH_DELIMITER = "|"
        const val BASE_URL_UNOFFICIAL = "https://iris.noncd.db.de/iris-tts/timetable"
        const val BASE_URL_OFFICIAL = "https://apis.deutschebahn.com/db-api-marketplace/apis/timetables/v1"
    }

    private val clientId: String?
    private val apiKey: String?

    private val irisCaller: IrisCaller

    constructor(clientId: String, apiKey: String) {
        this.clientId = clientId
        this.apiKey = apiKey
        this.irisCaller = IrisCaller(BASE_URL_OFFICIAL)
    }

    constructor() {
        this.clientId = null
        this.apiKey = null
        this.irisCaller = IrisCaller(BASE_URL_UNOFFICIAL)
    }

    fun getTimetable(stationEva: String, dateTime: LocalDateTime): Timetable {
        val irisTimetable = irisCaller.getCurrentTimetable(stationEva, dateTime)
        val irisChangeTimetable = irisCaller.getFullChangesTimetable(stationEva)

        val journeys = mutableListOf<Journey>()

        for (irisJourney in irisTimetable.journeys) {
            val platform: String = irisJourney.arrival?.platform ?: irisJourney.departure?.platform ?: ""
            val arrivalTime = getDateTime(irisJourney.arrival?.dateTime)
            val departureTime = getDateTime(irisJourney.departure?.dateTime)
            var arrivalDelay: Int? = null
            var departureDelay: Int? = null
            val messages = mutableListOf<Message>()
            val changedPath = mutableListOf<String>()

            for (irisChangeJourney in irisChangeTimetable.journeys) {
                if (irisChangeJourney.id == irisJourney.id) {
                    if (irisJourney.arrival != null && arrivalTime != null) {
                        val delayedArrival = getDateTime(irisChangeJourney.arrival?.changedTime)
                        if (delayedArrival != null) {
                            arrivalDelay = Duration.between(arrivalTime, delayedArrival).toMinutes().toInt()
                        }
                    }

                    if (irisJourney.departure != null && departureTime != null) {
                        val delayedDeparture = getDateTime(irisChangeJourney.departure?.changedTime)
                        if (delayedDeparture != null) {
                            departureDelay = Duration.between(departureTime, delayedDeparture).toMinutes().toInt()
                        }
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

        journeys.sortBy { journey -> journey.departureTime }

        return Timetable(irisTimetable.station, journeys)
    }

    private fun getDateTime(dateTime: String?): LocalDateTime? {
        if (dateTime == null) return null
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyMMddHHmm"))
    }

}