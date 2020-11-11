package org.openmbee.syncservice.twc.filter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.openmbee.syncservice.core.data.sourcesink.Source;
import org.openmbee.syncservice.core.syntax.Fields;
import org.openmbee.syncservice.core.syntax.Parser;
import org.openmbee.syncservice.core.syntax.Syntax;
import org.openmbee.syncservice.core.syntax.fields.Field;
import org.openmbee.syncservice.sysml.syntax.SysMLv1X;
import org.openmbee.syncservice.twc.syntax.fields.TwcFields;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class OnlyMainModelElementFilterTest {

    private OnlyMainModelElementFilter filter;

    @Mock
    private Source source;
    @Mock
    private Syntax syntax;
    @Mock
    private Parser parser;
    @Mock
    private Fields fields;
    @Mock
    private Field<SysMLv1X, String> typeField;
    @Mock
    private Field<SysMLv1X, JSONArray> ownedElementsField;
    @Mock
    private Field<TwcFields, String> esiIdField;
    @Mock
    private Field<TwcFields, String> defaultWorkingPackageField;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(source.getSyntax()).thenReturn(syntax);
        when(syntax.getParser()).thenReturn(parser);
        when(parser.getFields()).thenReturn(fields);
        when(fields.getField(SysMLv1X.ELEMENT_TYPE, String.class)).thenReturn(typeField);
        when(fields.getField(SysMLv1X.OWNED_ELEMENTS, JSONArray.class)).thenReturn(ownedElementsField);
        when(fields.getField(TwcFields.ESI_ID, String.class)).thenReturn(esiIdField);
        when(fields.getField(TwcFields.DEFAULT_WORKING_PACKAGE, String.class)).thenReturn(defaultWorkingPackageField);

        filter = new OnlyMainModelElementFilter(source);
    }

    @Test
    public void testModel_noMainModel_A() {
        JSONObject model1 = new JSONObject();
        when(typeField.get(model1)).thenReturn("uml:Model");
        when(ownedElementsField.get(model1)).thenReturn(new JSONArray("[{'@id':'1'},{'@id':'2'}]"));
        when(esiIdField.get(model1)).thenReturn("model1id");

        filter.add(model1);

        assertTrue(filter.getIgnoredIds().isEmpty());
    }

    @Test
    public void testModel_noMainModel_B() {
        JSONObject options = new JSONObject();
        when(typeField.get(options)).thenReturn("md.ce.rt.options:Options");
        when(esiIdField.get(options)).thenReturn("optionsid");

        JSONObject model1 = new JSONObject();
        when(typeField.get(model1)).thenReturn("uml:Model");
        when(ownedElementsField.get(model1)).thenReturn(new JSONArray("[{'@id':'1'},{'@id':'2'}]"));
        when(esiIdField.get(model1)).thenReturn("model1id");

        filter.add(options);
        filter.add(model1);

        assertEquals(1, filter.getIgnoredIds().size());
        assertTrue(filter.getIgnoredIds().contains("optionsid"));
    }

    @Test
    public void testModel_NonMainModel_first() {
        JSONObject options = new JSONObject();
        when(typeField.get(options)).thenReturn("md.ce.rt.options:Options");
        when(defaultWorkingPackageField.get(options)).thenReturn("mainmodelid");
        when(esiIdField.get(options)).thenReturn("optionsid");

        JSONObject model1 = new JSONObject();
        when(typeField.get(model1)).thenReturn("uml:Model");
        when(ownedElementsField.get(model1)).thenReturn(new JSONArray("[{'@id':'1'},{'@id':'2'}]"));
        when(esiIdField.get(model1)).thenReturn("model1id");

        filter.add(options);
        filter.add(model1);

        assertEquals(4, filter.getIgnoredIds().size());
        assertTrue(filter.getIgnoredIds().contains("1"));
        assertTrue(filter.getIgnoredIds().contains("2"));
        assertTrue(filter.getIgnoredIds().contains("model1id"));
        assertTrue(filter.getIgnoredIds().contains("optionsid"));
    }

    @Test
    public void testModel_NonMainModel_second() {
        JSONObject options = new JSONObject();
        when(typeField.get(options)).thenReturn("md.ce.rt.options:Options");
        when(defaultWorkingPackageField.get(options)).thenReturn("mainmodelid");
        when(esiIdField.get(options)).thenReturn("optionsid");

        JSONObject model1 = new JSONObject();
        when(typeField.get(model1)).thenReturn("uml:Model");
        when(ownedElementsField.get(model1)).thenReturn(new JSONArray("[{'@id':'1'},{'@id':'2'}]"));
        when(esiIdField.get(model1)).thenReturn("model1id");

        filter.add(model1);
        filter.add(options);

        assertEquals(4, filter.getIgnoredIds().size());
        assertTrue(filter.getIgnoredIds().contains("1"));
        assertTrue(filter.getIgnoredIds().contains("2"));
        assertTrue(filter.getIgnoredIds().contains("model1id"));
        assertTrue(filter.getIgnoredIds().contains("optionsid"));
    }

    @Test
    public void testModel_MainModel_first() {
        JSONObject options = new JSONObject();
        when(typeField.get(options)).thenReturn("md.ce.rt.options:Options");
        when(defaultWorkingPackageField.get(options)).thenReturn("mainmodelid");
        when(esiIdField.get(options)).thenReturn("optionsid");

        JSONObject main = new JSONObject();
        when(typeField.get(main)).thenReturn("uml:Model");
        when(ownedElementsField.get(main)).thenReturn(new JSONArray("[{'@id':'1'},{'@id':'2'}]"));
        when(esiIdField.get(main)).thenReturn("mainmodelid");

        filter.add(main);
        filter.add(options);

        assertEquals(1, filter.getIgnoredIds().size());
        assertTrue(filter.getIgnoredIds().contains("optionsid"));
    }

    @Test
    public void testModel_MainModel_second() {
        JSONObject options = new JSONObject();
        when(typeField.get(options)).thenReturn("md.ce.rt.options:Options");
        when(defaultWorkingPackageField.get(options)).thenReturn("mainmodelid");
        when(esiIdField.get(options)).thenReturn("optionsid");

        JSONObject main = new JSONObject();
        when(typeField.get(main)).thenReturn("uml:Model");
        when(ownedElementsField.get(main)).thenReturn(new JSONArray("[{'@id':'1'},{'@id':'2'}]"));
        when(esiIdField.get(main)).thenReturn("mainmodelid");

        filter.add(options);
        filter.add(main);

        assertEquals(1, filter.getIgnoredIds().size());
        assertTrue(filter.getIgnoredIds().contains("optionsid"));
    }

    @Test
    public void testModel_MainModel_twice_A() {
        JSONObject options = new JSONObject();
        when(typeField.get(options)).thenReturn("md.ce.rt.options:Options");
        when(defaultWorkingPackageField.get(options)).thenReturn("mainmodelid");
        when(esiIdField.get(options)).thenReturn("optionsid");

        JSONObject options2 = new JSONObject();
        when(typeField.get(options2)).thenReturn("md.ce.rt.options:Options");
        when(defaultWorkingPackageField.get(options2)).thenReturn("mainmodelid2");
        when(esiIdField.get(options2)).thenReturn("optionsid2");


        JSONObject main1 = new JSONObject();
        when(typeField.get(main1)).thenReturn("uml:Model");
        when(ownedElementsField.get(main1)).thenReturn(new JSONArray("[{'@id':'a'},{'@id':'b'}]"));
        when(esiIdField.get(main1)).thenReturn("mainmodelid");

        JSONObject main2 = new JSONObject();
        when(typeField.get(main2)).thenReturn("uml:Model");
        when(ownedElementsField.get(main2)).thenReturn(new JSONArray("[{'@id':'1'},{'@id':'2'}]"));
        when(esiIdField.get(main2)).thenReturn("mainmodelid2");

        filter.add(options);
        filter.add(options2);
        filter.add(main1);
        filter.add(main2);

        assertEquals(5, filter.getIgnoredIds().size());
        assertTrue(filter.getIgnoredIds().contains("1"));
        assertTrue(filter.getIgnoredIds().contains("2"));
        assertTrue(filter.getIgnoredIds().contains("mainmodelid2"));
        assertTrue(filter.getIgnoredIds().contains("optionsid"));
        assertTrue(filter.getIgnoredIds().contains("optionsid2"));
    }

    @Test
    public void testModel_MainModel_twice_B() {
        JSONObject options = new JSONObject();
        when(typeField.get(options)).thenReturn("md.ce.rt.options:Options");
        when(defaultWorkingPackageField.get(options)).thenReturn("mainmodelid");
        when(esiIdField.get(options)).thenReturn("optionsid");

        JSONObject options2 = new JSONObject();
        when(typeField.get(options2)).thenReturn("md.ce.rt.options:Options");
        when(defaultWorkingPackageField.get(options2)).thenReturn("mainmodelid2");
        when(esiIdField.get(options2)).thenReturn("optionsid2");


        JSONObject main1 = new JSONObject();
        when(typeField.get(main1)).thenReturn("uml:Model");
        when(ownedElementsField.get(main1)).thenReturn(new JSONArray("[{'@id':'a'},{'@id':'b'}]"));
        when(esiIdField.get(main1)).thenReturn("mainmodelid");

        JSONObject main2 = new JSONObject();
        when(typeField.get(main2)).thenReturn("uml:Model");
        when(ownedElementsField.get(main2)).thenReturn(new JSONArray("[{'@id':'1'},{'@id':'2'}]"));
        when(esiIdField.get(main2)).thenReturn("mainmodelid2");

        filter.add(main2);
        filter.add(main1);
        filter.add(options);
        filter.add(options2);

        assertEquals(5, filter.getIgnoredIds().size());
        assertTrue(filter.getIgnoredIds().contains("1"));
        assertTrue(filter.getIgnoredIds().contains("2"));
        assertTrue(filter.getIgnoredIds().contains("mainmodelid2"));
        assertTrue(filter.getIgnoredIds().contains("optionsid"));
        assertTrue(filter.getIgnoredIds().contains("optionsid2"));
    }
    
    @Test
    public void testIgnoredTypes() {
        List<String> types = List.of("esiproject:EsiProject",
            "md.ce.csh.rt.options:CshLanguageOptions",
            "project.options:UserProjectOptions",
            "project.options:CommonProjectOptions",
            "md.ce.corbaidl.rt.options:CORBAIDLLanguageOptions",
            "MDFoundation:MDExtension",
            "md.ce.java.rt.options:JavaLanguageOptions",
            "uuidmap:UUIDMap",
            "esiproject:ProxyInfoHolder",
            "md.ce.msil.rt.options:MsilLanguageOptions",
            "security:ProjectSecurity",
            "md.ce.cppAnsi.rt.options:CppLanguageOptions",
            "uml:PackageImport",
            "uml:ElementImport");

        int expectedFilterSize = 0;
        for (String type : types) {
            JSONObject obj = new JSONObject();
            when(typeField.get(obj)).thenReturn(type);
            when(ownedElementsField.get(obj)).thenReturn(new JSONArray(String.format("[{'@id':'%sa'},{'@id':'%sb'}]", type, type)));
            when(esiIdField.get(obj)).thenReturn(type + "id");
            expectedFilterSize += 3;

            filter.add(obj);

            assertEquals(expectedFilterSize, filter.getIgnoredIds().size());
            assertTrue(filter.getIgnoredIds().contains(type + "id"));
            assertTrue(filter.getIgnoredIds().contains(type + "a"));
            assertTrue(filter.getIgnoredIds().contains(type + "b"));
        }
    }

    @Test
    public void testIgnoredNoOwned() {
        JSONObject obj = new JSONObject();
        when(typeField.get(obj)).thenReturn("uml:PackageImport");
        when(esiIdField.get(obj)).thenReturn("id");

        filter.add(obj);

        assertEquals(1, filter.getIgnoredIds().size());
        assertTrue(filter.getIgnoredIds().contains("id"));
    }

    @Test
    public void testIgnoredNoOwnedOrId() {
        JSONObject obj = new JSONObject();
        when(typeField.get(obj)).thenReturn("uml:PackageImport");

        filter.add(obj);

        assertEquals(0, filter.getIgnoredIds().size());
    }

    @Test
    public void testIgnoredNoId() {
        JSONObject obj = new JSONObject();
        when(typeField.get(obj)).thenReturn("uml:PackageImport");
        when(ownedElementsField.get(obj)).thenReturn(new JSONArray("[{'@id':'a'},{'@id':'b'}]"));

        filter.add(obj);

        assertEquals(2, filter.getIgnoredIds().size());
        assertTrue(filter.getIgnoredIds().contains("a"));
        assertTrue(filter.getIgnoredIds().contains("b"));
    }

    @Test
    public void testNotIgnored() {
        JSONObject obj = new JSONObject();
        when(typeField.get(obj)).thenReturn("SomethingRandom");
        when(ownedElementsField.get(obj)).thenReturn(new JSONArray("[{'@id':'a'},{'@id':'b'}]"));
        when(esiIdField.get(obj)).thenReturn("id");

        filter.add(obj);

        assertEquals(0, filter.getIgnoredIds().size());
    }
}