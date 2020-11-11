package org.openmbee.syncservice.mms.mms4;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.openmbee.syncservice.core.syntax.fields.CommonFields;
import org.openmbee.syncservice.core.syntax.fields.Field;
import org.openmbee.syncservice.core.syntax.fields.FieldId;
import org.openmbee.syncservice.sysml.syntax.SysMLv1X;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class Mms4FieldsTest {
    @Spy
    private Mms4Fields mms4Fields;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getField_CommonFields() {
        //This just tests that no errors are thrown and the Fields are constructed
        JSONObject object = new JSONObject();
        int count = 0;
        for(CommonFields field : CommonFields.values()) {
            Field<CommonFields, String> f = mms4Fields.getField(field, String.class);
            if(f != null) {
                ++count;
                assertNull(f.get(object));
            }
        }
        assertTrue(count > 0);
    }

    @Test
    public void getField_SysMLv1X() {
        //This just tests that no errors are thrown and the Fields are constructed
        JSONObject object = new JSONObject();
        int count = 0;
        for(SysMLv1X field : SysMLv1X.values()) {
            Field<SysMLv1X, String> f = mms4Fields.getField(field, String.class);
            if(f != null) {
                ++count;
                assertNull(f.get(object));
            }
        }
        assertTrue(count > 0);
    }

    @Test
    public void getField_MmsFields() {
        //This just tests that no errors are thrown and the Fields are constructed
        JSONObject object = new JSONObject();
        int count = 0;
        for(MmsFields field : MmsFields.values()) {
            Field<MmsFields, String> f = mms4Fields.getField(field, String.class);
            if(f != null) {
                ++count;
                assertNull(f.get(object));
            }
        }
        assertTrue(count > 0);
    }

    @Test
    public void getField_Unknown() {
        FieldId unknownId = mock(FieldId.class);
        Field<?, String> f = mms4Fields.getField(unknownId, String.class);
        assertNull(f);
    }
}