package ee.moo.miner.exporter.miner;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class MinerConfig {

    private String id;

    private MinerType type;

    private String host;

    private int port;

    public static List<MinerConfig> createFromEnvironment() {
        var configs = new ArrayList<MinerConfig>();

        for (int i = 0; hasConfig(i); i++) {
            configs.add(MinerConfig.builder()
                .id(getId(i))
                .type(getType(i))
                .host(getHost(i))
                .port(getPort(i))
                .build()
            );
        }

        return configs;
    }

    private static boolean hasConfig(int n) {
        return hasId(n) && hasType(n) && hasHost(n) && hasPort(n);
    }

    private static boolean hasId(int n) {
        return System.getenv(String.format("MINER_%d_ID", n)) != null;
    }

    private static String getId(int n) {
        return System.getenv(String.format("MINER_%d_ID", n));
    }

    private static boolean hasType(int n) {
        return System.getenv(String.format("MINER_%d_TYPE", n)) != null;
    }

    private static MinerType getType(int n) {
        return MinerType.valueOf(System.getenv(String.format("MINER_%d_TYPE", n)));
    }

    private static boolean hasHost(int n) {
        return System.getenv(String.format("MINER_%d_HOST", n)) != null;
    }

    private static String getHost(int n) {
        return System.getenv(String.format("MINER_%d_HOST", n));
    }

    private static boolean hasPort(int n) {
        return System.getenv(String.format("MINER_%d_PORT", n)) != null;
    }

    private static int getPort(int n) {
        return Integer.parseInt(System.getenv(String.format("MINER_%d_PORT", n)));
    }
}
