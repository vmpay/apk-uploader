package eu.vmpay.apk.uploader.domain

class UploaderException(
    val error: UploaderError,
) : Exception() {

    override fun toString(): String {
        return "UploaderException(error=$error)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UploaderException

        return error == other.error
    }

    override fun hashCode(): Int {
        return error.hashCode()
    }
}

enum class UploaderError(val code: Int) {
    UNKNOWN(-1),
    ADB_NOT_FOUND(-40401),
    DEVICE_NOT_FOUND(-40411),
    FILE_NOT_FOUND(-40421),
}