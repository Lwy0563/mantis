package io.iohk.ethereum.metrics

object Metrics {
  /**
   * Signifies that Mantis has started.
   */
  final val StartEvent = "start.event"

  /**
   * Signifies that Mantis has stopped.
   */
  final val StopEvent = "stop.event"

  /**
   * Measures the block number of the last imported block.
   */
  final val LedgerImportBlockNumber = "ledger.import.block.number"

  /**
   * Signifies which one of the nodes is the Raft leader.
   * This assumes `consensus = atomix-raft`.
   */
  final val RaftLeaderIndex = "raft.leader.index"

  /**
   * Signifies a leadership change. This is emitted from the new leader.
   */
  final val RaftLeaderEvent = "raft.leader.event"

  /**
   * How many times this node became a leader
   */
  final val RaftLeadershipsNumber = "raft.leaderships.number"

  /**
   * How many blocks forged by the leader.
   */
  final val RaftLeaderForgedBlocksNumber = "raft.leader.forged.blocks.number"

  /**
   * The rate at which a leader forges blocks.
   */
  final val RaftLeaderForgedBlocksCounter = "raft.leader.forged.blocks.counter"

  final val RaftLeaderLastForgedBlockTime = "raft.leader.last.forged.block.time"
}
