package com.ddecola.blsdataquery.datastore;

import com.ddecola.blsdataquery.YearNotFoundException;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlsFilteredDataStoreTest {

    @ClassRule
    public static final TemporaryFolder tempFolder = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() throws IOException {
        tempFolder.newFolder();
    }

    @Test
    public void store_year1999_jsonContentsInFileCreated() throws IOException {
        final File dir = tempFolder.newFolder();
        final BlsFilteredDataStoreImpl dataStore = new BlsFilteredDataStoreImpl();
        dataStore.setBaseDirName(dir.getAbsolutePath());
        dataStore.initStore();

        dataStore.store("1999", preparedData());

        if (dir.listFiles().length > 0) {
            final String consumedFile = new String(Files.readAllBytes(Paths.get(dir.listFiles()[0].getAbsolutePath())));

            Assert.assertEquals(preparedJson(), consumedFile);
        } else {
            Assert.fail(String.format("expected a created file in %s", dir.getPath()));
        }
    }

    @Test
    public void retrieve_year1999_dataObjectsFromJSON() throws Exception {
        final BlsFilteredDataStoreImpl dataStore = new BlsFilteredDataStoreImpl();
        dataStore.setBaseDirName(Paths.get("src", "test", "resources").toString());
        dataStore.initStore();

        final List<Map<String,Object>> data = dataStore.retrieve("1999");

        Assert.assertEquals(preparedData(), data);
    }

    @Test(expected = YearNotFoundException.class)
    public void retrieve_yearDataNotStored_YearNotFoundException() throws Exception {
        final BlsFilteredDataStoreImpl dataStore = new BlsFilteredDataStoreImpl();
        dataStore.setBaseDirName(Paths.get("src", "test", "resources").toString());
        dataStore.initStore();

        dataStore.retrieve("2000");
    }

    @Test
    public void storedDataExists_dataExists_returnTrue() {
        final BlsFilteredDataStoreImpl dataStore = new BlsFilteredDataStoreImpl();
        dataStore.setBaseDirName(Paths.get("src", "test", "resources").toString());
        dataStore.initStore();

        Assert.assertTrue(dataStore.storedDataExists("1999"));
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
