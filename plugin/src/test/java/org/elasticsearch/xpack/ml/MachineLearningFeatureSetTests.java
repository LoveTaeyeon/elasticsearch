/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.ml;

import org.elasticsearch.action.support.PlainActionFuture;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.license.XPackLicenseState;
import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.xpack.XPackFeatureSet;
import org.elasticsearch.xpack.XPackFeatureSet.Usage;
import org.junit.Before;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MachineLearningFeatureSetTests extends ESTestCase {

    private XPackLicenseState licenseState;

    @Before
    public void init() throws Exception {
        licenseState = mock(XPackLicenseState.class);
    }

    public void testAvailable() throws Exception {
        MachineLearningFeatureSet featureSet = new MachineLearningFeatureSet(Settings.EMPTY, licenseState);
        boolean available = randomBoolean();
        when(licenseState.isMachineLearningAllowed()).thenReturn(available);
        assertThat(featureSet.available(), is(available));
        PlainActionFuture<Usage> future = new PlainActionFuture<>();
        featureSet.usage(future);
        XPackFeatureSet.Usage usage = future.get();
        assertThat(usage.available(), is(available));

        BytesStreamOutput out = new BytesStreamOutput();
        usage.writeTo(out);
        XPackFeatureSet.Usage serializedUsage = new MachineLearningFeatureSet.Usage(out.bytes().streamInput());
        assertThat(serializedUsage.available(), is(available));
    }

    public void testEnabled() throws Exception {
        boolean useDefault = randomBoolean();
        boolean enabled = true;
        Settings.Builder settings = Settings.builder();
        if (useDefault == false) {
            enabled = randomBoolean();
            settings.put("xpack.ml.enabled", enabled);
        }
        boolean expected = enabled || useDefault;
        MachineLearningFeatureSet featureSet = new MachineLearningFeatureSet(settings.build(), licenseState);
        assertThat(featureSet.enabled(), is(expected));
        PlainActionFuture<Usage> future = new PlainActionFuture<>();
        featureSet.usage(future);
        XPackFeatureSet.Usage usage = future.get();
        assertThat(usage.enabled(), is(expected));

        BytesStreamOutput out = new BytesStreamOutput();
        usage.writeTo(out);
        XPackFeatureSet.Usage serializedUsage = new MachineLearningFeatureSet.Usage(out.bytes().streamInput());
        assertThat(serializedUsage.enabled(), is(expected));
    }

}
