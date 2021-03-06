/*
 * Copyright 2017-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onosproject.pipelines.basic;

import com.google.common.collect.ImmutableList;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onosproject.driver.pipeline.DefaultSingleTablePipeline;
import org.onosproject.net.behaviour.Pipeliner;
import org.onosproject.net.device.PortStatisticsDiscovery;
import org.onosproject.net.pi.model.DefaultPiPipeconf;
import org.onosproject.net.pi.model.PiPipeconf;
import org.onosproject.net.pi.model.PiPipeconfId;
import org.onosproject.net.pi.model.PiPipelineInterpreter;
import org.onosproject.net.pi.model.PiPipelineModel;
import org.onosproject.net.pi.service.PiPipeconfService;
import org.onosproject.p4runtime.model.P4InfoParser;
import org.onosproject.p4runtime.model.P4InfoParserException;

import java.net.URL;
import java.util.Collection;

import static org.onosproject.net.pi.model.PiPipeconf.ExtensionType.BMV2_JSON;
import static org.onosproject.net.pi.model.PiPipeconf.ExtensionType.P4_INFO_TEXT;

/**
 * Component that produces and registers the basic pipeconfs when loaded.
 */
@Component(immediate = true)
public final class PipeconfLoader {

    private static final PiPipeconfId BASIC_PIPECONF_ID = new PiPipeconfId("org.onosproject.pipelines.basic");
    private static final String BASIC_JSON_PATH = "/p4c-out/bmv2/basic.json";
    private static final String BASIC_P4INFO = "/p4c-out/bmv2/basic.p4info";

    private static final PiPipeconfId ECMP_PIPECONF_ID = new PiPipeconfId("org.onosproject.pipelines.ecmp");
    private static final String ECMP_JSON_PATH = "/p4c-out/bmv2/ecmp.json";
    private static final String ECMP_P4INFO = "/p4c-out/bmv2/ecmp.p4info";

    public static final PiPipeconf BASIC_PIPECONF = buildBasicPipeconf();
    public static final PiPipeconf ECMP_PIPECONF = buildEcmpPipeconf();

    private static final Collection<PiPipeconf> ALL_PIPECONFS = ImmutableList.of(BASIC_PIPECONF, ECMP_PIPECONF);

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private PiPipeconfService piPipeconfService;

    @Activate
    public void activate() {
        // Registers all pipeconf at component activation.
        ALL_PIPECONFS.forEach(piPipeconfService::register);
    }

    @Deactivate
    public void deactivate() {
        ALL_PIPECONFS.stream().map(PiPipeconf::id).forEach(piPipeconfService::remove);
    }

    private static PiPipeconf buildBasicPipeconf() {
        final URL jsonUrl = PipeconfLoader.class.getResource(BASIC_JSON_PATH);
        final URL p4InfoUrl = PipeconfLoader.class.getResource(BASIC_P4INFO);

        return DefaultPiPipeconf.builder()
                .withId(BASIC_PIPECONF_ID)
                .withPipelineModel(parseP4Info(p4InfoUrl))
                .addBehaviour(PiPipelineInterpreter.class, BasicInterpreterImpl.class)
                .addBehaviour(Pipeliner.class, DefaultSingleTablePipeline.class)
                .addBehaviour(PortStatisticsDiscovery.class, PortStatisticsDiscoveryImpl.class)
                .addExtension(P4_INFO_TEXT, p4InfoUrl)
                .addExtension(BMV2_JSON, jsonUrl)
                // Put here other target-specific extensions,
                // e.g. Tofino's bin and context.json.
                .build();
    }

    private static PiPipeconf buildEcmpPipeconf() {
        final URL jsonUrl = PipeconfLoader.class.getResource(ECMP_JSON_PATH);
        final URL p4InfoUrl = PipeconfLoader.class.getResource(ECMP_P4INFO);

        return DefaultPiPipeconf.builder()
                .withId(ECMP_PIPECONF_ID)
                .withPipelineModel(parseP4Info(p4InfoUrl))
                .addBehaviour(PiPipelineInterpreter.class, EcmpInterpreterImpl.class)
                .addBehaviour(Pipeliner.class, DefaultSingleTablePipeline.class)
                .addBehaviour(PortStatisticsDiscovery.class, PortStatisticsDiscoveryImpl.class)
                .addExtension(P4_INFO_TEXT, p4InfoUrl)
                .addExtension(BMV2_JSON, jsonUrl)
                .build();
    }

    private static PiPipelineModel parseP4Info(URL p4InfoUrl) {
        try {
            return P4InfoParser.parse(p4InfoUrl);
        } catch (P4InfoParserException e) {
            throw new RuntimeException(e);
        }
    }
}
