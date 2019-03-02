import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class BusDriverTest : FreeSpec({
    ".move" - {
        "it cycles back to the start again when it ends" {
            val route = stopsOf("1", "2")
            BusDriver(0, route)
                .move()
                ?.move()
                ?.move()
                ?.move()
                ?.currentStop shouldBe Stop("1")
        }

        "can move only when num stops less than 481" {
            forAll(30, Gen.choose(1, 480), Gen.busDriver()) { numStop, busDriver ->
                busDriver.copy(numStop = numStop).move() != null
            }
        }

        "can not move when num stops greater than or equal 481" {
            val genNumStops = Gen.nats().filter { it >= 481 }
            forAll(30, genNumStops, Gen.busDriver()) { numStop, busDriver ->
                busDriver.copy(numStop = numStop).move() == null
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