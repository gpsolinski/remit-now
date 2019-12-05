/*
 * Copyright (c) 2019 Grzegorz Solinski. All rights reserved.
 */

package com.gpsolinski.remitnow.domain;

import java.io.Serializable;

public interface Identifiable<ID extends Serializable> {
    ID getId();
}
