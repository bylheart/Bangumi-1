package soko.ekibun.bangumi.api.xxxlin.bean

/**
 * 错误报告返回信息
 * @property msg String?
 * @property code Int
 * @constructor
 */
data class BaseResult(
        val msg: String?,
        val code: Int)