package edu.grinnell.csc207.blockchains;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

/**
 * Blocks to be stored in blockchains.
 */
public class Block {
  // Fields
  int number;           // Block number
  Transaction transactionF; // The transaction for the block
  Hash curHash;         // Current hash of the block
  Hash previousHash;    // Hash of the previous block
  HashValidator checking;  // Hash validator for mining
  long nonceF;          // Nonce for mining
  Block prevBlock;      // Previous block in the chain
  Block nextBlock;      // Next block in the chain

  // Constructor with HashValidator (for mining)
  public Block(int num, Transaction transaction, Hash prevHash, HashValidator check) {
    this.number = num;
    this.transactionF = transaction;
    this.previousHash = prevHash;
    this.nonceF = 0;
    mine(check);  // Mine the nonce
    computeHash(); // Compute the hash after mining
  }

  // Constructor with provided nonce
  public Block(int num, Transaction transaction, Hash prevHash, long nonce) {
    this.number = num;
    this.transactionF = transaction;
    this.previousHash = prevHash;
    this.nonceF = nonce;
    computeHash();  // Compute the hash
  }

  // Helper method to mine the block's nonce using the HashValidator
  private void mine(HashValidator check) {
    while (!check.isValid(calculateHash(this))) {
      this.nonceF++;  // Increment nonce until a valid hash is found
    }
  }

  // Compute the block's hash
  public void computeHash() {
    this.curHash = calculateHash(this);  // Recompute the hash
  }

  // Calculate the hash of the block
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
      return new Hash(hash);  // Return the computed hash as a new Hash object
    } catch (Exception e) {
      // Handle the exception (optional logging)
    }
    return null;
  }

  // Get the next block in the chain
  public Block getNextBlock() {
    return this.nextBlock;
  }

  // Get the previous block in the chain
  public Block getPrevBlock() {
    return this.prevBlock;
  }

  // Set the previous block
  public void setPrevBlock(Block prevBlk) {
    if (this.prevBlock != null && prevBlk != null) {
      this.prevBlock.nextBlock = null;
    }
    this.prevBlock = prevBlk;
    if (this.prevBlock != null) {
      prevBlk.nextBlock = this;
    }
  }

  // Set the next block
  public void setNextBlock(Block nextBlk) {
    if (this.nextBlock != null && nextBlk != null) {
      this.nextBlock.prevBlock = null;
    }
    this.nextBlock = nextBlk;
    if (this.nextBlock != null) {
      nextBlk.prevBlock = this;
    }
  }

  // Get the block's number
  public int getNum() {
    return this.number;
  }

  // Get the block's transaction
  public Transaction getTransaction() {
    return this.transactionF;
  }

  // Get the block's nonce
  public long getNonce() {
    return this.nonceF;
  }

  // Get the hash of the previous block
  public Hash getPrevHash() {
    return this.previousHash;
  }

  // Get the current hash of the block
  public Hash getHash() {
    return this.curHash;
  }

  // Get a string representation of the block
  public String toString() {
    StringBuilder output = new StringBuilder();
    output.append("Block " + this.number + " (Transaction: [");
    if (this.transactionF.getSource().equals("")) {
      output.append("Deposit,");
    } else {
      output.append("Source: " + this.transactionF.getSource());
    }
    output.append(", Target: " + this.transactionF.getTarget()
            + ", Amount: " + this.transactionF.getAmount()
            + "], Nonce: " + this.nonceF
            + ", prevHash: " + this.previousHash
            + ", hash: " + this.curHash + ")");
    return output.toString();
  }
}
