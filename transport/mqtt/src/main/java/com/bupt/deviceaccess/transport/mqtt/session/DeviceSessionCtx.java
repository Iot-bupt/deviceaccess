/**
 * Copyright © 2016-2017 The Thingsboard Authors
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
package com.bupt.deviceaccess.transport.mqtt.session;

import com.bupt.deviceaccess.common.data.id.SessionId;
import com.bupt.deviceaccess.common.msg.session.SessionCtrlMsg;
import com.bupt.deviceaccess.common.msg.session.SessionType;
import com.bupt.deviceaccess.common.msg.session.ctrl.SessionCloseMsg;
import com.bupt.deviceaccess.common.msg.session.ex.SessionException;
import com.bupt.deviceaccess.common.transport.SessionMsgProcessor;
import com.bupt.deviceaccess.common.transport.adaptor.AdaptorException;
import com.bupt.deviceaccess.common.transport.auth.DeviceAuthService;
import com.bupt.deviceaccess.common.transport.session.DeviceAwareSessionContext;
import com.bupt.deviceaccess.transport.mqtt.adaptors.MqttTransportAdaptor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;

import java.util.concurrent.atomic.AtomicInteger;

//import org.thingsboard.server.common.msg.session.SessionActorToAdaptorMsg;

/**
 * @author Andrew Shvayka
 */

public class DeviceSessionCtx extends DeviceAwareSessionContext {

    private final MqttTransportAdaptor adaptor;
    private final MqttSessionId sessionId;
    private ChannelHandlerContext channel;
    private volatile boolean allowAttributeResponses;
    private AtomicInteger msgIdSeq = new AtomicInteger(0);

    public DeviceSessionCtx(SessionMsgProcessor processor, DeviceAuthService authService, MqttTransportAdaptor adaptor) {
        super(processor, authService);
        this.adaptor = adaptor;
        this.sessionId = new MqttSessionId();
    }

    @Override
    public SessionType getSessionType() {
        return SessionType.ASYNC;
    }

    /**
    @Override
    public void onMsg(SessionActorToAdaptorMsg msg) throws SessionException {
        try {
            adaptor.convertToAdaptorMsg(this, msg).ifPresent(this::pushToNetwork);
        } catch (AdaptorException e) {
            //TODO: close channel with disconnect;
            logAndWrap(e);
        }
    }
    **/

    private void logAndWrap(AdaptorException e) throws SessionException {
        //log.warn("Failed to convert msg: {}", e.getMessage(), e);
        throw new SessionException(e);
    }

    private void pushToNetwork(MqttMessage msg) {
        channel.writeAndFlush(msg);
    }

    @Override
    public void onMsg(SessionCtrlMsg msg) throws SessionException {
        if (msg instanceof SessionCloseMsg) {
            pushToNetwork(new MqttMessage(new MqttFixedHeader(MqttMessageType.DISCONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0)));
            channel.close();
        }
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public long getTimeout() {
        return 0;
    }

    @Override
    public SessionId getSessionId() {
        return (SessionId) sessionId;
    }

    public void setChannel(ChannelHandlerContext channel) {
        this.channel = channel;
    }

    public void setAllowAttributeResponses() {
        allowAttributeResponses = true;
    }

    public void setDisallowAttributeResponses() {
        allowAttributeResponses = false;
    }

    public int nextMsgId() {
        return msgIdSeq.incrementAndGet();
    }
}
