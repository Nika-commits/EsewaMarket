package com.example.xml_app.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.xml_app.utils.CustomApplicationContext

class SearchViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val app = getApplication<CustomApplicationContext>()

}