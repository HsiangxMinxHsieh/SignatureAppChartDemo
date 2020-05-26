package com.timmymike.combinelinechartdemo2

import com.google.gson.annotations.SerializedName


data class UserRecordModel(
    @SerializedName("data")
    var `data`: List<Data> = listOf(),
    @SerializedName("result")
    var result: Boolean = false
) {
    data class Data(
        @SerializedName("department")
        var department: String = "",
        @SerializedName("department_id")
        var departmentId: Int = 0,
        @SerializedName("started_work_at")
        var startedWorkAt: String? = "",
        @SerializedName("finished_work_at")
        var finishedWorkAt: String? = ""
    ) : Comparable<Data> {
        override fun compareTo(other: Data): Int {
            return when {
                this.startedWorkAt == other.startedWorkAt || this.finishedWorkAt == other.finishedWorkAt -> 0
                this.startedWorkAt?:"" > other.startedWorkAt?:"" -> 1
                else -> -1
            }
        }

        override fun toString(): String {
            return "\nData(department='$department', departmentId=$departmentId, startedWorkAt='$startedWorkAt', finishedWorkAt='$finishedWorkAt')"
        }

    }
}