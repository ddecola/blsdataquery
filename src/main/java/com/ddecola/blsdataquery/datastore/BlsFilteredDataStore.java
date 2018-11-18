package com.ddecola.blsdataquery.datastore;

import com.ddecola.blsdataquery.YearNotFoundException;

import java.util.List;
import java.util.Map;

public interface BlsFilteredDataStore {
    boolean storedDataExists(final String year);
    void store(final String year, final List<Map<String,Object>> data);
    List<Map<String,Object>> retrieve(final String year) throws YearNotFoundException;
}
