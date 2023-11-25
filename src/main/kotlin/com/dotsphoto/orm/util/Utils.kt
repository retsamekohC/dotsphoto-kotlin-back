package com.dotsphoto.orm.util

import java.math.BigInteger
import java.security.MessageDigest

class Utils {
    companion object {
        fun getSHA1Hash(input: String): String {
            val md = MessageDigest.getInstance("SHA-1")
            val digest = md.digest(input.toByteArray())
            val num = BigInteger(1, digest)
            var hashText = num.toString(16)
            while (hashText.length < 32) {
                hashText = "0$hashText"
            }
            return hashText;
        }
    }
}