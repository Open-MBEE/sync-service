package org.openmbee.syncservice.translation.twc19_3tomms4;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.openmbee.syncservice.core.utils.JSONUtils;

import static org.junit.Assert.assertEquals;

public class Twc19_3ToMms4TranslatorTest {

    @Spy
    private JSONUtils jsonUtils;

    @InjectMocks
    @Spy
    private Twc19_3ToMms4Translator twc19_3ToMms4Translator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void translateBranchIdTest() {
        assertEquals("abc_123-a_567", twc19_3ToMms4Translator.translateBranchId("something", "aBC%123-A 567"));
    }
}