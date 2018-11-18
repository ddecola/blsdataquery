package com.ddecola.blsdataquery;

/**
 * Custom exception handler for when the requested year is not found in the data store
 */
public class YearNotFoundException extends Exception {

    private final String year;

    public YearNotFoundException(final String year) {
        this.year = year;
    }

    @Override
    public String getMessage() {
        return String.format("Data for year %s was not found in the data store.", year);
    }
}
