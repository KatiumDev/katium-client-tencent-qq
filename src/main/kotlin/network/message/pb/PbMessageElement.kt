package katium.client.qq.network.message.pb

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class PbMessageElement(
    @ProtoNumber(1) val text: Text? = null,
    @ProtoNumber(4) val notOnlineImage: NotOnlineImage? = null,
    @ProtoNumber(8) val customFace: CustomFace? = null,
    @ProtoNumber(12) val serviceMessage: ServiceMessage? = null,
    @ProtoNumber(21) val anonymousInfo: AnonymousGroupMessage? = null,
    @ProtoNumber(37) val generalFlags: GeneralFlags? = null,
    @ProtoNumber(45) val source: SourceMessage? = null,
) {

    @Serializable
    data class Text(
        @ProtoNumber(1) val string: String,
        @ProtoNumber(2) val link: String? = null,
        @ProtoNumber(3) val attribute6Buf: ByteArray? = null,
        @ProtoNumber(4) val attribute7Buf: ByteArray? = null,
        @ProtoNumber(11) val buffer: ByteArray? = null,
        @ProtoNumber(12) val pbReserve: ByteArray? = null,
    )

    @Serializable
    data class NotOnlineImage(
        @ProtoNumber(1) val filePath: String,
        @ProtoNumber(2) val fileLen: Int? = null,
        @ProtoNumber(3) val downloadPath: String,
        @ProtoNumber(4) val oldVerSendFile: ByteArray? = null,
        @ProtoNumber(5) val imgType: Int? = null,
        @ProtoNumber(6) val previewsImage: ByteArray? = null,
        @ProtoNumber(7) val pictureMd5: ByteArray,
        @ProtoNumber(8) val pictureHeight: Int? = null,
        @ProtoNumber(9) val pictureWidth: Int? = null,
        @ProtoNumber(10) val resourceID: String,
        @ProtoNumber(11) val flag: ByteArray? = null,
        @ProtoNumber(12) val thumbUrl: String? = null,
        @ProtoNumber(13) val original: Int? = null,
        @ProtoNumber(14) val bigUrl: String? = null,
        @ProtoNumber(15) val origUrl: String? = null,
        @ProtoNumber(16) val bizType: Int? = null,
        @ProtoNumber(17) val result: Int? = null,
        @ProtoNumber(18) val index: Int? = null,
        @ProtoNumber(19) val opFaceBuf: ByteArray? = null,
        @ProtoNumber(20) val oldPictureMd5: Boolean,
        @ProtoNumber(21) val thumbWidth: Int? = null,
        @ProtoNumber(22) val thumbHeight: Int? = null,
        @ProtoNumber(23) val fileID: Int? = null,
        @ProtoNumber(24) val showLen: Int? = null,
        @ProtoNumber(25) val downloadLen: Int? = null,
        @ProtoNumber(29) val pbReserve: ByteArray? = null,
    )

    @Serializable
    data class CustomFace(
        @ProtoNumber(1) val guid: ByteArray? = null,
        @ProtoNumber(2) val filePath: String,
        @ProtoNumber(3) val shortcut: String? = null,
        @ProtoNumber(4) val buffer: ByteArray? = null,
        @ProtoNumber(5) val flag: ByteArray? = null,
        @ProtoNumber(6) val oldData: ByteArray? = null,
        @ProtoNumber(7) val fileID: Long,
        @ProtoNumber(8) val serverIP: Int? = null,
        @ProtoNumber(9) val serverPort: Int? = null,
        @ProtoNumber(10) val fileType: Int,
        @ProtoNumber(11) val signature: ByteArray? = null,
        @ProtoNumber(12) val useful: Int,
        @ProtoNumber(13) val md5: ByteArray,
        @ProtoNumber(14) val thumbUrl: String? = null,
        @ProtoNumber(15) val bigUrl: String? = null,
        @ProtoNumber(16) val origUrl: String? = null,
        @ProtoNumber(17) val bizType: Int? = null,
        @ProtoNumber(18) val repeatIndex: Int? = null,
        @ProtoNumber(19) val repeatImage: Int? = null,
        @ProtoNumber(20) val imageType: Int,
        @ProtoNumber(21) val index: Int? = null,
        @ProtoNumber(22) val width: Int? = null,
        @ProtoNumber(23) val height: Int? = null,
        @ProtoNumber(24) val source: Int? = null,
        @ProtoNumber(25) val size: Int,
        @ProtoNumber(26) val origin: Int? = null,
        @ProtoNumber(27) val thumbWidth: Int? = null,
        @ProtoNumber(28) val thumbHeight: Int? = null,
        @ProtoNumber(29) val showLen: Int? = null,
        @ProtoNumber(30) val downloadLen: Int? = null,
        @ProtoNumber(31) val X400Url: String? = null,
        @ProtoNumber(32) val X400Width: Int? = null,
        @ProtoNumber(33) val X400Height: Int? = null,
        @ProtoNumber(34) val pbReserve: ByteArray? = null,
    )

    @Serializable
    data class ResvAttributes(
        @ProtoNumber(1) val imageBizType: Int? = null,
        @ProtoNumber(7) val imageShow: AnimationImageShow? = null,
    )

    @Serializable
    data class AnimationImageShow(
        @ProtoNumber(1) val effectID: Int? = null,
        @ProtoNumber(2) val animationParam: ByteArray? = null,
    )

    @Serializable
    data class ServiceMessage(
        @ProtoNumber(1) val template: ByteArray,
        @ProtoNumber(2) val serviceID: Int,
        @ProtoNumber(3) val resourceID: ByteArray? = null,
        @ProtoNumber(4) val random: Int? = null,
        @ProtoNumber(5) val sequence: Int? = null,
        @ProtoNumber(6) val flags: Int? = null,
    )

    @Serializable
    data class AnonymousGroupMessage(
        @ProtoNumber(1) val flags: Int? = null,
        @ProtoNumber(2) val id: ByteArray? = null,
        @ProtoNumber(3) val nick: ByteArray? = null,
        @ProtoNumber(4) val headPortrait: Int? = null,
        @ProtoNumber(5) val ex7pireTime: Int? = null,
        @ProtoNumber(6) val bubbleID: Int? = null,
        @ProtoNumber(7) val rankColor: ByteArray? = null,
    )

    @Serializable
    data class GeneralFlags(
        @ProtoNumber(1) val bubbleDiyTextId: Int? = null,
        @ProtoNumber(2) val groupFlagNew: Int? = null,
        @ProtoNumber(3) val uin: Long? = null,
        @ProtoNumber(4) val rpId: ByteArray? = null,
        @ProtoNumber(5) val prpFold: Int? = null,
        @ProtoNumber(6) val longTextFlag: Int? = null,
        @ProtoNumber(7) val longTextResourceID: String? = null,
        @ProtoNumber(8) val groupType: Int? = null,
        @ProtoNumber(9) val toUinFlag: Int? = null,
        @ProtoNumber(10) val glamourLevel: Int? = null,
        @ProtoNumber(11) val memberLevel: Int? = null,
        @ProtoNumber(12) val groupRankSeq: Long? = null,
        @ProtoNumber(13) val olympicTorch: Int? = null,
        @ProtoNumber(14) val babyQGuideMsgCookie: ByteArray? = null,
        @ProtoNumber(15) val uin32ExpertFlag: Int? = null,
        @ProtoNumber(16) val bubbleSubId: Int? = null,
        @ProtoNumber(17) val pendantId: Long? = null,
        @ProtoNumber(18) val rpIndex: ByteArray? = null,
        @ProtoNumber(19) val pbReserve: ByteArray? = null,
    )

    @Serializable
    data class SourceMessage(
        @ProtoNumber(1) val originSequences: List<Int> = emptyList(),
        @ProtoNumber(2) val senderUin: Long? = null,
        @ProtoNumber(3) val time: Int? = null,
        @ProtoNumber(4) val flag: Int? = null,
        @ProtoNumber(5) val elements: List<PbMessageElement>? = emptyList(),
        @ProtoNumber(6) val type: Int? = null,
        @ProtoNumber(7) val richMessage: ByteArray? = null,
        @ProtoNumber(8) val pbReserve: ByteArray? = null,
        @ProtoNumber(9) val sourceMessage: ByteArray? = null,
        @ProtoNumber(10) val toUin: Long? = null,
        @ProtoNumber(11) val troopName: ByteArray? = null,
    )

}