package dev.openfga.autoconfigure;

import dev.openfga.sdk.telemetry.Counters;
import dev.openfga.sdk.telemetry.Histograms;
import dev.openfga.sdk.telemetry.Metric;
import java.util.Objects;

public enum TelemetryMetric {
    CREDENTIALS_REQUEST(Counters.CREDENTIALS_REQUEST),
    QUERY_DURATION(Histograms.QUERY_DURATION),
    REQUEST_DURATION(Histograms.REQUEST_DURATION);

    private final Metric metric;

    TelemetryMetric(Metric metric) {
        this.metric = Objects.requireNonNull(metric);
    }

    Metric getMetric() {
        return metric;
    }
}
