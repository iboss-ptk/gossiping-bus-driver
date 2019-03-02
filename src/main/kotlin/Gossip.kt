data class Gossip(val id: Int) {
    companion object {
        fun spread(busDrivers: Set<BusDriver>): Set<BusDriver> {
            val accumulatedGossips = busDrivers
                .flatMap { it.gossips }
                .toSet()

            return busDrivers.map { it.listen(accumulatedGossips) }.toSet()
        }
    }
}