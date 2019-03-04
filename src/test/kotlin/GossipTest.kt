import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class GossipTest : FreeSpec({
    "#allFrom" - {
        "should return every gossips contained in all drivers" {
            val busDrivers = listOf(
                BusDriver(0, stopsOf("1", "2", "3"), setOf(Gossip(0), Gossip(2))),
                BusDriver(1, stopsOf("4", "5"), setOf(Gossip(1))),
                BusDriver(2, stopsOf("6", "7", "8", "9"), setOf(Gossip(2), Gossip(3)))
            )

            Gossip.allFrom(busDrivers) shouldBe setOf(Gossip(0), Gossip(1), Gossip(2), Gossip(3))
        }
    }
})