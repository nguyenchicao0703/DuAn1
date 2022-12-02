package com.fpoly.project1.firebase.controller

import com.fpoly.project1.firebase.Firebase
import com.fpoly.project1.firebase.model.ChatSession
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.ValueEventListener

class ControllerChatSession : ControllerBase<ChatSession>("table_chat_sessions") {
	override fun setSync(value: ChatSession, update: Boolean): Boolean {
		val tableReference = Firebase.database.child(table)

		return try {
			if (!update) {
				value.__id = "${value.sendingUser}_${value.targetUser}"
				Tasks.await(
					tableReference.child(value.__id!!)
						.setValue(value)
				)
			} else {
				Tasks.await(tableReference.child(value.__id!!).setValue(value))
			}
			true
		} catch (e: Exception) {
			e.printStackTrace()
			false
		}
	}

	override fun setAsync(
		value: ChatSession,
		update: Boolean,
		successListener: SuccessListener?,
		failureListener: FailureListener?
	) {
		Firebase.database.child(table).child(value.__id!!).setValue(value).addOnCompleteListener {
			if (it.isSuccessful)
				successListener?.run()
			else
				failureListener?.run(it.exception)
		}
	}

	override fun removeSync(referenceId: String?): Boolean {
		return try {
			Tasks.await(
				Firebase.database
					.child(table)
					.child(referenceId!!)
					.setValue(null)
			)
			true
		} catch (e: Exception) {
			e.printStackTrace()
			false
		}
	}

	override fun removeAsync(
		referenceId: String?,
		successListener: SuccessListener?,
		failureListener: FailureListener?
	) {
		TODO("Not yet implemented")
	}

	override fun getSync(referenceId: String?): ChatSession? {
		return try {
			Tasks.await(
				Firebase.database
					.child(table)
					.child(referenceId!!)
					.get()
			).getValue(ChatSession::class.java)
		} catch (e: Exception) {
			e.printStackTrace()
			null
		}
	}

	override fun getAsync(
		referenceId: String?,
		successListener: SuccessListener?,
		failureListener: FailureListener?
	) {
		TODO("Not yet implemented")
	}

	override fun getAllSync(): ArrayList<ChatSession>? {
		return try {
			val list = ArrayList<ChatSession>()

			for (dataSnapshot in Tasks.await(
				Firebase.database.child(table).get()
			).children) dataSnapshot.getValue(
				ChatSession::class.java
			)?.let {
				list.add(it)
			}

			list
		} catch (e: Exception) {
			e.printStackTrace()

			null
		}
	}

	override fun getAllAsync(successListener: SuccessListener?, failureListener: FailureListener?) {
		Firebase.database.child(table).get().addOnCompleteListener { task ->
			if (task.isSuccessful)
				successListener?.run(task.result)
			else
				failureListener?.run(task.exception)
		}
	}

	fun registerValueChangeListener(
		value: ChatSession,
		listener: ValueEventListener
	): ValueEventListener =
		Firebase.database.child(table).child(value.__id!!).addValueEventListener(listener)
}