package edu.grinnell.csc207.blockchains;

import java.util.*;

/**
 * A full blockchain.
 *
 * @author Sal & Koast
 */
public class BlockChain implements Iterable<Transaction> {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * Points to the first block in the chain.
   */
  private Block front;
  /**
   * Points to the last block in the chain.
   */
  private Block rear;
  /**
   * Number of blocks in the chain.
   */
  private int size;
  /**
   * Validator for hashing.
   */
  private HashValidator validator;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a BlockChain that possesses a single block with an empty source, target, and amount. The
   * prevHash field is set to an empty byte array. Uses the provided HashValidator.
   *
   * @param check The HashValidator used to validate hashes.
   */
  public BlockChain(HashValidator check) {
    this.validator = check;
    Block genesis = new Block(0, new Transaction("", "", 0), new Hash(new byte[] {}), check);
    this.front = genesis;
    this.rear = genesis;
    this.size = 1;
  } // BlockChain(HashValidator)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Helper method to validate that a block can be added to the chain.
   *
   * @param blk The block to validate.
   * @throws IllegalArgumentException if the block is invalid.
   */
  private void validateBlock(Block blk) {
    // Check if the hash is valid
    if (!validator.isValid(blk.getHash())) {
      throw new IllegalArgumentException("Block hash is invalid.");
    } // if

    // Check if the previous hash is correct
    if (!blk.getPrevHash().equals(rear.getHash())) {
      throw new IllegalArgumentException("Block's previous hash is incorrect.");
    } // if
  } // validateBlock

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Mine for a new valid block for the end of the chain, returning that block.
   *
   * @param t The transaction that goes in the block.
   * @return a new block with correct number, hashes, and such.
   */
  public Block mine(Transaction t) {
    long nonce = 0;
    Block newBlock;
    do {
      newBlock = new Block(size, t, rear.getHash(), nonce++);
    } while (!validator.isValid(newBlock.getHash()));

    return newBlock;
  } // mine(t)

  /**
   * Get the number of blocks currently in the chain.
   *
   * @return the number of blocks in the chain, including the initial block.
   */
  public int getSize() {
    return size;
  } // getSize()

  /**
   * Add a block to the end of the chain.
   *
   * @param blk The block to add to the end of the chain.
   * @throws IllegalArgumentException if the block is invalid.
   */
  public void append(Block blk) {
    validateBlock(blk);
    rear.nextBlock = blk; // Link the current last block to the new block
    blk.prevBlock = rear; // Link the new block back to the current last block
    rear = blk; // Update the rear to the new block
    size++;
  } //append(blk)

  /**
   * Attempt to remove the last block from the chain.
   *
   * @return false if the chain has only one block (in which case it's not removed) or true
   *     otherwise (in which case the last block is removed).
   */
  public boolean removeLast() {
    if (size == 1) {
      return false;
    } // if

    Block current = front;
    while (current != null && current.getNum() != rear.getNum() - 1) {
      current = current.prevBlock;
    } // while
    rear = current;
    size--;
    return true;
  } // removeLast()

  /**
   * Get the hash of the last block in the chain.
   *
   * @return the hash of the last block in the chain.
   */
  public Hash getHash() {
    return rear.getHash();
  } // getHash()

  /**
   * Determine if the blockchain is correct and consistent.
   *
   * @return true if the blockchain is correct, false otherwise.
   */
  public boolean isCorrect() {
    try {
      check();
      return true;
    } catch (Exception e) {
      return false;
    } // try/catch
  } // isCorrect()

  /**
   * Check the blockchain for consistency and validity.
   *
   * @throws Exception if there are inconsistencies or invalid blocks.
   */
  public void check() throws Exception {
    Block current = front;
    while (current != null) {
      if (!validator.isValid(current.getHash())) {
        throw new Exception("Invalid block hash at block " + current.getNum());
      } // if
      if (current != front && !current.getPrevHash().equals(current.prevBlock.getHash())) {
        throw new Exception("Invalid previous hash at block " + current.getNum());
      } // if
      current = current.prevBlock;
    } // while
  } // check()

  /**
   * Return an iterator of all the people who participated in the system.
   *
   * @return an iterator of all the people in the system.
   */
  public Iterator<String> users() {
    Set<String> users = new HashSet<>();
    Block current = front;
    while (current != null) {
      if (!current.transactionF.getSource().isEmpty()) {
        users.add(current.transactionF.getSource());
      } // if
      users.add(current.transactionF.getTarget());
      current = current.prevBlock;
    } // while
    return users.iterator();
  } // users()

  /**
   * Find one person's balance.
   *
   * @param user The user whose balance we want to find.
   * @return that user's balance (or 0 if the user is not in the system).
   */
  public int balance(String user) {
    int balance = 0;
    Block current = front;
    while (current != null) {
      Transaction t = current.transactionF;
      if (user.equals(t.getSource())) {
        balance -= t.getAmount();
      } // if
      if (user.equals(t.getTarget())) {
        balance += t.getAmount();
      } // if
      current = current.prevBlock;
    } // while
    return balance;
  } // balance()

  /**
   * Printing Transactions
   */
  public void printTransactions() {
    Block current = front;
    while (current != null) {
      System.out.println(
          current.transactionF); // Assuming transactionF is the field holding the transaction data
      current = current.nextBlock; // Move to the next block
    } // while
  } // printTransactions()

  /**
   * Get an iterator for all the blocks in the chain.
   *
   * @return an iterator for all the blocks in the chain.
   */
  public Iterator<Block> blocks() {
    return new Iterator<Block>() {
      private Block current = front;

      public boolean hasNext() {
        return current != null;
      } // hasNext()

      public Block next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        } // if
        Block temp = current;
        current = current.nextBlock; // Move to the next block
        return temp;
      } // next()
    };
  } //blocks()

  /**
   * Get an iterator for all the transactions in the chain.
   *
   * @return an iterator for all the transactions in the chain.
   */
  public Iterator<Transaction> iterator() {
    return new Iterator<Transaction>() {
      private Block current = front;

      public boolean hasNext() {
        return current != null;
      } // hasNext()

      public Transaction next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        } // if
        Transaction temp = current.transactionF;
        current = current.nextBlock;
        return temp;
      } // next()
    };
  } // iterator()
} // class BlockChain
