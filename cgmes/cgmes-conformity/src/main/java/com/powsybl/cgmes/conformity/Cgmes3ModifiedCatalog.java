/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.cgmes.conformity;

import com.powsybl.cgmes.model.test.TestGridModelResources;
import com.powsybl.commons.datasource.ResourceSet;

/**
 * @author Luma Zamarreño <zamarrenolm at aia.es>
 * @author José Antonio Marqués <marquesja at aia.es>
 */
public final class Cgmes3ModifiedCatalog {

    private Cgmes3ModifiedCatalog() {
    }

    public static TestGridModelResources smallGridBaseCaseTieFlowMappedToSwitch() {
        String base = CGMES_3_MODIFIED_TEST_MODELS
            + "/SmallGrid/tieFlowMappedToSwitch/";
        String baseOriginal = CGMES_3_TEST_MODELS
            + "/SmallGrid/";
        String baseBoundary = CGMES_3_TEST_MODELS
            + "/SmallGrid/";
        return new TestGridModelResources(
            "SmallGrid-TieFlow-Mapped-To-Switch",
            null,
            new ResourceSet(base,
                "20210112T1742Z_1D_GB_EQ_001.xml"),
            new ResourceSet(baseOriginal,
                "20210112T1742Z_1D_GB_DL_001.xml",
                "20210112T1742Z_1D_GB_SV_001.xml",
                "20210112T1742Z_1D_GB_EQ_001.xml",
                "20210112T1742Z_1D_GB_GL_001.xml",
                "20210112T1742Z_1D_GB_SSH_001.xml",
                "20210112T1742Z_1D_GB_TP_001.xml"),
            new ResourceSet(baseBoundary, "SmallGridTestConfiguration_EQ_BD_v3.0.0.xml"));
    }

    public static TestGridModelResources smallGridBaseCaseTieFlowMappedToEquivalentInjection() {
        String base = CGMES_3_MODIFIED_TEST_MODELS
            + "/SmallGrid/tieFlowMappedToEquivalentInjection/";
        String baseOriginal = CGMES_3_TEST_MODELS
            + "/SmallGrid/";
        String baseBoundary = CGMES_3_TEST_MODELS
            + "/SmallGrid/";
        return new TestGridModelResources(
            "SmallGrid-TieFlow-Mapped-To-EquivalentInjection",
            null,
            new ResourceSet(base,
                "20210112T1742Z_1D_GB_EQ_001.xml"),
            new ResourceSet(baseOriginal,
                "20210112T1742Z_1D_GB_DL_001.xml",
                "20210112T1742Z_1D_GB_SV_001.xml",
                "20210112T1742Z_1D_GB_EQ_001.xml",
                "20210112T1742Z_1D_GB_GL_001.xml",
                "20210112T1742Z_1D_GB_SSH_001.xml",
                "20210112T1742Z_1D_GB_TP_001.xml"),
            new ResourceSet(baseBoundary, "SmallGridTestConfiguration_EQ_BD_v3.0.0.xml"));
    }

    public static TestGridModelResources microGridBaseCaseRegulatingTerminalsDefinedOnSwitches() {
        String base = CGMES_3_MODIFIED_TEST_MODELS
            + "/MicroGrid/regulatingTerminalsDefinedOnSwitches/";
        String baseOriginal = CGMES_3_TEST_MODELS
            + "/MicroGrid/";
        String baseBoundary = CGMES_3_TEST_MODELS
            + "/MicroGrid/";
        return new TestGridModelResources(
            "MicroGrid-regulating-terminals-defined-on-switches",
            null,
            new ResourceSet(base,
                "20210209T1930Z_1D_BE_EQ_9.xml"),
            new ResourceSet(baseOriginal,
                "20210209T1930Z_1D_ASSEMBLED_DL_9.xml",
                "20210209T1930Z_1D_ASSEMBLED_SV_9.xml",
                "20210209T1930Z_1D_BE_GL_9.xml",
                "20210209T1930Z_1D_BE_SSH_9.xml",
                "20210209T1930Z_1D_NL_EQ_9.xml",
                "20210209T1930Z_1D_NL_GL_9.xml",
                "20210209T1930Z_1D_NL_SSH_9.xml",
                "20210209T2323Z_1D_ASSEMBLED_TP_9.xml"),
            new ResourceSet(baseBoundary, "20171002T0930Z_ENTSO-E_EQ_BD_2.xml"));
    }

    private static final String CGMES_3_TEST_MODELS = "/cgmes3-test-models";
    private static final String CGMES_3_MODIFIED_TEST_MODELS = "/cgmes3-test-models-modified";
}
