/*
 * Copyright 2015 Open Networking Laboratory
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
package org.onosproject.codec.impl;

import java.util.EnumMap;

import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;
import org.onosproject.net.flow.criteria.Criterion;
import org.onosproject.net.flow.criteria.EthCriterion;
import org.onosproject.net.flow.criteria.EthTypeCriterion;
import org.onosproject.net.flow.criteria.IPCriterion;
import org.onosproject.net.flow.criteria.IPDscpCriterion;
import org.onosproject.net.flow.criteria.IPEcnCriterion;
import org.onosproject.net.flow.criteria.IPProtocolCriterion;
import org.onosproject.net.flow.criteria.IPv6ExthdrFlagsCriterion;
import org.onosproject.net.flow.criteria.IPv6FlowLabelCriterion;
import org.onosproject.net.flow.criteria.IPv6NDLinkLayerAddressCriterion;
import org.onosproject.net.flow.criteria.IPv6NDTargetAddressCriterion;
import org.onosproject.net.flow.criteria.IcmpCodeCriterion;
import org.onosproject.net.flow.criteria.IcmpTypeCriterion;
import org.onosproject.net.flow.criteria.Icmpv6CodeCriterion;
import org.onosproject.net.flow.criteria.Icmpv6TypeCriterion;
import org.onosproject.net.flow.criteria.LambdaCriterion;
import org.onosproject.net.flow.criteria.MetadataCriterion;
import org.onosproject.net.flow.criteria.MplsCriterion;
import org.onosproject.net.flow.criteria.OchSignalTypeCriterion;
import org.onosproject.net.flow.criteria.PortCriterion;
import org.onosproject.net.flow.criteria.SctpPortCriterion;
import org.onosproject.net.flow.criteria.TcpPortCriterion;
import org.onosproject.net.flow.criteria.UdpPortCriterion;
import org.onosproject.net.flow.criteria.VlanIdCriterion;
import org.onosproject.net.flow.criteria.VlanPcpCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Criterion codec.
 */
public final class CriterionCodec extends JsonCodec<Criterion> {

    protected static final Logger log =
            LoggerFactory.getLogger(CriterionCodec.class);

    private final EnumMap<Criterion.Type, CriterionTypeFormatter> formatMap;

    public CriterionCodec() {
        formatMap = new EnumMap<>(Criterion.Type.class);

        formatMap.put(Criterion.Type.IN_PORT, new FormatInPort());
        formatMap.put(Criterion.Type.IN_PHY_PORT, new FormatInPort());
        formatMap.put(Criterion.Type.METADATA, new FormatMetadata());
        formatMap.put(Criterion.Type.ETH_DST, new FormatEth());
        formatMap.put(Criterion.Type.ETH_SRC, new FormatEth());
        formatMap.put(Criterion.Type.ETH_TYPE, new FormatEthType());
        formatMap.put(Criterion.Type.VLAN_VID, new FormatVlanVid());
        formatMap.put(Criterion.Type.VLAN_PCP, new FormatVlanPcp());
        formatMap.put(Criterion.Type.IP_DSCP, new FormatIpDscp());
        formatMap.put(Criterion.Type.IP_ECN, new FormatIpEcn());
        formatMap.put(Criterion.Type.IP_PROTO, new FormatIpProto());
        formatMap.put(Criterion.Type.IPV4_SRC, new FormatIp());
        formatMap.put(Criterion.Type.IPV4_DST, new FormatIp());
        formatMap.put(Criterion.Type.TCP_SRC, new FormatTcp());
        formatMap.put(Criterion.Type.TCP_DST, new FormatTcp());
        formatMap.put(Criterion.Type.UDP_SRC, new FormatUdp());
        formatMap.put(Criterion.Type.UDP_DST, new FormatUdp());
        formatMap.put(Criterion.Type.SCTP_SRC, new FormatSctp());
        formatMap.put(Criterion.Type.SCTP_DST, new FormatSctp());
        formatMap.put(Criterion.Type.ICMPV4_TYPE, new FormatIcmpV4Type());
        formatMap.put(Criterion.Type.ICMPV4_CODE, new FormatIcmpV4Code());
        formatMap.put(Criterion.Type.IPV6_SRC, new FormatIp());
        formatMap.put(Criterion.Type.IPV6_DST, new FormatIp());
        formatMap.put(Criterion.Type.IPV6_FLABEL, new FormatIpV6FLabel());
        formatMap.put(Criterion.Type.ICMPV6_TYPE, new FormatIcmpV6Type());
        formatMap.put(Criterion.Type.ICMPV6_CODE, new FormatIcmpV6Code());
        formatMap.put(Criterion.Type.IPV6_ND_TARGET, new FormatV6NDTarget());
        formatMap.put(Criterion.Type.IPV6_ND_SLL, new FormatV6NDTll());
        formatMap.put(Criterion.Type.IPV6_ND_TLL, new FormatV6NDTll());
        formatMap.put(Criterion.Type.MPLS_LABEL, new FormatMplsLabel());
        formatMap.put(Criterion.Type.IPV6_EXTHDR, new FormatIpV6Exthdr());
        formatMap.put(Criterion.Type.OCH_SIGID, new FormatOchSigId());
        formatMap.put(Criterion.Type.OCH_SIGTYPE, new FormatOchSigType());
        formatMap.put(Criterion.Type.DUMMY, new FormatDummyType());

        // Currently unimplemented
        formatMap.put(Criterion.Type.ARP_OP, new FormatUnknown());
        formatMap.put(Criterion.Type.ARP_SPA, new FormatUnknown());
        formatMap.put(Criterion.Type.ARP_TPA, new FormatUnknown());
        formatMap.put(Criterion.Type.ARP_SHA, new FormatUnknown());
        formatMap.put(Criterion.Type.ARP_THA, new FormatUnknown());
        formatMap.put(Criterion.Type.MPLS_TC, new FormatUnknown());
        formatMap.put(Criterion.Type.MPLS_BOS, new FormatUnknown());
        formatMap.put(Criterion.Type.PBB_ISID, new FormatUnknown());
        formatMap.put(Criterion.Type.TUNNEL_ID, new FormatUnknown());
        formatMap.put(Criterion.Type.UNASSIGNED_40, new FormatUnknown());
        formatMap.put(Criterion.Type.PBB_UCA, new FormatUnknown());
        formatMap.put(Criterion.Type.TCP_FLAGS, new FormatUnknown());
        formatMap.put(Criterion.Type.ACTSET_OUTPUT, new FormatUnknown());
        formatMap.put(Criterion.Type.PACKET_TYPE, new FormatUnknown());
    }

    private interface CriterionTypeFormatter {
        ObjectNode encodeCriterion(ObjectNode root, Criterion criterion);
    }

    private static class FormatUnknown implements CriterionTypeFormatter {
        @Override
        public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
            return root;
        }
    }

    private static class FormatInPort implements CriterionTypeFormatter {
        @Override
        public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
            final PortCriterion portCriterion = (PortCriterion) criterion;
            return root.put("port", portCriterion.port().toLong());
        }
    }

    private static class FormatMetadata implements CriterionTypeFormatter {
        @Override
        public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
            final MetadataCriterion metadataCriterion =
                    (MetadataCriterion) criterion;
            return root.put("metadata", metadataCriterion.metadata());
        }
    }

    private static class FormatEth implements CriterionTypeFormatter {
        @Override
        public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
            final EthCriterion ethCriterion = (EthCriterion) criterion;
            return root.put("mac", ethCriterion.mac().toString());
        }
    }

    private static class FormatEthType implements CriterionTypeFormatter {
        @Override
        public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
            final EthTypeCriterion ethTypeCriterion =
                    (EthTypeCriterion) criterion;
            return root.put("ethType", ethTypeCriterion.ethType());
        }
    }

    private static class FormatVlanVid implements CriterionTypeFormatter {
        @Override
        public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
            final VlanIdCriterion vlanIdCriterion =
                    (VlanIdCriterion) criterion;
            return root.put("vlanId", vlanIdCriterion.vlanId().toShort());
        }
    }

    private static class FormatVlanPcp implements CriterionTypeFormatter {
            @Override
            public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
                final VlanPcpCriterion vlanPcpCriterion =
                        (VlanPcpCriterion) criterion;
                return root.put("priority", vlanPcpCriterion.priority());
            }
    }

    private static class FormatIpDscp implements CriterionTypeFormatter {
            @Override
            public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
                final IPDscpCriterion ipDscpCriterion =
                        (IPDscpCriterion) criterion;
                return root.put("ipDscp", ipDscpCriterion.ipDscp());
            }
    }

    private static class FormatIpEcn implements CriterionTypeFormatter {
            @Override
            public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
                final IPEcnCriterion ipEcnCriterion =
                        (IPEcnCriterion) criterion;
                return root.put("ipEcn", ipEcnCriterion.ipEcn());
            }
    }

    private static class FormatIpProto implements CriterionTypeFormatter {
        @Override
        public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
            final IPProtocolCriterion iPProtocolCriterion =
                    (IPProtocolCriterion) criterion;
            return root.put("protocol", iPProtocolCriterion.protocol());
        }
    }

    private static class FormatIp implements CriterionTypeFormatter {
            @Override
            public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
                final IPCriterion iPCriterion = (IPCriterion) criterion;
                return root.put("ip", iPCriterion.ip().toString());
        }
    }

    private static class FormatTcp implements CriterionTypeFormatter {
            @Override
            public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
                final TcpPortCriterion tcpPortCriterion =
                        (TcpPortCriterion) criterion;
                return root.put("tcpPort", tcpPortCriterion.tcpPort());
            }
    }

    private static class FormatUdp implements CriterionTypeFormatter {
            @Override
            public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
                final UdpPortCriterion udpPortCriterion =
                        (UdpPortCriterion) criterion;
                return root.put("udpPort", udpPortCriterion.udpPort());
            }
    }

    private static class FormatSctp implements CriterionTypeFormatter {
        @Override
        public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
            final SctpPortCriterion sctpPortCriterion =
                    (SctpPortCriterion) criterion;
            return root.put("sctpPort", sctpPortCriterion.sctpPort());
        }
    }

    private static class FormatIcmpV4Type implements CriterionTypeFormatter {
        @Override
        public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
            final IcmpTypeCriterion icmpTypeCriterion =
                    (IcmpTypeCriterion) criterion;
            return root.put("icmpType", icmpTypeCriterion.icmpType());
        }
    }

    private static class FormatIcmpV4Code implements CriterionTypeFormatter {
        @Override
        public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
            final IcmpCodeCriterion icmpCodeCriterion =
                    (IcmpCodeCriterion) criterion;
            return root.put("icmpCode", icmpCodeCriterion.icmpCode());
        }
    }

    private static class FormatIpV6FLabel implements CriterionTypeFormatter {
        @Override
        public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
            final IPv6FlowLabelCriterion ipv6FlowLabelCriterion =
                    (IPv6FlowLabelCriterion) criterion;
            return root.put("flowLabel", ipv6FlowLabelCriterion.flowLabel());
        }
    }

    private static class FormatIcmpV6Type implements CriterionTypeFormatter {
        @Override
        public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
            final Icmpv6TypeCriterion icmpv6TypeCriterion =
                    (Icmpv6TypeCriterion) criterion;
            return root.put("icmpv6Type", icmpv6TypeCriterion.icmpv6Type());
        }
    }

    private static class FormatIcmpV6Code implements CriterionTypeFormatter {
        @Override
        public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
            final Icmpv6CodeCriterion icmpv6CodeCriterion =
                    (Icmpv6CodeCriterion) criterion;
            return root.put("icmpv6Code", icmpv6CodeCriterion.icmpv6Code());
        }
    }

    private static class FormatV6NDTarget implements CriterionTypeFormatter {
        @Override
        public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
            final IPv6NDTargetAddressCriterion ipv6NDTargetAddressCriterion
                = (IPv6NDTargetAddressCriterion) criterion;
            return root.put("targetAddress", ipv6NDTargetAddressCriterion.targetAddress().toString());
        }
    }

    private static class FormatV6NDTll implements CriterionTypeFormatter {
        @Override
        public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
            final IPv6NDLinkLayerAddressCriterion ipv6NDLinkLayerAddressCriterion
                = (IPv6NDLinkLayerAddressCriterion) criterion;
            return root.put("mac", ipv6NDLinkLayerAddressCriterion.mac().toString());
        }
    }

    private static class FormatMplsLabel implements CriterionTypeFormatter {
        @Override
        public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
            final MplsCriterion mplsCriterion =
                    (MplsCriterion) criterion;
            return root.put("label", mplsCriterion.label().toInt());
        }
    }

    private static class FormatIpV6Exthdr implements CriterionTypeFormatter {
        @Override
        public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
            final IPv6ExthdrFlagsCriterion exthdrCriterion =
                    (IPv6ExthdrFlagsCriterion) criterion;
            return root.put("exthdrFlags", exthdrCriterion.exthdrFlags());
        }
    }

    private static class FormatOchSigId implements CriterionTypeFormatter {
        @Override
        public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
            final LambdaCriterion lambdaCriterion =
                    (LambdaCriterion) criterion;
            return root.put("lambda", lambdaCriterion.lambda());
        }
    }

    private static class FormatOchSigType implements CriterionTypeFormatter {
        @Override
        public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
            final OchSignalTypeCriterion ochSignalTypeCriterion =
                    (OchSignalTypeCriterion) criterion;
            return root.put("ochSignalType", ochSignalTypeCriterion.signalType().name());
        }
    }

    private class FormatDummyType implements CriterionTypeFormatter {

        @Override
        public ObjectNode encodeCriterion(ObjectNode root, Criterion criterion) {
            checkNotNull(criterion, "Criterion cannot be null");

            return root.put("type", criterion.type().toString());

        }
    }

    @Override
    public ObjectNode encode(Criterion criterion, CodecContext context) {
        checkNotNull(criterion, "Criterion cannot be null");

        final ObjectNode result = context.mapper().createObjectNode()
                .put("type", criterion.type().toString());

        CriterionTypeFormatter formatter =
                checkNotNull(
                        formatMap.get(criterion.type()),
                        "No formatter found for criterion type "
                                + criterion.type().toString());

        return formatter.encodeCriterion(result, criterion);
    }


}
