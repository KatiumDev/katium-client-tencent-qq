package katium.client.qq.network.codec.highway

import katium.client.qq.network.pb.PbHighway

typealias HighwayRequestFrame = Pair<PbHighway.HighwayRequestHeader, UByteArray?>
typealias HighwayResponseFrame = Pair<PbHighway.HighwayResponseHeader, UByteArray?>
