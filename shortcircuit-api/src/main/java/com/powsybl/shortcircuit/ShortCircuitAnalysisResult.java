/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.shortcircuit;

import com.powsybl.commons.extensions.AbstractExtendable;

import java.util.*;

/**
 * Results of an localized short-circuit computation.
 * Will contain a fault result, with feeder results.
 * Will contain a list of short-circuit bus result for each buses of the network.
 *
 * @author Boubakeur Brahimi
 */
public class ShortCircuitAnalysisResult extends AbstractExtendable<ShortCircuitAnalysisResult> {

    private final Map<String, FaultResult> resultByFaultId = new TreeMap<>();
    private final Map<String, List<FaultResult>> resultByElementId = new TreeMap<>();

    public ShortCircuitAnalysisResult(List<FaultResult> faultResults) {
        Objects.requireNonNull(faultResults);
        faultResults.forEach(r -> {
            this.resultByFaultId.put(r.getFault().getId(), r);
            this.resultByElementId.computeIfAbsent(r.getFault().getElementId(), k -> new ArrayList<>()).add(r);
        });
    }

    /**
     * The associated fault results.
     */
    public List<FaultResult> getFaultResults() {
        return new ArrayList<>(resultByFaultId.values());
    }

    /**
     * Get a computation result associated to a given fault ID
     *
     * @param id the ID of the considered fault.
     * @return the computation result associated to a given fault ID.
     */
    public FaultResult getFaultResult(String id) {
        return resultByFaultId.get(id);
    }

    /**
     * Get a list of computation result associated to a given fault element ID
     *
     * @param elementId the ID of the considered element.
     * @return the computation result associated to a given element ID.
     */
    public List<FaultResult> getFaultResults(String elementId) {
        return resultByElementId.getOrDefault(elementId, Collections.emptyList());
    }
}
