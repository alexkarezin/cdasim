/*
 * Copyright (c) 2020 Fraunhofer FOKUS and others. All rights reserved.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contact: mosaic@fokus.fraunhofer.de
 */

package org.eclipse.mosaic.interactions.mapping;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import org.eclipse.mosaic.lib.objects.mapping.TrafficLightMapping;
import org.eclipse.mosaic.lib.objects.trafficlight.TrafficLightGroup;
import org.eclipse.mosaic.rti.api.Interaction;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collection;
import java.util.List;

/**
 * This extension of {@link Interaction} contains
 * the reference to a TrafficLightGroup object that describes the traffic lights forming the group and all available programs
 * and collection of lanes that are controlled by this group.
 *
 * This interaction should be generated by the mapping ambassador at startup.
 */
public final class TrafficLightRegistration extends Interaction {

    private static final long serialVersionUID = 1L;

    /**
     * String identifying the type of this interaction.
     */
    public final static String TYPE_ID = createTypeIdentifier(TrafficLightRegistration.class);

    /**
     * Reference to the traffic lights in the simulation.
     */
    private final TrafficLightGroup trafficLightGroup;

    /**
     * List of lanes controlled by the traffic light.
     */
    private final Collection<String> ctrLanes;

    /**
     * The traffic light.
     */
    private final TrafficLightMapping trafficLightMapping;

    /**
     * Constructor.
     *
     * @param time              Timestamp of this interaction, unit: [ns]
     * @param name              internal traffic light simulation unit identifier
     * @param group             traffic light group identifier
     * @param applications      installed applications of the traffic light
     * @param trafficLightGroup reference to the traffic light group in the simulation
     * @param controlledLanes   lanes controlled by the traffic light group
     */
    public TrafficLightRegistration(final long time, final String name, final String group, final List<String> applications,
                                    final TrafficLightGroup trafficLightGroup, final Collection<String> controlledLanes) {
        super(time);
        this.trafficLightMapping = new TrafficLightMapping(name, group, applications, trafficLightGroup.getFirstPosition());
        this.trafficLightGroup = trafficLightGroup;
        this.ctrLanes = controlledLanes;
    }

    public TrafficLightGroup getTrafficLightGroup() {
        return this.trafficLightGroup;
    }

    public Collection<String> getControlledLanes() {
        return ctrLanes;
    }

    public TrafficLightMapping getMapping() {
        return trafficLightMapping;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(7, 71)
                .append(trafficLightGroup)
                .append(ctrLanes)
                .append(trafficLightMapping)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        TrafficLightRegistration rhs = (TrafficLightRegistration) obj;
        return new EqualsBuilder()
                .append(this.trafficLightGroup, rhs.trafficLightGroup)
                .append(this.ctrLanes, rhs.ctrLanes)
                .append(this.trafficLightMapping, rhs.trafficLightMapping)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append("trafficLightGroup", trafficLightGroup)
                .append("trafficLightMapping", trafficLightMapping)
                .append("ctrLanes", ctrLanes)
                .toString();
    }
}
