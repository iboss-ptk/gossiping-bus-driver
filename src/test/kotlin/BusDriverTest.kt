import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class BusDriverTest : FreeSpec({
    ".move" - {
        "it cycles back to the start again when it ends" {
            forAll(50, Gen.route(), Gen.choose(0, BusDriver.DAILY_DRIVE_TIME_LIMIT)) { route, time ->
                var busDriver = BusDriver(0, route)
                repeat(time) {
                    busDriver = busDriver.move()
                }
                busDriver.currentStop == route[time % route.size]
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

    "#spreadGossip" - {
        "numbers of bus drivers after spread the gossips should be the same as before" {
            forAll(50, Gen.busDrivers()) { busDrivers ->
                val gossipedBusDriver = BusDriver.spreadGossip(busDrivers)
                busDrivers.size == gossipedBusDriver.size
            }
        }

        "all bus drivers will know the same set of gossips" {
            forAll(50, Gen.busDrivers()) { busDrivers ->
                val gossipedBusDriver = BusDriver.spreadGossip(busDrivers)
                val accumulatedGossips = busDrivers
                    .flatMap { it.gossips }
                    .toSet()

                gossipedBusDriver.all { it.gossips == accumulatedGossips }
            }
        }
    }

    "#timeToCompletelySpreadGossip" - {
        "completely spread at 1 when first stop of everyone is the same" {
            val result = BusDriver.timeToCompletelySpreadGossip(
                listOf(
                    BusDriver(0, stopsOf("1", "4", "5"), setOf(Gossip(0))),
                    BusDriver(1, stopsOf("1", "2"), setOf(Gossip(1))),
                    BusDriver(2, stopsOf("1", "3", "5", "1"), setOf(Gossip(2)))
                )
            )

            result shouldBe 0
        }

        "completely spread at n when nth stop of everyone is the same" {
            val result = BusDriver.timeToCompletelySpreadGossip(
                listOf(
                    BusDriver(0, stopsOf("2", "3"), setOf(Gossip(0))),
                    BusDriver(1, stopsOf("3"), setOf(Gossip(1))),
                    BusDriver(2, stopsOf("6", "7", "8", "3"), setOf(Gossip(2)))
                )
            )

            result shouldBe 3
        }

        "completely spread at minute 480 when nth stop of everyone is the same" {
            // minute 480 is at stop 481
            // prime factors of 481 are 13 x 37
            val routes = listOf(
                stopsOf((1..12).map { "$1-$it" }).plus(Stop("meet")),
                stopsOf((1..36).map { "$2-$it" }).plus(Stop("meet"))
            )

            val result = BusDriver.timeToCompletelySpreadGossip(
                routes.mapIndexed { id, route ->
                    BusDriver(id, route, setOf(Gossip(id)))
                }
            )

            result shouldBe 480
        }

        "never completely spread when the bus drivers can meet only at 481 minutes" {
            // minute 481 is at stop 482
            // prime factors of 482 are 2 x 241
            val routes = listOf(
                stopsOf((1..1).map { "$1-$it" }).plus(Stop("meet")),
                stopsOf((1..240).map { "$2-$it" }).plus(Stop("meet"))
            )

            val result = BusDriver.timeToCompletelySpreadGossip(
                routes.mapIndexed { id, route ->
                    BusDriver(id, route, setOf(Gossip(id)))
                }
            )

            result shouldBe null
        }

        "never completely spread when the bus drivers never met" {
            val result = BusDriver.timeToCompletelySpreadGossip(
                listOf(
                    BusDriver(0, stopsOf("1", "2", "3"), setOf(Gossip(0))),
                    BusDriver(1, stopsOf("4", "5"), setOf(Gossip(1))),
                    BusDriver(2, stopsOf("6", "7", "8", "9"), setOf(Gossip(2)))
                )
            )

            result shouldBe null
        }
    }
})