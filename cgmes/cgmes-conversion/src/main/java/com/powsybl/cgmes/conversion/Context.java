/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.cgmes.conversion;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import com.powsybl.iidm.network.IdentifiableType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.powsybl.cgmes.conversion.Conversion.Config;
import com.powsybl.cgmes.conversion.elements.hvdc.DcMapping;
import com.powsybl.cgmes.model.CgmesModel;
import com.powsybl.cgmes.model.CgmesNames;
import com.powsybl.cgmes.model.PowerFlow;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.Terminal;
import com.powsybl.triplestore.api.PropertyBag;
import com.powsybl.triplestore.api.PropertyBags;

/**
 * @author Luma Zamarreño <zamarrenolm at aia.es>
 */
public class Context {

    // Log messages
    private static final String FIXED_REASON = "Fixed {}. Reason: {}";
    private static final String INVALID_REASON = "Invalid {}. Reason: {}";
    private static final String IGNORED_REASON = "Ignored {}. Reason: {}";

    public Context(CgmesModel cgmes, Config config, Network network) {
        this.cgmes = Objects.requireNonNull(cgmes);
        this.config = Objects.requireNonNull(config);
        this.network = Objects.requireNonNull(network);

        // Even if the CGMES model is node-breaker,
        // we could decide to ignore the connectivity nodes and
        // create buses directly from topological nodes,
        // the configuration says if we are performing the conversion
        // based on existing node-breaker info
        nodeBreaker = cgmes.isNodeBreaker() && config.useNodeBreaker();

        namingStrategy = config.getNamingStrategy();
        cgmesBoundary = new CgmesBoundary(cgmes);
        substationIdMapping = new SubstationIdMapping(this);
        terminalMapping = new TerminalMapping();
        dcMapping = new DcMapping(this);
        loadingLimitsMapping = new LoadingLimitsMapping(this);
        regulatingControlMapping = new RegulatingControlMapping(this);
        nodeMapping = new NodeMapping();

        ratioTapChangerTables = new HashMap<>();
        phaseTapChangerTables = new HashMap<>();
        reactiveCapabilityCurveData = new HashMap<>();
        powerTransformerRatioTapChangers = new HashMap<>();
        powerTransformerPhaseTapChangers = new HashMap<>();
    }

    public CgmesModel cgmes() {
        return cgmes;
    }

    public Network network() {
        return network;
    }

    public Config config() {
        return config;
    }

    public boolean nodeBreaker() {
        return nodeBreaker;
    }

    public NamingStrategy namingStrategy() {
        return namingStrategy;
    }

    public TerminalMapping terminalMapping() {
        return terminalMapping;
    }

    public void convertedTerminal(String terminalId, Terminal t, int n, PowerFlow f) {
        // Record the mapping between CGMES and IIDM terminals
        terminalMapping().add(terminalId, t, n);
        // Update the power flow at terminal. Check that IIDM allows setting it
        if (f.defined() && setPQAllowed(t)) {
            t.setP(f.p());
            t.setQ(f.q());
        }
    }

    private boolean setPQAllowed(Terminal t) {
        return t.getConnectable().getType() != IdentifiableType.BUSBAR_SECTION;
    }

    public NodeMapping nodeMapping() {
        return nodeMapping;
    }

    public SubstationIdMapping substationIdMapping() {
        return substationIdMapping;
    }

    public CgmesBoundary boundary() {
        return cgmesBoundary;
    }

    public DcMapping dc() {
        return dcMapping;
    }

    public LoadingLimitsMapping loadingLimitsMapping() {
        return loadingLimitsMapping;
    }

    public RegulatingControlMapping regulatingControlMapping() {
        return regulatingControlMapping;
    }

    public static String boundaryVoltageLevelId(String nodeId) {
        Objects.requireNonNull(nodeId);
        return nodeId + "_VL";
    }

    public static String boundarySubstationId(String nodeId) {
        Objects.requireNonNull(nodeId);
        return nodeId + "_S";
    }

    public void loadReactiveCapabilityCurveData() {
        PropertyBags rccdata = cgmes.reactiveCapabilityCurveData();
        if (rccdata == null) {
            return;
        }
        rccdata.forEach(p -> {
            String curveId = p.getId("ReactiveCapabilityCurve");
            reactiveCapabilityCurveData.computeIfAbsent(curveId, cid -> new PropertyBags()).add(p);
        });
    }

    public PropertyBags reactiveCapabilityCurveData(String curveId) {
        return reactiveCapabilityCurveData.get(curveId);
    }

    public void loadRatioTapChangers() {
        cgmes.ratioTapChangers().forEach(ratio -> {
            String id = ratio.getId(CgmesNames.RATIO_TAP_CHANGER);
            powerTransformerRatioTapChangers.put(id, ratio);
        });
    }

    public PropertyBag ratioTapChanger(String id) {
        return powerTransformerRatioTapChangers.get(id);
    }

    public void loadPhaseTapChangers() {
        cgmes.phaseTapChangers().forEach(phase -> {
            String id = phase.getId(CgmesNames.PHASE_TAP_CHANGER);
            powerTransformerPhaseTapChangers.put(id, phase);
        });
    }

    public PropertyBag phaseTapChanger(String id) {
        return powerTransformerPhaseTapChangers.get(id);
    }

    public void loadRatioTapChangerTables() {
        PropertyBags rtcpoints = cgmes.ratioTapChangerTablesPoints();
        if (rtcpoints == null) {
            return;
        }
        rtcpoints.forEach(p -> {
            String tableId = p.getId("RatioTapChangerTable");
            ratioTapChangerTables.computeIfAbsent(tableId, tid -> new PropertyBags()).add(p);
        });
    }

    public void loadPhaseTapChangerTables() {
        PropertyBags ptcpoints = cgmes.phaseTapChangerTablesPoints();
        if (ptcpoints == null) {
            return;
        }
        ptcpoints.forEach(p -> {
            String tableId = p.getId("PhaseTapChangerTable");
            phaseTapChangerTables.computeIfAbsent(tableId, tid -> new PropertyBags()).add(p);
        });
    }

    public PropertyBags ratioTapChangerTable(String tableId) {
        return ratioTapChangerTables.get(tableId);
    }

    public PropertyBags phaseTapChangerTable(String tableId) {
        return phaseTapChangerTables.get(tableId);
    }

    public void invalid(String what, String reason) {
        LOG.warn(INVALID_REASON, what, reason);
    }

    public void invalid(String what, Supplier<String> reason) {
        if (LOG.isWarnEnabled()) {
            LOG.warn(INVALID_REASON, what, reason.get());
        }
    }

    public void invalid(Supplier<String> what, Supplier<String> reason) {
        if (LOG.isWarnEnabled()) {
            LOG.warn(INVALID_REASON, what.get(), reason.get());
        }
    }

    public void ignored(String what, String reason) {
        LOG.warn(IGNORED_REASON, what, reason);
    }

    public void ignored(String what, Supplier<String> reason) {
        if (LOG.isWarnEnabled()) {
            LOG.warn(IGNORED_REASON, what, reason.get());
        }
    }

    public void pending(String what, Supplier<String> reason) {
        if (LOG.isInfoEnabled()) {
            LOG.info("PENDING {}. Reason: {}", what, reason.get());
        }
    }

    public void fixed(String what, String reason) {
        LOG.warn(FIXED_REASON, what, reason);
    }

    public void fixed(Supplier<String> what, String reason) {
        if (LOG.isWarnEnabled()) {
            LOG.warn(FIXED_REASON, what.get(), reason);
        }
    }

    public void fixed(String what, Supplier<String> reason) {
        if (LOG.isWarnEnabled()) {
            LOG.warn(FIXED_REASON, what, reason.get());
        }
    }

    public void fixed(String what, String reason, double wrong, double fixed) {
        LOG.warn("Fixed {}. Reason: {}. Wrong {}, fixed {}", what, reason, wrong, fixed);
    }

    public void missing(String what) {
        LOG.warn("Missing {}", what);
    }

    public void missing(String what, double defaultValue) {
        LOG.warn("Missing {}. Used default value {}", what, defaultValue);
    }

    private final CgmesModel cgmes;
    private final Network network;
    private final Config config;
    private final boolean nodeBreaker;
    private final NamingStrategy namingStrategy;
    private final SubstationIdMapping substationIdMapping;
    private final CgmesBoundary cgmesBoundary;
    private final TerminalMapping terminalMapping;
    private final NodeMapping nodeMapping;
    private final DcMapping dcMapping;
    private final LoadingLimitsMapping loadingLimitsMapping;
    private final RegulatingControlMapping regulatingControlMapping;

    private final Map<String, PropertyBags> ratioTapChangerTables;
    private final Map<String, PropertyBags> phaseTapChangerTables;
    private final Map<String, PropertyBags> reactiveCapabilityCurveData;
    private final Map<String, PropertyBag> powerTransformerRatioTapChangers;
    private final Map<String, PropertyBag> powerTransformerPhaseTapChangers;

    private static final Logger LOG = LoggerFactory.getLogger(Context.class);
}
