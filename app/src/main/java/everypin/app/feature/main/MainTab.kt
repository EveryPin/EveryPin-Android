package everypin.app.feature.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import everypin.app.R
import everypin.app.feature.addpin.AddPinRoute
import everypin.app.feature.home.HomeRoute
import everypin.app.feature.my.MyRoute

enum class MainTab(
    @StringRes val labelResId: Int,
    @DrawableRes val selectedIconResId: Int,
    @DrawableRes val unselectedIconResId: Int,
    val route: String
) {
    HOME(
        labelResId = R.string.home,
        selectedIconResId = R.drawable.ic_home_selected,
        unselectedIconResId = R.drawable.ic_home_unselected,
        route = HomeRoute.route
    ),
    ADD_PIN(
        labelResId = R.string.add_pin,
        selectedIconResId = R.drawable.ic_add_location_alt_selected,
        unselectedIconResId = R.drawable.ic_add_location_alt_unselected,
        route = AddPinRoute.route
    ),
    MY(
        labelResId = R.string.my,
        selectedIconResId = R.drawable.ic_account_circle_selected,
        unselectedIconResId = R.drawable.ic_account_circle_unselected,
        route = MyRoute.route
    )
}