package dev.kingrabbit.fruitfulutilities.pathviewer;

public enum Currency {

    // General
    COINS("Coin", '6', 'e', true),
    GOLD("Bank Gold", '6', 'e', false),

    // Religion
    FAVORS("Favor", '2', 'a', true),

    // Science
    RESEARCH_PHYSICS("Research (Physics)", '3', 'b', false),
    RESEARCH_SOCIETY("Research (Society)", '2', 'a', false),
    RESEARCH_TECHNOLOGY("Research (Technology)", '6', 'e', false),

    // Beneath
    RESERVE("Reserve", '6', 'e', false),
    FUNDING("Funding", '6', 'e', false),

    ;

    private final String display;
    private final char primaryColor;
    private final char secondaryColor;
    private final boolean makePlural;

    Currency(String display, char primaryColor, char secondaryColor, boolean makePlural) {
        this.display = display;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.makePlural = makePlural;
    }

    public String format(long amount) {
        if (makePlural && amount != 1) {
            return display + "s";
        } else {
            return display;
        }
    }

    public char getPrimaryColor() {
        return primaryColor;
    }

    public char getSecondaryColor() {
        return secondaryColor;
    }

}
