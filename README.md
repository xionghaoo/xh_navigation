# xh_navigation

第三方地图导航插件

1. 首先检查手机内是否安装高德、百度、腾讯地图

2. 如果已安装其中任意一款地图，则跳转到对应的地图App进行导航

## ios集成注意

info.plist文件添加如下键值对
```
<key>LSApplicationQueriesSchemes</key>
<array>
    <string>qqmap</string>
    <string>baidumap</string>
    <string>iosamap</string>
</array>
```

