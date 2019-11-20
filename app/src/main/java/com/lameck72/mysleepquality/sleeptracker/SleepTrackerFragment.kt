/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lameck72.mysleepquality.sleeptracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.trackmysleepquality.sleeptracker.SleepTrackerViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.lameck72.mysleepquality.R
import com.lameck72.mysleepquality.database.SleepDatabase
import com.lameck72.mysleepquality.databinding.FragmentSleepTrackerBinding

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class SleepTrackerFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)

        /**reference to the app that this fragment is attached*/
        val application = requireNotNull(this.activity).application

        /**reference to data source via a reference to the DAO*/
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao

        /**an instance of the viewModelFactory*/
        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)

        /**Get a reference to the ViewModel associated with this fragment*/
        val sleepTrackerViewModel = ViewModelProviders.of(
                this, viewModelFactory).get(SleepTrackerViewModel::class.java)
        binding.sleepTrackerViewModel = sleepTrackerViewModel

        binding.lifecycleOwner = this

        sleepTrackerViewModel.navigateToSleepQuality.observe(this, Observer {
                night ->
            night?.let {this.findNavController().navigate(SleepTrackerFragmentDirections
                        .actionSleepTrackerFragmentToSleepQualityFragment(night.nightId))
                sleepTrackerViewModel.doneNavigating()
            }
        })

        sleepTrackerViewModel.showSnackBarEvent.observe(this, Observer {
            if (it == true) { // Observed state is true.
                Snackbar.make(
                    activity!!.findViewById(android.R.id.content),
                    getString(R.string.cleared_message),
                    Snackbar.LENGTH_SHORT // How long to display the message.
                ).show()
                sleepTrackerViewModel.doneShowingSnackbar()
            }
        })

        val adapter = SleepNightAdapter(SleepNightListener { nightId ->
            Toast.makeText(context, "$nightId", Toast.LENGTH_LONG).show()
            sleepTrackerViewModel.onSleepNightClicked(nightId)
        })

        sleepTrackerViewModel.nights.observe(viewLifecycleOwner, Observer {
            sleepTrackerViewModel.nights.observe(viewLifecycleOwner, Observer {
                it?.let {
                    adapter.addHeaderAndSubmitList(it)
                }
            })
        })

        binding.sleepList.adapter = adapter // Associate the adapter with the RecyclerView


        sleepTrackerViewModel.navigateToSleepDetail.observe(this, Observer { night ->
            night?.let {
                this.findNavController().navigate(
                    SleepTrackerFragmentDirections
                        .actionSleepTrackerFragmentToSleepDetailFragment(night))
                sleepTrackerViewModel.onSleepDetailNavigated()
            }
        })


        val manager = GridLayoutManager(activity, 3)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) =  when (position) {
                0 -> 3
                else -> 1
            }
        }

        /**tell the RecyclerView to use this GridLayoutManager*/
        binding.sleepList.layoutManager = manager

        return binding.root
    }
}
