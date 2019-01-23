package de.tum.`in`.tumcampusapp.component.ui.ticket.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.other.generic.adapter.EqualSpacingItemDecoration
import de.tum.`in`.tumcampusapp.component.prefs.AppConfig
import de.tum.`in`.tumcampusapp.component.ui.chat.model.ChatMember
import de.tum.`in`.tumcampusapp.component.ui.ticket.EventsViewModel
import de.tum.`in`.tumcampusapp.component.ui.ticket.EventsViewState
import de.tum.`in`.tumcampusapp.component.ui.ticket.adapter.EventsAdapter
import de.tum.`in`.tumcampusapp.component.ui.ticket.di.TicketsModule
import de.tum.`in`.tumcampusapp.component.ui.ticket.model.EventType
import de.tum.`in`.tumcampusapp.di.ViewModelFactory
import de.tum.`in`.tumcampusapp.di.injector
import de.tum.`in`.tumcampusapp.utils.Const.CHAT_MEMBER
import de.tum.`in`.tumcampusapp.utils.Utils
import de.tum.`in`.tumcampusapp.utils.observeNonNull
import kotlinx.android.synthetic.main.fragment_events.*
import org.jetbrains.anko.defaultSharedPreferences
import javax.inject.Inject
import javax.inject.Provider

class EventsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private val eventType: EventType by lazy {
        arguments?.getSerializable(KEY_EVENT_TYPE) as EventType
    }

    private val appConfig: AppConfig by lazy {
        AppConfig(requireContext())
    }

    @Inject
    lateinit var provider: Provider<EventsViewModel>

    private val viewModel: EventsViewModel by lazy {
        val factory = ViewModelFactory(provider)
        ViewModelProviders.of(this, factory).get(EventsViewModel::class.java)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        injector.ticketsComponent()
                .ticketsModule(TicketsModule(requireContext()))
                .eventType(eventType)
                .eventId(0) // not relevant here
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(view) {
        eventsRecyclerView.setHasFixedSize(true)
        eventsRecyclerView.layoutManager = LinearLayoutManager(context)
        eventsRecyclerView.itemAnimator = DefaultItemAnimator()
        eventsRecyclerView.adapter = EventsAdapter(context)

        val spacing = Math.round(resources.getDimension(R.dimen.material_card_view_padding))
        eventsRecyclerView.addItemDecoration(EqualSpacingItemDecoration(spacing))

        eventsRefreshLayout.setOnRefreshListener(this@EventsFragment)
        eventsRefreshLayout.setColorSchemeResources(
                R.color.color_primary,
                R.color.tum_A100,
                R.color.tum_A200
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.viewState.observeNonNull(viewLifecycleOwner, this::render)
    }

    private fun render(viewState: EventsViewState) {
        val isEmpty = viewState.events.isEmpty()
        eventsRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
        placeholderTextView.visibility = if (isEmpty) View.VISIBLE else View.GONE

        eventsRefreshLayout.isRefreshing = false

        if (viewState.events.isNotEmpty()) {
            val adapter = eventsRecyclerView.adapter as EventsAdapter
            adapter.update(viewState.events)
        } else {
            placeholderTextView.setText(eventType.placeholderResId)
        }

        eventsRefreshLayout.isRefreshing = viewState.isLoading
        viewState.errorResId?.let {
            Utils.showToast(requireContext(), it)
        }
    }

    override fun onRefresh() {
        val isLoggedIn = appConfig.chatMember != null
        viewModel.refreshEventsAndTickets(isLoggedIn)
    }

    companion object {

        private const val KEY_EVENT_TYPE = "type"

        @JvmStatic
        fun newInstance(eventType: EventType): EventsFragment {
            return EventsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_EVENT_TYPE, eventType)
                }
            }
        }
    }

}
