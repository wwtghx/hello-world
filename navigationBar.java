
Android判断手机时候有导航栏的方法
2017年04月01日 10:02:26 珊灵之路 阅读数：3490
参考：https://zhidao.baidu.com/question/1241728811608853219.html

            https://segmentfault.com/q/1010000004387583

            http://www.cnblogs.com/huxdiy/p/3977232.html

            http://tieba.baidu.com/p/3690819624


第一种：判断手机是否有物理按键，有就没有导航栏，反之就有（这个有点问题，逻辑不严谨，4.0以上所有手机都可以显示NavigationBar，只是手机厂家屏蔽了）。

public static boolean checkDeviceHasNavigationBar(Context activity) {
        //通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
        boolean hasMenuKey = ViewConfiguration.get(activity)
                .hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap
                .deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            // 做任何自己需要做的,这个设备有一个导航栏
            return true;
        }
        return false;
    }

第二种：获取手机是否允许使用（显示）NavigationBar

//获取是否存在NavigationBar
public static boolean checkDeviceHasNavigationBar(Context context) {
    boolean hasNavigationBar = false;
    Resources rs = context.getResources();
    int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
    if (id > 0) {
        hasNavigationBar = rs.getBoolean(id);
    }
    try {
        Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
        Method m = systemPropertiesClass.getMethod("get", String.class);
        String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
        if ("1".equals(navBarOverride)) {
            hasNavigationBar = false;
        } else if ("0".equals(navBarOverride)) {
            hasNavigationBar = true;
        }
    } catch (Exception e) {

    }
    return hasNavigationBar;

}


第三种：通过获取不同状态的屏幕高度对比判断是否有NavigationBar


1.
//获取屏幕尺寸，不包括虚拟功能高度<br><br>
getWindowManager().getDefaultDisplay().getHeight();


2.

获取屏幕原始尺寸高度，包括虚拟功能键高度，


private int getDpi()
    {  int dpi = 0;
            Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics dm =new DisplayMetrics();
        @SuppressWarnings("rawtypes")
                Class c;
        try{
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
                        Method method = c.getMethod("getRealMetrics",DisplayMetrics.class);
            method.invoke(display, dm);
            dpi=dm.heightPixels;
        }catch(Exception e){
            e.printStackTrace();
        } 
        returndpi;
    }
用“2”中的高度减去“1”中的高度如果大于0就存在NavigationBar，反之不存在。

还看到一个方法跟以上类似：
/**
* 判断底部navigator是否已经显示
* @param windowManager
* @return
*/
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
private boolean hasSoftKeys(WindowManager windowManager){
Display d = windowManager.getDefaultDisplay();


DisplayMetrics realDisplayMetrics = new DisplayMetrics();
d.getRealMetrics(realDisplayMetrics);


int realHeight = realDisplayMetrics.heightPixels;
int realWidth = realDisplayMetrics.widthPixels;


DisplayMetrics displayMetrics = new DisplayMetrics();
d.getMetrics(displayMetrics);


int displayHeight = displayMetrics.heightPixels;
int displayWidth = displayMetrics.widthPixels;


return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
}



 /**
 *添加全局监听
 */
 mView.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                handleBottonSoftKey(this);
            }
        });
 /**
* 具体处理
*/
public void handleBottonSoftKey(Activity activity){
	if (isHaveSoftKey(activity)) {
            DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mViewContainer.getLayoutParams();
            params.setMargins(0, 0, 0, ScreenUtils.getBottomSoftKeysHeight(activity));
            mViewContainer.setLayoutParams(params);
        } else {
            DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) mViewContainer.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            mViewBinding.homeDrawCoordinatorCl.setLayoutParams(params);
        }
}

 /**
 *是否存在底部虚拟按键
 */

 	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isHaveSoftKey(Activity activity) {
        Display d = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);
        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);
        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;
        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

     /**
     * 获取底部虚拟按键的高度
     *
     * @param activity
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getBottomSoftKeysHeight(Activity activity) {
        Display d = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);
        int realHeight = realDisplayMetrics.heightPixels;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);
        int displayHeight = displayMetrics.heightPixels;
        return (realHeight - displayHeight);
    }
	
	
android键盘及虚拟键适配
2017年12月21日 11:11:02 形上为道 阅读数：754
聊天界面，需要实现切换软键盘及输入框下面的其它聊天工具时，输入框的位置不变。Android系统未提供判断软键盘是否弹出及虚拟按键是否显示的方法（注：个人认为在inputManager.toggleSoftInput()方法的内部实现有可能有判断键盘是否弹出的方法，可惜无法点到源码，如果有谁找到了可通过反射调用的方法，请分享），在网上查到的资料，只能通过OnGlobalLayoutListener对可见屏幕高度的变化进行监听。但使用这个监听要注意，其监听的时机并非我们想象的只有屏幕可见高度变化才会调用，有时会调用多次；而且有些手机，即使隐藏了虚拟键，弹出键盘时也会自动弹出，所以在开发过程中遇到各种坑。尝试了大约5天，仍然无法完美解决，只能说还算可以接受。微信是直接锁定显示虚拟键，不允许用户隐藏，这个我在网上没查到资料，有哪位大神知道如何做到的，可以在下面评论。

无论如何也是自己花了这么长时间的成果，这期间耗费了不知多少脑细胞，所以还是把相关代码贴出来。

	//获取虚拟按键高度
        int id = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        final int vmKeyHeight = id == 0 ? 0 : getActivity().getResources().getDimensionPixelSize(id);
 
        onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                decorView.getWindowVisibleDisplayFrame(rect);
                if (isFirstShow) {//第一次，进行数据初始化
                    isFirstShow = false;
                    isVmKeyLastShow = rect.bottom != decorView.getHeight();
                    lastBottom = rect.bottom;
                    int initHight = EdoPreference.getInt(EdoPreference.KEY_KEYBOARD_HEIGHT, 0);
                    if (initHight > 0)
                        chatBottomView.setContentViewHeight(initHight);
                    else
                        chatBottomView.setContentViewHeight((int) (decorView.getHeight() * 0.4));
                    return;
                }
                int keyboardHeight = decorView.getHeight() - rect.bottom;//虚拟按键+键盘高度
                Log.i(TAG, "onGlobalLayout: 键盘高度=" + keyboardHeight);
                if (hasNavigationBar) {//手机有虚拟键
                    if (lastBottom == decorView.getHeight() - vmKeyHeight)
                        isVmKeyLastShow = true;
                    else if (lastBottom == decorView.getHeight())
                        isVmKeyLastShow = false;
 
                    int bottomHeight = EdoPreference.getInt(EdoPreference.KEY_KEYBOARD_HEIGHT, 0);
                    if (keyboardHeight > vmKeyHeight) {//键盘处于显示状态
                        if (lastBottom >= decorView.getHeight() - vmKeyHeight)
                            chatBottomView.isKeyboardOperatedByApp = false;//重置默认值
                        if (bottomHeight == keyboardHeight && !isVmKeyLastShow) {//虚拟键隐藏
                            Log.i(TAG, "onGlobalLayout: 虚拟键隐藏，高度不变");
                            lastBottom = rect.bottom;
                            return;
                        } else if (bottomHeight == keyboardHeight - vmKeyHeight && isVmKeyLastShow) {//虚拟按键显示
                            Log.i(TAG, "onGlobalLayout: 虚拟按键显示，高度不变");
                            lastBottom = rect.bottom;
                            return;
                        }
                        //其它情况，如切换输入法导致的键盘高度变化、横竖屏切换等
                        if (isVmKeyLastShow && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                            keyboardHeight -= vmKeyHeight;
                        Log.i(TAG, "onGlobalLayout: 其它情况");
                    } else if (keyboardHeight == vmKeyHeight) {//虚拟按键显示，但有可能是键盘弹出/隐藏过程中显示的
                        if (!isVmKeyLastShow && lastBottom == decorView.getHeight()) {//之前未显示，现在显示了，应减小高度***(如果是先弹出虚拟键然后就弹出键盘会有问题)
                            Log.i(TAG, "onGlobalLayout: 虚拟按键处于显示状态");
                            keyboardHeight = bottomHeight - vmKeyHeight;
                            isVmKeyLastShow = true;
 
                        } else if (isVmKeyLastShow && lastBottom < decorView.getHeight() - vmKeyHeight) {//键盘隐藏，但虚拟按键显示
                            Log.i(TAG, "onGlobalLayout: 键盘隐藏，但虚拟按键显示");
                            if (chatBottomView.isKeyboardOperatedByApp) {//切换chatTool使app隐藏了键盘
                                chatBottomView.isKeyboardOperatedByApp = false;//重置默认值
                                Log.i(TAG, "onGlobalLayout: 切换chatTool使app隐藏了键盘");
                            } else {//user主动隐藏键盘
                                chatBottomView.changeChatToolsView(false);
                                Log.i(TAG, "onGlobalLayout: user主动隐藏键盘");
                            }
                            lastBottom = rect.bottom;
                            return;
                        } else
                            return;
                    } else if (keyboardHeight == 0) {//键盘和虚拟按键均隐藏
                        if (lastBottom < decorView.getHeight() - vmKeyHeight) {//键盘隐藏
                            Log.i(TAG, "onGlobalLayout: 键盘隐藏，虚拟按键隐藏");
                            if (chatBottomView.isKeyboardOperatedByApp) {//切换chatTool使app隐藏了键盘
                                chatBottomView.isKeyboardOperatedByApp = false;//重置默认值
                                Log.i(TAG, "onGlobalLayout: 切换chatTool使app隐藏了键盘");
                            } else {//user主动隐藏键盘
                                chatBottomView.changeChatToolsView(false);
                                Log.i(TAG, "onGlobalLayout: user主动隐藏键盘");
                            }
                            isVmKeyLastShow = false;
                            keyboardHeight = bottomHeight + vmKeyHeight;
//                        lastBottom = rect.bottom;
//                        return;
                        } else if (lastBottom == decorView.getHeight() - vmKeyHeight) {//虚拟按键隐藏
                            keyboardHeight = bottomHeight + vmKeyHeight;
                            Log.i(TAG, "onGlobalLayout: 虚拟按键隐藏了");
                            isVmKeyLastShow = false;
                        } else
                            return;
                    } else
                        return;
                    EdoLog.i(TAG, "onGlobalLayout: " + keyboardHeight);
                    lastBottom = rect.bottom;
                    if (keyboardHeight == bottomHeight)
                        return;
                    chatBottomView.setContentViewHeight(keyboardHeight);
                    chatBottomView.setVisibility(View.INVISIBLE);
                    chatBottomView.postDelayed(new Runnable() {//解决输入框闪烁问题
                        @Override
                        public void run() {
                            chatBottomView.setVisibility(View.VISIBLE);
                            chatBottomView.requestEdittextFocus();
                        }
                    }, 200);
                    EdoPreference.setPref(EdoPreference.KEY_KEYBOARD_HEIGHT, keyboardHeight);
                } else {//手机没有虚拟按键
                    if (keyboardHeight > 0) {//键盘显示
                        int bottomHeight = EdoPreference.getInt(EdoPreference.KEY_KEYBOARD_HEIGHT, 0);
                        chatBottomView.isKeyboardOperatedByApp = false;
                        if (keyboardHeight == bottomHeight)
                            return;
                        chatBottomView.setContentViewHeight(keyboardHeight);
                        EdoPreference.setPref(EdoPreference.KEY_KEYBOARD_HEIGHT, keyboardHeight);
                    } else {//键盘隐藏
                        if (chatBottomView.isKeyboardOperatedByApp) {
                            chatBottomView.isKeyboardOperatedByApp = false;
                        } else {
                            chatBottomView.changeChatToolsView(false);
                        }
                    }
                }
            }
        };
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);


底部虚拟导航栏适配问题
置顶 2017年10月26日 09:56:06 T丶快乐知己丬 阅读数：2540 标签： sdk 手机 android 适配  更多
个人分类： android
版权声明：本文为博主原创文章，未经博主允许不得转载。	https://blog.csdn.net/qq_19707091/article/details/78349736
问题 
安卓机型太多适配比较麻烦，这里来讲讲部分手机有的底部虚拟导航按钮适配问题，我遇到有虚拟底部导航栏的手机就有：华为，索尼，OPPO……。 
底部导航栏适配需要解决的以下几个问题：

哪些机型有底部虚拟导航栏;
底部导航栏的高度是多少；
针对有底部导航对布局高度进行调整；
是否显示底部导航栏 
SDK在版本17之后增加了一个获取Window显示区域分辨率和实际分辨率大小的两个方法：

  Display display = act.getWindowManager().getDefaultDisplay();
 display.getSize(size);
 display.getRealSize(realSize);  
1
2
3
realSize.y其实包含了虚拟导航栏的高度，如果两个size的y坐标不想等，那么就说明有虚拟底部导航栏。 
在低版本里面我们就只能通过，设备是否有永久菜单或者有返回键来判断，如果在设备上有了物理返回按钮或菜单就不需要再添加虚拟的导航栏，如果两个都有，那么这个手机厂商估计也倒闭了，直接代码：

boolean menu = ViewConfiguration.get(act).hasPermanentMenuKey(); 
boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

只要menu或者back有一个就表示有物理导航栏，没有虚拟底部导航栏了。

底部导航栏的高度 
获取底部导航栏高度，在系统的资源里面有这个高度的值，我们通过获取系统资源数据就OK： 
int resourceId = context.getResources().getIdentifier(“navigation_bar_height”, “dimen”, “android”); 
context.getResources().getDimensionPixelSize(resourceId);

根据导航栏调整布局的高度 
调整布局高度根据需求作出响应的调整就OK。

代码 
最后把第1，2的代码逻辑整理成工具方法，大家可以直接拷贝使用：

/**
 * 判断底部导航栏是否显示
 *
 * @param act
 * @return
 */
public static boolean isNavigationBarShow(Activity act) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        Display display = act.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        Point realSize = new Point();
        display.getSize(size);
        display.getRealSize(realSize);
        return realSize.y != size.y;
    } else {
        boolean menu = ViewConfiguration.get(act).hasPermanentMenuKey();
        boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if (menu || back) {
            return false;
        } else {
            return true;
        }
    }
}
   /**
 * 如果有底部导航栏 获取底部导航栏高度
 * @param context
 * @return
 */
public static int getBottomNavigatorHeight(Context context) {
    int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
    if (0 != rid) {
       int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }
    return 0;
}

华为底部虚拟导航栏挡住布局
2018年01月24日 17:20:51 Red风信子 阅读数：3341
问题：在实现ViewPager+Fragment+侧滑栏的界面时，华为搭载Android5.0以上操作系统的手机出现底部虚拟导航栏挡住布局。如下图所示：

这里写图片描述

问题解决后： 
这里写图片描述

尝试
在实现这个功能的时候，我发现底部虚拟导航栏遮盖布局不同的情况对应不同的解决方法。当没有侧滑功能的时候，主要有一下两种：

1. OnCreate()方法中不能出现下边的代码：
getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
1
 就是设置导航栏半透明，这会使布局向上向下扩展至整个屏幕，导航栏则覆盖在布局上边，就会导致导航栏挡住布局。有的说法是换成设置状态栏半透明，如下边的代码：

getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
1
 这种做法其实是不好的，属于伤敌一千自伤八百。因为，我们设置这个属性一般是为了实现沉浸式状态栏的，去掉了第一种代码，就不能实现了。比如说我使用了SystemBarTint第三方框架来实现沉浸式状态栏。这时就需要用到方法2了。 


2. 在布局的根布局中添加android:fitsSystemWindows=”true”
比如：

<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true
    android:orientation="vertical">
 
    <View
        android:layout_width="match_parent"
        android:layout_height="@dimen/theme_divide_height"
        android:background="#3D81D6"/>
 
</LinearLayout>

我们看一下，Android官方API对这个属性的解释：

Boolean internal attribute to adjust view layout based on system windows such as the status bar. 
If true, adjusts the padding of this view to leave space for the system windows. Will only take effect if this view is in a non-embedded activity.
 
May be a boolean value, such as "true" or "false".
1
2
3
4
翻译： 
 布尔内部属性，基于系统窗口（如状态栏）来调整视图布局。如果为true，则调整此视图的填充，以便为系统窗口留出空间。只有在非嵌入activity中此视图才会生效。 

 这个方法就使系统窗口可以自动调整，可以实现需求。但是如果界面中有侧滑菜单的，并且实现了顶部导航栏透明，和底部导航栏颜色填充的话，就需要下边的方法了。 


有效方法
在 style.xml 文件中的项目的主题样式中添加
<item name="android:windowDrawsSystemBarBackgrounds">false</item>
1
我们看一下，Android官方API对这个属性的解释：

Flag indicating whether this Window is responsible for drawing the background for the system bars. If true and the window is not floating, the system bars are 
drawn with a transparent background and the corresponding areas in this window are filled with the colors specified in statusBarColor and navigationBarColor. Corresponds to FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS.
 
May be a boolean value, such as "true" or "false".
1
2
3
4
翻译： 
 标志是指示此窗口是否负责绘制系统栏的背景。如果真正的窗口不浮，系统栏被画在这个窗口透明背景和相应领域内statusbarcolor和navigationbarcolor指定的颜色。对应于flag_draws_system_bar_backgrounds。 

 可以看出该属性是负责绘制系统栏的背景的，如果真正的窗口被遮盖了，设置true，则会绘制系统栏的背景，使真正的窗口上移，不被遮挡住。 

 如果你的项目兼容的最低版本小于21的话 ，会红线提示错误，虽然可以运行但是代码无效。解决方法是：在提示错误的代码上Alt+Enter，会提示： 

这里写图片描述

 选择第一个，就会自动生成适配Android 21的values文件夹：values-v21，里边有包含该属性的styles.xml文件。之前添加的报错的属性就可以删掉了。当然，你也可以自己新建文件夹，自己实现。如下图： 

这里写图片描述 


如果不知道项目的主题样式在哪儿，可以用下边的查找方式：

 打开资源配置文件AndroidManifest.xml，跟进属性 Android:theme=”@style/AppTheme”中的style： 

这里写图片描述 

 tips：android:windowDrawsSystemBarBackgrounds在Android官方API文档版本21以上的可以查到，下边附一个我使用的文档的连接： 
最新版Android官方API文档 

 好了，到此就结束了。希望能帮到大家。


最简单解决Android适配之【虚拟按键遮挡布局】
2018年10月30日 16:54:06 佳姝 阅读数：480
最近开发项目有个适配的小问题，在用android studio 开发程序的时候发现一个问题，虚拟按键会遮挡布局，在6.0.1的系统中虚拟按键会挡住布局，特别是华为手机和小米底部会有虚拟按键等，而在6.0以下的API手机上测试不会出现这个问题。

在网上有好几种解决方案，但在我一一尝试之下最简单，最省事，最快速的解决方案如下： 

                                                          
佳姝1：在该xml布局的父布局中加上下面这句代码
android:fitsSystemWindows="true"

佳姝2：在Activity或BaseActivity的Oncreate()方法中添加所需状态栏或导航栏 
//状态栏 @ 顶部getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//导航栏 @ 底部getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

代码示例：
 protected void initSystemBarTint() {

    Window window = getWindow();

    if (translucentStatusBar()) {

        // 沉浸式状态栏

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |           View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(Color.BLACK);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        }
        return;
    }
 当然还有其他方法但对于我试出来的没效果，咱都可以借鉴以下也以便我做🖊记

还有更多🖊记：https://me.csdn.net/lj_18210158431


虚拟导航栏遮挡布局
2016年09月28日 23:11:04 phyooos 阅读数：3558 标签： 导航 布局  更多
个人分类： bug
版权声明：本文为博主原创文章，未经博主允许不得转载。	https://blog.csdn.net/phyooos/article/details/52695113
从某种意义上来说,其实是我们的布局顶到人家虚拟导航键下面去了 
如图: 
这里写图片描述

这个BUG客户发现的,他用的华为m8手机,自带虚拟导航栏,你懂得!!!! 
客户:这是什么情况 ??? 黑人问号? 
我 : 额,这个,,我回去看看,放心,只是小问题

1.然后百度到一个方法:
//根布局加
android:fitsSystemWindows="true"
1
2
效果简直爆炸:

这是真机
这里写图片描述

虚拟机
这里写图片描述

我就想问,多出来这一块算谁的?

2.后来,我就认认真真的读资料
fitSystemWindows属性：

官方描述:

Boolean internal attribute to adjust view layout based on system windows such as the status bar. If true, adjusts the padding of this view to leave space for the system windows. Will only take effect if this view is in a non-embedded activity.
1
简单描述：

这个一个boolean值的内部属性，让view可以根据系统窗口(如status bar)来调整自己的布局，如果值为true,就会调整view的paingding属性来给system windows留出空间….

实际效果：

当status bar为透明或半透明时(4.4以上),系统会设置view的paddingTop值为一个适合的值(status bar的高度)让view的内容不被上拉到状态栏，当在不占据status bar的情况下(4.4以下)会设置paddingTop值为0(因为没有占据status bar所以不用留出空间)。

读完,我才发现我自己已经用代码实现了该功能 
这就意味着我只要是加入了fitSystemWindows属性,那就是

双倍的padding

附 : 这是我的代码实现
//被我写在了BaseActivity.java

 /**
   * 设置沉浸式状态栏
   */
protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final int statusHeight = getStatusBarHeight();
            UUtils.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTitleBarView.getLayoutParams();
                    params.setMargins(0,statusHeight,0,0);
                    mTitleBarView.setLayoutParams(params);
                }
            });
        }
    }


/**
* 获取状态栏的高度
* @return
*/
protected int getStatusBarHeight(){
        try
        {
            Class<?> c=Class.forName("com.android.internal.R$dimen");
            Object obj=c.newInstance();
            Field field=c.getField("status_bar_height");
            int x=Integer.parseInt(field.get(obj).toString());
            return  getResources().getDimensionPixelSize(x);
        }catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }

3.终于:
在我折腾的过程中,注释掉了

  //透明导航栏
  getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
1
2
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
           getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
           // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
     }

好了哎,真特么的神奇
4.分析
我的手机和虚拟机都是6.0.1 
满足 :

Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

所以,双倍的padding造成下面多出来一部分,其实那就是你的根布局

一开始的虚拟键挡住了我的布局 
其实就是

getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION); 
的功劳,他把虚拟键给搞透明了,布局就直接下去了…哈哈

结论:
好好学习,天天向上
