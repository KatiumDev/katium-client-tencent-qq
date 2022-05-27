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
package katium.client.qq.network.sso

import katium.client.qq.network.codec.jce.SimpleJceStruct

class SsoListRequestData(other: SimpleJceStruct) : SimpleJceStruct(other) {

    constructor() : this(SimpleJceStruct())
    constructor(tags: MutableMap<UByte, Any>) : this(SimpleJceStruct(tags))

    var uin: Long by number(1u)
    var unknown2: Long by number(2u)
    var unknown3: Byte by number(3u)

    /**
     * IMSI(MCC + MNC)
     *
     * 46000: CMCC, 46001: CUCC
     * See https://zh.wikipedia.org/wiki/%E7%A7%BB%E5%8A%A8%E8%AE%BE%E5%A4%87%E7%BD%91%E7%BB%9C%E4%BB%A3%E7%A0%81 for more
     */
    var imsi: String by string(4u, "00000")

    /**
     * 100 for WI-FI and 1 for mobile network
     */
    var networkType: Int by number(5u, 100)
    var appID: Int by number(6u, 537100432)

    // @TODO: Custom IMEI && random IMEI
    var imei: String by string(7u, "468356291846738")
    var unknown8: Long by number(8u)
    var unknown9: Long by number(9u)
    var unknown10: Long by number(10u)

    /**
     * Unknown usage, 1 for true
     */
    var shouldDoSpeedTest: Long by number(11u)
    var activeIPFamily: Byte by number(12u)
    var unknown13: Long by number(13u)

    override fun toString() =
        "SsoListRequestData(uin=$uin, unknown2=$unknown2, unknown3=$unknown3, imsi='$imsi', networkType=$networkType, " +
                "appID=$appID, imei='$imei', unknown8=$unknown8, unknown9=$unknown9, unknown10=$unknown10, " +
                "shouldDoSpeedTest=$shouldDoSpeedTest, activeIPFamily=$activeIPFamily, unknown13=$unknown13)"

}