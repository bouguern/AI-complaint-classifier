package com.bouguern.demo.eval.dataset;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public final class EvalDatasetLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private EvalDatasetLoader() {
    }

    public static List<EvalCase> load() throws IOException {

        try (InputStream stream = EvalDatasetLoader.class
                .getResourceAsStream("/eval/golden-dataset.json")) {

            if (stream == null) {
                throw new IOException(
                        "Golden dataset not found at /eval/golden-dataset.json - "
                                + "make sure it is in src/test/resources/eval/");
            }

            List<EvalCase> cases = MAPPER.readValue(
                    stream, new TypeReference<List<EvalCase>>() {
                    });

            System.out.printf("Loaded %d eval cases from golden dataset%n", cases.size());
            
            return cases;
        }
    }
}