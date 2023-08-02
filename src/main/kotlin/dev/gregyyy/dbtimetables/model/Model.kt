package dev.gregyyy.dbtimetables.model

import java.lang.IllegalArgumentException
import java.time.LocalDateTime

data class Timetable(
    val station: String,
    val journeys: List<Journey>
)

data class Journey(
    val trainType: TrainType?,
    val line: String?,
    val number: String,
    val platform: String,
    val arrivalTime: LocalDateTime?,
    val departureTime: LocalDateTime?,
    val arrivalDelay: Int?,
    val departureDelay: Int?,
    val messages: List<Message>,
    val path: List<String>,
    val changedPath: List<String>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Journey

        if (trainType != other.trainType) return false
        if (line != other.line) return false

        return true
    }

    override fun hashCode(): Int {
        var result = trainType?.hashCode() ?: 0
        result = 31 * result + (line?.hashCode() ?: 0)
        return result
    }
}

data class Message(
    val timestamp: LocalDateTime,
    val type: MessageType,
    val from: LocalDateTime?,
    val to: LocalDateTime?,
    val text: String?
)

enum class MessageType(val id: String, val message: String) {
    DETAILED_INFORMATION_SHORTLY("1", "Nähere Information in Kürze"),
    POLICE_INVESTIGATION("2", "Polizeiliche Ermittlung"),
    FIREFIGHTER("3", "Feuerwehreinsatz neben der Strecke"),
    SHORT_TERM_STAFF_SHORTAGE("4", "Kurzfristiger Personalausfall"),
    MEDICAL_CARE("5", "Ärztliche Versorgung eines Fahrgastes"),
    EMERGENCY_BRAKE("6", "Betätigen der Notbremse"),
    PERSONS_IN_TRACK("7", "Personen im Gleis"),
    EMERGENCY_MEDICAL_CARE("8", "Notarzteinsatz am Gleis"),
    STRIKE_EFFECTS("9", "Streikauswirkungen"),
    ANIMALS_IN_TRACK("10", "Ausgebrochene Tiere im Gleis"),
    WEATHER("11", "Unwetter"),
    WAITING_FOR_DELAYED_SHIP("12", "Warten auf ein verspätetes Schiff"),
    CUSTOMS_CHECK("13", "Pass- und Zollkontrolle"),
    DEFECT_ON_STATION("14", "Defekt am Bahnhof"),
    VANDALISM("15", "Beeinträchtigung durch Vandalismus"),
    EXPLOSIVE_DEVICE("16", "Entschärfung einer Fliegerbombe"),
    BRIDGE_DAMAGE("17", "Beschädigung einer Brücke"),
    TREE_DOWN("18", "Umgestürzter Baum im Gleis"),
    ACCIDENT_AT_CROSSING("19", "Unfall an einem Bahnübergang"),
    ANIMALS_IN_CROSSING("20", "Tiere im Gleis"),
    WAITING_FOR_PASSENGERS("21", "Warten auf weitere Reisende"),
    WEATHER_CONDITIONS("22", "Witterungsbedingte Störung"),
    FIRE_DEPARTMENT("23", "Feuerwehreinsatz auf Bahngelände"),
    DELAY_FROM_ABROAD("24", "Verspätung aus dem Ausland"),
    WAITING_FOR_DELAYED_WAGONS("25", "Warten auf verspätete Zugteile"),
    HANGING_WAGONS("26", "Abhängen von Wagen"),
    OBJECTS_IN_TRACK("28", "Gegenstände im Gleis"),
    CONSTRUCTION_WORKS("31", "Bauarbeiten"),
    DELAY_AT_BOARDING("32", "Verzögerung beim Ein-/Ausstieg"),
    OVERHEAD_WIRE_PROBLEM("33", "Oberleitungsstörung"),
    SIGNAL_PROBLEM("34", "Signalstörung"),
    TRACK_BLOCKED("35", "Streckensperrung"),
    PROBLEM_IN_TRAIN("36", "Defekt am Zug"),
    PROBLEM_WAGON("37", "Defekt am Wagen"),
    PROBLEM_ON_TRACK("38", "Defekt an der Strecke"),
    ADDITIONAL_WAGONS("39", "Anhängen von zusätzlichen Wagen"),
    SIGNAL_BOX_PROBLEM("40", "Stellwerksstörung/-ausfall"),
    PROBLEM_AT_CROSSING("41", "Störung an einem Bahnübergang"),
    RESTRICTED_SPEED("42", "Außerplanmäßige Geschwindigkeitsbeschränkung"),
    DELAY_OF_PRECEDING_TRAIN("43", "Verspätung eines vorausfahrenden Zuges"),
    WAITING_FOR_OTHER_TRAIN("44", "Warten auf einen entgegenkommenden Zug"),
    OVERTAKING_BY_TRAIN("45", "Überholung durch anderen Zug"),
    WAITING_FOR_FREE_TRACK("46", "Warten auf freie Einfahrt"),
    DELAYED_PREPARATION("47", "Verspätete Bereitstellung"),
    DELAY_FROM_PREVIOUS_TRAIN("48", "Verspätung aus vorheriger Fahrt"),
    SHORT_TERM_STAFF_SHORTAGE_2("49", "Kurzfristiger Personalausfall"),
    SHORT_TERM_STAFF_ILLNESS("50", "Kurzfristige Erkrankung von Personal"),
    DELAY_FROM_PREVIOUS_TRAIN_PERSONAL("51", "Verspätetes Personal aus vorheriger Fahrt"),
    STRIKE("52", "Streik"),
    STORM_EFFECTS("53", "Unwetterauswirkungen"),
    AVAILABILITY_TRACKS_INTERRUPTED("54", "Verfügbarkeit der Gleise derzeit eingeschränkt"),
    TECHNICAL_MALFUNCTION_TRAIN("55", "Technische Störung an einem anderen Zug"),
    WAITING_FOR_PASSENGERS_FROM_BUS("56", "Warten auf Fahrgäste aus einem Bus"),
    ADDITIONAL_STOP("57", "Zusätzlicher Halt"),
    REROUTING("58", "Umleitung"),
    SNOW_ICE("59", "Schnee und Ice"),
    REDUCED_SPEED_STORM("60", "Reduzierte Geschwindigkeit wegen Sturm"),
    DOOR_PROBLEM("61", "Türstörung"),
    TECHNICAL_PROBLEM_FIXED("62", "Behobene technische Störung am Zug"),
    TECHNICAL_INSPECTION("63", "Technische Untersuchung am Zug"),
    SWITCH_PROBLEM("64", "Weichenstörung"),
    LANDSLIDE("65", "Erdrutsch"),
    FLOOD("66", "Hochwasser"),
    OFFICIAL_ACTION("67", "Behördliche Maßnahme"),
    DELAY_HIGH_PASSENGER_NUMBERS_ENTER_EXIT("68", "Hohes Fahrgastaufkommen verlängert Ein- und Ausstieg"),
    LOWER_SPEED("69", "Zug verkehrt mit verminderter Geschwindigeit"),
    NO_WLAN("70", "WLAN nicht verfügbar"),
    WLAN_IN_SINGLE_WAGONS("71", "WLAN in einzelnen Wagen nicht verfügbar"),
    NO_ENTERTAINMENT("72", "Info/Entertainment nicht verfügbar"),
    MULTIPURPOSE_COMPARTMENT_FRONT("73", "Mehrzweckabteil vorne"),
    MULTIPURPOSE_COMPARTMENT_BACK("74", "Mehrzweckabteil hinten"),
    FIRST_CLASS_FRONT("75", "1. Klasse vorne"),
    FIRST_CLASS_BACK("76", "1. Klasse hinten"),
    NO_FIRST_CLASS("77", "Ohne 1. Klasse"),
    REPLACEMENT_BUS_SERVICE("78", "Ersatzverkehr mit Bus ist eingerichtet"),
    NO_MULTIPURPOSE_COMPARTMENT("79", "Ohne Mehrzweckabteil"),
    DEVIATING_WAGON_ORDER("80", "Abweichende Wagenreihung"),
    TRAIN_CHANGED("81", "Fahrzeugtausch"),
    MULTIPLE_WAGONS_MISSING("82", "Mehrere Wagen fehlen"),
    MISSING_TRAIN_PART("83", "Fehlender Zugteil"),
    TRAIN_OPERATES_IN_CORRECT_ORDER("84", "Zug verkehrt richtig gereiht"),
    SINGLE_WAGON_MISSING("85", "Ein Wagen fehlt"),
    NO_RESERVATION_DISPLAY("86", "Keine Reservierungsanzeige"),
    SINGLE_WAGONS_WITHOUT_RESERVATION_DISPLAY("87", "Einzelne Wagen ohne Reservierungsanzeige"),
    NO_QUALITY_DEFECTS("88", "Keine Qualitätsmängel"),
    RESERVATIONS_ARE_BACK("89", "Reservierungen sind wieder vorhanden"),
    NO_ONBOARD_RESTAURANT("90", "Kein Bordrestaurant/Bordbistro"),
    RESTRICTED_BICYCLE_CARRYING("91", "Eingeschränkte Fahrradmitnahme"),
    AIR_CONDITIONING_IN_SINGLE_WAGONS("92", "Klimaanlage in einzelnen Wagen ausgefallen"),
    MISSING_OR_BROKEN_ACCESSIBLE_FACILITIES("93", "Fehlende oder gestörte behindertengerechte Einrichtung"),
    SUBSTITUTE_CATERING("94", "Ersatzbewirtschaftung"),
    NO_ACCESSIBLE_TOILET("95", "Ohne behindertengerechtes WC"),
    TRAIN_IS_HEAVILY_OVERCROWDED("96", "Der Zug ist stark überbesetzt"),
    TRAIN_IS_OVERCROWDED("97", "Der Zug ist überbesetzt"),
    OTHER_QUALITY_DEFECTS("98", "Sonstige Qualitätsmängel"),
    DELAYS_IN_OPERATIONS("99", "Verzögerungen im Betriebsablauf"),
    BUS_WAITING("900", "Anschlussbus wartet"),
    UNKNOWN("unknown", "Unbekannt");

    companion object {
        fun getByCode(code: String?): MessageType {
            if (code == null || code == "0") {
                return UNKNOWN
            }

            for (value in values()) {
                if (value.id == code) {
                    return value
                }
            }

            return UNKNOWN
        }
    }

}

enum class TrainType {
    // DB and cooperation high speed trains
    ICE, IC, EC, IR, D, ECE, RJ, RJX, TGV,
    // other EVUs high speed trains
    THA, FLX, HBX, WB,
    // night trains
    EN, NJ, DN, NEX,
    // regional trains
    IRE, MEX, RE, FEX, RB, S,
    // other EVUs
    AKN, ALX, BE, BEX, BOB, ENO, ERB, ERX, GABW, GABY, HZL, ME, MRB, NBE, NOB, NWB, OE, RT, RTB, SAB, SBB, SB, SE, SWE,
    WFB, TRI, BRB, RDC, STN, TL, RRB, HLB, VIA, TLX, erx, R, UEX,
    // non trains
    Bus
}