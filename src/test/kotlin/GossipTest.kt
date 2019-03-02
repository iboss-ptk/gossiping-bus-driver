import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.specs.FreeSpec

class GossipTest : FreeSpec({
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