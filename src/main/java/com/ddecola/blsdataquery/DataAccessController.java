package com.ddecola.blsdataquery;

import com.ddecola.blsdataquery.service.QueryDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@RestController
public class DataAccessController {

    @Autowired
    private QueryDataService dataService;

    /**
     * GET endpoint for requesting the stored data from a previous POST filter query executed
     * @param year
     * @return
     * @throws YearNotFoundException
     */
    @GetMapping("/blsdata/{year}")
    @ResponseBody
    public List<Map<String,Object>> getStoredBlsDataByYear(@PathVariable final String year) throws YearNotFoundException {
        return dataService.getStoredBlsDataByYear(year);
    }

    /**
     * POST endpoint for requesting from the BLS table, filtered by year
     * The data is stored locally as a JSON file to be retrieved again via the GET endpoint
     * @param year The required year to filter the BLS unemployment data by.
     * @return the result data set from the filtered query
     */
    @PostMapping("/blsdata")
    public ResponseEntity filterBlsDataByYear(@RequestBody final QueryFilter year) {
        // indicate 201 created when the post request actually queries GCE and 200 after that
        final HttpStatus statusCode = dataService.storedDataExists(year.getYear()) ? HttpStatus.OK : HttpStatus.CREATED;

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.add(HttpHeaders.LOCATION, "/blsdata/"+year.getYear());
        headers.add(HttpHeaders.DATE, ZonedDateTime.now().toString());

        return new ResponseEntity<List<Map<String,Object>>>(dataService.filterBlsDataByYear(year.getYear()), headers, statusCode);
    }
}
