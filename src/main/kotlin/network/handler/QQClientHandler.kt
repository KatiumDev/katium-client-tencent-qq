package katium.client.qq.network.handler

import katium.core.util.service.Service
import katium.core.util.service.ServiceLookup
import java.util.EventListener

interface QQClientHandler : Service, EventListener {

    companion object {

        val lookup = ServiceLookup(QQClientHandler::class)

    }

}