package org.openmbee.syncservice.translation.twc19_3tomms4;

import org.openmbee.syncservice.core.syntax.fields.CommonFields;
import org.openmbee.syncservice.core.translation.elements.ElementBuilder;
import org.openmbee.syncservice.core.translation.elements.Detail;
import org.openmbee.syncservice.mms.mms4.MmsFields;
import org.openmbee.syncservice.sysml.syntax.SysMLv1X;
import org.openmbee.syncservice.twc.syntax.fields.TwcFields;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Mms4ElementBuilder extends ElementBuilder<Twc19_3ToMms4Translator> {

    private static final Logger logger = LoggerFactory.getLogger(Mms4ElementBuilder.class);

    protected final TranslationContext context;
    protected final List<Detail> details = new ArrayList<>();

    public Mms4ElementBuilder(Twc19_3ToMms4Translator translator, TranslationContext context) {
        super(translator);
        this.context = context;

        addDetail(SysMLv1X.ELEMENT_TYPE, v -> v == null ? null : v.replaceAll("uml:",""), String.class);
        addDetail(SysMLv1X.NAME, String.class);
        addDetail(SysMLv1X.OWNER_ID, this::translateId, String.class);
        addDetail(CommonFields.ID, String.class);

        addDetail(TwcFields.MD_EXTENTION, MmsFields.MD_EXTENTION, this::translateIdArray, JSONArray.class, JSONArray.class);
        addDetail(SysMLv1X.TEMPLATE_BINDING, this::translateIdArray, JSONArray.class, JSONArray.class);
        addDetail(SysMLv1X.CLIENT_DEPENDENCIES, this::translateIdArray, JSONArray.class, JSONArray.class);

        addDetail(SysMLv1X.APPLIED_STEREOTYPE_INSTANCE, this::translateId, String.class);
        addDetail(SysMLv1X.TEMPLATE_PARAMETER, this::translateId, String.class);
        addDetail(SysMLv1X.IS_ACTIVE, this::parseBoolean, String.class, Boolean.class);
        addDetail(SysMLv1X.OWNED_ELEMENTS, MmsFields.IS_LEAF, v -> v == null || v.length() == 0, JSONArray.class, Boolean.class);
        addDetail(SysMLv1X.SYNC_ELEMENT, this::translateId, String.class);
        addDetail(SysMLv1X.SUPPLIER_DEPENDENCIES, this::translateIdArray, JSONArray.class, JSONArray.class);
        addDetail(SysMLv1X.NAME_EXPRESSION, String.class);
        addDetail(SysMLv1X.PACKAGE_IMPORTS, this::translateIdArray, JSONArray.class, JSONArray.class);
        addDetail(SysMLv1X.VISIBILITY, String.class);
        addDetail(SysMLv1X.ELEMENT_IMPORTS, this::translateIdArray, JSONArray.class, JSONArray.class);
        //TODO add the rest of the fields

    }

    protected String translateId(String id) {
        return translator.translateEsiId(context, id);
    }

    protected JSONArray translateIdArray(JSONArray v) {
        if(v == null) {
            return null;
        }
        List<String> idList = translator.getJsonUtils().flattenObjectArray(v, "@id");
        return new JSONArray(idList.parallelStream().map(this::translateId).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    protected Boolean parseBoolean(String value) {
        try {
            return Boolean.parseBoolean(value);
        } catch(Exception ex) {
            return null;
        }
    }

    protected TranslationContext getContext() {
        return context;
    }
}
