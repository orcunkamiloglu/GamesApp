package com.allybros.videogamesapp.feature.games

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.allybros.videogamesapp.R
import com.allybros.videogamesapp.commons.GameItem
import com.allybros.videogamesapp.commons.Games
import com.allybros.videogamesapp.commons.InfiniteScrollListener
import com.allybros.videogamesapp.commons.RxBaseFragment
import com.allybros.videogamesapp.commons.extensions.inflate
import com.allybros.videogamesapp.feature.games.adapter.GamesAdapter
import com.allybros.videogamesapp.feature.games.adapter.ViewPagerAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_games.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import kotlin.collections.ArrayList

class GamesFragment : RxBaseFragment() {

    private var games: Games? = null
    private val gamesManager by lazy { GamesManager() }
    private var gamesArray: Array<GameItem>? = null
    var viewPagerAdapter: ViewPagerAdapter? = null
    private var queryGames: ArrayList<GameItem>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_games)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        games_recyleView.setHasFixedSize(true)
        val linearLayout = LinearLayoutManager(context)
        games_recyleView.layoutManager = linearLayout
        games_recyleView.clearOnScrollListeners()
        games_recyleView.addOnScrollListener(InfiniteScrollListener({ requestGames() }, linearLayout))
        initAdapter()
        if (savedInstanceState == null) {
            requestGames()
        }
    }

    private fun requestGames(){
        val subscription = gamesManager.getGames(games?.next ?: "1")
                .subscribeOn(Schedulers.io())/*It is a API request*/
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { retrievedGames ->
                            Log.d("Receiver: ",games?.next.toString());
                            games = retrievedGames
                            gamesArray = arrayOf<GameItem>(retrievedGames.games[0],retrievedGames.games[1],retrievedGames.games[2])
                            Log.d("Images",""+retrievedGames.games[0]+"\n"+retrievedGames.games[1]+"\n"+retrievedGames.games[2])
                            (games_recyleView.adapter as GamesAdapter).addGame(retrievedGames.games)

                            viewPagerAdapter = ViewPagerAdapter(gamesArray, context)
                            viewPager.adapter = viewPagerAdapter
                        },
                        { e->
                            Snackbar.make(games_recyleView, e.message ?: "",Snackbar.LENGTH_LONG).show()
                        }
                )
        subscriptions.add(subscription)
    }


    private fun initAdapter() {
        if (games_recyleView.adapter == null) {
            games_recyleView.adapter = GamesAdapter()
        }
    }
}






















