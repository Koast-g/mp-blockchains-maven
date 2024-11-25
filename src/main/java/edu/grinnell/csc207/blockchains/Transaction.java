package edu.grinnell.csc207.blockchains;

/**
 * A simple transaction.
 */
public class Transaction {
  private String source;  // Source of the transfer
  private String target;  // Target of the transfer
  private int amount;     // Amount transferred

  // Constructor for creating a new transaction (deposit or transfer)
  public Transaction(String src, String tgt, int amt) {
    this.source = src;
    this.target = tgt;
    this.amount = amt;
  }

  // Get the source of the transaction
  public String getSource() {
    return this.source;
  }

  // Get the target of the transaction
  public String getTarget() {
    return this.target;
  }

  // Get the amount of the transaction
  public int getAmount() {
    return this.amount;
  }

  // Convert to string form
  public String toString() {
    return String.format("[%s, Target: %s, Amount: %s]",
        ("".equals(this.source)) ? "Deposit" : "Source: " + this.source,
        this.target,
        this.amount);
  }

  // Get a hash code
  public int hashCode() {
    return this.toString().hashCode();
  }

  // Check if this transaction is equal to another transaction
  public boolean equals(Object other) {
    return (other instanceof Transaction)
        && this.equals((Transaction) other);
  }

  // Check if this transaction is equal to another Transaction
  public boolean equals(Transaction other) {
    return other.source.equals(this.source)
        && other.target.equals(this.target)
        && other.amount == this.amount;
  }
}
