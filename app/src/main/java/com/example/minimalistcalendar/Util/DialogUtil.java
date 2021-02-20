package com.example.minimalistcalendar.Util;

import android.content.Context;
import android.util.Log;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.enums.PopupAnimation;
import com.lxj.xpopup.enums.PopupPosition;
import com.lxj.xpopup.interfaces.OnConfirmListener;
import com.lxj.xpopup.interfaces.SimpleCallback;

/**
 * @ProjectName: MinimalistCalendar
 * @Package: com.example.minimalistcalendar.Util
 * @ClassName: DialogUtil
 * @Description: 作用
 * @Author: 作者名
 * @CreateDate: 2021/2/19 9:58
 */
public class DialogUtil {
    //Dialog工具用来提示
    //具体参数https://github.com/li-xiaojun/XPopup/wiki/5.-%E5%B8%B8%E7%94%A8%E8%AE%BE%E7%BD%AE

    //弹出加载中的提示框 比如上传下载
    public static void progressDialog(Context context,String text){
        new XPopup.Builder(context)
                .hasShadowBg(true) // 是否有半透明的背景，默认为true
                .hasBlurBg(false) // 是否有高斯模糊的背景，默认为false
                .isDestroyOnDismiss(true) //是否在消失的时候销毁资源，默认false。如果你的弹窗对象只使用一次，非常推荐设置这个，可以杜绝内存泄漏。如果会使用多次，千万不要设置
                .dismissOnBackPressed(true) // 按返回键是否关闭弹窗，默认为true
                .dismissOnTouchOutside(false) // 点击外部是否关闭弹窗，默认为true
                .popupAnimation(PopupAnimation.ScrollAlphaFromTop) // 设置内置的动画
                .borderRadius(50)  //为弹窗设置圆角，默认是15，对内置弹窗生效
                .autoDismiss(true) // 操作完毕后是否自动关闭弹窗，默认为true；比如点击ConfirmPopup的确认按钮，默认自动关闭；如果为false，则不会关闭
                .setPopupCallback(new SimpleCallback(){
                    @Override
                    public void onCreated(BasePopupView popupView) {
                        //创建的时候
                        super.onCreated(popupView);
                    }

                    @Override
                    public void onShow(BasePopupView popupView) {
                        //显示的时候
                        super.onShow(popupView);
                    }

                    @Override
                    public void onDismiss(BasePopupView popupView) {
                        //隐藏的时候
                        super.onDismiss(popupView);
                    }
                })
                .asLoading(text)
                .show();
    }
    //弹出消息提示框 context必须是Activity类型
    public static void messageDialog(Context context,String title,String content){
        new XPopup.Builder(context)
                .hasShadowBg(true) // 是否有半透明的背景，默认为true
                .hasBlurBg(false) // 是否有高斯模糊的背景，默认为false
                .isDestroyOnDismiss(true) //是否在消失的时候销毁资源，默认false。如果你的弹窗对象只使用一次，非常推荐设置这个，可以杜绝内存泄漏。如果会使用多次，千万不要设置
                .dismissOnBackPressed(true) // 按返回键是否关闭弹窗，默认为true
                .dismissOnTouchOutside(true) // 点击外部是否关闭弹窗，默认为true
                .autoOpenSoftInput(true) //是否弹窗显示的同时打开输入法，只在包含输入框的弹窗内才有效，默认为false
                .popupAnimation(PopupAnimation.ScrollAlphaFromTop) // 设置内置的动画
                .moveUpToKeyboard(false) // 软键盘弹出时，弹窗是否移动到软键盘上面，默认为true
                .enableDrag(true) //是否启用拖拽，默认为true，目前对Bottom和Drawer弹窗有用
                .isThreeDrag(true) //是否启用三阶拖拽（类似于BottomSheet），默认为false，目前对Bottom弹窗有用。如果enableDrag(false)则无效。
                .isDarkTheme(false)  //是否启用暗色主题
                .borderRadius(50)  //为弹窗设置圆角，默认是15，对内置弹窗生效
                .autoDismiss(true) // 操作完毕后是否自动关闭弹窗，默认为true；比如点击ConfirmPopup的确认按钮，默认自动关闭；如果为false，则不会关闭
                .setPopupCallback(new SimpleCallback(){
                    @Override
                    public void onCreated(BasePopupView popupView) {
                        //创建的时候
                        super.onCreated(popupView);
                    }

                    @Override
                    public void onShow(BasePopupView popupView) {
                        //显示的时候
                        super.onShow(popupView);
                    }

                    @Override
                    public void onDismiss(BasePopupView popupView) {
                        //隐藏的时候
                        super.onDismiss(popupView);
                    }
                })
                .asConfirm(title, content,
                        new OnConfirmListener() {
                            @Override
                            public void onConfirm() {

                            }
                        })
                .show();

    }
}
