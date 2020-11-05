package org.openmbee.syncservice.translation.twc19_3tomms4;

import org.openmbee.syncservice.core.syntax.Fields;
import org.openmbee.syncservice.core.syntax.Parser;
import org.openmbee.syncservice.core.syntax.fields.CommonFields;
import org.openmbee.syncservice.core.syntax.fields.Field;
import org.openmbee.syncservice.sysml.syntax.SysMLv1X;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openmbee.syncservice.translation.twc19_3tomms4.valuebuilders.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SlotBuilder extends AsiSlotBaseBuilder {
    private final static Logger logger = LoggerFactory.getLogger(SlotBuilder.class);
    private final static Set<String> slotValueTypes = new HashSet<>(
            List.of("uml:LiteralString",
                "uml:LiteralInteger",
                "uml:LiteralBoolean",
                "uml:LiteralReal",
                "uml:LiteralUnlimitedNatural",
                "uml:ElementValue",
                "uml:InstanceValue"));

    public static Set<String> getSlotValueTypes() {
        return slotValueTypes;
    }

    public SlotBuilder(Twc19_3ToMms4Translator twc19_3ToMms4Translator, TranslationContext context) {
        super(twc19_3ToMms4Translator, context);

        addDetail(SysMLv1X.DEFINING_FEATURE, this::translateId, String.class);

        addDetail((original, out) -> {
            //CommonFields.ID field for Slot is derived from the "grandparent" owner
            Parser parser = translator.getSourceSyntax().getParser();
            Field<SysMLv1X, String> ownerIdField = parser.getFields().getField(SysMLv1X.OWNER_ID, String.class);
            String ownerEsiId = parser.getFieldFromElement(ownerIdField, original);
            JSONObject owner = context.getElement(ownerEsiId);
            String grandparentEsiId = parser.getFieldFromElement(ownerIdField, owner);
            String asiId = getAsiId(grandparentEsiId);

            String slotBaseId = parser.getFieldFromElement(CommonFields.ID, original, String.class);
            final String slotId = getSlotId(asiId, slotBaseId);

            Fields targetFields = translator.getSinkSyntax().getParser().getFields();
            Field<SysMLv1X, String> targetOwnerIdField = targetFields.getField(SysMLv1X.OWNER_ID, String.class);
            Field<CommonFields, String> targetIdField = targetFields.getField(CommonFields.ID, String.class);
            targetIdField.put(slotId, out);

            //Slot values are held separately in TWC, but embedded in MMS
            JSONArray valueElements = parser.getFieldFromElement(SysMLv1X.VALUE, original, JSONArray.class);
            List<String> valueElementIds = translator.getJsonUtils().flattenObjectArray(valueElements, "@id");
            List<JSONObject> values = new ArrayList<>(valueElementIds.size());

            for (int i = 0; i < valueElementIds.size(); i++) {
                JSONObject valueElement = context.getElement(valueElementIds.get(i));
                String type = translator.getSourceSyntax().getParser().getFieldFromElement(SysMLv1X.ELEMENT_TYPE, valueElement, String.class);
                ValueBuilder valueBuilder = getValueBuilder(type);
                if(valueBuilder != null) {
                    JSONObject value = valueBuilder.buildElementFrom(valueElement);
                    targetOwnerIdField.put(slotId, value);
                    targetIdField.put(getSlotValueId(slotId, i, type), value);
                    values.add(value);
                }
            }

            Field<SysMLv1X, JSONArray> targetValueField = translator.getSinkSyntax().getParser().getFields().getField(SysMLv1X.VALUE, JSONArray.class);
            targetValueField.put(new JSONArray(values), out);
        });
    }

    private String getSlotValueId(String slotId, int index, String type) {
        return String.format("%s-slotvalue-%d-%s", slotId, index, type.replaceAll("uml:", "").toLowerCase());
    }

    protected ValueBuilder getValueBuilder(String type) {
        if(type == null) {
            return null;
        }
        switch(type) {
            case "uml:LiteralString":
                return new LiteralStringValueBuilder(getTranslator(), getContext());
            case "uml:LiteralInteger":
                return new LiteralIntegerValueBuilder(getTranslator(), getContext());
            case "uml:LiteralBoolean":
                return new LiteralBooleanValueBuilder(getTranslator(), getContext());
            case "uml:LiteralReal":
                return new LiteralRealValueBuilder(getTranslator(), getContext());
            case "uml:LiteralUnlimitedNatural":
                return new LiteralUnlimitedNaturalValueBuilder(getTranslator(), getContext());
            case "uml:ElementValue":
                return new ElementValueBuilder(getTranslator(), getContext());
            case "uml:InstanceValue":
                return new InstanceValueBuilder(getTranslator(), getContext());
            default:
                logger.warn("Unknown SLOT value type: " + type);
                return null;
        }
    }


}
