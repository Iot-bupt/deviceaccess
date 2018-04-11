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
package com.bupt.deviceaccess.common.transport.session;

import com.bupt.deviceaccess.common.data.Device;
import com.bupt.deviceaccess.common.data.security.DeviceCredentialsFilter;
import com.bupt.deviceaccess.common.msg.session.SessionContext;
import com.bupt.deviceaccess.common.msg.session.SessionCtrlMsg;
import com.bupt.deviceaccess.common.msg.session.ex.SessionException;
import com.bupt.deviceaccess.common.transport.SessionMsgProcessor;
import com.bupt.deviceaccess.common.transport.auth.DeviceAuthResult;
import com.bupt.deviceaccess.common.transport.auth.DeviceAuthService;

import java.util.Optional;

//import org.slf4j.*;

/**
 * @author Andrew Shvayka
 */
@SuppressWarnings("ALL")
//@Slf4j
public abstract class DeviceAwareSessionContext implements SessionContext {

    protected final DeviceAuthService authService;
    protected final SessionMsgProcessor processor;

    protected volatile Device device;

    public DeviceAwareSessionContext(SessionMsgProcessor processor, DeviceAuthService authService) {
        this.processor = processor;
        this.authService = authService;
    }

    public DeviceAwareSessionContext(SessionMsgProcessor processor, DeviceAuthService authService, Device device) {
        this(processor, authService);
        this.device = device;
    }


    @SuppressWarnings("Since15")
    public boolean login(DeviceCredentialsFilter credentials) {
        DeviceAuthResult result = authService.process(credentials);
        if (result.isSuccess()) {
            //noinspection Since15
            @SuppressWarnings("Since15") Optional<Device> deviceOpt = authService.findDeviceById(result.getDeviceId());
            //noinspection Since15
            if (deviceOpt.isPresent()) {
                //noinspection Since15
                device = deviceOpt.get();
            }
            return true;
        } else {
            //log.debug("Can't find device using credentials [{}] due to {}", credentials, result.getErrorMsg());
            return false;
        }
    }

    public DeviceAuthService getAuthService() {
        return authService;
    }

    public SessionMsgProcessor getProcessor() {
        return processor;
    }

    public Device getDevice() {
        return device;
    }

    public abstract void onMsg(SessionCtrlMsg msg) throws SessionException;
}
