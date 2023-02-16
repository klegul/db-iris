import dev.gregyyy.dbtimetables.IrisCaller
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class TestIrisCaller {

    @Test
    fun testGetCurrentTimetable() {
        val timetable = IrisCaller.getCurrentTimetable("8000191", LocalDateTime.now())
        println(timetable)
    }

    @Test
    fun testGetFullChangesTimetable() {
        val timetable = IrisCaller.getFullChangesTimetable("8000191")
        println(timetable)
    }

}