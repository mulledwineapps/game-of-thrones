package ru.skillbranch.gameofthrones.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_root.*
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.ui.splash.SplashFragmentDirections

// 00:04:30
class RootActivity : AppCompatActivity() {

    private lateinit var viewModel: RootViewModel
    lateinit var navController: NavController

    // чтобы при клавише назад мы перемещались вверх по иерархии
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        initViewModel()
        savedInstanceState ?: prepareData()
        navController = Navigation.findNavController(
            this,
            R.id.nav_host_fragment
        )
    }

    // 00:04:40
    private fun prepareData() {
        // syncDataIfNeed - данные нужно синхронизировать только при первом запуске приложения
        viewModel.syncDataIfNeed().observe(this, Observer<LoadResult<Boolean>> {
            when (it) {
                is LoadResult.Loading -> {
                    navController.navigate(R.id.nav_splash)
                }
                is LoadResult.Success -> {
                    val action = SplashFragmentDirections.actionNavSplashToNavHouses()
                    // 00:10:35 здесь аргументом именно action, потому что он использует поведение,
                    // не дающее вернуться с экрана домов на сплэш экран
                    navController.navigate(action)
                }
                is LoadResult.Error -> {
                    Snackbar.make(
                        root_container, // добавила в xml
                        it.errorMessage.toString(),
                        Snackbar.LENGTH_INDEFINITE
                    ).show()
                }
            }
        })
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(RootViewModel::class.java)
    }
}
