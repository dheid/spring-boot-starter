package dev.openfga.autoconfigure;

import dev.openfga.sdk.telemetry.Attribute;
import dev.openfga.sdk.telemetry.Attributes;
import java.util.Objects;

public enum TelemetryAttribute {
    FGA_CLIENT_REQUEST_CLIENT_ID(Attributes.FGA_CLIENT_REQUEST_CLIENT_ID),
    FGA_CLIENT_REQUEST_METHOD(Attributes.FGA_CLIENT_REQUEST_METHOD),
    FGA_CLIENT_REQUEST_MODEL_ID(Attributes.FGA_CLIENT_REQUEST_MODEL_ID),
    FGA_CLIENT_REQUEST_STORE_ID(Attributes.FGA_CLIENT_REQUEST_STORE_ID),
    FGA_CLIENT_RESPONSE_MODEL_ID(Attributes.FGA_CLIENT_RESPONSE_MODEL_ID),
    HTTP_HOST(Attributes.HTTP_HOST),
    HTTP_REQUEST_METHOD(Attributes.HTTP_REQUEST_METHOD),
    HTTP_REQUEST_RESEND_COUNT(Attributes.HTTP_REQUEST_RESEND_COUNT),
    HTTP_RESPONSE_STATUS_CODE(Attributes.HTTP_RESPONSE_STATUS_CODE),
    URL_FULL(Attributes.URL_FULL),
    URL_SCHEME(Attributes.URL_SCHEME),
    USER_AGENT(Attributes.USER_AGENT);

    private final Attribute attribute;

    TelemetryAttribute(Attribute attribute) {
        this.attribute = Objects.requireNonNull(attribute);
    }

    Attribute getAttribute() {
        return attribute;
    }
}
