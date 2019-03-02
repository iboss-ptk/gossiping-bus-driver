import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class MainTest : FreeSpec({
    "numStopsToCompletelySpread" - {
        "can be fully distributed" {
            val schedule = """
                3 1 2 3
                3 2 3 1
                4 2 3 4 5
            """.trimIndent()

            entryPoint(schedule) shouldBe "5"
        }

        "can not be fully distributed" {
            val schedule = """
                2 1 2
                5 2 8
            """.trimIndent()

            entryPoint(schedule) shouldBe "never"
        }
    }
})

