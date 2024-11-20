package edu.grinnell.csc207.blockchains;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;

/**
 * Blocks to be stored in blockchains.
 *
 * @author Your Name Here
 * @author Samuel A. Rebelsky
 */
public class Block {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+
  /** number of the blocks */
  int number;

  /** Transaction */
  Transaction transactionF;

  /** current hash */
  Hash curHash;

  /** Previous hash */
  Hash previousHash;

  /** Validation of the block */
  HashValidator checking;

  /** nonce of the block */
  long nonceF;

  /** previous block */
  Block prevBlock;

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

  /** Compute the hash of the block given all the other info already stored in the block. */
  public void computeHash() {
    this.curHash = calculateHash();
  } // computeHash()

  public Hash calculateHash() {
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    byteStream.writeBytes(this.getBytes());
    if(this.prevBlock != null){
      byteStream.writeBytes(this.prevBlock.getBytes());
    }
    try {
      MessageDigest md = MessageDigest.getInstance("sha-256");
      md.update(byteStream.toByteArray());
      byte[] hash = md.digest();
      return new Hash(hash);
    } catch (Exception e) {
      //do nothing
    } // try/catch
    return null;
  }

  public byte[] getBytes(){
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    byteStream.write((byte) this.number);
    byteStream.writeBytes(this.transactionF.getBytes());
    byteStream.writeBytes(this.previousHash.getBytes());
    byteStream.write((byte) this.nonceF);
    return byteStream.toByteArray();
  } 
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
    return ""; // STUB
  } // toString()
} // class Block
