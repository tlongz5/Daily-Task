package com.example.anew.repo

import android.util.Log
import com.example.anew.model.Team
import com.example.anew.model.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

//in Home
class ProjectRepo {
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun createProject(team: Team){
        try {
            db.collection("projects").document(team.id).set(team).await()
            Log.d("project", "createProject success")
        }catch (e: Exception){
            Log.d("project", "createProject failed")
        }
    }

    suspend fun getProjectsData(uid: String): MutableList<Team>{
        val projectList= mutableListOf<Team>()
        try {
            val snapshot = db.collection("projects")
                .whereArrayContains("members",uid)
                .get()
                .await()
            for(document in snapshot){
                val project = document.toObject(Team::class.java)
                projectList.add(project)
            }
        }
        catch (e: Exception){
            Log.d("project", "getProject failed")
        }

        return projectList
    }

    suspend fun getProjectFromId(id: String): Team{
        try {
            val snapshot = db.collection("projects")
                .document(id).get().await()
            return snapshot.toObject(Team::class.java)!!
        }catch (e:Exception){
            Log.d("project", "getProject failed")
            throw e
        }
    }

    suspend fun updateStatusProject(listToUpdate: List<String>){
        try {
            val docRef = db.collection("projects")
            db.runBatch { batch ->
                listToUpdate.forEach {
                    batch.update(docRef.document(it), "inProgress", false)
                }
            }.await()
        }catch (e: Exception){
            Log.d("project", "updateStatusProject failed")
        }
    }

    suspend fun updateProgress(id: String, isChecked: Boolean, userId:String): Team?{
        return try {
            db.runTransaction { transaction ->
                val snapshot = db.collection("projects").document(id)
                val team = transaction.get(snapshot).toObject(Team::class.java)
                if(isChecked) team!!.membersCompleted += userId
                else team!!.membersCompleted -= userId

                val cntDone = team!!.membersCompleted.size
                val cntAll = team.members.size
                val percent = (100f/cntAll *cntDone).toInt()

                team!!.completedPercent = percent
                transaction.update(snapshot, "completedPercent", percent)
                transaction.update(snapshot, "membersCompleted", team.membersCompleted)
                if(percent==100) {
                    transaction.update(snapshot, "inProgress", false)
                    team!!.inProgress=false
                }
                Log.d("project", "updateProgress")
                team
            }.await()
        }catch (e : Exception){
            Log.d("project", "updateProgress failed")
            null
        } as Team?
    }

    suspend fun updateDataAfterAddOrDelete(id: String, isAdd: Boolean, userIdList: List<String>): Team?{
        return try {
            db.runTransaction { transaction ->
                val snapshot = db.collection("projects").document(id)
                val team = transaction.get(snapshot).toObject(Team::class.java)
                team!!.membersCompleted -= userIdList
                if(isAdd) team!!.members += userIdList else team!!.members-=userIdList


                val cntDone = team.membersCompleted.size
                val cntAll = team.members.size
                val percent = (100f/cntAll *cntDone).toInt()

                team!!.completedPercent = percent
                transaction.update(snapshot, "completedPercent", percent)
                transaction.update(snapshot, "membersCompleted", team.membersCompleted)
                transaction.update(snapshot, "members", team.members)

                Log.d("project", "updateProgress success")
                team
            }.await()
        }catch (e : Exception){
            Log.d("project", "updateProgress failed")
            null
        } as Team?
    }

    suspend fun editNameProject(id: String, groupName: String){
        try {
            db.collection("projects").document(id)
                .update("name",groupName).await()
        }catch (e: Exception){
            Log.d("project", "editNameProject failed")
        }
    }



}