package com.example.deepltranslationservice.testdata;

import com.example.deepltranslationservice.dto.response.DeepLResponse;
import com.example.deepltranslationservice.dto.response.Translation;

import java.util.ArrayList;
import java.util.List;

public class DeepLResponseTestBuilder {
    private List<Translation> translations = new ArrayList<>();

    public static DeepLResponseTestBuilder aDeepLResponse() {
        return new DeepLResponseTestBuilder();
    }

    public DeepLResponseTestBuilder withTranslation(String text) {
        Translation translation = new Translation();
        translation.setText(text);
        this.translations.add(translation);
        return this;
    }

    public DeepLResponse build() {
        DeepLResponse response = new DeepLResponse();
        response.setTranslations(translations);
        return response;
    }
}
