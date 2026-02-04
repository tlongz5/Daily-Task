package com.example.anew.ui.fragment.add.adapter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.anew.model.User
import com.example.anew.data.repo.FriendRepo

class LoadFriendPaging(
    private val friendRepo: FriendRepo,
    private val uid: String
): PagingSource<String, User>(){
    override suspend fun load(params: LoadParams<String>): LoadResult<String, User> {
        return try {
            val startAfterFriendId = params.key
            val result = friendRepo.loadFriendsPage(
                uid,
                startAfterFriendId,
                params.loadSize
            )
            LoadResult.Page(
                data = result.users,
                prevKey = null,
                nextKey = result.lastFriendId
            )
        }catch (e: Exception){
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, User>): String? {
        return null
    }
}

