package katium.client.qq.network.pb

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class PbGroupInfo(
    @ProtoNumber(1) val groupOwner: Long? = null,
    @ProtoNumber(2) val groupCreateTime: Int? = null,
    @ProtoNumber(3) val groupFlag: Int? = null,
    @ProtoNumber(4) val groupFlagExt: Int? = null,
    @ProtoNumber(5) val groupMemberMaxNum: Int? = null,
    @ProtoNumber(6) val groupMemberNum: Int? = null,
    @ProtoNumber(7) val groupOption: Int? = null,
    @ProtoNumber(8) val groupClassExt: Int? = null,
    @ProtoNumber(9) val groupSpecialClass: Int? = null,
    @ProtoNumber(10) val groupLevel: Int? = null,
    @ProtoNumber(11) val groupFace: Int? = null,
    @ProtoNumber(12) val groupDefaultPage: Int? = null,
    @ProtoNumber(13) val groupInfoSeq: Int? = null,
    @ProtoNumber(14) val groupRoamingTime: Int? = null,
    @ProtoNumber(15) val groupName: ByteArray? = null,
    @ProtoNumber(16) val groupMemo: ByteArray? = null,
    @ProtoNumber(17) val groupFingerMemo: ByteArray? = null,
    @ProtoNumber(18) val groupClassText: ByteArray? = null,
    @ProtoNumber(19) val groupAllianceCode: List<Int> = emptyList(),
    @ProtoNumber(20) val groupExtraAadmNum: Int? = null,
    @ProtoNumber(21) val groupUin: Long,
    @ProtoNumber(22) val groupCurrentMessageSequence: Int? = null,
    @ProtoNumber(23) val groupLastMessageTime: Int? = null,
    @ProtoNumber(24) val groupQuestion: ByteArray? = null,
    @ProtoNumber(25) val groupAnswer: ByteArray? = null,
    @ProtoNumber(26) val groupVisitorMaxNum: Int? = null,
    @ProtoNumber(27) val groupVisitorCurNum: Int? = null,
    @ProtoNumber(28) val levelNameSeq: Int? = null,
    @ProtoNumber(29) val groupAdminMaxNum: Int? = null,
    @ProtoNumber(30) val groupAioSkinTimestamp: Int? = null,
    @ProtoNumber(31) val groupBoardSkinTimestamp: Int? = null,
    @ProtoNumber(32) val groupAioSkinUrl: ByteArray? = null,
    @ProtoNumber(33) val groupBoardSkinUrl: ByteArray? = null,
    @ProtoNumber(34) val groupCoverSkinTimestamp: Int? = null,
    @ProtoNumber(35) val groupCoverSkinUrl: ByteArray? = null,
    @ProtoNumber(36) val groupGrade: Int? = null,
    @ProtoNumber(37) val activeMemberNum: Int? = null,
    @ProtoNumber(38) val certificationType: Int? = null,
    @ProtoNumber(39) val certificationText: ByteArray? = null,
    @ProtoNumber(40) val groupRichFingerMemo: ByteArray? = null,
    @ProtoNumber(41) val tagRecord: List<TagRecord> = emptyList(),
    @ProtoNumber(42) val groupGeoInfo: GroupGeoInfo? = null,
    @ProtoNumber(43) val headPortraitSeq: Int? = null,
    @ProtoNumber(44) val msgHeadPortrait: GroupHeaderPortrait? = null,
    @ProtoNumber(48) val cmduinMsgSeq: Int? = null,
    @ProtoNumber(49) val cmduinJoinTime: Int? = null,
    @ProtoNumber(50) val cmduinUinFlag: Int? = null,
    @ProtoNumber(51) val cmduinFlagEx: Int? = null,
    @ProtoNumber(52) val cmduinNewMobileFlag: Int? = null,
    @ProtoNumber(53) val cmduinReadMsgSeq: Int? = null,
    @ProtoNumber(54) val cmduinLastMsgTime: Int? = null,
    @ProtoNumber(55) val groupTypeFlag: Int? = null,
    @ProtoNumber(56) val appPrivilegeFlag: Int? = null,
    @ProtoNumber(57) val stGroupExInfo: GroupExInfoOnly? = null,
    @ProtoNumber(58) val groupSecLevel: Int? = null,
    @ProtoNumber(59) val groupSecLevelInfo: Int? = null,
    @ProtoNumber(60) val cmduinPrivilege: Int? = null,
    @ProtoNumber(61) val poidInfo: ByteArray? = null,
    @ProtoNumber(62) val cmduinFlagEx2: Int? = null,
    @ProtoNumber(63) val confUin: Long? = null,
    @ProtoNumber(64) val confMaxMsgSeq: Int? = null,
    @ProtoNumber(65) val confToGroupTime: Int? = null,
    @ProtoNumber(66) val passwordRedbagTime: Int? = null,
    @ProtoNumber(67) val subscriptionUin: Long? = null,
    @ProtoNumber(68) val memberListChangeSeq: Int? = null,
    @ProtoNumber(69) val membercardSeq: Int? = null,
    @ProtoNumber(70) val rootId: Long? = null,
    @ProtoNumber(71) val parentId: Long? = null,
    @ProtoNumber(72) val teamSeq: Int? = null,
    @ProtoNumber(73) val historyMsgBeginTime: Long? = null,
    @ProtoNumber(74) val inviteNoAuthNumLimit: Long? = null,
    @ProtoNumber(75) val cmduinHistoryMsgSeq: Int? = null,
    @ProtoNumber(76) val cmduinJoinMsgSeq: Int? = null,
    @ProtoNumber(77) val groupFlagext3: Int? = null,
    @ProtoNumber(78) val groupOpenAppid: Int? = null,
    @ProtoNumber(79) val isConfGroup: Int? = null,
    @ProtoNumber(80) val isModifyConfGroupFace: Int? = null,
    @ProtoNumber(81) val isModifyConfGroupName: Int? = null,
    @ProtoNumber(82) val noFingerOpenFlag: Int? = null,
    @ProtoNumber(83) val noCodeFingerOpenFlag: Int? = null,
) {

    @Serializable
    data class TagRecord(
        @ProtoNumber(1) val fromUin: Long? = null,
        @ProtoNumber(2) val groupCode: Long? = null,
        @ProtoNumber(3) val tagId: ByteArray? = null,
        @ProtoNumber(4) val setTime: Long? = null,
        @ProtoNumber(5) val goodNum: Int? = null,
        @ProtoNumber(6) val badNum: Int? = null,
        @ProtoNumber(7) val tagLen: Int? = null,
        @ProtoNumber(8) val tagValue: ByteArray? = null,
    )

    @Serializable
    data class GroupGeoInfo(
        @ProtoNumber(1) val owneruin: Long? = null,
        @ProtoNumber(2) val settime: Int? = null,
        @ProtoNumber(3) val cityid: Int? = null,
        @ProtoNumber(4) val longitude: Long? = null,
        @ProtoNumber(5) val latitude: Long? = null,
        @ProtoNumber(6) val geocontent: ByteArray? = null,
        @ProtoNumber(7) val poiId: Long? = null,
    )

    @Serializable
    data class GroupExInfoOnly(
        @ProtoNumber(1) val tribeId: Int? = null,
        @ProtoNumber(2) val moneyForAddGroup: Int? = null,
    )

    @Serializable
    data class GroupHeaderPortraitInfo(
        @ProtoNumber(1) val picId: Int? = null,
    )

    @Serializable
    data class GroupHeaderPortrait(
        @ProtoNumber(1) val picCount: Int? = null,
        @ProtoNumber(2) val msgInfo: List<GroupHeaderPortraitInfo> = emptyList(),
        @ProtoNumber(3) val defaultId: Int? = null,
        @ProtoNumber(4) val verifyingPicCnt: Int? = null,
        @ProtoNumber(5) val msgVerifyingPicInfo: List<GroupHeaderPortraitInfo> = emptyList(),
    )
}