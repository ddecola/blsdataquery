package com.ddecola.blsdataquery;

import org.junit.Assert;
import org.junit.Test;

public class YearValidatorTest {
    private final YearValidator validator = new YearValidator();

    @Test
    public void testValidYear19() {
        Assert.assertTrue(validator.isValidYear("1999"));
    }

    @Test
    public void testValidYear20() {
        Assert.assertTrue(validator.isValidYear("2001"));
    }

    @Test
    public void testInvalidYear20001() {
        Assert.assertFalse(validator.isValidYear("20001"));
    }

    @Test
    public void testNotAYear() {
        Assert.assertFalse(validator.isValidYear("nope"));
    }

    @Test
    public void testNormalNumber() {
        Assert.assertFalse(validator.isValidYear("4"));
    }

    @Test
    public void testNonYear4DigitNumber() {
        Assert.assertFalse(validator.isValidYear("7000"));
    }
}
