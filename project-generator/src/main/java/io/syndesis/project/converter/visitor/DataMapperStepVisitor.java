/*
 * Copyright (C) 2016 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.syndesis.project.converter.visitor;


import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import io.syndesis.integration.model.steps.Endpoint;
import io.syndesis.integration.model.steps.Step;

public class DataMapperStepVisitor implements StepVisitor {

    public static final String MAPPER = "mapper";

    private final GeneratorContext generatorContext;

    public static class Factory implements StepVisitorFactory<DataMapperStepVisitor> {

        @Override
        public String getStepKind() {
            return MAPPER;
        }

        @Override
        public DataMapperStepVisitor create(GeneratorContext generatorContext) {
            return new DataMapperStepVisitor(generatorContext);
        }
    }

    public DataMapperStepVisitor(GeneratorContext generatorContext) {
        this.generatorContext = generatorContext;
    }

    @Override
    public Step visit(StepVisitorContext stepContext) {
        Map<String, String> configuredProperties = stepContext.getStep().getConfiguredProperties();

        String resourceName = "mapping-step-" + stepContext.getIndex() + ".json";
        try {
            byte[] resourceData = utf8(configuredProperties.get("atlasmapping"));

            generatorContext.addTarEntry("src/main/resources/" + resourceName, resourceData);
            return new Endpoint("atlas:" + resourceName);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot write " + resourceName + ":" + e,e);
        }
    }

    private static byte[] utf8(String value) {
        if (value == null) {
            return null;
        }
        return value.getBytes(Charset.forName("UTF-8"));
    }
}
