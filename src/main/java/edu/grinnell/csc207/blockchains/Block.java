package edu.grinnell.csc207.blockchains;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

/** Blocks to be stored in blockchains. */
public class Block {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+
  /** Block number. */
  int number;

  /** The transaction for the block. */
  Transaction transactionF;

  /** Current hash of the block. */
  Hash curHash;

  /** Hash of the previous block. */
  Hash previousHash;

  /** Hash validator for mining. */
  HashValidator checking;

  /** Nonce for mining. */
  long nonceF;

  /** Previous block in the chain. */
  Block prevBlock;

  /** Next block in the chain. */
  Block nextBlock;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new block from the specified block number, transaction, and previous hash, mining to
   * choose a nonce that meets the requirements of the validator.
   *
   * @param num The number of the block.
   * @param transaction The transaction for the block.
   * @param prevHash The hash of the previous block.
   * @param check The validator used to check the block.
   */
  public Block(int num, Transaction transaction, Hash prevHash, HashValidator check) {
    this.number = num;
    this.transactionF = transaction;
    this.previousHash = prevHash;
    this.nonceF = 0;
    mine(check); // Mine the nonce
    computeHash(); // Compute the hash after mining
  } // Block(int, Transaction, Hash, HashValidator)

  /**
   * Create a new block, computing the hash for the block.
   *
   * @param num The number of the block.
   * @param transaction The transaction for the block.
   * @param prevHash The hash of the previous block.
   * @param nonce The nonce of the block.
   */
  public Block(int num, Transaction transaction, Hash prevHash, long nonce) {
    this.number = num;
    this.transactionF = transaction;
    this.previousHash = prevHash;
    this.nonceF = nonce;
    computeHash(); // Compute the hash
  } // Block(int, Transaction, Hash, long)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Helper method to mine the block's nonce using the HashValidator.
   *
   * @param check HashValidator
   */
  private void mine(HashValidator check) {
    while (!check.isValid(calculateHash(this))) {
      this.nonceF++; // Increment nonce until a valid hash is found
    } // while
  } // mine(HashValidator)

  /** Compute the hash of the block given all the other info already stored in the block. */
  public void computeHash() {
    this.curHash = calculateHash(this); // Recompute the hash
  } // computeHash()

  /**
   * Compute the hash of the block given all the other info already stored in the block.
   *
   * @param block this block
   * @return Hash
   */
  public Hash calculateHash(Block block) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      md.update(ByteBuffer.allocate(4).putInt(block.getNum()).array());
      md.update(block.getTransaction().getSource().getBytes());
      md.update(block.getTransaction().getTarget().getBytes());
      md.update(ByteBuffer.allocate(4).putInt(block.getTransaction().getAmount()).array());
      md.update(block.getPrevHash().getBytes());
      md.update(ByteBuffer.allocate(Long.BYTES).putLong(block.getNonce()).array());
      byte[] hash = md.digest();
      return new Hash(hash); // Return the computed hash as a new Hash object
    } catch (Exception e) {
      // Handle the exception (optional logging)
    } // try/catch
    return null;
  } // calculateHash(Block)

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+
  /**
   * Get the next block in the chain.
   *
   * @return next block in the chain
   */
  public Block getNextBlock() {
    return this.nextBlock;
  } // getNextBlock()

  /**
   * Get the previous block in the chain.
   *
   * @return previous block
   */
  public Block getPrevBlock() {
    return this.prevBlock;
  } // getPrevBlock()

  /**
   * Setting previous block.
   *
   * @param prevBlk previous Block
   */
  public void setPrevBlock(Block prevBlk) {
    if (this.prevBlock != null && prevBlk != null) {
      this.prevBlock.nextBlock = null;
    } // if
    this.prevBlock = prevBlk;
    if (this.prevBlock != null) {
      prevBlk.nextBlock = this;
    } // if
  } // setPrevBlock(Block)

  /**
   * Setting next block.
   *
   * @param nextBlk next block
   */
  public void setNextBlock(Block nextBlk) {
    if (this.nextBlock != null && nextBlk != null) {
      this.nextBlock.prevBlock = null;
    } // if
    this.nextBlock = nextBlk;
    if (this.nextBlock != null) {
      nextBlk.prevBlock = this;
    } // if
  } // setNextBlock(Block)

  /**
   * Get the number of the block.
   *
   * @return the number of the block.
   */
  public int getNum() {
    return this.number;
  } // getNum()

  /**
   * Get the transaction stored in this block.
   *
   * @return the transaction.
   */
  public Transaction getTransaction() {
    return this.transactionF;
  } // getTransaction()

  /**
   * Get the nonce of this block.
   *
   * @return the nonce.
   */
  public long getNonce() {
    return this.nonceF;
  } // getNonce()

  /**
   * Get the hash of the previous block.
   *
   * @return the hash of the previous block.
   */
  public Hash getPrevHash() {
    return this.previousHash;
  } // getPrevHash

  /**
   * Get the hash of the current block.
   *
   * @return the hash of the current block.
   */
  public Hash getHash() {
    return this.curHash;
  } // getHash()

  /**
   * Get a string representation of the block.
   *
   * @return a string representation of the block.
   */
  public String toString() {
    StringBuilder output = new StringBuilder();
    output.append("Block " + this.number + " (Transaction: [");
    if (this.transactionF.getSource().equals("")) {
      output.append("Deposit,");
    } else {
      output.append("Source: " + this.transactionF.getSource());
    } // if/else
    output.append(
        ", Target: "
            + this.transactionF.getTarget()
            + ", Amount: "
            + this.transactionF.getAmount()
            + "], Nonce: "
            + this.nonceF
            + ", prevHash: "
            + this.previousHash
            + ", hash: "
            + this.curHash
            + ")");
    return output.toString();
  } // toString()
} // Block
