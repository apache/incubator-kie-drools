/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.test.utils;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.SecureRandom;

import javax.net.ServerSocketFactory;

public final class SocketUtils {

    /**
     * Minimal port to use.
     *
     * @see <a href="https://en.wikipedia.org/wiki/List_of_TCP_and_UDP_port_numbers#Dynamic.2C_private_or_ephemeral_ports">Dynamic, private or ephemeral ports</a>
     */
    static final int PORT_RANGE_MIN = 49152;
    static final int PORT_RANGE_MAX = 65535;
    private static final SecureRandom RND = new SecureRandom();

    private SocketUtils() {
    }

    public static int findAvailablePort() {
        int portRange = PORT_RANGE_MAX - PORT_RANGE_MIN;
        int candidatePort;
        int searchCounter = 0;
        do {
            if (searchCounter > portRange) {
                throw new IllegalStateException(String.format(
                        "Could not find an available port in the range [%d, %d] after %d attempts",
                        PORT_RANGE_MIN, PORT_RANGE_MAX, searchCounter));
            }
            candidatePort = findRandomPort(PORT_RANGE_MIN, PORT_RANGE_MAX);
            searchCounter++;
        } while (!isPortAvailable(candidatePort));

        return candidatePort;
    }

    private static int findRandomPort(int minPort, int maxPort) {
        int portRange = maxPort - minPort;
        return minPort + RND.nextInt(portRange + 1);
    }

    private static boolean isPortAvailable(int port) {
        try {
            ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(port, 1, InetAddress.getByName("localhost"));
            serverSocket.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
