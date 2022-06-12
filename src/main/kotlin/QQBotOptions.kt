/*
 * Copyright 2022 Katium Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package katium.client.qq

class QQBotOptions(config: Map<String, String>) {

    /**
     * Login
     */
    val allowSlider = config["qq.login.allow_slider"]?.toBoolean() ?: true
    val cacheSession = config["qq.login.cache_session"]?.toBoolean() ?: true
    val password = config["qq.login.password.plain"] ?: config["qq.login.password"]
    val passwordMD5 = config["qq.login.password.md5"]
    val interactiveLogin = config["qq.login.interactive"]?.toBoolean() ?: true
    val loginSolutionProvider = config["qq.login.solution.provider"]
    val loginSolutionSelector = config["qq.login.solution.selector"]
    val loginSmsProvider = config["qq.login.solution.sms_provider"]

    /**
     * Remote server
     */
    val remoteServerAddress = config["qq.remote_server.address"]
    val remoteServerPort = config["qq.remote_server.port"]?.toInt() ?: run {
        if (remoteServerAddress != null)
            throw UnsupportedOperationException("qq.remote_server.port not found but qq.remote_server.address set")
        null
    }

    /**
     * Protocol info
     */
    val deviceInfoFile = config["qq.protocol.device_info_file"]
    val clientVersionFile = config["qq.protocol.client_version_info_file"]
    val protocolType = config["qq.protocol.type"] ?: "ANDROID_PHONE"
    val ecdhV2Enabled = config["qq.protocol.ecdh_v2.enabled"]?.toBoolean() ?: true
    val ecdhV2Verifying = config["qq.protocol.ecdh_v2.verifying"]?.toBoolean() ?: true

    /**
     * SSO connection
     */
    val ssoRetryTimes = config["qq.sso.retry_times"]?.toInt() ?: 10
    val ssoRetryDelay = config["qq.sso.retry_delay"]?.toLong() ?: 5000L

    /**
     * Heartbeat
     */
    val heartbeatEnabled = config["qq.heartbeat.enabled"]?.toBoolean() ?: true
    val heartbeatInterval = config["qq.heartbeat.interval"]?.toLong() ?: 30000

    /**
     * Auto read report
     */
    val autoReadReportEnabled = config["qq.auto_read_report.enabled"]?.toBoolean() ?: true
    val autoReadReportIntervalMin = config["qq.auto_read_report.interval.min"]?.toLong() ?: 60000L
    val autoReadReportIntervalMax = config["qq.auto_read_report.interval.max"]?.toLong() ?: 2000000L
    val autoReadReportFull = config["qq.auto_read_report.full"]?.toBoolean() ?: false

    /**
     * Highway
     */
    val highwayTcpNoDelay = config["qq.highway.tcp_no_delay"]?.toBoolean() ?: true
    val highwayConnectTimeout = config["qq.highway.connect_timeout"]?.toInt() ?: 3000
    val highwayReconnectTimes = config["qq.highway.reconnect_times"]?.toInt() ?: 10
    val highwayParallelUploadMinSize = config["qq.highway.parallel_upload.min_size"]?.toInt() ?: (3 * 1024 * 1024)
    val highwayParallelThreads = config["qq.highway.parallel_upload.threads"]?.toInt() ?: 5

    /**
     * Chat
     */
    val maxForwardMessageLength = config["qq.chat.max_forward_message_length"]?.toInt() ?: 200

}