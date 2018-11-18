package com.ddecola.blsdataquery.datastore;

import com.ddecola.blsdataquery.YearNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BlsFilteredDataStoreImpl implements BlsFilteredDataStore {
    private static final Map<String, List<Map<String,Object>>> YEAR_DATA_MAP = new HashMap<String, List<Map<String,Object>>>();

    // The base directory created in the current working directory of the running application
    private final String BASE_DIR = "blsDataStore";

    // Initial setup is for an 'in-memory' store, only try to write to the 'on disk' data store if we can mkdir & write
    protected boolean useOnDiskDataStore = false;

    @PostConstruct
    public void initStore() {
        useOnDiskDataStore = createBaseStorageDir(getBaseDirName());
    }

    /**
     * Used to peek at the datastore, useful for setting HttpStatus Codes with Post request
     * @param year
     * @return true / false if the data exists already
     */
    public boolean storedDataExists(final String year) {
        return useOnDiskDataStore ? Files.exists(getPathToStorageFile(year)) : YEAR_DATA_MAP.containsKey(year);
    }

    /**
     * @param year The year the data was filtered on, used as the file name
     * @param data The filtered data by year to be stored as json in file
     */
    public void store(final String year, final List<Map<String,Object>> data) {
        if (useOnDiskDataStore) {
            try {
                storeDataAsJsonFile(year, data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else
            YEAR_DATA_MAP.put(year, data);
    }

    /**
     * @param year The year the data was filtered by and stored
     * @return The stored data
     * @throws YearNotFoundException
     */
    public List<Map<String,Object>> retrieve(final String year) throws YearNotFoundException {
        if (useOnDiskDataStore) {
            if (!Files.exists(getPathToStorageFile(year)))
                throw new YearNotFoundException(year);

            try {
                return resolveDataFromJsonFile(year);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            if (YEAR_DATA_MAP.containsKey(year))
                return YEAR_DATA_MAP.get(year);
            else
                throw new YearNotFoundException(year);
        }
    }

    protected String getBaseDirName() {
        return BASE_DIR;
    }

    protected void storeDataAsJsonFile(final String year, final List<Map<String,Object>> data) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();

        mapper.writeValue(getPathToStorageFile(year).toFile(), data);
    }

    protected List<Map<String,Object>> resolveDataFromJsonFile(final String year) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(getPathToStorageFile(year).toFile(), List.class);
    }

    private Path getPathToStorageFile(final String year) {
        return Paths.get(getBaseDirName(),year + ".json");
    }

    private boolean createBaseStorageDir(final String baseDir) {
        final Path baseDirPath = Paths.get(baseDir);

        if (!Files.exists(baseDirPath)) {
            try {
                Files.createDirectory(baseDirPath);
            } catch (IOException e) {
                // swallow IOException and fall back to in memory store
                return false;
            }
        }

        return Files.exists(baseDirPath) && Files.isDirectory(baseDirPath) && Files.isWritable(baseDirPath);
    }

}
