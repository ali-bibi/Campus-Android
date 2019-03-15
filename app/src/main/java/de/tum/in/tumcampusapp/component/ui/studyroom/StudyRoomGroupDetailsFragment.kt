package de.tum.`in`.tumcampusapp.component.ui.studyroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.other.generic.adapter.GridEqualSpacingDecoration
import de.tum.`in`.tumcampusapp.utils.Const
import de.tum.`in`.tumcampusapp.utils.onScrolled

/**
 * Fragment for each study room group. Shows study room details in a list.
 */
class StudyRoomGroupDetailsFragment : Fragment() {

    private val groupId: Int by lazy {
        arguments?.getInt(Const.STUDY_ROOM_GROUP_ID) ?: throw IllegalStateException()
    }

    private val scrollListener: ScrollListener by lazy {
        requireContext() as? ScrollListener ?: throw IllegalStateException()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.fragment_study_rooms, container, false)
        val studyRooms = StudyRoomGroupManager(requireContext()).getAllStudyRoomsForGroup(groupId)

        if (studyRooms.isEmpty()) {
            rootView.findViewById<View>(R.id.study_room_placeholder).visibility = View.VISIBLE
            return rootView
        }

        rootView.findViewById<RecyclerView>(R.id.fragment_item_detail_recyclerview).apply {
            val spanCount = 2
            layoutManager = GridLayoutManager(context, spanCount)
            adapter = StudyRoomAdapter(this@StudyRoomGroupDetailsFragment, studyRooms)

            val spacing = Math.round(resources.getDimension(R.dimen.material_card_view_padding))
            addItemDecoration(GridEqualSpacingDecoration(spacing, spanCount))

            onScrolled {
                val canScrollUp = it.canScrollVertically(Const.SCROLL_DIRECTION_UP)
                scrollListener.onScrolled(canScrollUp)
            }
        }
        return rootView
    }

    interface ScrollListener {
        fun onScrolled(canScrollUp: Boolean)
    }

    companion object {

        fun newInstance(groupId: Int): StudyRoomGroupDetailsFragment {
            return StudyRoomGroupDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(Const.STUDY_ROOM_GROUP_ID, groupId)
                }
            }
        }

    }

}
