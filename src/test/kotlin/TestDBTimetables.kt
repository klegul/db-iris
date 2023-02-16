import dev.gregyyy.dbtimetables.DBTimetables
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class TestDBTimetables {

    @Test
    fun getTimetable() {
        val timetable = DBTimetables("", "").getTimetable("8000191", LocalDateTime.now())
        println(timetable)
    }

}