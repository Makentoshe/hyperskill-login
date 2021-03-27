package org.hyperskill.login

import okhttp3.OkHttpClient

interface RequireOkHttpClient {

    var client: OkHttpClient
}

