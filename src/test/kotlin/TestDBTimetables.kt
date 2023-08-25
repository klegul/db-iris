import dev.gregyyy.dbtimetables.DBTimetables
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class TestDBTimetables {

    @Test
    fun getTimetable() {
        val timetable = DBTimetables().getTimetable("8000191", ZonedDateTime.now().minusHours(1))
        println(timetable)
    }

}