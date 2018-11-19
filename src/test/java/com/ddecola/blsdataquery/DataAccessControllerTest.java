package com.ddecola.blsdataquery;

import com.ddecola.blsdataquery.service.QueryDataService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DataAccessControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private QueryDataService dataService;

    @Test
    public void getStoredBlsDataByYear_alreadyStoredData_1999_returnResultsStatus200() throws Exception {
        when(dataService.getStoredBlsDataByYear("1999")).thenReturn(preparedData());

        mvc.perform(get("/blsdata/1999").contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(preparedJson()));
    }

    @Test
    public void getStoredBlsDataByYear_noStoreData_2000_ExceptionStatus404() throws Exception {
        when(dataService.getStoredBlsDataByYear("1999")).thenThrow(new YearNotFoundException("1999"));

        mvc.perform(get("/blsdata/1999").contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getStoredBlsDataByYear_asdfYear_ExceptionStatus400() throws Exception {
        when(dataService.getStoredBlsDataByYear("asdf")).thenThrow(new IllegalArgumentException("asdf"));

        mvc.perform(get("/blsdata/asdf").contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void filterBlsDataByYear_storedData_1999_returnResultsStatus201() throws Exception {
        when(dataService.filterBlsDataByYear("1999")).thenReturn(preparedData());

        mvc.perform(post("/blsdata/").contentType(MediaType.APPLICATION_JSON_UTF8).content("{\"year\":\"1999\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(header().string(HttpHeaders.LOCATION, "/blsdata/1999"))
                .andExpect(content().string(preparedJson()));
    }

    @Test
    public void filterBlsDataByYear_alreadyStoredData_1999_returnResultsStatus200() throws Exception {
        when(dataService.storedDataExists("1999")).thenReturn(true);
        when(dataService.filterBlsDataByYear("1999")).thenReturn(preparedData());

        mvc.perform(post("/blsdata/").contentType(MediaType.APPLICATION_JSON_UTF8).content("{\"year\":\"1999\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(header().string(HttpHeaders.LOCATION, "/blsdata/1999"))
                .andExpect(content().string(preparedJson()));
    }

    private List<Map<String, Object>> preparedData() {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("key", "yup a value");

        final Map<String, Object> anotherMap = new HashMap<String, Object>();
        anotherMap.put("anotherkey", "one more value");

        return Arrays.asList(map, anotherMap);
    }

    private String preparedJson() {
        return "[{\"key\":\"yup a value\"},{\"anotherkey\":\"one more value\"}]";
    }
}
