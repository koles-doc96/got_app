package ru.skillbranch.gameofthrones.data.remote.res

interface IRes {
    var isBookmarked: Boolean
    val id :String
    fun String.lastSegment(divider: String = "/"): String {
        return split(divider).last()
    }
}