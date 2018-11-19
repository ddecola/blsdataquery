package com.ddecola.blsdataquery.bigquery;

import com.google.cloud.bigquery.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Accessor for GCE, specific to the bigquery-public-data.bls.unemployment_cps data set
 */

@Service
public class BigQueryDataAccessor {
    private static final Logger logger = LoggerFactory.getLogger(BigQueryDataAccessor.class);

    private static final String BLS_UNEMPLOYMENT_DATA_QUERY = "SELECT * FROM `bigquery-public-data.bls.unemployment_cps` WHERE YEAR = ";

    /**
     * @param year The required year to filter the BLS unemployment data by.
     * @return the result data set from the filtered query
     */
    public List<Map<String,Object>> filterBlsDataByYear(final String year) {
        Job queryJob = createBigQueryJob(year);

        TableResult result = null;
        try {
            queryJob = queryJob.waitFor();

            if (queryJob == null) {
                throw new RuntimeException("Job no longer exists");
            } else if (queryJob.getStatus().getError() != null) {
                throw new RuntimeException(queryJob.getStatus().getError().toString());
            }

            result = queryJob.getQueryResults();

            logger.info(String.format("GCE query took %s milliseconds", queryJob.getStatistics().getEndTime() - queryJob.getStatistics().getStartTime()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return prepareResultData(result);
    }

    private Job createBigQueryJob(final String year) {
        final BigQuery bigQuery = BigQueryOptions.getDefaultInstance().getService();

        final QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(BLS_UNEMPLOYMENT_DATA_QUERY + year).build();

        return bigQuery.create(JobInfo.newBuilder(queryConfig).setJobId(JobId.of(UUID.randomUUID().toString())).build());
    }

    private List<Map<String,Object>> prepareResultData(final TableResult result) {
        final List<String> fields = new LinkedList<String>();

        // collect the field names from the schema
        if (result.getSchema() != null && result.getSchema().getFields() != null)
            for (final Field f : result.getSchema().getFields())
                fields.add(f.getName());

        final List<Map<String,Object>> data = new LinkedList<Map<String,Object>>();

        for (final FieldValueList row : result.iterateAll()) {
            final Map<String,Object> map = new HashMap<String,Object>();
            // get the values for each row
            for (final String f : fields) {
                map.put(f, row.get(f).getValue());
            }

            data.add(map);
        }

        return data;
    }
}
