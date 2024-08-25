/**
 * Copyright (c) 2010-2024 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.synologysurveillancestation.internal;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * The {@link SynoCameraConfig} is class for handling the camera Thing configuration
 *
 * @author Pavion - Initial contribution
 */
@NonNullByDefault
public class SynoCameraConfig {
    private int refreshRateSnapshot = 10;
    private int refreshRateEvents = 3;
    private int refreshRateMdParam = 0;
    private int snapshotStreamId = 1;

    /**
     * @return refreshRateSnapshot the refreshRateSnapshot to set
     */
    public int getRefreshRateSnapshot() {
        return refreshRateSnapshot;
    }

    /**
     * @param refreshRateSnapshot the refreshRateSnapshot to set
     */
    public void setRefreshRateSnapshot(int refreshRateSnapshot) {
        this.refreshRateSnapshot = refreshRateSnapshot;
    }

    /**
     * @return refreshRateEvents
     */
    public int getRefreshRateEvents() {
        return refreshRateEvents;
    }

    /**
     * @param refreshRateEvents the refreshRateEvents to set
     */
    public void setRefreshRateEvents(int refreshRateEvents) {
        this.refreshRateEvents = refreshRateEvents;
    }

    /**
     * @param refreshRateMdParam the refreshRateMdParam to set
     */
    public void setRefreshRateMdParam(int refreshRateMdParam) {
        this.refreshRateMdParam = refreshRateMdParam;
    }

    /**
     * @return refreshRateMdParam
     */
    public int getRefreshRateMdParam() {
        return refreshRateMdParam;
    }

    /**
     * @param refreshRateMdParam the refreshRateMdParam to set
     */
    public void setSnapshotStreamId(int snapshotStreamId) {
        this.snapshotStreamId = snapshotStreamId;
    }

    /**
     * @return refreshRateMdParam
     */
    public int getSnapshotStreamId() {
        return snapshotStreamId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(refreshRateEvents, refreshRateMdParam, refreshRateSnapshot, snapshotStreamId);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SynoCameraConfig other = (SynoCameraConfig) obj;
        return refreshRateEvents == other.refreshRateEvents && refreshRateMdParam == other.refreshRateMdParam
                && refreshRateSnapshot == other.refreshRateSnapshot && snapshotStreamId == other.snapshotStreamId;
    }

    @Override
    public String toString() {
        return "SynoCameraConfig [refreshRateSnapshot=" + refreshRateSnapshot + ", refreshRateEvents="
                + refreshRateEvents + ", refreshRateMdParam=" + refreshRateMdParam + ", snapshotStreamId="
                + snapshotStreamId + "]";
    }
}
