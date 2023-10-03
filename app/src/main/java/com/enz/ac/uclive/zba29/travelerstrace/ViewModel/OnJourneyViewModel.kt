package com.enz.ac.uclive.zba29.travelerstrace.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel

class OnJourneyViewModel: ViewModel() {
    var journeyTitle by mutableStateOf(TextFieldValue(""))
    var description by mutableStateOf(TextFieldValue(""))

    /**
     * TODO: Create a function that collects the users long and lat and store the data along with the journey id.
     */
}