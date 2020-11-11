package org.openmbee.syncservice.twc.syntax.fields;

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

public class TwcFields19_0_3Test {

    @Spy
    private TwcFields19_0_3 twcFields19_0_3;

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
            Field<CommonFields, String> f = twcFields19_0_3.getField(field, String.class);
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
            Field<SysMLv1X, String> f = twcFields19_0_3.getField(field, String.class);
            if(f != null) {
                ++count;
                assertNull(f.get(object));
            }
        }
        assertTrue(count > 0);
    }

    @Test
    public void getField_TwcFields() {
        //This just tests that no errors are thrown and the Fields are constructed
        JSONObject object = new JSONObject();
        int count = 0;
        for(TwcFields field : TwcFields.values()) {
            Field<TwcFields, String> f = twcFields19_0_3.getField(field, String.class);
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
        Field<?, String> f = twcFields19_0_3.getField(unknownId, String.class);
        assertNull(f);
    }

}