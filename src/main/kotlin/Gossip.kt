data class Gossip(val id: Int) {
    companion object {
        fun allFrom(busDrivers: List<BusDriver>): Set<Gossip> =
            busDrivers
                .flatMap { it.gossips }
                .toSet()
    }
}
