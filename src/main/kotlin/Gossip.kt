import arrow.core.*
import arrow.data.fix
import arrow.instances.list.traverse.traverse
import arrow.instances.option.applicative.applicative

data class Gossip(val id: Int) {
    companion object {
        fun spread(busDrivers: Set<BusDriver>): Set<BusDriver> {
            val accumulatedGossips = Gossip.allFrom(busDrivers)
            return busDrivers.map { it.listen(accumulatedGossips) }.toSet()
        }

        tailrec fun numStopsToCompletelySpread(busDrivers: Set<BusDriver>): Int? {
            val gossipedBusDrivers = busDrivers
                .groupBy { it.currentStop }
                .flatMap { Gossip.spread(it.value.toSet()) }
                .toSet()

            val totalGossip = Gossip.allFrom(gossipedBusDrivers)
            val isCompletelySpread = gossipedBusDrivers.all { it.gossips == totalGossip }

            return if (isCompletelySpread) {
                gossipedBusDrivers.first().numStop
            } else {
                val nextStateBusDrivers = gossipedBusDrivers
                    .map { it.move() }
                    .sequence()
                    ?.toSet()

                when (nextStateBusDrivers) {
                    null -> null
                    else -> numStopsToCompletelySpread(nextStateBusDrivers)
                }
            }
        }

        private fun allFrom(busDrivers: Set<BusDriver>): Set<Gossip> =
            busDrivers
                .flatMap { it.gossips }
                .toSet()
    }
}

fun <E> List<E?>.sequence(): List<E>? =
    this
        .traverse(Option.applicative()) { it.toOption() }
        .fix()
        .orNull()
        ?.fix()
