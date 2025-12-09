package com.example.anew.repo

import android.util.Log
import com.example.anew.model.Team
import com.example.anew.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

//in Home
class ProjectRepo {
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun createProject(team: Team){
        try {
            db.collection("projects").add(team).await()
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

    suspend fun getUserFromUid(uid: String): User{
        val snapshot = db.collection("users").document(uid).get().await()
        return snapshot.toObject(User::class.java)!!
    }

}