/**
 * A class that represents a card that holds a suit and a value.
 */
public class Card {

    /**
     * Value of card:
     * 0: 2 or 9
     * 1: 3 or T
     * 2: 4 or J
     * 3: 5 or Q
     * 4: 6 or K
     * 5: 7 or A
     */
    public final int value;

    /**
     * Value of suit from 0 to 7.
     * 0: Low clubs
     * 1: High clubs
     * 2: Low diamonds
     * 3: High diamonds
     * 4: Low hearts
     * 5: High hearts
     * 6: Low spades
     * 7: High spades
     */
    public final int suit;

    /**
     * Constructor for Card class accepting value and suit
     * @param value Value of card
     * @param suit Suit of card
     */
    public Card(int value, int suit) {
        if (value < 0 || value > 5 || suit < 0 || suit > 7) {
            throw new IllegalArgumentException();
        }
        this.value = value;
        this.suit = suit;
    }
}
