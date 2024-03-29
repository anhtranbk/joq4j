package org.joq4j.common.net;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.joq4j.common.utils.Strings;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
@Accessors(chain = true, fluent = true)
public @Data class ConnectionUrl {
    private String scheme;
    private String host;
    private int port;
    private String username;
    private String password;
    private String database;

    @Getter(AccessLevel.MODULE)
    private Map<String, String> parameters = new TreeMap<>();

    @Override
    public String toString() {
        return this.asString(false);
    }

    public ConnectionUrl addParameters(String... params) {
        Preconditions.checkArgument(params.length % 2 == 0,
                "Invalid number parameters, must be an even number");
        for (int i = 0; i < params.length; i += 2) {
            this.parameters.put(params[i], params[i+1]);
        }
        return this;
    }

    public String asString(boolean withCredentials) {
        StringBuilder sb = new StringBuilder();
        sb.append(scheme).append("://");
        if (withCredentials && Strings.isNonEmpty(password)) {
            sb.append(username != null ? username : "");
            sb.append(":").append(password).append("@");
        }
        sb.append(host);
        if (port != 0) {
            sb.append(":").append(port);
        }
        if (database != null) {
            sb.append("/").append(database);
        }
        if (!parameters.isEmpty()) {
            int i = 0;
            for (Map.Entry<String, String> e : parameters.entrySet()) {
                String separator = i++ == 0 ? "?" : "&";
                sb.append(separator);
                sb.append(e.getKey()).append("=").append(e.getValue());
            }
        }
        return sb.toString();
    }

    public static ConnectionUrl parseFromString(String connectionString) {
        try {
            ConnectionUrl uri = new ConnectionUrl();

            String[] p1 = connectionString.split("://");
            uri.scheme(p1[0]);
            String[] p2 = p1[1].split("@");

            String tmp;
            if (p2.length > 1) {
                String[] p3 = p2[0].split(":");
                uri.username(p3[0]);
                uri.password(p3[1]);
                tmp = p2[1];
            } else {
                tmp = p2[0];
            }

            String[] p3 = tmp.split("\\?");
            String[] p4 = p3[0].split("/");
            if (p4.length > 1) {
                uri.database(p4[1]);
            }
            String[] p5 = p4[0].split(":");
            if (p5.length > 1) {
                uri.port(Integer.parseInt(p5[1]));
            }
            uri.host(p5[0]);

            if (p3.length > 1) {
                String[] p6 = p3[1].split("&");
                Map<String, String> m = new HashMap<>(p6.length);
                for (String q : p6) {
                    String[] p7 = q.split("=");
                    m.put(p7[0], p7[1]);
                }
                uri.parameters(m);
            }

            return uri;
        } catch (Exception e) {
            throw new IllegalArgumentException("Parse ConnectionUri failed", e);
        }
    }
}
