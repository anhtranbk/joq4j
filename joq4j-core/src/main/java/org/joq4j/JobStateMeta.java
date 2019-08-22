package org.joq4j;

import lombok.Data;

import java.util.Date;

public @Data
class JobStateMeta {

    private Date enqueuedAt;

    private Date startedAt;

    private Date finishedAt;
}
