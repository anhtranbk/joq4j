package org.joq4j.backend;

import org.joq4j.config.Config;

public interface BackendFactory {

    StorageBackend createBackend(Config config, String url);
}
