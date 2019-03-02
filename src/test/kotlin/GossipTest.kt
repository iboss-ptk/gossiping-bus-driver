import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class GossipTest : FreeSpec({
    "#numStopsToCompletelySpread" - {
        "completely spread at 1 when first stop of everyone is the same" {
            val result = Gossip.numStopsToCompletelySpread(setOf(
                BusDriver(0, stopsOf("1", "4", "5"), setOf(Gossip(0))),
                BusDriver(1, stopsOf("1", "2"), setOf(Gossip(1))),
                BusDriver(2, stopsOf("1", "3", "5", "1"), setOf(Gossip(2)))
            ))

            result shouldBe 1
        }

        "completely spread at n when nth stop of everyone is the same" {
            val result = Gossip.numStopsToCompletelySpread(setOf(
                BusDriver(0, stopsOf("2", "3"), setOf(Gossip(0))),
                BusDriver(1, stopsOf("3"), setOf(Gossip(1))),
                BusDriver(2, stopsOf("6", "7", "8", "3"), setOf(Gossip(2)))
            ))

            result shouldBe 4
        }

        "never completely spread when the bus drivers never met" {
            val result = Gossip.numStopsToCompletelySpread(setOf(
                BusDriver(0, stopsOf("1", "2", "3"), setOf(Gossip(0))),
                BusDriver(1, stopsOf("4", "5"), setOf(Gossip(1))),
                BusDriver(2, stopsOf("6", "7", "8", "9"), setOf(Gossip(2)))
            ))

            result shouldBe null
        }
    }

    "#spread" - {
        "numbers of bus drivers after spread the gossips should be the same as before" {
            forAll(50, Gen.busDrivers()) { busDrivers ->
                val gossipedBusDriver = Gossip.spread(busDrivers)
                busDrivers.size == gossipedBusDriver.size
            }
        }

        "all bus drivers will know the same set of gossips" {
            forAll(50, Gen.busDrivers()) { busDrivers ->
                val gossipedBusDriver = Gossip.spread(busDrivers)
                val accumulatedGossips = busDrivers
                    .flatMap { it.gossips }
                    .toSet()

                gossipedBusDriver.all { it.gossips == accumulatedGossips }
            }
        }
    }
})