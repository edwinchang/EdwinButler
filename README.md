2016-9-20 22:20
----------
版本：1.0.0（包含源码和APK文件）
概述：当前为Android版APP，免费软件
软件功能：当前版本提供每天定时文件同步工具，原理是在局域网中通过PC端（远端）的文件共享夹功能与APP指定文件夹进行同步（当前仅适用于PC端向手机等终端的单向传输），同时用户可自行设定每天定时同步时间（默认时间为晚上21:30）。
需求来源：本人每天主要在PC端进行工作，但在PC端不在身边的出行过程中又需要查看工作中的一些文件，同时又不希望这些重要文件通过互联网的方式进行存储或传输。
------
应用场景：不希望通过互联网传输的软件来同步文件到手机，并希望得到自定义定时自动同步功能。
------
其它描述：本工具在局域网中使用，并且手机等终端需已获得root权限，适用Android4.4.4版本以上。
关于为何当前只能有root权限才能使用：因为Android系统自带的一个未修复的bug，详情可参见软件源码。关于如何绕开这个bug的设计方法已在源码的TODO list中描述，欢迎有兴趣的程序猿(媛)朋友去GitHub上Fork。
之后如果大家喜欢的话会推出更多功能及出IOS版本。
------
文件夹位置：手机等终端的APP指定文件夹一般存在“/data/data/com.edwin.edwinbutler/files/”下。
------
已打包的APK文件在：当前项目的“EdwinButler_apk”目录下。
------
关于如何在PC端进行文件夹共享可参见：http://jingyan.baidu.com/article/e8cdb32b3299f737042bad59.html
------
另外，当前网上实现类似使用smb协议传输文件功能的较好的APP是：AndSMB（为共享软件，高级功能收费，发行公司：LYSESOFT），有兴趣也可以去下载试用。