import arrow.syntax.collections.tail

data class BusDriver(
    val id: Int,
    val route: List<Stop>,
    val gossips: Set<Gossip> = setOf(Gossip(id))
) {
    val currentStop: Stop = route.first()

    fun move() =
        copy(route = route.tail() + route.first())

    fun listen(newGossip: Set<Gossip>): BusDriver =
        this.copy(gossips = gossips.union(newGossip))

    companion object {
        const val DAILY_DRIVE_TIME_LIMIT = 480

        fun fromString(string: String): List<BusDriver> =
            string
                .lines()
                .filter { it.isNotBlank() }
                .mapIndexed { id, line -> BusDriver(id, stopsOf(line.split(" "))) }

        fun spreadGossip(busDrivers: List<BusDriver>): List<BusDriver> {
            val accumulatedGossips = Gossip.allFrom(busDrivers)
            return busDrivers.map { it.listen(accumulatedGossips) }
        }

        tailrec fun timeToCompletelySpreadGossip(busDrivers: List<BusDriver>, time: Int = 0): Int? =
            if (time > DAILY_DRIVE_TIME_LIMIT) {
                null
            } else {
                val gossipedBusDrivers = busDrivers
                    .groupBy { it.currentStop }
                    .flatMap { BusDriver.spreadGossip(it.value) }

                val totalGossip = Gossip.allFrom(gossipedBusDrivers)
                val isCompletelySpread = gossipedBusDrivers.all { it.gossips == totalGossip }

                when {
                    isCompletelySpread -> time
                    else -> timeToCompletelySpreadGossip(
                        busDrivers = gossipedBusDrivers.map { it.move() },
                        time = time + 1
                    )
                }
            }
    }
}