package stefanowicz.kacper.util;

public interface Config {
    /**
     * LOYALTY CARD CONSTANTS
     */
    Integer startingDiscount = 5;
    Integer startingMoviesNumber = 3;
    Integer loyaltyCardPeriod = 30;
    Integer boughtMoviesToGetFirstLoyaltyCard = 5;
    Integer maxLoyaltyCardDiscount = 30;

    /**
     * MOVIES CONSTANTS
     */
    Integer upcomingMoviesDays = 4;

    Integer SALT_LENGTH = 512;
    Integer ITERATIONS = 65536;
    Integer KEY_LENGTH = 512;
    String ALGORITHM = "PBKDF2WithHmacSHA512";
}
