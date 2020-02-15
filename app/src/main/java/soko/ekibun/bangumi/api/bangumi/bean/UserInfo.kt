package soko.ekibun.bangumi.api.bangumi.bean

import org.jsoup.nodes.Element
import soko.ekibun.bangumi.api.bangumi.Bangumi

/**
 * 用户信息类
 * @property id Int
 * @property username String?
 * @property nickname String?
 * @property avatar String?
 * @property sign String?
 * @property url String
 * @constructor
 */
data class UserInfo(
        var id: Int = 0,
        var username: String? = null,
        var nickname: String? = null,
        var avatar: String? = null,
        var sign: String? = null
) {
    val url = "${Bangumi.SERVER}/user/$username"

    companion object {
        /**
         * 从url中提取用户名
         * @param href String?
         * @return String?
         */
        fun getUserName(href: String?): String? {
            return Regex("""/user/([^/]*)""").find(href ?: "")?.groupValues?.get(1)
        }

        /**
         * 获取用户信息
         * @param user Element?
         * @param avatar String?
         * @return UserInfo
         */
        fun parse(user: Element?, avatar: String? = null): UserInfo {
            val username = getUserName(user?.attr("href"))
            return UserInfo(
                    id = username?.toIntOrNull() ?: 0,
                    username = username,
                    nickname = user?.text(),
                    avatar = avatar
            )
        }
    }
}