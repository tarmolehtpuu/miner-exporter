package ee.moo.miner.exporter.client.cgminer.response;

import ee.moo.miner.exporter.client.cgminer.model.CGMinerStatus;

public interface CGMinerResponseOld {

    CGMinerStatus getStatus();

    default boolean isError() {
        if (getStatus() == null) {
            return true;
        }

        if (getStatus().getStatus() == null) {
            return true;
        }

        return !getStatus().getStatus().equals("S");
    }

    default String getError() {
        if (!isError()) {
            return null;
        }

        if (getStatus() == null) {
            return "Unknown error";
        }

        if (getStatus().getMessage() == null) {
            return "Unknown error";
        }

        return getStatus().getMessage();
    }
}
