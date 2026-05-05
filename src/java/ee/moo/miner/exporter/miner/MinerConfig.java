package ee.moo.miner.exporter.miner;

import ee.moo.miner.exporter.client.ClientException;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.client.Authentication;
import org.eclipse.jetty.client.BasicAuthentication;
import org.eclipse.jetty.client.DigestAuthentication;
import org.eclipse.jetty.client.HttpClient;
import util.StringUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class MinerConfig {

    public enum AuthMode {
        NONE,
        BASIC,
        DIGEST
    }

    private String id;

    private MinerType type;

    private URI uri;

    private AuthMode auth = AuthMode.NONE;

    private String username;

    private String password;

    private Duration connectTimeout = Duration.ofMillis(2000);

    private Duration readTimeout = Duration.ofMillis(2000);

    private int readBufferSize = 8192;

    public void validate() {
        if (StringUtil.isEmpty(id)) {
            throw new ClientException("Miner ID is required");
        }

        if (StringUtil.isEmpty(uri.toString())) {
            throw new ClientException("Miner URL is required (miner=%s)", id);
        }

        if (auth == AuthMode.BASIC || auth == AuthMode.DIGEST) {
            if (!uri.toString().startsWith("http://") && !uri.toString().startsWith("https://")) {
                throw new ClientException("AuthMode.%s is only supported for http|https URL-s (miner=%s)", auth, id);
            }


            if (StringUtil.isEmpty(username)) {
                throw new ClientException("Miner username is required for AuthMode.%s (miner=%s)", auth, id);
            }

            if (StringUtil.isEmpty(password)) {
                throw new ClientException("Password is required for AuthMode.%s (miner=%s)", auth, id);
            }
        }

        if (readBufferSize < 1024) {
            log.warn("Too low read buffer size: {} (miner={}). Defaulting to 1024", readBufferSize, id);
        }
    }

    public HttpClient createHttpClient() {
        var base = uri.toString();
        if (!base.startsWith("http://") && !base.startsWith("https://")) {
            throw new ClientException("Unable to create http client for URI=%s (miner=%s)", uri, id);
        }

        if (!base.endsWith("/")) {
            base = String.format("%s/", base);
        }

        try {
            var client = new HttpClient();

            client.setConnectTimeout(connectTimeout.toMillis());
            client.setResponseBufferSize(readBufferSize);
            client.start();

            if (auth == MinerConfig.AuthMode.BASIC) {
                client
                    .getAuthenticationStore()
                    .addAuthentication(new BasicAuthentication(
                        new URI(base),
                        Authentication.ANY_REALM,
                        getUsername(),
                        getPassword()
                    ));
            }

            if (auth == MinerConfig.AuthMode.DIGEST) {
                client
                    .getAuthenticationStore()
                    .addAuthentication(new DigestAuthentication(
                        new URI(base),
                        Authentication.ANY_REALM,
                        username,
                        password
                    ));
            }

            return client;

        } catch (Exception e) {
            throw new MinerException(e.getMessage(), e);
        }
    }

    public static List<MinerConfig> createFromEnvironment() {
        return createFromEnvironment(System.getenv());
    }

    public static List<MinerConfig> createFromEnvironment(Map<String, String> env) {
        var configs = new ArrayList<MinerConfig>();

        for (int i = 0; hasConfig(env, i); i++) {
            var config = new MinerConfig();
            config.setId(getId(env, i));
            config.setType(getType(env, i));
            config.setUri(getUri(env, i));

            if (hasAuth(env, i)) {
                config.setAuth(getAuth(env, i));
            }

            if (hasUsername(env, i)) {
                config.setUsername(getUsername(env, i));
            }

            if (hasPassword(env, i)) {
                config.setPassword(getPassword(env, i));
            }

            if (hasConnectTimeout(env, i)) {
                config.setConnectTimeout(getConnectTimeout(env, i));
            }

            if (hasReadTimeout(env, i)) {
                config.setReadTimeout(getReadTimeout(env, i));
            }

            if (hasReadBufferSize(env, i)) {
                config.setReadBufferSize(getReadBufferSize(env, i));
            }

            config.validate();
            configs.add(config);
        }

        return configs;
    }


    private static boolean hasConfig(Map<String, String> env, int n) {
        return hasId(env, n) && hasType(env, n) && hasUri(env, n);
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

    private static boolean hasUri(Map<String, String> env, int n) {
        return env.containsKey(String.format("MINER_%d_URI", n));
    }

    private static URI getUri(Map<String, String> env, int n) {
        try {
            return new URI(env.get(String.format("MINER_%d_URI", n)));
        } catch (URISyntaxException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }

    private static boolean hasAuth(Map<String, String> env, int n) {
        return env.containsKey(String.format("MINER_%d_AUTH", n));
    }

    private static AuthMode getAuth(Map<String, String> env, int n) {
        return AuthMode.valueOf(env.get(String.format("MINER_%d_AUTH", n)));
    }

    private static boolean hasUsername(Map<String, String> env, int n) {
        return env.containsKey(String.format("MINER_%d_USERNAME", n));
    }

    private static String getUsername(Map<String, String> env, int n) {
        return env.get(String.format("MINER_%d_USERNAME", n));
    }

    private static boolean hasPassword(Map<String, String> env, int n) {
        return env.containsKey(String.format("MINER_%d_PASSWORD", n));
    }

    private static String getPassword(Map<String, String> env, int n) {
        return env.get(String.format("MINER_%d_PASSWORD", n));
    }

    private static boolean hasConnectTimeout(Map<String, String> env, int n) {
        return env.containsKey(String.format("MINER_%d_CONNECT_TIMEOUT", n));
    }

    private static Duration getConnectTimeout(Map<String, String> env, int n) {
        return Duration.parse(env.get(String.format("MINER_%d_CONNECT_TIMEOUT", n)));
    }

    private static boolean hasReadTimeout(Map<String, String> env, int n) {
        return env.containsKey(String.format("MINER_%d_READ_TIMEOUT", n));
    }

    private static Duration getReadTimeout(Map<String, String> env, int n) {
        return Duration.parse(env.get(String.format("MINER_%d_READ_TIMEOUT", n)));
    }

    private static boolean hasReadBufferSize(Map<String, String> env, int n) {
        return env.containsKey(String.format("MINER_%d_READ_BUFFER_SIZE", n));
    }

    private static int getReadBufferSize(Map<String, String> env, int n) {
        return Integer.parseInt(env.get(String.format("MINER_%d_READ_BUFFER_SIZE", n)));
    }
}
