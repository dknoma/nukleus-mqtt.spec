/**
 * Copyright 2016-2019 The Reaktivity Project
 *
 * The Reaktivity Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.reaktivity.specification.mqtt.internal;

import static org.junit.Assert.assertEquals;
import static org.kaazing.k3po.lang.internal.el.ExpressionFactoryUtils.newExpressionFactory;

import javax.el.ELContext;
import javax.el.ExpressionFactory;

import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Before;
import org.junit.Test;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;

import org.reaktivity.specification.mqtt.internal.types.control.MqttRouteExFW;
import org.reaktivity.specification.mqtt.internal.types.stream.MqttBeginExFW;
import org.reaktivity.specification.mqtt.internal.types.stream.MqttDataExFW;
import org.reaktivity.specification.mqtt.internal.types.stream.MqttEndExFW;
import org.reaktivity.specification.mqtt.internal.types.stream.MqttAbortExFW;

public class MqttFunctionsTest
{
    private ExpressionFactory factory;
    private ELContext ctx;

    @Before
    public void setUp() throws Exception
    {
        factory = newExpressionFactory();
        ctx = new ExpressionContext();
    }

    @Test
    public void shouldGetMapper()
    {
        MqttFunctions.Mapper mapper = new MqttFunctions.Mapper();
        assertEquals("mqtt", mapper.getPrefixName());
    }

    @Test
    public void shouldEncodeMqttRouteEx()
    {
        final byte[] array = MqttFunctions.routeEx()
                .topic("sensor/one")
                .build();

        DirectBuffer buffer = new UnsafeBuffer(array);
        MqttRouteExFW mqttRouteEx = new MqttRouteExFW().wrap(buffer, 0, buffer.capacity());
        assertEquals(mqttRouteEx.topic().asString(), "sensor/one");
    }

    @Test
    public void shouldEncodeMqttBeginExtAsSubscribe()
    {
        final byte[] array = MqttFunctions.beginEx()
                .typeId(0)
                .role("RECEIVER")
                .clientId("client")
                .topic("sensor/one")
                .topicOptions(0)
                .build();

        DirectBuffer buffer = new UnsafeBuffer(array);
        MqttBeginExFW mqttBeginEx = new MqttBeginExFW().wrap(buffer, 0, buffer.capacity());

        assertEquals("RECEIVER", mqttBeginEx.role().toString());
        assertEquals("client", mqttBeginEx.clientId().asString());
        assertEquals("sensor/one", mqttBeginEx.topic().asString());
        assertEquals(0, mqttBeginEx.topicOptions());
    }

    @Test
    public void shouldEncodeMqttBeginExAsSuback()
    {
        final byte[] array = MqttFunctions.beginEx()
                .typeId(0)
                .role("SENDER")
                .clientId("client")
                .topic("sensor/one")
                .topicOptions(0)
                .build();

        DirectBuffer buffer = new UnsafeBuffer(array);
        MqttBeginExFW mqttBeginEx = new MqttBeginExFW().wrap(buffer, 0, buffer.capacity());

        assertEquals("SENDER", mqttBeginEx.role().toString());
        assertEquals("client", mqttBeginEx.clientId().asString());
        assertEquals("sensor/one", mqttBeginEx.topic().asString());
        assertEquals(0, mqttBeginEx.topicOptions());
    }

    @Test
    public void shouldEncodeMqttDataEx()
    {
        final byte[] array = MqttFunctions.dataEx()
                .typeId(0)
                .topic("sensor/one")
                .messageExpiry(15)
                .contentType("message")
                .payloadFormat("TEXT")
                .respTopic("sensor/one")
                .respTopicCorrelationData("info")
                .build();

        DirectBuffer buffer = new UnsafeBuffer(array);
        MqttDataExFW mqttDataEx = new MqttDataExFW().wrap(buffer, 0, buffer.capacity());

        assertEquals(0, mqttDataEx.typeId());
        assertEquals("sensor/one", mqttDataEx.topic().asString());
        assertEquals(15, mqttDataEx.messageExpiry());
        assertEquals("message", mqttDataEx.contentType().asString());
        assertEquals("TEXT", mqttDataEx.payloadFormat().toString());
        assertEquals("sensor/one",  mqttDataEx.respTopic().asString());
        assertEquals("MQTT_BINARY [length=4, bytes=octets[4]]",  mqttDataEx.correlationInfo().toString());
    }

    @Test
    public void shouldEncodeMqttEndExAsUnsubscribe()
    {
        final byte[] array = MqttFunctions.endEx()
                .typeId(0)
                .build();

        DirectBuffer buffer = new UnsafeBuffer(array);
        MqttEndExFW mqttEndEx = new MqttEndExFW().wrap(buffer, 0, buffer.capacity());
        assertEquals(0, mqttEndEx.typeId());
    }

    @Test
    public void shouldEncodeMqttAbortExAsUnsubscribe()
    {
        final byte[] array = MqttFunctions.abortEx()
                .typeId(0)
                .reason(0xF9)
                .build();

        DirectBuffer buffer = new UnsafeBuffer(array);
        MqttAbortExFW mqttAbortEx = new MqttAbortExFW().wrap(buffer, 0, buffer.capacity());
        assertEquals(0, mqttAbortEx.typeId());
        assertEquals(0xF9, mqttAbortEx.reason());
    }
}
