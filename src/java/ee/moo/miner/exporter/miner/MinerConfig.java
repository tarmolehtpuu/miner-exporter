package ee.moo.miner.exporter.miner;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class MinerConfig {

    private String id;

    private MinerType type;

    private String host;

    private int port;

    public static List<MinerConfig> createFromEnvironment() {
        return createFromEnvironment(System.getenv());
    }

    public static List<MinerConfig> createFromEnvironment(Map<String, String> env) {
        var configs = new ArrayList<MinerConfig>();

        for (int i = 0; hasConfig(env, i); i++) {
            configs.add(MinerConfig.builder()
                .id(getId(env, i))
                .type(getType(env, i))
                .host(getHost(env, i))
                .port(getPort(env, i))
                .build()
            );
        }

        return configs;
    }

    private static boolean hasConfig(Map<String, String> env, int n) {
        return hasId(env, n) && hasType(env, n) && hasHost(env, n) && hasPort(env, n);
    }

    private static boolean hasId(Map<String, String> env, int n) {
        return env.containsKey(String.format("MINER_%d_ID", n));
    }

    private static String getId(Map<String, String> env, int n) {
        return env.get(String.format("MINER_%d_ID", n));
    }

    private static boolean hasType(Map<String, String> env, int n) {
        return env.containsKey(String.format("MINER_%d_TYPE", n));
    }

    private static MinerType getType(Map<String, String> env, int n) {
        return MinerType.valueOf(env.get(String.format("MINER_%d_TYPE", n)));
    }

    private static boolean hasHost(Map<String, String> env, int n) {
        return env.containsKey(String.format("MINER_%d_HOST", n));
    }

    private static String getHost(Map<String, String> env, int n) {
        return env.get(String.format("MINER_%d_HOST", n));
    }

    private static boolean hasPort(Map<String, String> env, int n) {
        return env.containsKey(String.format("MINER_%d_PORT", n));
    }

    private static int getPort(Map<String, String> env, int n) {
        return Integer.parseInt(env.get(String.format("MINER_%d_PORT", n)));
    }
}
