package de.tum.`in`.tumcampusapp.component.ui.studyroom

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class StudyRoomsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private var currentId: Int = 0

    /**
     * Create a StudyRoomGroupDetailsFragment with the correct parameter for the selected group
     */
    override fun getItem(position: Int): Fragment {
        return StudyRoomGroupDetailsFragment.newInstance(currentId)
    }

    override fun getCount() = 1

    internal fun setStudyRoomGroupId(selectedGroupId: Int) {
        currentId = selectedGroupId
    }
}
