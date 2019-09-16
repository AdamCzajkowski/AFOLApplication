package com.application.afol.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.afol.R
import com.application.afol.models.GroupedThemes
import com.application.afol.models.ThemesResult
import com.application.afol.ui.activities.LegoSetFromThemeActivity
import com.application.afol.ui.adapters.ThemesRecyclerViewAdapter
import com.application.afol.vm.themesViewModel.ThemesViewModel
import com.application.afol.vm.themesViewModel.ThemesViewModelFactory
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import kotlinx.android.synthetic.main.fragment_themes.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.support.kodein
import org.kodein.di.generic.instance

class ThemesFragment : Fragment(), KodeinAware {
    override val kodein: Kodein by kodein()

    private val themesViewModelFactory: ThemesViewModelFactory by instance()

    private lateinit var themesViewModel: ThemesViewModel

    private lateinit var themesRecyclerViewAdapter: ThemesRecyclerViewAdapter

    private val pageSize = 700

    private var page = 1

    private var listOfThemes = mutableListOf<ThemesResult.Result>()

    private var listOfGroupedThemesFinished = mutableListOf<GroupedThemes>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_themes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeLegoViewModel()
        initializeRecyclerView(rv_themes)
        getErrorRespond()
        getExceptionRespond()
        getSuccessRespond()
        getThemes(page, pageSize)
        selectedItem()
        searchTheme()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun selectedItem() {
        themesRecyclerViewAdapter.selectedItem = {
            startThemesActivity(it)
        }
    }

    private fun searchTheme() {
        search_edit_text_theme_id.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // not used
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // not used
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filteredList =
                    listOfGroupedThemesFinished.filter { it.name.contains(s.toString(), true) }
                Log.i("filtered", "${filteredList.size}")
                themesRecyclerViewAdapter.listOfThemes = filteredList.toMutableList()
                themesRecyclerViewAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun mapThemesToGroup(listOfThemes: MutableList<ThemesResult.Result>): MutableList<GroupedThemes> {
        val listOfGroupedThemes = mutableListOf<GroupedThemes>()
        Log.i("mapped", "start mapping")
        listOfThemes.forEach { theme ->
            Log.i("mapped", "new finding start for ${theme.name}")
            if (listOfGroupedThemes.isEmpty()) {
                val firstGroupedTheme = GroupedThemes(theme.name, mutableListOf(theme.id))
                Log.i("mapped", "create first item for ${theme.name}")
                listOfGroupedThemes.add(firstGroupedTheme)
            } else {
                Log.i(
                    "mapped",
                    "${listOfGroupedThemes.size},  list of groupedTheme is not empty, ${listOfGroupedThemes.last()}"
                )
                Log.i("mapped", "comaparing ${listOfGroupedThemes.last().name} to ${theme.name}")
                if (listOfGroupedThemes.last().name == theme.name) {
                    listOfGroupedThemes.last().listOfID.add(theme.id)
                    Log.i("mapped", "find match ${theme.name}")
                } else {
                    Log.i("mapped", "find new ${theme.name}")
                    listOfGroupedThemes.add(GroupedThemes(theme.name, mutableListOf(theme.id)))
                }
            }
        }
        Log.i("mapped", "Finish ${listOfGroupedThemes}")
        listOfGroupedThemesFinished = listOfGroupedThemes
        return listOfGroupedThemesFinished
    }

    private fun initializeLegoViewModel() {
        themesViewModel =
            ViewModelProviders.of(this, themesViewModelFactory).get(ThemesViewModel::class.java)
    }

    private fun initializeRecyclerView(recyclerView: RecyclerView) {
        themesRecyclerViewAdapter = ThemesRecyclerViewAdapter()
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        val alphaAdapter = AlphaInAnimationAdapter(themesRecyclerViewAdapter)
        recyclerView.adapter = ScaleInAnimationAdapter(alphaAdapter)
    }


    private fun getSuccessRespond() {
        themesViewModel.getThemesSuccess.observe(viewLifecycleOwner,
            Observer { it ->
                listOfThemes = it.results.toMutableList()
                listOfThemes.sortBy { it.name }
                themesRecyclerViewAdapter.listOfThemes = mapThemesToGroup(listOfThemes)
            })
    }

    private fun startThemesActivity(groupedThemes: GroupedThemes) {
        val intent = LegoSetFromThemeActivity.getIntent(context!!, groupedThemes)
        startActivity(intent)
    }

    private fun getErrorRespond() {
        themesViewModel.getThemesError.observe(this,
            Observer {
                Log.i("themeError", it)
            })
    }

    private fun getExceptionRespond() {
        themesViewModel.getThemesException.observe(this,
            Observer {
                Log.i("themeException", it.toString())
            })
    }

    private fun getThemes(page: Int, pageSize: Int) {
        themesViewModel.getThemes(page, pageSize)
    }
}