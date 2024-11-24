package edu.grinnell.csc207.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;

import edu.grinnell.csc207.blockchains.Block;
import edu.grinnell.csc207.blockchains.BlockChain;
import edu.grinnell.csc207.blockchains.HashValidator;
import edu.grinnell.csc207.blockchains.Transaction;
import edu.grinnell.csc207.util.IOUtils;

/**
 * A simple UI for our BlockChain class.
 */
public class BlockChainUI {
  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * The number of bytes we validate. Should be set to 3 before submitting.
   */
  static final int VALIDATOR_BYTES = 3;

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Print out the instructions.
   *
   * @param pen The pen used for printing instructions.
   */
  public static void instructions(PrintWriter pen) {
    pen.println("""
        Valid commands:
          mine: discovers the nonce for a given transaction
          append: appends a new block onto the end of the chain
          remove: removes the last block from the end of the chain
          check: checks that the block chain is valid
          users: prints a list of users
          balance: finds a user's balance
          transactions: prints out the chain of transactions
          blocks: prints out the chain of blocks (for debugging only)
          help: prints this list of commands
          quit: quits the program
        """);
  } // instructions(PrintWriter)

  // +------+--------------------------------------------------------
  // | Main |
  // +------+

  /**
   * Run the UI.
   *
   * @param args Command-line arguments (currently ignored).
   */
  public static void main(String[] args) throws Exception {
    PrintWriter pen = new PrintWriter(System.out, true);
    BufferedReader eyes = new BufferedReader(new InputStreamReader(System.in));

    // Set up our blockchain.
    HashValidator validator = (h) -> {
      if (h.length() < VALIDATOR_BYTES) return false;
      for (int i = 0; i < VALIDATOR_BYTES; i++) {
        if (h.get(i) != 0) return false;
      }
      return true;
    };
    BlockChain chain = new BlockChain(validator);

    instructions(pen);
    boolean done = false;

    while (!done) {
      pen.print("\nCommand: ");
      pen.flush();
      String command = eyes.readLine();
      if (command == null) {
        command = "quit";
      }

      try {
        switch (command.toLowerCase()) {
          case "mine" -> {
            String source = IOUtils.readLine(pen, eyes, "Source (return for deposit): ");
            String target = IOUtils.readLine(pen, eyes, "Target: ");
            int amount = IOUtils.readInt(pen, eyes, "Amount: ");
            Block minedBlock = chain.mine(new Transaction(source, target, amount));
            pen.println("Use nonce: " + minedBlock.getNonce());
          }

          case "append" -> {
            String source = IOUtils.readLine(pen, eyes, "Source (return for deposit): ");
            String target = IOUtils.readLine(pen, eyes, "Target: ");
            int amount = IOUtils.readInt(pen, eyes, "Amount: ");
            long nonce = IOUtils.readLong(pen, eyes, "Nonce: ");
            Block newBlock = new Block(chain.getSize(), new Transaction(source, target, amount), chain.getHash(), nonce);
            chain.append(newBlock);
            pen.printf("Appended: %s\n", newBlock);
          }

          case "remove" -> {
            if (chain.removeLast()) {
              pen.println("Removed last element");
            } else {
              pen.println("Cannot remove the genesis block");
            }
          }

          case "check" -> {
            if (chain.isCorrect()) {
              pen.println("The blockchain checks out.");
            }
          }

          case "users" -> {
            Iterator<String> users = chain.users();
            while (users.hasNext()) {
              pen.println(users.next());
            }
          }

          case "balance" -> {
            String user = IOUtils.readLine(pen, eyes, "User: ");
            int balance = chain.balance(user);
            pen.printf("%s's balance is %d\n", user, balance);
          }

          case "transactions" -> {
            Iterator<Transaction> transactions = chain.iterator();
            while (transactions.hasNext()) {
              pen.println(transactions.next());
            }
          }

          case "blocks" -> {
            Iterator<Block> blocks = chain.blocks();
            while (blocks.hasNext()) {
              pen.println(blocks.next());
            }
          }

          case "help" -> instructions(pen);

          case "quit" -> done = true;

          default -> pen.printf("Invalid command: '%s'. Try again.\n", command);
        }
      } catch (IllegalArgumentException e) {
        pen.printf("Could not append: %s\n", e.getMessage());
      } catch (Exception e) {
        pen.printf("Error: %s\n", e.getMessage());
      }
    }

    pen.println("\nGoodbye");
    eyes.close();
    pen.close();
  }
} // class BlockChainUI