package com.ddecola.blsdataquery;

/**
 * Simple wrapper class for easy json translation and potential expansion of filter query with additional parameters
 */
public class QueryFilter {
    private String year;

    public String getYear() {
        return year;
    }
    public void setYear(final String year) {
        this.year = year;
    }
}