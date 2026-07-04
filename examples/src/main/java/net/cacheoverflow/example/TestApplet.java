/*
 * Copyright 2026 Cedric Hammes <contact@cach30verfl0w.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.cacheoverflow.example;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISOException;

public final class TestApplet extends Applet {

    private TestApplet(byte[] bArray, short bOffset, byte bLength) {
        register();
    }

    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new TestApplet(bArray, bOffset, bLength);
    }

    @Override
    public void process(APDU command) throws ISOException {
        if (selectingApplet()) return;

        byte[] buffer = command.getBuffer();
        short bytesRead = command.setIncomingAndReceive();
        command.setOutgoing();
        command.setOutgoingLength(bytesRead);
        command.sendBytes((short) 0, bytesRead);
    }
}
