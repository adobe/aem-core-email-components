/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.email.core.components.internal.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class URIExceptionTest {

    @Test
    void constructorTest() {
        URIException uriException = new URIException();
        assertEquals(uriException.getReasonCode(), 0);
        assertNull(uriException.reason);
        uriException = new URIException(5);
        assertEquals(uriException.getReasonCode(), 5);
        uriException = new URIException(7, "important Reason!");
        assertEquals(uriException.getReasonCode(), 7);
        assertEquals(uriException.reason, "important Reason!");
        uriException = new URIException("unimportant Reason!");
        assertEquals(uriException.getReasonCode(), 0);
        assertEquals(uriException.reason, "unimportant Reason!");
    }

}
