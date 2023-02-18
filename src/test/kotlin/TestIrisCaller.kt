import dev.gregyyy.dbtimetables.DBTimetables
import dev.gregyyy.dbtimetables.IrisCaller
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class TestIrisCaller {

    @Test
    fun testGetCurrentTimetable() {
        val timetable = IrisCaller(DBTimetables.BASE_URL_UNOFFICIAL).getCurrentTimetable("8000191", LocalDateTime.now())
        println(timetable)
    }

    @Test
    fun testGetFullChangesTimetable() {
        val timetable = IrisCaller(DBTimetables.BASE_URL_UNOFFICIAL).getFullChangesTimetable("8000191")
        println(timetable)
    }

}