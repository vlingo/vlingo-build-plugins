// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.maven.codegen;

import java.util.LinkedHashSet;
import java.util.Set;

public final class Data {
    public static final Set<String> FAKE_CLASSES = new LinkedHashSet<>();
    static {
        FAKE_CLASSES.add("io.vlingo.nativeexample.ping.Ping");
        FAKE_CLASSES.add("io.vlingo.nativeexample.ping.Ping__Proxy");
        FAKE_CLASSES.add("io.vlingo.nativeexample.pong.Pong");
        FAKE_CLASSES.add("io.vlingo.nativeexample.pong.Pong__Proxy");
        FAKE_CLASSES.add("io.some.vlingo.app.infra.persistence.StateStore__Proxy");
        FAKE_CLASSES.add("io.some.vlingo.app.infra.persistence.StateStore");
        FAKE_CLASSES.add("io.some.vlingo.app.infra.persistence.DispatcherControl__Proxy");
        FAKE_CLASSES.add("io.some.vlingo.app.infra.persistence.DispatcherControl");
    }
}
