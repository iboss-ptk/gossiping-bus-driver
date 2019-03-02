import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class BusDriverTest : FreeSpec({
    ".dailyRoute" - {
        "it cycles back to the start again when it ends" {
            val route = stopsOf("1", "2")
            BusDriver(0, route)
                .dailyRoute
                .take(5)
                .toList() shouldBe stopsOf("1", "2", "1", "2", "1")
        }

        "it has total 481 stops" {
            forAll(30, Gen.busDriver()) { busDriver ->
                busDriver.dailyRoute.count() == 481
            }
        }
    }

    ".listen" - {
        val genGossipsAndItsSubset = Gen.pair(Gen.gossips(), Gen.gossips())
            .flatMap { (gossipsA, gossipsB) ->
                val subsetA = gossipsA.intersect(gossipsB)
                Gen.pair(Gen.constant(gossipsA), Gen.constant(subsetA))
            }

        "listen to no gossip should not change anything" {
            val busDriver = BusDriver(0, stopsOf("1", "2"))
            busDriver.listen(setOf()).gossips shouldBe busDriver.gossips
        }

        "listen to gossips that the driver has already know should not change anything" {
            forAll(Gen.route(), genGossipsAndItsSubset) { route, (gossips, gossipSubset) ->
                val busDriver = BusDriver(0, route, gossips)
                busDriver.listen(gossipSubset).gossips == gossips
            }
        }

        "listen to new gossips should make driver know both new and old gossip" {
            forAll(Gen.gossips(), Gen.gossips(), Gen.route()) { gossipsA, gossipsB, route ->
                val busDriver = BusDriver(0, route, gossipsA)
                val gossipResult = busDriver.listen(gossipsB).gossips

                gossipResult == gossipsA + gossipsB
            }
        }
    }

    "#fromString" - {
        "if the string is empty, return empty list" {
            BusDriver.fromString("") shouldBe listOf()
        }

        "if the string contains only white spaces, return empty list" {
            val genWhiteSpaces = Gen
                .list(Gen.from(listOf(" ", "\n", "\r")))
                .map { it.joinToString("") }

            genWhiteSpaces.forAll { whiteSpaces ->
                BusDriver.fromString(whiteSpaces) == listOf<BusDriver>()
            }
        }

        "result should be list of BusDriver with route defined in each line" {
            val schedule = """
                        2 1 2
                        5 2 8 6
                    """.trimIndent()

            BusDriver.fromString(schedule) shouldBe listOf(
                BusDriver(0, stopsOf("2", "1", "2")),
                BusDriver(1, stopsOf("5", "2", "8", "6"))
            )
        }

        "empty line should be ignored" {
            val schedule = """

                        2 1 2

                        5 2 8 6

                        6 1 4 6 5
                    """.trimIndent()

            BusDriver.fromString(schedule) shouldBe listOf(
                BusDriver(0, stopsOf("2", "1", "2")),
                BusDriver(1, stopsOf("5", "2", "8", "6")),
                BusDriver(2, stopsOf("6", "1", "4", "6", "5"))
            )
        }
    }
})