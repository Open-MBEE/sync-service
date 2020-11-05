package org.openmbee.syncservice.translation.twc19_3tomms4;

import org.openmbee.syncservice.sysml.syntax.SysMLv1X;
import org.json.JSONArray;

public class ClassBuilder extends Mms4ElementBuilder {
    public ClassBuilder(Twc19_3ToMms4Translator translator, TranslationContext context) {
        super(translator, context);

        addDetail(SysMLv1X.REPRESENTATION, this::translateIdArray, JSONArray.class, JSONArray.class);
        addDetail(SysMLv1X.POWERTYPE_EXTENT, this::translateIdArray, JSONArray.class, JSONArray.class);
        addDetail(SysMLv1X.USE_CASES, this::translateIdArray, JSONArray.class, JSONArray.class);
        addDetail(SysMLv1X.CLASSIFIER_BEHAVIOR, this::translateId, String.class, String.class);
        addDetail(SysMLv1X.INTERFACE_REALIZATIONS, this::translateIdArray, JSONArray.class, JSONArray.class);
        addDetail(SysMLv1X.OWNED_ATTRIBUTES, this::translateIdArray, JSONArray.class, JSONArray.class);
        addDetail(SysMLv1X.SUBSTITUTIONS, this::translateIdArray, JSONArray.class, JSONArray.class);
        addDetail(SysMLv1X.REDEFINED_CLASSIFIERS, this::translateIdArray, JSONArray.class, JSONArray.class);
        addDetail(SysMLv1X.IS_ABSTRACT, this::parseBoolean, String.class, Boolean.class);
        addDetail(SysMLv1X.GENERALIZATIONS, this::translateIdArray, JSONArray.class, JSONArray.class);
        addDetail(SysMLv1X.OWNED_OPERATIONS, this::translateIdArray, JSONArray.class, JSONArray.class);
        addDetail(SysMLv1X.COLLABORATION_USES, this::translateIdArray, JSONArray.class, JSONArray.class);
        addDetail(SysMLv1X.IS_FINAL_SPECIALIZATION, this::parseBoolean, String.class, Boolean.class);
    }
}
