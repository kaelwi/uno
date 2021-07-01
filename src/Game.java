import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * This Class works the hardest in the UNO Program.
 * It creates Players and Bots, has instructions for starting of the Game,
 */
public class Game {

    private static List<Player> player = new ArrayList<>(4);
    private static Deck deck = new Deck(108);
    private static Deck discardDeck = new Deck(0);
    private static final Scanner input = new Scanner(System.in);;
    private static final PrintStream output = System.out;
    private static int turn = setFirst();
    private static int reverse = 1;

    public static void startGame() {
        deck.fillDeck();
        deck.shuffleCards();
        setPlayers();
        discardDeck.addCardToDiscardDeck(deck.getCards().get(0));
        deck.removeCardFromDeck();
    }

    public static void setReverse() {
        reverse *= -1;
    }

    public static int getReverse() {
        return reverse;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public void setDiscardDeck(Deck discardDeck) {
        this.discardDeck = discardDeck;
    }

    /**
     * This method determines the number of human and bot players
     */
    private static void setPlayers() {
        output.println("Tell me how many bots would you like to involve: ");
        int bots = checkNrBots();
        createBots(bots);

        output.println("Now let's settle the rest!");
        output.println("You may create " + (4-bots) + " players.");
        createPlayers(4-bots);
    }

    /**
     * This Method creates human players (number of human players needs to be entered as parameter)
     * and adds them to the Arraylist of Players
     * @param humans int
     */
    private static void createPlayers(int humans) {
        for (int i = 0; i < humans; i++) {
            output.println("Please enter a name for your player (player nr. " + (i+1) + ")");
            String name = input.nextLine();

            while(name.isEmpty() || playerNames().contains(name)) {
                output.println("I'm sorry, you either did not enter a name, or the name is already taken. Please try again.");
                name = input.nextLine();
            }
            player.add(new Human(name, deck.giveCards()));
        }
    }

    /**
     * This Method creates bot players (takes number of bots as parameter)
     * and adds them to the Arraylist of Players
     * @param bots
     */
    private static void createBots(int bots) {
        for (int i = 0; i < bots; i++) {
            output.println("Please enter a name for bot nr. " + (i+1));
            String name = input.nextLine();

            while(name.isEmpty() || playerNames().contains(name)) {
                output.println("I'm sorry, you either did not enter a name, or the entered name is already taken. Please try again.");
                name = input.nextLine();
            }
            player.add(new Bot(name, deck.giveCards()));
        }
    }

    /**
     * This Method creates a list of Player's Names
     * @returns the Arraylist of names
     */
    private static List<String> playerNames() {
        List<String> names = new ArrayList<>();
        for (Player player : player) {
            names.add(player.getName());
        }
        return names;
    }

    /**
     * This Method checks the number of bot players
     * @returns the number of bot players
     */
    private static int checkNrBots() {
        int bots = Integer.parseInt(input.nextLine());
        while(bots < 0 || bots > 4) {
            if (bots > 4) {
                output.println("The maximum number of allowed players is 4!");
            } else {
                output.println("You know you need at least 1 player...");
            }
            bots = Integer.parseInt(input.nextLine());
        }
        return bots;
    }

    public static Deck getDeck() {
        return deck;
    }

    public static Deck getDiscardDeck() {
        return discardDeck;
    }

    public static Player getPlayer(int position) {
        return player.get(position);
    }

    public static List<Player> getPlayers() {
        return player;
    }

    public static int getTurn() {
        return turn;
    }

    /**
     * This Method determines the first Players
     * @returns the random first Player
     */
    private static int setFirst() {
        Random random = new Random();
        return random.nextInt(3);
    }

    public static void setTurn(int i) {
        turn = i;
    }

    public void printHelp() {
        output.println("Print help - needs to be done.");
        output.println();
    }

    /**
     * This Method deals the cards to the players
     * @param player (to which player are the cards dealt)
     * @param number (number of cards to be dealt)
     * The Method also checks if the Deck is empty
     */
    public static void givePlayerDrawCards(Player player, int number) {
        for (int i = 0; i < number; i++) {
            player.takeCards(getDeck().takeCards(1));
            checkEmptyDeck();
        }
        // player.takeCards(this.getDeck().takeCards(number));
    }
    /**
     * This Method gives TWO penalty card to the Player who forgot to say "UNO" with only one card left in the hand
     * and the next Player already drew a new card
     * @param player who needs to get penalty cards
     */
    public static void missingUnoPenalty(Player player) {
        player.takeCards(getDeck().takeCards(2));
    }

    /**
     * This Method gives ONE penalty card to the Player who forgot to say "UNO" with only one card left in the hand
     * and the next Player did not draw a new card
     * @param player who needs to get penalty card
     */
    public static void giveOnePenaltyCard(Player player) {
        player.takeCards(getDeck().takeCards(1));
    }

    /**
     * This Method checks if the card we want to play is appropriate to play
     * (if the color or the value of this card matches the color or the value of the top card on the discard Deck)
     * @param card we want to play
     * @param discardDeckCard top card on the discard Deck
     * @returns if the card should be played
     */
    public static boolean cardValidation(Card card, Card discardDeckCard) {
        if (card.getValue().equals(discardDeckCard.getValue()) || card.getColor().equals(discardDeckCard.getColor()) || card.getColor().equals("") || discardDeckCard.getColor().equals("")) {
            return true;
        }
        return false;
    }

    /**
     * This Method ?
     */
    public static void turnOverflow() {
        if (getTurn() < 0) {
            setTurn(3);
        } else if (getTurn() > 3) {
            setTurn(0);
        }
    }

    /**
     * This Method shuffles the discard Deck, when it is full
     * @param discardDeck
     * @returns shuffled discard Deck
     */
    public Deck shuffleDiscardDeck(Deck discardDeck) {
        discardDeck.shuffleCards();
        return discardDeck;
    }

    /**
     * This Method checks if the main Deck is empty of cards.
     * If it is, than the discard Deck full with cards becomes the main Deck
     * and the new discard Deck starts afresh
     */
    public static void checkEmptyDeck() {
        if (deck.isEmpty()) {
            output.println("The deck is empty! Let me shuffle a new one...");
            Deck newDeck = new Deck(1);
            newDeck.addCardToDiscardDeck(discardDeck.getDiscardDeckCard());
            discardDeck.removeCardFromDeck();
            deck = discardDeck;
            discardDeck = newDeck;
        }
    }

    public static boolean moveValidation(Card card, Player player) {
        if (cardValidation(card, getDiscardDeck().getDiscardDeckCard())) {
            return true;
        } else {
            output.println("Invalid move!");
            player.takeCardBack(card);
            giveOnePenaltyCard(player);
            return false;
        }
    }

    public static void doChecks() {
        if (getDiscardDeck().checkReverse()) {
            setReverse();
        }
        checkColorChange();
        setTurn(getTurn()+getReverse());
        doOtherChecks();
    }

    private static void checkColorChange() {
        if (getDiscardDeck().getDiscardDeckCard().getColor().equals("")) {
            boolean rightInput = false;
            String color = "";
            while(!rightInput) {
                output.println("You can choose the color.");
                color = input.nextLine();
                for (int i = 0; i < Card.getAllColors().length-1; i++) {
                    if (color.equals(Card.getAllColors()[i])) {
                        rightInput = true;
                        break;
                    }
                }
                if (!rightInput) {
                    output.println("Invalid choice of color!");
                }
            }
            getDiscardDeck().addCardToDiscardDeck(new Card(color, getDiscardDeck().getDiscardDeckCard().getValue(), -1));
        }
    }

    private static void doOtherChecks() {
        turnOverflow();
        checkStop();
        checkTakeTwo();
        checkTakeFour();
        turnOverflow();
    }

    private static void checkStop() {
        if (getDiscardDeck().getDiscardDeckCard().getValue().equals("X")) {
            Printer.printState(App.exit, getDiscardDeck().checkReverse(), getDiscardDeck().getDiscardDeckCard());
            Printer.printPlayerCards(getPlayer(getTurn()));
            output.println("Stop! You are not allowed to play!");
            setTurn(getTurn() + getReverse());
        }
    }

    private static void checkTakeTwo() {
        if (getDiscardDeck().getDiscardDeckCard().getValue().equals("+2")) {
            Printer.printState(App.exit, getDiscardDeck().checkReverse(), getDiscardDeck().getDiscardDeckCard());
            Printer.printPlayerCards(getPlayer(getTurn()));
            output.println("You have to take 2 cards!");
            for (int i = 0; i < 2; i++) {
                givePlayerDrawCards(getPlayer(getTurn()), 1);
                checkEmptyDeck();
            }
            setTurn(getTurn() + getReverse());
        }
    }

    private static void checkTakeFour() {
        if (getDiscardDeck().getDiscardDeckCard().getValue().equals("W+4")) {
            Printer.printState(App.exit, getDiscardDeck().checkReverse(), getDiscardDeck().getDiscardDeckCard());
            Printer.printPlayerCards(getPlayer(getTurn()));
            output.println("You have to take 4 cards!");
            for (int i = 0; i < 4; i++) {
                givePlayerDrawCards(getPlayer(getTurn()), 1);
                checkEmptyDeck();
            }
            setTurn(getTurn()+getReverse());
        }
    }
}

