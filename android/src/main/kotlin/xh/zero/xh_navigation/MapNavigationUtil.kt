package xh.zero.xh_navigation

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import java.util.*
import java.math.BigDecimal


class MapNavigationUtil {
    companion object {
        // 导航方式
        const val NAVIGATION_AMAP = 0
        const val NAVIGATION_BAIDU = 1
        const val NAVIGATION_QQMAP = 2

        const val VEHICLE_TYPE_ELEBIKE = "electric" // 电动车
        const val VEHICLE_TYPE_CAR = "minivan" // 小汽车

        private const val X_PI = 3.14159265358979324 * 3000.0 / 180.0

        fun checkInstalledMapApps(
            context: Activity,
            error: (e: String) -> Unit,
            hasInstalledMap: (hasAmap: Boolean, hasBaidu: Boolean, hasTencent: Boolean) -> Unit
        ) {
            Thread {
                val pm = context.packageManager
                // 查询所有已经安装的应用程序
                val apps = pm?.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES)
                Collections.sort(apps, ApplicationInfo.DisplayNameComparator(pm))// 排序
                var hasAmap = false
                var hasBaidu = false
                var hasTencent = false
                apps?.let { it ->
                    for (app in it) {
                        if ("com.baidu.BaiduMap" == app.packageName) {
                            hasBaidu = true
                        }
                        if ("com.autonavi.minimap" == app.packageName) {
                            hasAmap = true
                        }
                        if ("com.tencent.map" == app.packageName) {
                            hasTencent = true
                        }
                    }
                }
                context.runOnUiThread {
                    if (!hasAmap && !hasBaidu && !hasTencent) {
//                        Toast.makeText(context, "您没有安装高德、百度或腾讯地图", Toast.LENGTH_SHORT).show()
                        error("您没有安装高德、百度或腾讯地图")
                    } else {
                        hasInstalledMap(hasAmap, hasBaidu, hasTencent)
                    }
                }
            }.start()
        }

        // 显示地图App选择框
        fun showNavigationSelect(
            context: Activity,
            progressDialog: AlertDialog?,
            endPointLat: Double,
            endPointLng: Double,
            endAddress: String,
            vehicleType: String?
        ) {

            progressDialog?.show()
            Thread {
                val pm = context.packageManager
                // 查询所有已经安装的应用程序
                val apps = pm?.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES)
                Collections.sort(apps, ApplicationInfo.DisplayNameComparator(pm))// 排序
                var hasAmap = false
                var hasBaidu = false
                var hasQQ = false
                apps?.let { it ->
                    for (app in it) {
                        if ("com.baidu.BaiduMap" == app.packageName) {
                            hasBaidu = true
                        }
                        if ("com.autonavi.minimap" == app.packageName) {
                            hasAmap = true
                        }
                        if ("com.tencent.map" == app.packageName) {
                            hasQQ = true
                        }
                    }
                }
                context.runOnUiThread {
                    if (!hasAmap && !hasBaidu && !hasQQ) {
                        Toast.makeText(context, "您没有安装高德、百度或腾讯地图", Toast.LENGTH_SHORT).show()
                    } else {
                        val v = LayoutInflater.from(context).inflate(R.layout.dialog_navigation, null)
                        val rbAmap = v.findViewById<RadioButton>(R.id.navigation_amap)
                        val rbBaidu = v.findViewById<RadioButton>(R.id.navigation_baidu)
                        val rbQQ = v.findViewById<RadioButton>(R.id.navigation_qq)

                        rbAmap.visibility = if (hasAmap) View.VISIBLE else View.GONE
                        rbBaidu.visibility = if (hasBaidu) View.VISIBLE else View.GONE
                        rbQQ.visibility = if (hasQQ) View.VISIBLE else View.GONE

                        val dialog = AlertDialog.Builder(context)
                            .setTitle("请选择导航方式")
                            .setView(v)
                            .create()

                        rbAmap.setOnClickListener {
                            if (it is RadioButton && it.isChecked) {
                                //调起高德地图
                                navigate(context, endPointLat, endPointLng, endAddress, NAVIGATION_AMAP, vehicleType)
                                dialog.dismiss()
                            }
                        }

                        rbBaidu.setOnClickListener {
                            if (it is RadioButton && it.isChecked) {
                                // 高德坐标转百度坐标
                                val x = endPointLng
                                val y = endPointLat
                                val z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * X_PI)
                                val theta = Math.atan2(y, x) + 0.000003 * Math.cos(x *  X_PI)
                                val lat = dataDigit(6,z * Math.sin(theta) + 0.006)
                                val lng = dataDigit(6,z * Math.cos(theta) + 0.0065)
                                //调起百度地图
                                navigate(context, lat, lng, endAddress, NAVIGATION_BAIDU, vehicleType)
                                dialog.dismiss()
                            }
                        }

                        rbQQ.setOnClickListener {
                            if (it is RadioButton && it.isChecked) {
                                //调起腾讯地图
                                navigate(context, endPointLat, endPointLng, endAddress, NAVIGATION_QQMAP, vehicleType)
                                dialog.dismiss()
                            }
                        }

                        dialog.show()
                    }
                    progressDialog?.dismiss()
                }
            }.start()
        }

        // 调起地图App
        fun navigate(
            context: Context,
            endPointLat: Double,
            endPointLng: Double,
            endAddress: String,
            type: Int,
            vehicleType: String?
        ) {
            when (type) {
                NAVIGATION_AMAP -> {
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    intent.addCategory(Intent.CATEGORY_DEFAULT)

                    val t = when (vehicleType) {
                        VEHICLE_TYPE_CAR -> 0
                        VEHICLE_TYPE_ELEBIKE -> 3
                        else -> 0
                    }

                    val rideType = if (vehicleType == VEHICLE_TYPE_ELEBIKE) "elebike" else ""

                    // 起点采用手机GPS定位，参数默认为空
                    // 高德导航参考文档 https://lbs.amap.com/api/amap-mobile/guide/android/route
                    val uri = Uri.parse(
                        "amapuri://route/plan/?sid=" +
                                "&slat=&slon=" +
                                "&sname=&did=" +
                                "&dlat=$endPointLat&dlon=$endPointLng" +
                                "&dname=$endAddress" +
                                "&dev=0&t=$t&rideType=$rideType"
                    )
                    intent.data = uri

                    //启动该页面即可
                    context.startActivity(intent)
                    Toast.makeText(context, "正在打开高德地图App", Toast.LENGTH_SHORT).show()
                }
                NAVIGATION_BAIDU -> {
                    // 百度导航参考文档 http://lbsyun.baidu.com/index.php?title=uri/api/android
                    /**
                     * "baidumap://map/direction?destination=name:$endAddress|latlng:$endPointLat,$endPointLng&coord_type=bd09ll&mode=transit&sy=3&index=0&target=1&src=andr.baidu.openAPIdemo"
                     */
                    val intent = Intent()
                    val mode = when (vehicleType) {
                        VEHICLE_TYPE_CAR -> "driving"
                        VEHICLE_TYPE_ELEBIKE -> "riding"
                        else -> "driving"
                    }
                    val url = "baidumap://map/direction?destination=name:$endAddress|latlng:$endPointLat,$endPointLng" +
                            "&coord_type=bd09ll&mode=$mode&sy=3&index=0&target=1&src=andr.baidu.openAPIdemo"
//                    intent.data = Uri.parse("baidumap://map/navi?query=$endAddress" +
//                            "&location=$endPointLat,$endPointLng" +
//                            "&src=andr.baidu.openAPIdemo" +
//                            "&mode=$mode")
                    intent.data = Uri.parse(url)
                    context.startActivity(intent)
                    Toast.makeText(context, "正在打开百度地图App", Toast.LENGTH_SHORT).show()
                }
                NAVIGATION_QQMAP -> {
                    // 腾讯地图参考文档 https://lbs.qq.com/uri_v1/guide-mobile-navAndRoute.html
                    val intent = Intent()
                    val typeQQMap = when (vehicleType) {
                        VEHICLE_TYPE_CAR -> "drive"
                        VEHICLE_TYPE_ELEBIKE -> "bike"
                        else -> "drive"
                    }
                    intent.data = Uri.parse(
                        "qqmap://map/routeplan?type=$typeQQMap" +
                                "&from=&fromcoord=CurrentLocation" +
                                "&to=$endAddress&tocoord=$endPointLat,$endPointLng" +
                                "&referer=OB4BZ-D4W3U-B7VVO-4PJWW-6TKDJ-WPB77"
                    )
                    context.startActivity(intent)
                    Toast.makeText(context, "正在打开腾讯地图App", Toast.LENGTH_SHORT).show()
                }
            }

        }

        fun showNavigationSelect(
            context: Activity,
            progressDialog: AlertDialog?,
            endAddress: String?,
            vehicleType: String? = VEHICLE_TYPE_CAR
        ) {

            progressDialog?.show()
            Thread {
                val pm = context.packageManager
                // 查询所有已经安装的应用程序
                val apps = pm?.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES)
                Collections.sort(apps, ApplicationInfo.DisplayNameComparator(pm))// 排序
                var hasAmap = false
                var hasBaidu = false
                var hasQQ = false
                apps?.let { it ->
                    for (app in it) {
                        if ("com.baidu.BaiduMap" == app.packageName) {
                            hasBaidu = true
                        }
                        if ("com.autonavi.minimap" == app.packageName) {
                            hasAmap = true
                        }
                        if ("com.tencent.map" == app.packageName) {
                            hasQQ = true
                        }
                    }
                }
                context.runOnUiThread {
                    if (!hasAmap && !hasBaidu && !hasQQ) {
                        Toast.makeText(context, "您没有安装高德、百度或腾讯地图", Toast.LENGTH_SHORT).show()
                    } else {
                        val v = LayoutInflater.from(context).inflate(R.layout.dialog_navigation, null)
                        val rbAmap = v.findViewById<RadioButton>(R.id.navigation_amap)
                        val rbBaidu = v.findViewById<RadioButton>(R.id.navigation_baidu)
                        val rbQQ = v.findViewById<RadioButton>(R.id.navigation_qq)

                        rbAmap.visibility = if (hasAmap) View.VISIBLE else View.GONE
                        rbBaidu.visibility = if (hasBaidu) View.VISIBLE else View.GONE
                        rbQQ.visibility = if (hasQQ) View.VISIBLE else View.GONE

                        val dialog = AlertDialog.Builder(context)
                            .setTitle("请选择导航方式")
                            .setView(v)
                            .create()

                        rbAmap.setOnClickListener {
                            if (it is RadioButton && it.isChecked) {
                                //调起高德地图
                                navigate(context, endAddress, NAVIGATION_AMAP, vehicleType)
                                dialog.dismiss()
                            }
                        }

                        rbBaidu.setOnClickListener {
                            if (it is RadioButton && it.isChecked) {
                                // 高德坐标转百度坐标
//                                val x = endPointLng
//                                val y = endPointLat
//                                val z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * X_PI)
//                                val theta = Math.atan2(y, x) + 0.000003 * Math.cos(x *  X_PI)
//                                val lat = dataDigit(6,z * Math.sin(theta) + 0.006)
//                                val lng = dataDigit(6,z * Math.cos(theta) + 0.0065)
                                //调起百度地图
                                navigate(context, endAddress, NAVIGATION_BAIDU, vehicleType)
                                dialog.dismiss()
                            }
                        }

                        rbQQ.setOnClickListener {
                            if (it is RadioButton && it.isChecked) {
                                //调起腾讯地图
                                navigate(context, endAddress, NAVIGATION_QQMAP, vehicleType)
                                dialog.dismiss()
                            }
                        }

                        dialog.show()
                    }
                    progressDialog?.dismiss()
                }
            }.start()
        }

        // 调起地图App
        private fun navigate(
            context: Context,
            endAddress: String?,
            type: Int,
            vehicleType: String?
        ) {
            when (type) {
                NAVIGATION_AMAP -> {
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    intent.addCategory(Intent.CATEGORY_DEFAULT)

                    val t = when (vehicleType) {
                        VEHICLE_TYPE_CAR -> 0
                        VEHICLE_TYPE_ELEBIKE -> 3
                        else -> 0
                    }

                    val rideType = if (vehicleType == VEHICLE_TYPE_ELEBIKE) "elebike" else ""

                    // 起点采用手机GPS定位，参数默认为空
                    // 高德导航参考文档 https://lbs.amap.com/api/amap-mobile/guide/android/route
                    val uri = Uri.parse(
                        "amapuri://route/plan/?sid=" +
                            "&slat=&slon=" +
                            "&sname=&did=" +
                            "&dlat=&dlon=" +
                            "&dname=$endAddress" +
                            "&dev=0&t=$t&rideType=$rideType"
                    )
                    intent.data = uri

                    //启动该页面即可
                    context.startActivity(intent)
                    Toast.makeText(context, "正在打开高德地图App", Toast.LENGTH_SHORT).show()
                }
                NAVIGATION_BAIDU -> {
                    // 百度导航参考文档 http://lbsyun.baidu.com/index.php?title=uri/api/android
                    /**
                     * "baidumap://map/direction?destination=name:$endAddress|latlng:$endPointLat,$endPointLng&coord_type=bd09ll&mode=transit&sy=3&index=0&target=1&src=andr.baidu.openAPIdemo"
                     */
                    val intent = Intent()
                    val mode = when (vehicleType) {
                        VEHICLE_TYPE_CAR -> "driving"
                        VEHICLE_TYPE_ELEBIKE -> "riding"
                        else -> "driving"
                    }
                    val url = "baidumap://map/direction?destination=name:$endAddress|latlng:" +
                        "&coord_type=bd09ll&mode=$mode&sy=3&index=0&target=1&src=andr.baidu.openAPIdemo"
//                    intent.data = Uri.parse("baidumap://map/navi?query=$endAddress" +
//                            "&location=$endPointLat,$endPointLng" +
//                            "&src=andr.baidu.openAPIdemo" +
//                            "&mode=$mode")
                    intent.data = Uri.parse(url)
                    context.startActivity(intent)
                    Toast.makeText(context, "正在打开百度地图App", Toast.LENGTH_SHORT).show()
                }
                NAVIGATION_QQMAP -> {
                    // 腾讯地图参考文档 https://lbs.qq.com/uri_v1/guide-mobile-navAndRoute.html
                    val intent = Intent()
                    val typeQQMap = when (vehicleType) {
                        VEHICLE_TYPE_CAR -> "drive"
                        VEHICLE_TYPE_ELEBIKE -> "bike"
                        else -> "drive"
                    }
                    intent.data = Uri.parse(
                        "qqmap://map/routeplan?type=$typeQQMap" +
                            "&from=&fromcoord=CurrentLocation" +
                            "&to=$endAddress&tocoord=" +
                            "&referer=OB4BZ-D4W3U-B7VVO-4PJWW-6TKDJ-WPB77"
                    )
                    context.startActivity(intent)
                    Toast.makeText(context, "正在打开腾讯地图App", Toast.LENGTH_SHORT).show()
                }
            }

        }

        private fun dataDigit(digit: Int, num: Double) : Double {
            return BigDecimal(num).setScale(6, BigDecimal.ROUND_HALF_UP).toDouble()

        }
    }
}