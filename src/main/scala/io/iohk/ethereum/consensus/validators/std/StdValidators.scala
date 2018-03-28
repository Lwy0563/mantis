package io.iohk.ethereum.consensus.validators.std

import akka.util.ByteString
import io.iohk.ethereum.consensus.{GetBlockHeaderByHash, GetNBlocksBack}
import io.iohk.ethereum.consensus.validators._
import io.iohk.ethereum.domain.{Block, Receipt}
import io.iohk.ethereum.ledger.BlockExecutionError.{ValidationAfterExecError, ValidationBeforeExecError}
import io.iohk.ethereum.ledger.{BlockExecutionError, BlockExecutionSuccess}
import io.iohk.ethereum.utils.BlockchainConfig
import org.spongycastle.util.encoders.Hex

class StdValidators(
  val blockValidator: BlockValidator,
  val blockHeaderValidator: BlockHeaderValidator,
  val signedTransactionValidator: SignedTransactionValidator
) extends Validators {

  def validateBlockBeforeExecution(
    block: Block,
    getBlockHeaderByHash: GetBlockHeaderByHash,
    getNBlocksBack: GetNBlocksBack
  ): Either[ValidationBeforeExecError, BlockExecutionSuccess] = {

    StdValidators.validateBlockBeforeExecution(
      self = this,
      block = block,
      getBlockHeaderByHash = getBlockHeaderByHash,
      getNBlocksBack = getNBlocksBack
    )
  }

  def validateBlockAfterExecution(
    block: Block,
    stateRootHash: ByteString,
    receipts: Seq[Receipt],
    gasUsed: BigInt
  ): Either[BlockExecutionError, BlockExecutionSuccess] = {

    StdValidators.validateBlockAfterExecution(
      self = this,
      block = block,
      stateRootHash = stateRootHash,
      receipts = receipts,
      gasUsed = gasUsed
    )
  }
}

object StdValidators {
  def apply(blockchainConfig: BlockchainConfig): StdValidators =
    new StdValidators(
      StdBlockValidator,
      new StdBlockHeaderValidator(blockchainConfig),
      new StdSignedTransactionValidator(blockchainConfig)
    )

  def validateBlockBeforeExecution(
    self: Validators,
    block: Block,
    getBlockHeaderByHash: GetBlockHeaderByHash,
    getNBlocksBack: GetNBlocksBack
  ): Either[ValidationBeforeExecError, BlockExecutionSuccess] = {

    val header = block.header
    val body = block.body

    val result = for {
      _ <- self.blockHeaderValidator.validate(header, getBlockHeaderByHash)
      _ <- self.blockValidator.validateHeaderAndBody(header, body)
    } yield BlockExecutionSuccess

    result.left.map(ValidationBeforeExecError)
  }

  def validateBlockAfterExecution(
    self: Validators,
    block: Block,
    stateRootHash: ByteString,
    receipts: Seq[Receipt],
    gasUsed: BigInt
  ): Either[BlockExecutionError, BlockExecutionSuccess] = {

    val header = block.header
    val blockAndReceiptsValidation = self.blockValidator.validateBlockAndReceipts(header, receipts)

    if(header.gasUsed != gasUsed)
      Left(ValidationAfterExecError(s"Block has invalid gas used, expected ${header.gasUsed} but got $gasUsed"))
    else if(header.stateRoot != stateRootHash)
      Left(ValidationAfterExecError(
        s"Block has invalid state root hash, expected ${Hex.toHexString(header.stateRoot.toArray)} but got ${Hex.toHexString(stateRootHash.toArray)}")
      )
    else if(blockAndReceiptsValidation.isLeft)
      Left(ValidationAfterExecError(blockAndReceiptsValidation.left.get.toString))
    else
      Right(BlockExecutionSuccess)
  }
}
