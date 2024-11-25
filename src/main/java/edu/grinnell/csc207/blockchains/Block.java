package edu.grinnell.csc207.blockchains;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.*;

/**
 * Blocks to be stored in blockchains.
 *
 * @author Sal & Koast
 * @author Samuel A. Rebelsky
 */
public class Block {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+
  /** number of the blocks. */
  int number;

  /** Transaction. */
  Transaction transactionF;

  /** current hash. */
  Hash curHash;

  /** Previous hash. */
  Hash previousHash;

  /** Validation of the block. */
  HashValidator checking;

  /** nonce of the block */
  long nonceF;

  /** previous block. */
  Block prevBlock;

  /** next blokc. */
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
    mine(check);
    computeHash();
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
    computeHash();
  } // Block(int, Transaction, Hash, long)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+
  /**
   * Mines the nonce
   *
   * @param check HashValidator
   * @return long
   */
  private void mine(HashValidator check) {
    while (!check.isValid(calculateHash(this))) {
      this.nonceF++;
    } // while
  } // mine(HashValidator)

  /** Compute the hash of the block given all the other info already stored in the block. */
  public void computeHash() {
    this.curHash = calculateHash(this);
  } // computeHash()

  /**
   * Helping method calculating the hash of the giving block.
   *
   * @return a Hash
   */
  public Hash calculateHash(Block block) {
    try {
      MessageDigest md = MessageDigest.getInstance("sha-256");
      md.update(ByteBuffer.allocate(4).putInt(block.getNum()).array());
      md.update(block.getTransaction().getSource().getBytes());
      md.update(block.getTransaction().getTarget().getBytes());
      md.update(ByteBuffer.allocate(4).putInt(block.getTransaction().getAmount()).array());
      md.update(block.getPrevHash().getBytes());
      md.update(ByteBuffer.allocate(Long.BYTES).putLong(block.getNonce()).array());
      byte[] hash = md.digest();
      return new Hash(hash);
    } catch (Exception e) {
      // do nothing
    } // try/catch
    return null;
  } // calculatehash()

  /**
   * Gets the next blookc in the chain.
   *
   * @return next Block
   */
  public Block getNextBlock() {
    return this.nextBlock;
  } // getNextBlock()

  /**
   * Gets the previous block in the chain.
   *
   * @return previous Block
   */
  public Block getPrevBlock() {
    return this.prevBlock;
  } // getPrevBlock()

  /**
   * Setting Previous block.
   *
   * @param prevBlk previous Block
   */
  public void setPrevBlock(Block prevBlk) {
    if (this.prevBlock != null) {
      prevBlk.nextBlock = null;
    } // if
    this.prevBlock = prevBlk;
    if (this.prevBlock != null) {
      prevBlk.nextBlock = this;
    } // if
  } // setPreviousBlock(Block)

  /**
   * Setting Next block.
   *
   * @param nextBlock next Block
   */
  public void setNextBlock(Block nextBlk) {
    if (this.nextBlock != null) {
      nextBlk.prevBlock = null;
    } // if
    this.nextBlock = nextBlk;
    if (this.nextBlock != null) {
      nextBlk.prevBlock = this;
    } // if
  } // setNextBlock(Block)

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

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
  Hash getPrevHash() {
    return this.previousHash;
  } // getPrevHash

  /**
   * Get the hash of the current block.
   *
   * @return the hash of the current block.
   */
  Hash getHash() {
    return this.curHash;
  } // getHash

  /**
   * Get a string representation of the block.
   *
   * @return a string representation of the block.
   */
  public String toString() {
    StringBuffer output = new StringBuffer();
    output.append("Block " + this.number + " (Transaction: [");
    if (prevBlock.transactionF.getSource().equals("")) {
      output.append("Deposit,");
    } else {
      output.append("Source: " + this.transactionF.getSource());
    }
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
} // class Block
