package com.ddecola.blsdataquery.service;

import com.ddecola.blsdataquery.YearNotFoundException;

import java.util.List;
import java.util.Map;

public interface QueryDataService {
    boolean storedDataExists(final String year);
    List<Map<String,Object>> getStoredBlsDataByYear(final String year) throws YearNotFoundException;
    List<Map<String,Object>> filterBlsDataByYear(final String year);
}
