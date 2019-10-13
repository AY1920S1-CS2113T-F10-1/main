package duke.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import duke.exceptions.ModBadRequestStatus;
import duke.exceptions.ModFailedJsonException;
import duke.modules.data.ModuleInfoDetailed;
import duke.modules.data.ModuleInfoSummary;

public class JsonWrapper {

    private Gson gson;
    private RequestsData requestsData;
    private final String listFile = "data/modsListData.json";
    private final String listDetailedFile = "data/modsDetailedListData.json";
    private final String academicYear = "2019-2020";

    public enum Requests {
        DETAILED, SUMMARY
    }

    /**
     * Constructor for JsonWrapper to access module information.
     */
    public JsonWrapper() {
        gson = new Gson();
        requestsData = new RequestsData();
    }

    /**
     * For each data set, request for nusMods API.
     */
    public void runRequests(Storage store) throws ModBadRequestStatus {
        for (Requests req : Requests.values()) {
            storeJson(req, store);
        }
    }

    private void storeJson(Requests type, Storage store) throws ModBadRequestStatus {
        switch (type) {
            case SUMMARY: {
                store.setDataPath(Paths.get(listFile));
                if (store.getDataPathExists()) {
                    break;
                }
                requestsData.storeModData(requestsData.requestModuleList(academicYear), store);
                break;
            }
            case DETAILED: {
                store.setDataPath(Paths.get(listDetailedFile));
                if (store.getDataPathExists()) {
                    break;
                }
                requestsData.storeModData(requestsData.requestModuleListDetailed(academicYear), store);
                break;
            }
            default: {
                throw new IllegalStateException("Unexpected value: " + type);
            }
        }
    }


    //TODO: This function would return a string list of
    //      all modules in NUS in this academic year.

    /**
     * Reads the Json file for to be parsed into a java object. Since the data is
     * presented in a JSON array, our class object class would need to be wrapped
     * in an array as well.
     */
    private List<ModuleInfoSummary> getModuleListObject() {
        try {
            JsonReader reader = new JsonReader(new FileReader(listFile));
            Type listType = new TypeToken<List<ModuleInfoSummary>>(){}.getType();
            return gson.fromJson(reader, listType);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException ei) {
            System.out.println(Arrays.toString(ei.getStackTrace()));
        }
        return null;
    }

    /**
     * Main helper function to obtained HashMap of summary info from modsListData.json.
     * @return HashMap with module code as the key and ModuleInfoSummary object as the value.
     * @throws ModFailedJsonException If the previous call to getModuleListObject() returns null.
     */
    public HashMap<String, ModuleInfoSummary> getModuleSummaryMap() throws ModFailedJsonException {
        List<ModuleInfoSummary> modsList = getModuleListObject();
        if (modsList == null) {
            throw new ModFailedJsonException();
        }
        HashMap<String, ModuleInfoSummary> ret = new HashMap<>();
        for (ModuleInfoSummary temp : modsList) {
            String modCode = temp.getModuleCode();
            ret.put(modCode, temp);
        }
        return ret;
    }

    private List<ModuleInfoDetailed> getModuleListDetailedObject() {
        try {
            JsonReader reader = new JsonReader(new FileReader(listDetailedFile));
            Type listType = new TypeToken<List<ModuleInfoDetailed>>(){}.getType();
            return gson.fromJson(reader, listType);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException ei) {
            System.out.println(Arrays.toString(ei.getStackTrace()));
        }
        return null;
    }

    /**
     * Main helper function to obtained HashMap of detailed info from modsDetailedListData.json.
     * @return HashMap with module code as the key and ModuleInfoSummary object as the value.
     * @throws ModFailedJsonException If the previous call to getModuleListObject() returns null.
     */
    public HashMap<String, ModuleInfoDetailed> getModuleDetailedMap() throws ModFailedJsonException {
        List<ModuleInfoDetailed> modsList = getModuleListDetailedObject();
        if (modsList == null) {
            throw new ModFailedJsonException();
        }
        HashMap<String, ModuleInfoDetailed> ret = new HashMap<>();
        for (ModuleInfoDetailed temp : modsList) {
            String modCode = temp.getModuleCode();
            ret.put(modCode, temp);
        }
        return ret;
    }
}
