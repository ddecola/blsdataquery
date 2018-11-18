package com.ddecola.blsdataquery;

import java.util.regex.Pattern;

/**
 * This is just a simple validator class to make sure the data passed as a year is somewhere in between 1900 - 2999
 */
public class YearValidator {
    private final String YEAR_REGEX = "^([1,2][0-9]{3})$";

    public boolean isValidYear(final String year) {
        return Pattern.matches(YEAR_REGEX, year);
    }
}
