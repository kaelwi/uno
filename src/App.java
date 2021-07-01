import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class App {
    private final Scanner input;
    private final PrintStream output;
    public static boolean exit = false;

    public App(Scanner input, PrintStream output){
        this.input = input;
        this.output = output;
    }

    public void Run() throws SQLException {
        initialize();
        Printer.printState(exit, Game.getDiscardDeck().checkReverse(), Game.getDiscardDeck().getDiscardDeckCard());

        while(!exit) {
            Printer.printPlayerCards(Game.getPlayer(Game.getTurn()));
            readUserInput(Game.getPlayer(Game.getTurn()));
            Printer.printState(exit, Game.getDiscardDeck().checkReverse(), Game.getDiscardDeck().getDiscardDeckCard());
        }
    }

    private void initialize() {
        DBManager.createTable();
        Game.startGame();
        Printer.printBegin(Game.getPlayers());
        Printer.whoBegins(Game.getPlayer(Game.getTurn()));
        Game.checkStartingColor();
    }

    private void readUserInput(Player player) throws SQLException {
        output.println("Tell me your next step.");
        Card cardToBePlayed = player.turn(Game.getDiscardDeck().getDiscardDeckCard());
        updateState(cardToBePlayed, player);
    }

    private void updateState(Card card, Player player) throws SQLException {
        if (exit) {
            Printer.printEndGame(Game.getPlayers());
        } else {
            if (card != null) {
                if (Game.moveValidation(card, player)) {
                    Game.getDiscardDeck().addCardToDiscardDeck(card);
                }
            }

            Game.doChecks();
            checkWinner(player);
        }
    }

    private void checkWinner(Player player) throws SQLException {
        if (player.getPlayerCards().isEmpty()) {
            Printer.printWin(player);
            for (Player p : Game.getPlayers()) {
                if (!p.equals(player)) {
                    for (Card cardsLeft : p.getPlayerCards()) {
                        player.setPoints(player.getPoints()+cardsLeft.getPoints());
                    }
                }
            }

            DBManager.insertOrUpdate(Game.getPlayers());
            Printer.printEndRound();
            exit = checkOverallWinner(Game.getPlayers());
            if (!exit) {
                Game.newRound();
                Printer.startingNewRound();
                Printer.whoBegins(Game.getPlayer(Game.getTurn()));
                Game.checkStartingColor();
            }
        }
    }

    private boolean checkOverallWinner(List<Player> players) {
        try {
            for (Player player : players) {
                ResultSet resultSet = DBManager.selectPoints(player);
                while(resultSet.next()) {
                    if (resultSet.getInt("points") >= 500) {
                        output.println();
                        output.println("Player " + player.getName() + " wins this game with " + resultSet.getInt("points") + " points");
                        return true;
                    }
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
}
