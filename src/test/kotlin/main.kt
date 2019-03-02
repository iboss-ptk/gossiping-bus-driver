import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class GossipingBusDriverTest : FreeSpec({
    "findFirstFullySpreadGossipStop" - {
        "can be fully distributed" {
            val schedule = """
                3 1 2 3
                3 2 3 1
                4 2 3 4 5
            """.trimIndent()

            findFirstFullySpreadGossipStop(schedule) shouldBe "5"
        }

        "can not be fully distributed" {
            val schedule = """
                2 1 2
                5 2 8
            """.trimIndent()

            findFirstFullySpreadGossipStop(schedule) shouldBe "never"
        }
    }
})

