import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class GossipingBusDriverTest : FreeSpec({
    "findFirstFullyDistributeGossipStop" - {
        "can be fully distributed" {
            val schedule = """
                3 1 2 3
                3 2 3 1
                4 2 3 4 5
            """.trimIndent()

            findFirstFullyDistributeGossipStop(schedule) shouldBe "5"
        }

        "can not be fully distributed" {
            val schedule = """
                2 1 2
                5 2 8
            """.trimIndent()

            findFirstFullyDistributeGossipStop(schedule) shouldBe "never"
        }
    }

    "BusDriver#fromString" - {
        "if the string is empty, return empty list" {
            BusDriver.fromString("") shouldBe listOf()
        }

        "if the string contains only white spaces, return empty list" {
            val genWhiteSpace = Gen.from(listOf(" ", "\n", "\r"))
            Gen.list(genWhiteSpace).forAll { whitespacesList ->
                val input = whitespacesList.joinToString("")
                BusDriver.fromString(input) == listOf<BusDriver>()
            }
        }

        "result should be list of BusDriver with route defined in each line" {
            val schedule = """
                2 1 2
                5 2 8 6
            """.trimIndent()

            BusDriver.fromString(schedule) shouldBe listOf(
                BusDriver(listOf(2, 1, 2)),
                BusDriver(listOf(5, 2, 8, 6))
            )
        }

        "empty line should be ignored" {
            val schedule = """

                2 1 2

                5 2 8 6

            """.trimIndent()

            BusDriver.fromString(schedule) shouldBe listOf(
                BusDriver(listOf(2, 1, 2)),
                BusDriver(listOf(5, 2, 8, 6))
            )
        }
    }
})
