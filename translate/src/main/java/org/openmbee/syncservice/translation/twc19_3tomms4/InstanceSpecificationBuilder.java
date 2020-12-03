package org.openmbee.syncservice.translation.twc19_3tomms4;

import org.json.JSONArray;
import org.openmbee.syncservice.core.syntax.fields.CommonFields;
import org.openmbee.syncservice.core.syntax.fields.Field;
import org.openmbee.syncservice.mms.mms4.MmsFields;
import org.openmbee.syncservice.sysml.syntax.SysMLv1X;

import java.util.List;
import java.util.stream.Collectors;

public class InstanceSpecificationBuilder extends AsiSlotBaseBuilder {
    public InstanceSpecificationBuilder(Twc19_3ToMms4Translator twc19_3ToMms4Translator, TranslationContext context) {
        super(twc19_3ToMms4Translator, context);

        //ID field for InstanceSpecifications is derived from the owner id
        addDetail(SysMLv1X.OWNER_ID, CommonFields.ID, this::getAsiId, String.class, String.class);
        //MmsFields.SLOTS
        //SLOT ids are derived from the id of the owning instance specification
        addDetail((original, out) -> {
            Field<SysMLv1X, JSONArray> ownedElementsField = translator.getSourceSyntax().getParser().getFields().getField(SysMLv1X.OWNED_ELEMENTS, JSONArray.class);
            JSONArray ownedElementsArray = translator.getSourceSyntax().getParser().getFieldFromElement(ownedElementsField, original);
            List<String> slotEsiIdList = getTranslator().getJsonUtils().flattenObjectArray(ownedElementsArray, "@id");
            Field<SysMLv1X, String> ownerIdField = translator.getSourceSyntax().getParser().getFields().getField(SysMLv1X.OWNER_ID, String.class);
            String ownerEsiId = ownerIdField.get(original);
            String asiId = getAsiId(ownerEsiId);

            List<String> slotIds = slotEsiIdList.stream().map(v -> getSlotId(asiId, translateId(v))).collect(Collectors.toList());
            Field<MmsFields, JSONArray> targetField = translator.getSinkSyntax().getParser().getFields().getField(MmsFields.SLOTS, JSONArray.class);
            targetField.put(new JSONArray(slotIds), out);
        });

    }




}
