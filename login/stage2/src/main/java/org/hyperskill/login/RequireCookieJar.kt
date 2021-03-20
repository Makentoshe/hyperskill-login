package org.hyperskill.login

import okhttp3.CookieJar

interface RequireCookieJar {

    val cookieJar: CookieJar
}