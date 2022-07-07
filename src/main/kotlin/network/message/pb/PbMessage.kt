package katium.client.qq.network.message.pb

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class PbMessage(
    @ProtoNumber(1) val header: MessageHeader,
    @ProtoNumber(2) val content: ContentHeader? = null,
    @ProtoNumber(3) val body: Body,
) {

    @Serializable
    data class MessageHeader(
        @ProtoNumber(1) val fromUin: Long = 0,
        @ProtoNumber(2) val toUin: Long = 0,
        @ProtoNumber(3) val type: Int,
        @ProtoNumber(4) val c2cCmd: Int? = null,
        @ProtoNumber(5) val sequence: Int,
        @ProtoNumber(6) val time: Int = 0,
        @ProtoNumber(7) val uid: Long = 0,
        @ProtoNumber(8) val c2cHeader: C2CTempMessageHeader? = null,
        @ProtoNumber(9) val groupInfo: GroupInfo? = null,
        @ProtoNumber(10) val fromAppID: Int? = null,
        @ProtoNumber(11) val fromInstID: Int? = null,
        @ProtoNumber(12) val userActive: Int? = null,
        @ProtoNumber(13) val discussionInfo: DiscussionInfo? = null,
        @ProtoNumber(14) val fromNick: String? = null,
        @ProtoNumber(15) val authUin: Long? = null,
        @ProtoNumber(16) val authNick: String? = null,
        @ProtoNumber(17) val flag: Int? = null,
        @ProtoNumber(18) val authRemark: String? = null,
        @ProtoNumber(19) val groupName: String? = null,
        @ProtoNumber(20) val multiTransHeader: MultiTransHeader? = null,
        @ProtoNumber(21) val instControl: InstControl? = null,
        @ProtoNumber(22) val publicAccountGroupSendFlag: Int? = null,
        @ProtoNumber(23) val wseqInC2cMsghead: Int? = null,
        @ProtoNumber(24) val cpid: Long? = null,
        @ProtoNumber(25) val extGroupKeyInfo: ExtensionGroupKeyInfo? = null,
        @ProtoNumber(26) val multiCompatibleText: String? = null,
        @ProtoNumber(27) val authSex: Int? = null,
        @ProtoNumber(28) val isSourceMessage: Boolean? = null,
    ) {

        @Serializable
        data class GroupInfo(
            @ProtoNumber(1) val groupCode: Long,
            @ProtoNumber(2) val groupType: Int? = null,
            @ProtoNumber(3) val groupInfoSeq: Long? = null,
            @ProtoNumber(4) val groupCard: String? = null,
            @ProtoNumber(5) val groupRank: ByteArray? = null,
            @ProtoNumber(6) val groupLevel: Int? = null,
            @ProtoNumber(7) val groupCardType: Int? = null,
            @ProtoNumber(8) val groupName: ByteArray? = null,
        )

        @Serializable
        data class C2CTempMessageHeader(
            @ProtoNumber(1) val c2cType: Int? = null,
            @ProtoNumber(2) val serviceType: Int? = null,
            @ProtoNumber(3) val groupUin: Long? = null,
            @ProtoNumber(4) val groupCode: Long? = null,
            @ProtoNumber(5) val sig: ByteArray? = null,
            @ProtoNumber(6) val sigType: Int? = null,
            @ProtoNumber(7) val fromPhone: String? = null,
            @ProtoNumber(8) val toPhone: String? = null,
            @ProtoNumber(9) val lockDisplay: Int? = null,
            @ProtoNumber(10) val directionFlag: Int? = null,
            @ProtoNumber(11) val reserved: ByteArray? = null,
        )

        @Serializable
        data class DiscussionInfo(
            @ProtoNumber(1) val uin: Long? = null,
            @ProtoNumber(2) val type: Int? = null,
            @ProtoNumber(3) val infoSequence: Long? = null,
            @ProtoNumber(4) val remark: ByteArray? = null,
            @ProtoNumber(5) val name: ByteArray? = null,
        )

        @Serializable
        data class MultiTransHeader(
            @ProtoNumber(1) val status: Int? = null,
            @ProtoNumber(2) val messageID: Int? = null,
        )

        @Serializable
        data class InstControl(
            @ProtoNumber(1) val toInst: Set<InstInfo> = emptySet(),
            @ProtoNumber(2) val excludeInst: Set<InstInfo> = emptySet(),
            @ProtoNumber(3) val fromInst: InstInfo? = null,
        )

        @Serializable
        data class InstInfo(
            @ProtoNumber(1) val appID: Int? = null,
            @ProtoNumber(2) val instID: Int? = null,
            @ProtoNumber(3) val platform: Int? = null,
            @ProtoNumber(10) val deviceType: Int? = null,
        )

        @Serializable
        data class ExtensionGroupKeyInfo(
            @ProtoNumber(1) val maxSequence: Int? = null,
            @ProtoNumber(2) val time: Long? = null,
        )

    }

    @Serializable
    data class ContentHeader(
        @ProtoNumber(1) val packageNumber: Int,
        @ProtoNumber(2) val packageIndex: Int,
        @ProtoNumber(3) val divideSequence: Int,
        @ProtoNumber(4) val autoReply: Int? = null,
    )

    @Serializable
    data class Body(
        @ProtoNumber(1) val richText: RichText,
        @ProtoNumber(2) val content: ByteArray? = null,
        @ProtoNumber(3) val encryptContent: ByteArray? = null,
    ) {

        @Serializable
        data class RichText(
            @ProtoNumber(1) val attributes: Attributes = Attributes(random = 0),
            @ProtoNumber(2) val elements: List<PbMessageElement> = emptyList(),
            @ProtoNumber(3) val notOnlineFile: NotOnlineFile? = null,
            @ProtoNumber(4) val ptt: Ptt? = null,
        )

        @Serializable
        data class Attributes(
            @ProtoNumber(1) val codePage: Int? = null,
            @ProtoNumber(2) val time: Int? = null,
            @ProtoNumber(3) val random: Int,
            @ProtoNumber(4) val color: Int? = null,
            @ProtoNumber(5) val size: Int? = null,
            @ProtoNumber(6) val effect: Int? = null,
            @ProtoNumber(7) val charSet: Int? = null,
            @ProtoNumber(8) val pitchAndFamily: Int? = null,
            @ProtoNumber(9) val fontName: String? = null,
            @ProtoNumber(10) val reserveData: ByteArray? = null,
        )

        @Serializable
        data class NotOnlineFile(
            @ProtoNumber(1) val fileType: Int? = null,
            @ProtoNumber(2) val sig: ByteArray? = null,
            @ProtoNumber(3) val fileUuid: ByteArray? = null,
            @ProtoNumber(4) val fileMd5: ByteArray? = null,
            @ProtoNumber(5) val fileName: ByteArray? = null,
            @ProtoNumber(6) val fileSize: Long? = null,
            @ProtoNumber(7) val note: ByteArray? = null,
            @ProtoNumber(8) val reserved: Int? = null,
            @ProtoNumber(9) val subcmd: Int? = null,
            @ProtoNumber(10) val microCloud: Int? = null,
            @ProtoNumber(11) val bytesFileUrls: ByteArray,
            @ProtoNumber(12) val downloadFlag: Int? = null,
            @ProtoNumber(50) val dangerEvel: Int? = null,
            @ProtoNumber(51) val lifeTime: Int? = null,
            @ProtoNumber(52) val uploadTime: Int? = null,
            @ProtoNumber(53) val absFileType: Int? = null,
            @ProtoNumber(54) val clientType: Int? = null,
            @ProtoNumber(55) val expireTime: Int? = null,
            @ProtoNumber(56) val pbReserve: ByteArray? = null,
        )

        @Serializable
        data class Ptt(
            @ProtoNumber(1) val fileType: Int? = null,
            @ProtoNumber(2) val sourceUin: Long? = null,
            @ProtoNumber(3) val fileUuid: ByteArray? = null,
            @ProtoNumber(4) val fileMd5: ByteArray? = null,
            @ProtoNumber(5) val fileName: String? = null,
            @ProtoNumber(6) val fileSize: Int? = null,
            @ProtoNumber(7) val reserve: ByteArray? = null,
            @ProtoNumber(8) val fileId: Int? = null,
            @ProtoNumber(9) val serverIp: Int? = null,
            @ProtoNumber(10) val serverPort: Int? = null,
            @ProtoNumber(11) val boolValid: Boolean? = null,
            @ProtoNumber(12) val signature: ByteArray? = null,
            @ProtoNumber(13) val shortcut: ByteArray? = null,
            @ProtoNumber(14) val fileKey: ByteArray? = null,
            @ProtoNumber(15) val magicPttIndex: Int? = null,
            @ProtoNumber(16) val voiceSwitch: Int? = null,
            @ProtoNumber(17) val pttUrl: ByteArray? = null,
            @ProtoNumber(18) val groupFileKey: ByteArray? = null,
            @ProtoNumber(19) val time: Int? = null,
            @ProtoNumber(20) val downPara: ByteArray? = null,
            @ProtoNumber(29) val format: Int? = null,
            @ProtoNumber(30) val pbReserve: ByteArray? = null,
            @ProtoNumber(31) val bytesPttUrls: ByteArray,
            @ProtoNumber(32) val downloadFlag: Int? = null,
        )

    }

}