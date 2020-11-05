package org.openmbee.syncservice.core.utils;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JSONUtilsTest {

    @Spy
    private JSONUtils jsonUtils;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void parseStringToJsonArrayTest1() {
        JSONArray a = jsonUtils.parseStringToJsonArray("[{},{}]");
        assertNotNull(a);
        assertEquals(2, a.length());
    }

    @Test
    public void parseStringToJsonArrayTest2() {
        JSONArray a = jsonUtils.parseStringToJsonArray("[]");
        assertNull(a);
    }

    @Test
    public void parseStringToJsonArrayTest3() {
        try {
            JSONArray a = jsonUtils.parseStringToJsonArray(null);
            fail("Should have thrown...");
        } catch(Exception ex) {}
    }

    @Test
    public void parseStringToJsonArrayTest4() {
        try {
            JSONArray a = jsonUtils.parseStringToJsonArray("{}");
            fail("Should have thrown...");
        } catch(Exception ex) {}
    }

    @Test
    public void convertJsonArrayToStringListTest1() {
        List<String> list = jsonUtils.convertJsonArrayToStringList(new JSONArray("['one','two']"));
        assertNotNull(list);
        assertEquals(2, list.size());
    }

    @Test
    public void convertJsonArrayToStringListTest2() {
        List<String> list = jsonUtils.convertJsonArrayToStringList(new JSONArray("['one', 1]"));
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("1", list.get(1));
    }

    @Test
    public void convertJsonArrayToStringListTest3() {
        List<String> list = jsonUtils.convertJsonArrayToStringList(null);
        assertNull(list);
    }

    @Test
    public void getStringFromArrayOfJSONObjectsTest1() {
        String s = jsonUtils.getStringFromArrayOfJSONObjects(new JSONArray("[{},{'key':'val'}]"), "key");
        assertNotNull(s);
        assertEquals("val", s);
    }

    @Test
    public void getStringFromArrayOfJSONObjectsTest2() {
        String s = jsonUtils.getStringFromArrayOfJSONObjects(new JSONArray("[{},{'keyyy':'val'}]"), "key");
        assertNull(s);
    }

    @Test
    public void getStringFromArrayOfJSONObjectsTest3() {
        String s = jsonUtils.getStringFromArrayOfJSONObjects(null, "key");
        assertNull(s);
    }

    @Test
    public void getStringFromArrayOfJSONObjectsTest4() {
        try {
            String s = jsonUtils.getStringFromArrayOfJSONObjects(new JSONArray("[{},{'key':1}]"), "key");
            fail("Should have thrown...");
        } catch(Exception ex) {}

    }

    @Test
    public void getIntFromArrayOfJSONObjectsTest1() {
        Integer s = jsonUtils.getIntFromArrayOfJSONObjects(new JSONArray("[{},{'key':1}]"), "key");
        assertNotNull(s);
        assertEquals(1, s);
    }

    @Test
    public void getIntFromArrayOfJSONObjectsTest2() {
        Integer s = jsonUtils.getIntFromArrayOfJSONObjects(new JSONArray("[{},{'keyyy':1}]"), "key");
        assertNull(s);
    }

    @Test
    public void getIntFromArrayOfJSONObjectsTest3() {
        Integer s = jsonUtils.getIntFromArrayOfJSONObjects(null, "key");
        assertNull(s);
    }

    @Test
    public void getIntFromArrayOfJSONObjectsTest4() {
        try {
            Integer s = jsonUtils.getIntFromArrayOfJSONObjects(new JSONArray("[{},{'key':'val'}]"), "key");
            fail("Should have thrown...");
        } catch(Exception ex) {}

    }
}