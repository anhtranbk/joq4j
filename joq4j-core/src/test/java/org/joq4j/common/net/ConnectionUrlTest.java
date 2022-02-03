package org.joq4j.common.net;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class ConnectionUrlTest {

    @Test
    public void addParameters1() {
        ConnectionUrl uri = new ConnectionUrl()
                .scheme("kafka")
                .host("localhost")
                .port(9092)
                .addParameters("autoReconnect", "true")
                .addParameters("secure", "true", "valueSerializer", "byte", "encoding", "utf8");

        Map<String, String> params = uri.parameters();
        assertEquals("true", params.get("secure"));
        assertEquals("true", params.get("autoReconnect"));
        assertEquals("byte", params.get("valueSerializer"));
        assertEquals("utf8", params.get("encoding"));
    }

    @Test
    public void addParameters2() {
        try {
            ConnectionUrl.parseFromString("kafka://localhost:9092")
                    .addParameters("autoReconnect", "true")
                    .addParameters("secure", "true", "valueSerializer", "string", "");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
            return;
        }
        fail();
    }

    @Test
    public void asString1() {
        ConnectionUrl uri = new ConnectionUrl()
                .scheme("redis")
                .host("192.168.1.199");

        assertEquals("redis://192.168.1.199", uri.asString(false));

        uri.port(6379);
        assertEquals("redis://192.168.1.199:6379", uri.asString(false));

        uri.database("1");
        assertEquals("redis://192.168.1.199:6379/1", uri.asString(false));

        uri.password("abc123");
        assertEquals("redis://192.168.1.199:6379/1", uri.asString(false));
        assertEquals("redis://:abc123@192.168.1.199:6379/1", uri.asString(true));
    }

    @Test
    public void asString2() {
        ConnectionUrl uri = new ConnectionUrl()
                .scheme("mysql")
                .host("localhost")
                .username("admin")
                .password("admin123")
                .parameters(ImmutableMap.of(
                        "autoReconnect", "true",
                        "useSSL", "true"
                ));

        assertEquals("mysql://admin:admin123@localhost?autoReconnect=true&useSSL=true",
                uri.asString(true));
        assertEquals("mysql://localhost?autoReconnect=true&useSSL=true",
                uri.asString(false));

        uri.port(3306).database("shopdb");
        assertEquals("mysql://admin:admin123@localhost:3306/shopdb?autoReconnect=true&useSSL=true",
                uri.asString(true));
        assertEquals("mysql://localhost:3306/shopdb?autoReconnect=true&useSSL=true",
                uri.asString(false));
    }

    @Test
    public void parseFromString1() {
        ConnectionUrl uri = ConnectionUrl.parseFromString("redis://localhost:6379/0");

        assertEquals("redis", uri.scheme());
        assertEquals("localhost", uri.host());
        assertEquals(6379, uri.port());
        assertEquals("0", uri.database());
        assertNull(uri.username());
        assertNull(uri.password());
        assertEquals(0, uri.parameters().size());
    }

    @Test
    public void parseFromString2() {
        ConnectionUrl uri = ConnectionUrl.parseFromString(
                "mysql://192.168.1.100:3306/crawlerdb" +
                        "?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&useSSL=false");

        assertEquals("mysql", uri.scheme());
        assertEquals("192.168.1.100", uri.host());
        assertEquals(3306, uri.port());
        assertEquals("crawlerdb", uri.database());
        assertNull(uri.username());
        assertNull(uri.password());

        Map<String, String> params = uri.parameters();
        assertEquals("true", params.get("autoReconnect"));
        assertEquals("true", params.get("useUnicode"));
        assertEquals("utf-8", params.get("characterEncoding"));
        assertEquals("false", params.get("useSSL"));
    }

    @Test
    public void parseFromString3() {
        ConnectionUrl uri = ConnectionUrl.parseFromString(
                "mongodb://sieunhan:sieunhan2022@192.168.1.101/shopdb" +
                        "?authSource=admin&authMechanism=SCRAM-SHA-1");

        assertEquals("mongodb", uri.scheme());
        assertEquals("192.168.1.101", uri.host());
        assertEquals(0, uri.port());
        assertEquals("shopdb", uri.database());
        assertEquals("sieunhan", uri.username());
        assertEquals("sieunhan2022", uri.password());

        Map<String, String> params = uri.parameters();
        assertEquals("admin", params.get("authSource"));
        assertEquals("SCRAM-SHA-1", params.get("authMechanism"));
    }

    @Test
    public void parseFromString4() {
        ConnectionUrl uri = ConnectionUrl.parseFromString("redis://:sieunhan2022@localhost:6379?evictStrategy=lfu");

        assertEquals("redis", uri.scheme());
        assertEquals("localhost", uri.host());
        assertEquals(6379, uri.port());
        assertNull(uri.database());
        assertEquals("", uri.username());
        assertEquals("sieunhan2022", uri.password());
        assertEquals("lfu", uri.parameters().get("evictStrategy"));
    }

    @Test
    public void parseFromString5() {
        try {
            ConnectionUrl.parseFromString("redis://");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
            return;
        }
        fail();
    }

    @Test
    public void parseFromString6() {
        try {
            ConnectionUrl.parseFromString("redis://");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
            return;
        }
        fail();
    }

    @Test
    public void parseFromString7() {
        try {
            ConnectionUrl.parseFromString("redis://");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
            return;
        }
        fail();
    }
}