package org.openmbee.syncservice.core.translation.elements;

public interface DetailTranslation <S,T> {
    T translate(S s);
}
