package com.ddecola.blsdataquery.service;

import com.ddecola.blsdataquery.YearNotFoundException;
import com.ddecola.blsdataquery.YearValidator;
import com.ddecola.blsdataquery.bigquery.BigQueryDataAccessor;
import com.ddecola.blsdataquery.datastore.BlsFilteredDataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service endpoints called by Rest Controller for filtered data from GCE via BigQuery and retrieving stored data
 */

@Service
public class QueryDataServiceImpl implements QueryDataService {

    @Autowired
    private BigQueryDataAccessor accessor;

    @Autowired
    private BlsFilteredDataStore dataStore;

    /**
     * @param year The required year to retrieve the stored filtered BLS unemployment data by.
     * @return The result data set from the filtered query.
     * @throws YearNotFoundException If the data for the requested year is not found in the data store.
     */
    public List<Map<String,Object>> getStoredBlsDataByYear(final String year) throws YearNotFoundException {
        validateYear(year);

        return dataStore.retrieve(year);
    }

    /**
     * Used to check if the data from the POST request already exists, useful for setting HTTP Status Codes
     *
     * @param year The required year to retrieve the stored filtered BLS unemployment data by.
     * @return true if the data already has been stored
     */
    public boolean storedDataExists(final String year) {
        validateYear(year);

        return dataStore.storedDataExists(year);
    }

    /**
     * @param year The required year to filter the BLS unemployment data by.
     * @return the result data set from the filtered query
     */
    public List<Map<String,Object>> filterBlsDataByYear(final String year) {
        validateYear(year);

        if (dataStore.storedDataExists(year)) {
            try {
                return dataStore.retrieve(year);
            } catch (YearNotFoundException e) {
                // swallow exception and fall through, this shouldn't happen unless the data store is being externally modified
            }
        }

        final List<Map<String,Object>> data = accessor.filterBlsDataByYear(year);

        dataStore.store(year, data);

        return data;
    }

    /**
     * Consolidate the input year validation / exception handling
     * @param year
     */
    private void validateYear(final String year) {
        if (year == null || year.length() == 0)
            throw new IllegalArgumentException("no year received");

        if (!new YearValidator().isValidYear(year))
            throw new IllegalArgumentException(String.format("%s is not a valid year", year));
    }
}
