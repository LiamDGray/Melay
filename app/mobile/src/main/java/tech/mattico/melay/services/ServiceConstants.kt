package tech.mattico.melay.services

object ServiceConstants {

    val ACTIVE_SYNC_URL = 1

    val INACTIVE_SYNC_URL = 0

    val ACTIVE_SYNC = 1

    val INACTIVE_SYNC = 0

    val SYNC_STATUS = "sync_status"

    val DEFAULT_SMS_PROVIDER = "tech.mattico.melay.defaultsmsprovider"

    var CHECK_TASK_SERVICE_REQUEST_CODE = 0

    var CHECK_TASK_SCHEDULED_SERVICE_REQUEST_CODE = 1

    var AUTO_SYNC_SERVICE_REQUEST_CODE = 2

    var AUTO_SYNC_SCHEDULED_SERVICE_REQUEST_CODE = 3

    var MESSAGE_RESULTS_SCHEDULED_SERVICE_REQUEST_CODE = 4

    var AUTO_SYNC_ACTION = "tech.mattico.melay.syncservices.autosync"

    var CHECT_TASK_ACTION = "tech.mattico.melay.syncservices.checktask"

    var AUTO_SYNC_SCHEDULED_ACTION = "tech.mattico.melay.syncservices.autosyncscheduled"

    var CHECT_TASK_SCHEDULED_ACTION = "tech.mattico.melay.syncservices.checktaskscheduled"

    var FAILED_ACTION = "tech.mattico.melay.syncservices.failed"

    var BATTERY_LEVEL = "tech.mattico.melay.syncservices.batterylevel"

    var MESSAGE_UUID = "message_uuid"

    var UPDATE_MESSAGE = "UPDATE_MESSAGE"

    var DELETE_MESSAGE = "DELETE_MESSAGE"
}
